	처음 실행 시 이름 입력
    '/' 없이 그냥 치는 것은 전부 메세지
    
    '/' 커맨드 종류
    /users -> 채팅방 접속자 명단 출력
    /change|{이름} -> 내 이름 변경
    /exit -> 채팅방 접속 종료

----
# 구조 설계
#### 1. Client에는 두 개의 Handler (Reader, Writer)
#### 2. Server에서는 ServerSocket을 통해 Socket 통신 연결 대기
#### 3. Socket 연결이 확인되면 SessionManager를 통해 각 스레드 Session을 통합 관리
#### 4. 새로운 세션 입장, 이름 변경, 메세지의 경우 자신 이외의 모든 세션에 메세지 전송
-----

## 자원정리 방식
- ### client
handler 인자로 client를 넘겨 exit 커맨드 입력 시 client의 close를 호출
이후 inputStream, outputStream, socket 정리
중복 호출을 막기 위한 closed 변수
```java
public synchronized void close() {
	if (closed) return;
    closed = true;
    readHandler.close();
    writeHandler.close();
    closeAll(socket, dis, dos);
}
```
- ### server
각 세션 연결 종료시 SessionManager의 리스트에서 session 삭제 및 자원정리
server 강제 종료 시 ShutdownHook 호출
```java
public static void main(String[] args) throws IOException {
	ShutdownHook shutdownHook = new ShutdownHook(serverSocket, sessionManager);
	Runtime.getRuntime().addShutdownHook(new Thread(shutdownHook, "shutdown"));
}

static class ShutdownHook implements Runnable {

	private final ServerSocket serverSocket;
    private final SessionManager sessionManager;

    public ShutdownHook(ServerSocket serverSocket, SessionManager sessionManager) {
        this.serverSocket = serverSocket;
        this.sessionManager = sessionManager;
    }

    @Override
    public void run() {
    log("shutdownHook 실행");
        try {
            sessionManager.closeAll();
            serverSocket.close();

            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("e = " + e);
        }
    }
}
```

-----
## Util 간소화

- Unum Class Command 활용
( JOIN, MESSAGE, CHANGE, USERS, EXIT )
- String 형태의 Socket 통신에서 DELIMETER를 '|'로 구분지어 변수 분리

```java
public class RegexUtil {

    private static final String REGEX = "\\|";

    public static Command getCommand(String userCommand){
        String command = userCommand.split(REGEX)[0];
        return switch (command) {
            case "/join" -> Command.JOIN;
            case "/message" -> Command.MESSAGE;
            case "/change" -> Command.CHANGE;
            case "/users" -> Command.USERS;
            case "/exit" -> Command.EXIT;
            default -> null;
        };
    }

    public static String writeCommand(Command command) {
        return switch (command){
            case JOIN -> "/join|";
            case MESSAGE -> "/message|";
            case CHANGE -> "/change|";
            case USERS -> "/users|";
            case EXIT -> "/exit";
        };
    }

    public static String getDetail(String userCommand){
        return userCommand.split(REGEX)[1];
    }

    public static String[] getUsers(String userCommand){
        String[] users = userCommand.split(REGEX);
        return Arrays.copyOfRange(users, 1, users.length);
    }
}
```

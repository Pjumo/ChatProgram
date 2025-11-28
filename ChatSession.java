package chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static chat.util.Command.*;
import static chat.util.RegexUtil.*;
import static chat.util.SocketCloseUtil.closeAll;
import static util.MyLogger.log;

public class ChatSession implements Runnable {
    private final Socket socket;
    private final DataInputStream dis;
    private final DataOutputStream dos;
    private final SessionManager sessionManager;

    private String name;

    private boolean closed = false;

    public ChatSession(Socket socket, SessionManager sessionManager) throws IOException {
        this.socket = socket;
        this.dis = new DataInputStream(socket.getInputStream());
        this.dos = new DataOutputStream(socket.getOutputStream());
        this.sessionManager = sessionManager;
        this.sessionManager.addSession(this);
    }

    @Override
    public void run() {
        try {
            while (true) {
                String received = dis.readUTF();
                log("사용자 메세지 수신: " + received + "\n" + " 발신지: " + socket);

                if (getCommand(received) == EXIT) {
                    dos.writeUTF(writeCommand(EXIT));
                    break;
                }

                switch (getCommand(received)) {
                    case JOIN -> {
                        name = getDetail(received);
                        dos.writeUTF(writeCommand(JOIN) + name + "님 환영합니다.");
                        sessionManager.sendNewJoin(this);
                        log("Join 메세지 전송");
                    }
                    case MESSAGE -> {
                        sessionManager.sendAll(this, getDetail(received));
                        log("메세지 전송");
                    }
                    case CHANGE -> {
                        String oldName = name;
                        name = getDetail(received);
                        dos.writeUTF(writeCommand(CHANGE) + "이름이 \"" + name + "\"으로 변경되었습니다.");
                        sessionManager.sendRename(this, oldName);
                        log("이름 변경 메세지 전송");
                    }
                    case USERS -> {
                        dos.writeUTF(writeCommand(USERS) + sessionManager.getUsers());
                        log("유저 목록 전송");
                    }
                    case null, default -> {
                        throw new IOException("잘못된 커맨드 수신");
                    }
                }
            }
        } catch (IOException e) {
            log(e);
        } finally {
            log("session 종료");
            sessionManager.removeSession(this);
            close();
        }
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized void sendMessage(String message) throws IOException {
        dos.writeUTF(writeCommand(MESSAGE) + message);
    }

    public synchronized void close() {
        if (closed) return;

        closeAll(socket, dis, dos);
        closed = true;
        log("연결 종료: " + socket);
    }
}

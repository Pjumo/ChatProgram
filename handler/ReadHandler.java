package chat.handler;

import chat.ChatClient;
import chat.util.Command;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

import static chat.util.Command.EXIT;
import static chat.util.RegexUtil.*;
import static util.MyLogger.log;

public class ReadHandler implements Runnable {

    private final DataInputStream dis;
    private final ChatClient client;
    private boolean isClosed = false;

    public ReadHandler(DataInputStream dis, ChatClient client) {
        this.dis = dis;
        this.client = client;
    }

    @Override
    public void run() {
        try {
//            log("readHandler 시작");
            while (true) {
                String received = dis.readUTF();
//                log("서버 메세지 수신" + received);
                Command command = getCommand(received);

                if (command == EXIT) {
                    break;
                }

                switch (command) {
                    case JOIN, CHANGE, MESSAGE -> System.out.println(getDetail(received));
                    case USERS -> System.out.println(Arrays.toString(getUsers(received)));
                    case null, default -> throw new IOException("잘못된 커맨드 수신");
                }
            }
        } catch (IOException e) {
            log(e);
        } finally {
            client.close();
        }
    }

    public synchronized void close() {
        if (isClosed) return;

        isClosed = true;
        try {
            System.in.close();
        } catch (IOException e) {
            log(e);
        }
//        log("readHandler 종료");
    }
}

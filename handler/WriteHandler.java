package chat.handler;

import chat.ChatClient;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

import static chat.util.Command.EXIT;
import static chat.util.Command.MESSAGE;
import static chat.util.RegexUtil.getCommand;
import static chat.util.RegexUtil.writeCommand;
import static util.MyLogger.log;

public class WriteHandler implements Runnable {

    private final Scanner scanner;
    private final DataOutputStream dos;
    private final ChatClient client;
    private boolean isClosed = false;

    public WriteHandler(Scanner scanner, DataOutputStream dos, ChatClient client) {
        this.scanner = scanner;
        this.dos = dos;
        this.client = client;
    }

    @Override
    public void run() {
        try {
//            log("writeHandler 시작");
            while (true) {
                String userCommand = scanner.nextLine();
//                log("사용자 커맨드 입력" + userCommand);

                if (getCommand(userCommand) == null) {
                    userCommand = writeCommand(MESSAGE) + userCommand;
                }

                dos.writeUTF(userCommand);
                if (getCommand(userCommand) == EXIT) {
                    break;
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
//        log("writeHandler 종료");
    }
}

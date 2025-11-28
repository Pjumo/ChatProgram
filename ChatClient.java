package chat;

import chat.handler.ReadHandler;
import chat.handler.WriteHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import static chat.util.Command.JOIN;
import static chat.util.RegexUtil.writeCommand;
import static chat.util.SocketCloseUtil.closeAll;

public class ChatClient {

    private final String host;
    private final int port;

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private WriteHandler writeHandler;
    private ReadHandler readHandler;

    private boolean closed = false;

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("이름을 입력하세요: ");
        String name = scanner.nextLine();

        socket = new Socket(host, port);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());

        dos.writeUTF(writeCommand(JOIN) + name);
//        log("접속 메세지 송신 | 접속자: " + name);

        readHandler = new ReadHandler(dis, this);
        Thread readThread = new Thread(readHandler, "readHandler");
        writeHandler = new WriteHandler(scanner, dos, this);
        Thread writeThread = new Thread(writeHandler, "writeHandler");

        readThread.start();
        writeThread.start();
    }

    public synchronized void close() {
        if (closed) return;

        closed = true;
        readHandler.close();
        writeHandler.close();
        closeAll(socket, dis, dos);
    }
}

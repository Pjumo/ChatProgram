package chat;

import java.io.IOException;

public class ChatClientMain {

    private static final int PORT = 12345;

    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost", PORT);
        client.start();
    }
}

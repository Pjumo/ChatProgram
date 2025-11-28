package chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SessionManager {
    private static List<ChatSession> sessions = new ArrayList<>();

    public synchronized void addSession(ChatSession session) {
        sessions.add(session);
    }

    public synchronized void removeSession(ChatSession session) {
        sessions.remove(session);
    }

    public synchronized void sendAll(ChatSession sender, String message) throws IOException {
        for (ChatSession session : sessions) {
            if (sender.equals(session)) continue;
            session.sendMessage('[' + sender.getName() + "] " + message);
        }
    }

    public synchronized void sendNewJoin(ChatSession chatSession) throws IOException {
        for (ChatSession session : sessions) {
            if (chatSession.equals(session)) continue;
            session.sendMessage(chatSession.getName() + "님이 입장하셨습니다.");
        }
    }

    public synchronized void sendRename(ChatSession chatSession, String oldName) throws IOException {
        for (ChatSession session : sessions) {
            if (chatSession.equals(session)) continue;
            session.sendMessage(oldName + "님의 이름이 " + chatSession.getName() + "으로 변경되었습니다.");
        }
    }

    public synchronized String getUsers() {
        StringBuilder users = new StringBuilder();
        for (ChatSession session : sessions) {
            users.append(session.getName());
            if (sessions.getLast().equals(session)) break;
            users.append('|');
        }
        return users.toString();
    }

    public synchronized void closeAll() {
        for (ChatSession session : sessions) {
            session.close();
        }
        sessions.clear();
    }
}
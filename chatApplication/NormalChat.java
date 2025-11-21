package chatApplication;

import java.util.ArrayList;

public class NormalChat {
    User user1;
    User user2;
    FileStorage storage;

    public NormalChat(User u1, User u2, FileStorage storage) {
        this.user1 = u1;
        this.user2 = u2;
        this.storage = storage;
    }

    public void displayMessagesFor(User viewer) {
        String viewerName = viewer.getUsername();
        String other = viewerName.equals(user1.getUsername()) ? user2.getUsername() : user1.getUsername();
        ArrayList<String> messages = storage.loadMessages(viewerName, other);
        if (messages == null || messages.isEmpty()) {
            System.out.println(Main.YELLOW + "Say 'HI 👋' to Start " + other + Main.RESET);
            return;
        }
        System.out.println(Main.CYAN + "------------ Conversation with " + other + " ------------" + Main.RESET);
        for (String m : messages)
            System.out.println(m);
    }

    public void sendMessage(User sender, String content) {
        String timestamp = storage.getTimestamp();
        String out = "[" + timestamp + "] " + sender.getUsername() + ": " + content;
        storage.saveMessage(sender.getUsername(), (sender == user1 ? user2.getUsername() : user1.getUsername()), out);
    }
}

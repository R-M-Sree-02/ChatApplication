package chatApplication;

public class Message {
    String timestamp, sender, content;

    public Message(String timestamp, String sender, String content) {
        this.timestamp = timestamp;
        this.sender = sender;
        this.content = content;
    }

    public String format() {
        return "[" + timestamp + "] " + sender + ": " + content;
    }
}

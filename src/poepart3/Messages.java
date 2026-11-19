
package poepart3;

/**
 *
 * @author Student
 */
public class Messages {

    private String messageID;
    private String sender;
    private String recipient;
    private String message;
    private String messageHash;
    private String flag;
    private int messageNumber;

    public Messages(String messageID, String sender, String recipient,
            String message, String messageHash, String flag,
            int messageNumber) {

        this.messageID = messageID;
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.messageHash = messageHash;
        this.flag = flag;
        this.messageNumber = messageNumber;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public String getFlag() {
        return flag;
    }

    public int getMessageNumber() {
        return messageNumber;
    }
}



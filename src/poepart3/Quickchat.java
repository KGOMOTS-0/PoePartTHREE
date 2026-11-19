
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poepart3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author Student
 */
public class Quickchat {

    private static final ArrayList<Messages> sentMessages = new ArrayList<>();
    private static final ArrayList<Messages> disregardedMessages = new ArrayList<>();
    private static final ArrayList<Messages> storedMessages = new ArrayList<>();
    private static final ArrayList<String> messageHashes = new ArrayList<>();
    private static final ArrayList<String> messageIDs = new ArrayList<>();

    private static final String FILE_NAME = "storedMessages.json";
    private static int messageNumber = 0;

    public static void main(String[] args) {

        loadStoredMessages();

        boolean running = true;

        while (running) {

            String choice = JOptionPane.showInputDialog(
                    "QUICKCHAT MENU\n\n"
                    + "1. Send a Message\n"
                    + "2. Display Sent Messages\n"
                    + "3. Quit\n"
                    + "4. Stored Messages\n\n"
                    + "Enter your choice:");

            if (choice == null) {
                break;
            }

            switch (choice) {
                case "1":
                    createNewMessage();
                    break;

                case "2":
                    displaySentMessages();
                    break;

                case "3":
                    running = false;
                    JOptionPane.showMessageDialog(null, "Thank you for using QuickChat.");
                    break;

                case "4":
                    storedMessagesMenu();
                    break;

                default:
                    JOptionPane.showMessageDialog(null, "Please enter a valid option.");
                    break;
            }
        }
    }

    private static void createNewMessage() {

        String sender = JOptionPane.showInputDialog("Enter sender name:");

        if (sender == null) {
            return;
        }

        String recipient = JOptionPane.showInputDialog("Enter recipient cellphone number:");

        if (recipient == null) {
            return;
        }

        String message = JOptionPane.showInputDialog("Enter your message:");

        if (message == null) {
            return;
        }

        String id = JOptionPane.showInputDialog(
                "Enter a message ID, or leave blank to generate one:");

        if (id == null) {
            return;
        }

        if (id.trim().isEmpty()) {
            id = generateMessageID();
        }

        String[] options = {"Sent", "Stored", "Disregard"};

        int selected = JOptionPane.showOptionDialog(
                null,
                "What would you like to do with this message?",
                "Message Options",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (selected == -1) {
            return;
        }

        messageNumber++;

        String hash = createMessageHash(id, messageNumber, message);

        Messages newMessage = new Messages(
                id, sender, recipient, message, hash,
                options[selected], messageNumber);

        addMessage(newMessage);

        if (options[selected].equals("Sent")) {
            JOptionPane.showMessageDialog(null, "Message successfully sent.");

        } else if (options[selected].equals("Stored")) {
            saveStoredMessages();
            JOptionPane.showMessageDialog(null, "Message successfully stored.");

        } else {
            JOptionPane.showMessageDialog(null, "Message disregarded.");
        }
    }

    public static void addMessage(Messages message) {

        if (message == null) {
            return;
        }

        String flag = message.getFlag();

        messageIDs.add(message.getMessageID());
        messageHashes.add(message.getMessageHash());

        if (flag.equalsIgnoreCase("Sent")) {
            sentMessages.add(message);

        } else if (flag.equalsIgnoreCase("Stored")) {
            storedMessages.add(message);

        } else if (flag.equalsIgnoreCase("Disregard")
                || flag.equalsIgnoreCase("Disregarded")) {
            disregardedMessages.add(message);
        }

        if (message.getMessageNumber() > messageNumber) {
            messageNumber = message.getMessageNumber();
        }
    }

    public static String generateMessageID() {

        Random random = new Random();
        StringBuilder id = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            id.append(random.nextInt(10));
        }

        return id.toString();
    }

    public static String createMessageHash(String id, int number, String message) {

        String[] words = message.trim().split("\\s+");

        String firstWord = words[0];
        String lastWord = words[words.length - 1];

        String firstTwoDigits = id.length() >= 2
                ? id.substring(0, 2) : id;

        return (firstTwoDigits + ":" + number + ":"
                + firstWord + lastWord).toUpperCase();
    }

    private static void displaySentMessages() {

        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "There are no sent messages.");
            return;
        }

        StringBuilder output = new StringBuilder("SENT MESSAGES\n\n");

        for (Messages message : sentMessages) {
            output.append("Message ID: ").append(message.getMessageID())
                    .append("\nRecipient: ").append(message.getRecipient())
                    .append("\nMessage: ").append(message.getMessage())
                    .append("\n\n");
        }

        JOptionPane.showMessageDialog(null, output.toString());
    }

    private static void storedMessagesMenu() {

        boolean back = false;

        while (!back) {

            String choice = JOptionPane.showInputDialog(
                    "STORED MESSAGES MENU\n\n"
                    + "1. Display stored message senders and recipients\n"
                    + "2. Display the longest stored message\n"
                    + "3. Search for a message using its ID\n"
                    + "4. Search messages by recipient\n"
                    + "5. Delete a message using its hash\n"
                    + "6. Display the full sent messages report\n"
                    + "7. Back to main menu\n\n"
                    + "Enter your choice:");

            if (choice == null || choice.equals("7")) {
                back = true;
                continue;
            }

            switch (choice) {

                case "1":
                    displayStoredSendersAndRecipients();
                    break;

                case "2":
                    displayLongestStoredMessage();
                    break;

                case "3":
                    searchMessageByID();
                    break;

                case "4":
                    searchMessagesByRecipient();
                    break;

                case "5":
                    deleteMessageByHash();
                    break;

                case "6":
                    displayFullReport();
                    break;

                default:
                    JOptionPane.showMessageDialog(null, "Please enter a valid option.");
                    break;
            }
        }
    }

    public static String displayStoredSendersAndRecipients() {

        StringBuilder output = new StringBuilder();

        if (storedMessages.isEmpty()) {
            output.append("There are no stored messages.");

        } else {
            for (Messages message : storedMessages) {
                output.append("Sender: ").append(message.getSender())
                        .append("\nRecipient: ").append(message.getRecipient())
                        .append("\n\n");
            }
        }

        JOptionPane.showMessageDialog(null, output.toString());
        return output.toString();
    }

    public static String getLongestStoredMessage() {

        if (storedMessages.isEmpty()) {
            return "";
        }

        Messages longest = storedMessages.get(0);

        for (Messages message : storedMessages) {
            if (message.getMessage().length() > longest.getMessage().length()) {
                longest = message;
            }
        }

        return longest.getMessage();
    }

    public static void displayLongestStoredMessage() {

        String longest = getLongestStoredMessage();

        if (longest.isEmpty()) {
            JOptionPane.showMessageDialog(null, "There are no stored messages.");
        } else {
            JOptionPane.showMessageDialog(null, "Longest stored message:\n\n" + longest);
        }
    }

    public static String searchMessageByID(String id) {

        for (Messages message : storedMessages) {
            if (message.getMessageID().equals(id)) {
                return "Recipient: " + message.getRecipient()
                        + "\nMessage: " + message.getMessage();
            }
        }

        for (Messages message : sentMessages) {
            if (message.getMessageID().equals(id)) {
                return "Recipient: " + message.getRecipient()
                        + "\nMessage: " + message.getMessage();
            }
        }

        return "Message ID not found.";
    }

    private static void searchMessageByID() {

        String id = JOptionPane.showInputDialog("Enter the message ID:");

        if (id != null) {
            JOptionPane.showMessageDialog(null, searchMessageByID(id));
        }
    }

    public static String searchMessagesByRecipient(String recipient) {

        StringBuilder output = new StringBuilder();
        boolean found = false;

        for (Messages message : sentMessages) {
            if (message.getRecipient().equals(recipient)) {
                output.append(message.getMessage()).append("\n\n");
                found = true;
            }
        }

        for (Messages message : storedMessages) {
            if (message.getRecipient().equals(recipient)) {
                output.append(message.getMessage()).append("\n\n");
                found = true;
            }
        }

        if (!found) {
            return "No messages found for this recipient.";
        }

        return output.toString();
    }

    private static void searchMessagesByRecipient() {

        String recipient = JOptionPane.showInputDialog("Enter the recipient number:");

        if (recipient != null) {
            JOptionPane.showMessageDialog(null, searchMessagesByRecipient(recipient));
        }
    }

    public static String deleteMessageByHash(String hash) {

        for (int i = 0; i < storedMessages.size(); i++) {

            Messages message = storedMessages.get(i);

            if (message.getMessageHash().equals(hash)) {

                String deletedMessage = message.getMessage();

                storedMessages.remove(i);
                messageHashes.remove(hash);
                messageIDs.remove(message.getMessageID());

                saveStoredMessages();

                return "Message: \"" + deletedMessage + "\" successfully deleted.";
            }
        }

        return "Message hash not found.";
    }

    private static void deleteMessageByHash() {

        String hash = JOptionPane.showInputDialog("Enter the message hash:");

        if (hash != null) {
            JOptionPane.showMessageDialog(null, deleteMessageByHash(hash));
        }
    }

    public static String displayFullReport() {

        StringBuilder report = new StringBuilder("SENT MESSAGES REPORT\n\n");

        if (sentMessages.isEmpty()) {
            report.append("There are no sent messages.");

        } else {
            for (Messages message : sentMessages) {

                report.append("Message Hash: ")
                        .append(message.getMessageHash())
                        .append("\nRecipient: ")
                        .append(message.getRecipient())
                        .append("\nMessage: ")
                        .append(message.getMessage())
                        .append("\n\n");
            }
        }

        JOptionPane.showMessageDialog(null, report.toString());
        return report.toString();
    }

    private static void saveStoredMessages() {

        StringBuilder json = new StringBuilder("[\n");

        for (int i = 0; i < storedMessages.size(); i++) {

            Messages message = storedMessages.get(i);

            json.append("  {\n")
                    .append("    \"messageID\": \"").append(escapeJSON(message.getMessageID())).append("\",\n")
                    .append("    \"sender\": \"").append(escapeJSON(message.getSender())).append("\",\n")
                    .append("    \"recipient\": \"").append(escapeJSON(message.getRecipient())).append("\",\n")
                    .append("    \"message\": \"").append(escapeJSON(message.getMessage())).append("\",\n")
                    .append("    \"messageHash\": \"").append(escapeJSON(message.getMessageHash())).append("\",\n")
                    .append("    \"flag\": \"Stored\",\n")
                    .append("    \"messageNumber\": \"").append(message.getMessageNumber()).append("\"\n")
                    .append("  }");

            if (i < storedMessages.size() - 1) {
                json.append(",");
            }

            json.append("\n");
        }

        json.append("]");

        try {
            Files.write(Paths.get(FILE_NAME),
                    json.toString().getBytes(StandardCharsets.UTF_8));

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not save stored messages.");
        }
    }

    private static void loadStoredMessages() {

        storedMessages.clear();

        try {
            if (!Files.exists(Paths.get(FILE_NAME))) {
                return;
            }

            String json = new String(
                    Files.readAllBytes(Paths.get(FILE_NAME)),
                    StandardCharsets.UTF_8);

            Pattern objectPattern = Pattern.compile("\\{([^}]*)\\}");
            Matcher objectMatcher = objectPattern.matcher(json);

            while (objectMatcher.find()) {

                String object = objectMatcher.group(1);

                String id = readJSONValue(object, "messageID");
                String sender = readJSONValue(object, "sender");
                String recipient = readJSONValue(object, "recipient");
                String message = readJSONValue(object, "message");
                String hash = readJSONValue(object, "messageHash");
                String numberText = readJSONValue(object, "messageNumber");

                int number = Integer.parseInt(numberText);

                Messages storedMessage = new Messages(
                        id, sender, recipient, message, hash, "Stored", number);

                storedMessages.add(storedMessage);
                messageIDs.add(id);
                messageHashes.add(hash);

                if (number > messageNumber) {
                    messageNumber = number;
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Could not read the stored messages file.");
        }
    }

    private static String readJSONValue(String object, String key) {

        Pattern pattern = Pattern.compile(
                "\"" + key + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"");

        Matcher matcher = pattern.matcher(object);

        if (matcher.find()) {
            return unescapeJSON(matcher.group(1));
        }

        return "";
    }

    private static String escapeJSON(String value) {

        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private static String unescapeJSON(String value) {

        return value.replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    public static ArrayList<Messages> getSentMessages() {
        return sentMessages;
    }

    public static ArrayList<Messages> getDisregardedMessages() {
        return disregardedMessages;
    }

    public static ArrayList<Messages> getStoredMessages() {
        return storedMessages;
    }

    public static ArrayList<String> getMessageHashes() {
        return messageHashes;
    }

    public static ArrayList<String> getMessageIDs() {
        return messageIDs;
    }
}

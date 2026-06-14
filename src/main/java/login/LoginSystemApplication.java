package login;

import com.google.gson.Gson;
import java.io.FileWriter;

public class LoginSystemApplication {

    //USER DETAILS
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String cellNumber;

    //ARRAYS
    private String[] sentMessages = new String[100];
    private String[] storedMessages = new String[100];
    private String[] disregardedMessages = new String[100];

    private String[] messageIds = new String[100];
    private String[] messageHashes = new String[100];

    private int sentCount = 0;
    private int storedCount = 0;
    private int disregardedCount = 0;
    private int idCount = 0;
    private int hashCount = 0;

    

    public boolean checkUserName(String userName) {
        return userName.contains("_") && userName.length() <= 5;
    }

    public boolean checkPasswordComplexity(String password) {

        if (password.length() < 8) return false;

        boolean upper = false;
        boolean number = false;
        boolean special = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) upper = true;
            if (Character.isDigit(c)) number = true;
            if (!Character.isLetterOrDigit(c)) special = true;
        }

        return upper && number && special;
    }

    public boolean checkCellphoneNumber(String cell) {
        return cell.startsWith("+27") && cell.length() == 12;
    }

    //REGISTER

    public String registerUser(String u, String p, String f, String l, String c) {

        if (!checkUserName(u)) {
            return "Username incorrectly formatted.";
        }

        if (!checkPasswordComplexity(p)) {
            return "Password incorrectly formatted.";
        }

        if (!checkCellphoneNumber(c)) {
            return "Cellphone incorrectly formatted.";
        }

        userName = u;
        password = p;
        firstName = f;
        lastName = l;
        cellNumber = c;

        return "User successfully registered.";
    }

    //LOGIN

    public boolean loginUser(String u, String p) {
        return u.equals(userName) && p.equals(password);
    }

    public String returnLoginStatus(boolean status) {
        return status
                ? "Welcome " + firstName + ", " + lastName + " it is great to see you."
                : "Login failed.";
    }

    //MESSAGE ID + HASH

    public String createMessageId(String message, int count) {
        return message.substring(0, 2).toUpperCase() + count;
    }

    public String createMessageHash(String id, String message) {
        String[] words = message.split(" ");
        return id + ":" + words[0].toUpperCase() + words[words.length - 1].toUpperCase();
    }

    public String validateMessage(String message) {

        if (message.length() <= 250) {
            return "Message ready to send.";
        }

        return "Message exceeds 250 characters by " + (message.length() - 250);
    }

    //ADD MESSAGE

    public void addMessage(String recipient, String message, String status) {

        String id = createMessageId(message, idCount);
        String hash = createMessageHash(id, message);

        messageIds[idCount++] = id;
        messageHashes[hashCount++] = hash;

        if (status.equalsIgnoreCase("Sent")) {
            sentMessages[sentCount++] = message;

        } else if (status.equalsIgnoreCase("Stored")) {
            storedMessages[storedCount++] = message;
            saveToJson(id, recipient, message, status);

        } else {
            disregardedMessages[disregardedCount++] = message;
        }

        System.out.println(validateMessage(message));
        System.out.println("Message ID: " + id);
        System.out.println("Message Hash: " + hash);
    }

    //JSON STORAGE

    public void saveToJson(String id, String recipient, String message, String status) {

        try (FileWriter writer = new FileWriter("messages.json", true)) {

            Gson gson = new Gson();

            StoredMessage obj = new StoredMessage(id, recipient, message, status);

            writer.write(gson.toJson(obj));
            writer.write("\n");

        } catch (Exception e) {
            System.out.println("File error");
        }
    }

  

    public String longestMessage() {

        String longest = "";

        for (int i = 0; i < storedCount; i++) {
            if (storedMessages[i] != null &&
                storedMessages[i].length() > longest.length()) {
                longest = storedMessages[i];
            }
        }

        return longest;
    }

    public boolean deleteByHash(String hash) {

        for (int i = 0; i < hashCount; i++) {
            if (messageHashes[i] != null && messageHashes[i].equals(hash)) {
                messageHashes[i] = null;
                return true;
            }
        }
        return false;
    }

    public String displayAllMessages() {

        String output = "";

        for (int i = 0; i < sentCount; i++) {
            output += "SENT: " + sentMessages[i] + "\n";
        }

        for (int i = 0; i < storedCount; i++) {
            output += "STORED: " + storedMessages[i] + "\n";
        }

        for (int i = 0; i < disregardedCount; i++) {
            output += "DISREGARDED: " + disregardedMessages[i] + "\n";
        }

        return output;
    }

//MAIN METHOD

    public static void main(String[] args) {
        java.util.Scanner scanner = new
        java.util.Scanner(System.in);

        LoginSystemApplication app = new LoginSystemApplication();

        System.out.println("=================================");
        System.out.println("       WELCOME TO QUICKCHAT");
        System.out.println("=================================");

         //REGISTER
    System.out.print("Enter username: ");
    String username = scanner.nextLine();

    System.out.print("Enter password: ");
    String password = scanner.nextLine();

    System.out.print("Enter first name: ");
    String firstName = scanner.nextLine();

    System.out.print("Enter last name: ");
    String lastName = scanner.nextLine();

    System.out.print("Enter cellphone number: ");
    String cell = scanner.nextLine();

    System.out.println(app.registerUser(username, password, firstName, lastName, cell));

    //LOGIN
    System.out.println("\nLOGIN");

    System.out.print("Enter username: ");
    String loginUser = scanner.nextLine();

    System.out.print("Enter password: ");
    String loginPass = scanner.nextLine();

    boolean login = app.loginUser(loginUser, loginPass);
    System.out.println(app.returnLoginStatus(login));

    if (!login) {
        System.out.println("Exiting system...");
        return;
    }

    //MENU
    int choice;

    do {
        System.out.println("\n===== MENU =====");
        System.out.println("1. Add Message");
        System.out.println("2. View All Messages");
        System.out.println("3. Exit");

        System.out.print("Choose option: ");
        choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {

            System.out.print("Enter recipient: ");
            String recipient = scanner.nextLine();

            System.out.print("Enter message: ");
            String message = scanner.nextLine();

            System.out.print("Enter status (Sent/Stored/Disregard): ");
            String status = scanner.nextLine();

            app.addMessage(recipient, message, status);
        }

        if (choice == 2) {
            System.out.println(app.displayAllMessages());
        }

    } while (choice != 3);

    System.out.println("Goodbye!");
}
}





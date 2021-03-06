package javasocketserver;

/**
 *
 * @author 30039802 Caspian Maclean
 *
 * Question 4 – JMC wishes to have a standard login functionality for all their
 * terminals around the ship, this should be accomplished via logging into a
 * central server to test user and password combinations (you must have at least
 * one administrator password setup) You must create a two Server Client
 * program; each must use two different IPC mechanisms to communicate. Your
 * program must have a login that uses standard hashing techniques
 *
 * This is the server program for the system using sockets.
 *
 * Socket connection code based on provided example "ClientServerDemo"
 *
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.HashMap;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class JavaSocketServer {

    // client state - status string and others.
    static String currentUser = "";
    static String clientStatusString = "offline";
    static boolean loggedIn = false;
    static boolean administrator = false;

    static String ready = "ready";
    static String message = "message";
    static String query = "query";

    static String availableStatus = "available";
    static String busyStatus = "busy";

    static String adminUser = "admin";
    static String user2 = "user123432";
    static String guestUser = "guest";

    static String storedUserName = "friend";
    static String storedPassword="1234";
    
    static User storedUser;
    static HashMap<String, User> usersByName = new HashMap<>();
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
        addStoredUsers();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            System.out.println("Unexpected exception - exiting program");
            return;
        }
        System.out.println("Starting");
        int requestPortNumber = 1234;
        try ( ServerSocket serverSocket = new ServerSocket(requestPortNumber)) {
            System.out.println("The server is listening on port " + serverSocket.getLocalPort());
            try ( Socket clientSocket = serverSocket.accept()) {

                // client state - status string and others.
                clientStatusString = "offline";
                loggedIn = false;
                currentUser = "";
                administrator = false;

                System.out.println("The server is connected, connection from " + clientSocket.getInetAddress().getHostName());
                DataInputStream inStream = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream outStream = new DataOutputStream(clientSocket.getOutputStream());

                // protocol format: 
                // client -> server - send lines from user input.
                // server -> client: message will have a number, then that number of strings.
                // command strings in protocol:
                String helpCommand = "help";
                String loginCommand = "login";
                String statusCommand = "status";
                String addUserCommand = "adduser";

                write2(outStream, message, "Enter 'help' for server commands");

                while (true) {
                    write1(outStream, ready);

                    String inLine = inStream.readUTF();
                    String clientResponse = "";
                    if (inLine.equalsIgnoreCase(helpCommand)) {
                        if (!loggedIn) {
                            write2(outStream, message, "Enter 'login' to start login.");
                            write2(outStream, message, "You will be prompted for username and password.");
                        } else {
                            write2(outStream, message, "You are logged in.");
                            write2(outStream, message, "Enter 'status' to show or change your status.");
                            if (administrator) {
                                write2(outStream, message, "Enter 'adduser' to start adding a new user.");
                            }
                        }
                    } else if (inLine.equalsIgnoreCase(loginCommand)) {
                        loginResponse(inStream, outStream);
                    } else if (inLine.equalsIgnoreCase(statusCommand)) {
                        if (loggedIn) {
                            write2(outStream, message, "Status: " + clientStatusString);
                            write2(outStream, message, "Enter 'available' or 'busy' to update status,");
                            write2(outStream, message, "or use the shortcuts 'a' or 'b'");
                            write2(outStream, message, "or enter a blank line to leave it unchanged");
                            write2(outStream, query, "Status update");
                            clientResponse = inStream.readUTF().toLowerCase();
                            if (clientResponse.startsWith("a")) {
                                clientStatusString = availableStatus;
                                write2(outStream, message, "New status: " + clientStatusString);
                            } else if (clientResponse.startsWith("b")) {
                                clientStatusString = busyStatus;
                                write2(outStream, message, "New status: " + clientStatusString);
                            } else {
                                write2(outStream, message, "Status unchanged: " + clientStatusString);
                            }
                        } else {
                            write2(outStream, message, "You need to log in to use status command.");
                        }
                    } else if (inLine.equalsIgnoreCase(addUserCommand)) {
                        if (administrator) {
                            authorisedAddUserResponse(inStream, outStream);
                            //write2(outStream, query, "username to add");
//                            write2(outStream, message, "Adding user guest, password guest.");
//                            user2 = guestUser;
//                            addUserToTable(guestUser, guestUser);
                        } else {
                            write2(outStream, message, "Cannot add user without logging in as administrator");
                        }
                    } else {
                        System.err.println("Unknown from client: " + inLine);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("IO Exception: " + e);
        }
        System.out.println("Finished");

    }

    private static void write1(DataOutputStream outStream, String command) throws IOException {
        outStream.writeInt(1);
        outStream.writeUTF(command);
    }

    private static void write2(DataOutputStream outStream, String command, String parameter) throws IOException {
        outStream.writeInt(2);
        outStream.writeUTF(command);
        outStream.writeUTF(parameter);
    }

    private static void loginResponse(DataInputStream inStream, DataOutputStream outStream) throws IOException {

        write2(outStream, query, "username");
        String clientResponse = inStream.readUTF();
        currentUser = clientResponse;
        if (clientResponse.equals(adminUser)) {
            administrator = true;
        } else if (usersByName.containsKey(clientResponse)) {
            // Put code that will change to use a list of users here
            administrator = false;
            currentUser = clientResponse;
        } else {
            loggedIn = false;
            administrator = false;
            currentUser = "";
            write2(outStream, message, "No such user.");
            return;
        }
        // If here, then it's a valid username

        write2(outStream, query, "password");
        String passwordIn = inStream.readUTF();

        if (passwordCorrect(currentUser, passwordIn)) {
            loggedIn = true;
            clientStatusString = availableStatus;
            write2(outStream, message, "Logged in.");
        } else {
            loggedIn = false;
            administrator = false;
            currentUser = "";
            write2(outStream, message, "Wrong password.");
        }
    }

                                
    private static void authorisedAddUserResponse(DataInputStream inStream, DataOutputStream outStream)  throws IOException {
        write2(outStream, query, "username to add");
        String userNameResponse = inStream.readUTF();
        String passwordToSet = userNameResponse.concat("ABC");
        write2(outStream, message, "Adding user " + userNameResponse + " with password " + passwordToSet);
        addUserToTable(userNameResponse, passwordToSet);
    }
    
    
    private static boolean passwordCorrect(String user, String passwordIn) {
        User foundUser = usersByName.get(user);
        // null if not found, which needs to be checked for
        if (foundUser != null) {
            //return passwordIn.equals(storedPassword);
            try {
                return PasswordUtilities.isCorrectPassword(foundUser, passwordIn);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
                // in case of failure, don't allow the login
                return false;
            }
        }

        return false;
    }

    private static void addUserToTable(String userName, String password) {
        try {
            User user = PasswordUtilities.prepareUser(userName, password);
            usersByName.put(userName, user);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            // Shouldn't fail, but if it does, don't add the user
            System.out.println("Unexpected exception - cannot add user");
            return;
        }
    }
    private static void addStoredUsers() throws NoSuchAlgorithmException, InvalidKeySpecException {
        
        PasswordUtilities.initPasswordUtilities();
         
        //storedUser = PasswordUtilities.prepareUser(storedUserName, storedPassword);
        //usersByName.put(storedUserName, storedUser);
        addUserToTable(storedUserName, storedPassword);
        addUserToTable("admin", "admin");
        
    }
}

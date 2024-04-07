import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TCPClient {
    private static final String SERVER_HOSTNAME = "0.tcp.in.ngrok.io";
    private static final int SERVER_PORT = 16271;
    
    TCPClient() {
        
    }

    // Method to hash the password using SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int login(String username, String password) {
        Socket socket = null;
        BufferedReader reader = null;
        PrintWriter out = null;

        try {
            // Connect to server
            socket = new Socket(SERVER_HOSTNAME, SERVER_PORT);
            System.out.println("Connected to server.");

            // Setup streams
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Send username and hashed password to the server
            String hashedPassword = hashPassword(password);
            out.println(username);
            out.println(hashedPassword);

            // Read response from the server
            String response = reader.readLine();
            System.out.println("Server response: " + response);

            // Parse the response as an integer
            if (response != null && !response.isEmpty()) {
                try {
                    return Integer.parseInt(response);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing server response as integer: " + response);
                    return -1;
                }
            } else {
                System.err.println("Empty response received from the server.");
                return -1;
            }

        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + SERVER_HOSTNAME);
            e.printStackTrace();
            return -1;
        } catch (IOException e) {
            System.err.println("I/O error occurred while connecting to the server: " + e.getMessage());
            e.printStackTrace();
            return -1;
        } finally {
            // Close resources
            try {
                if (socket != null) socket.close();
                if (reader != null) reader.close();
                if (out != null) out.close();
            } catch (IOException e) {
                System.err.println("Error while closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}

import java.io.*;
import java.net.*;

public class Servidor {

    public static void main(String[] args) {
        if (args.length < 1) return;

        int port = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();

                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                String username = reader.readLine();
                System.out.println("Username: " + username);
                String password = reader.readLine();
                System.out.println("Password: " + password);

                boolean authenticated = authenticate(username, password);

                while (!authenticated) {
                    System.out.println("Authentication failed");
                    writer.println("Authentication failed. Please try again.");
                    username = reader.readLine();
                    password = reader.readLine();
                    authenticated = authenticate(username, password);
                }

                writer.println("Authentication succeeded.");
                System.out.println("Client authenticated: " + username);
            }

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static boolean authenticate(String username, String password) {
        return Login.verify(username,password);
    }
}
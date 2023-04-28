import java.net.*;
import java.io.*;
import java.util.concurrent.*;
public class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String line;
            while ((line = in.readLine()) != null) {
                String username = line;
                String password = in.readLine();
                System.out.println("Username: " + username);
                System.out.println("Password: " + password);
                System.out.println("Teste5");
                boolean authenticated = authenticate(username, password);

                if(authenticated) {
                    out.println("Authentication succeeded.");
                    System.out.println("Client authenticated: " + username);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean authenticate(String username, String password) {
        return Login.verify(username,password);
    }
}

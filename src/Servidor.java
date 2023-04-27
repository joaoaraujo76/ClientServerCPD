import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;

public class Servidor {

    public static void main(String[] args) {
        if (args.length < 1) return;

        int port = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server is listening on port " + port +"\n") ;

            while (true) {
                Socket socket = serverSocket.accept();

                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                String option = reader.readLine();

                String username = null;
                String password = null;
                boolean authenticated = false;
                boolean tryLogin = false;

                if (option.equals("login")) {
                    System.out.println("Login -----------");
                    username = reader.readLine();
                    System.out.println("Username: " + username);
                    password = reader.readLine();
                    System.out.println("Password: " + Hashing.hashPassword(password));
                    authenticated = authenticate(username, password);
                    tryLogin = true;
                }

                // Se o cliente deseja registrar uma nova conta, solicita o nome de usuário e a senha
                else {
                    System.out.println("Register -----------");
                    username = reader.readLine();
                    System.out.println("Username: " + username);
                    password = reader.readLine();
                    System.out.println("Password: " + Hashing.hashPassword(password));
                    if (register(username, password)) {
                        System.out.println("Register succeeded");
                        writer.println("Registration succeeded. Please login to your account.");
                    } else {
                        System.out.println("Registration failed");
                        writer.println("Registration failed. Username already exists.");
                    }
                }

                while (!authenticated) {
                    System.out.println("\n");
                    if(tryLogin){
                        writer.println("Authentication failed. Please try again or register a new account (r).");
                    }
                    option = reader.readLine();

                    if (option.equals("register")) {
                        tryLogin = false;
                        System.out.println("Register -----------");
                        username = reader.readLine();
                        System.out.println("Username: " + username);
                        password = reader.readLine();
                        System.out.println("Password: " + Hashing.hashPassword(password));
                        if (register(username, password)) {
                            System.out.println("Registration succeeded");
                            writer.println("Registration succeeded. Please login to your account.");
                        }
                        else {
                            System.out.println("Registration Failed");
                            writer.println("Registration failed. Username already exists.");
                        }
                    }
                    else {
                        tryLogin = true;
                        System.out.println("Login -----------");
                        username = reader.readLine();
                        System.out.println("Username: " + username);
                        password = reader.readLine();
                        System.out.println("Password: " + Hashing.hashPassword(password));
                        authenticated = authenticate(username, password);
                        if (!authenticated) {
                            System.out.println("Authenticated failed");
                        }
                    }
                }

                System.out.println("Authenticated succeded");
                writer.println("Authentication succeeded.");
                System.out.println("\nClient authenticated: " + username);
            }

        } catch (IOException | NoSuchAlgorithmException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static boolean authenticate(String username, String password) {
        return Login.verify(username, password);
    }

    private static boolean register(String username, String password) {
        return Register.newUser(username, password);
    }
}

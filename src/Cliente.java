import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {

    public static void main(String[] args) {
        if (args.length < 2) return;

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(hostname, port)) {

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            Scanner scanner = new Scanner(System.in);

            boolean authenticated = false;

            do {
                System.out.print("\nDo you want to register or login? (r/l) ");
                String option = scanner.nextLine();
                if (option.equals("r")) {
                    writer.println("register");

                    System.out.println("Register -----------");
                    System.out.print("Username: ");
                    String username = scanner.nextLine();
                    writer.println(username);

                    System.out.print("Password: ");
                    String password = scanner.nextLine();
                    writer.println(password);

                    String authResult = reader.readLine();
                    System.out.println(authResult);
                }
                else if (option.equals("l")) {
                    writer.println("login");

                    System.out.println("Login -----------");
                    System.out.print("Username: ");
                    String username = scanner.nextLine();
                    writer.println(username);

                    System.out.print("Password: ");
                    String password = scanner.nextLine();
                    writer.println(password);

                    String authResult = reader.readLine();
                    System.out.println(authResult);
                    authenticated = authResult.equals("Authentication succeeded.");
                }
                else {
                    System.out.println("Invalid option. Please try again.");
                }

            } while (!authenticated);

        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}
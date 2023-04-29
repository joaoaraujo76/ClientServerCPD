import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;
import java.security.NoSuchAlgorithmException;

public class Servidor {

    private static int port;

    public static void main(String[] args) {
        if (args.length < 1) return;

        port = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server is listening on port " + port +"\n") ;

            while (true) {

                Socket socket = serverSocket.accept();

                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                String tokenClient = reader.readLine();
                System.out.println("Token received: " + tokenClient);

                boolean validToken = false;
                String username = null;

                if (!tokenClient.equals("null")){
                    username = findUserByToken(tokenClient);
                    if (!(username == null)) {
                        validToken = true;
                    }
                    else {
                        username = null;
                    }
                }

                if(username == null){
                    writer.println("Token unauthorized");
                }

                else{
                    writer.println("Token authorized");
                }

                if(!validToken) {

                    String password = null;
                    boolean authenticated = false;
                    boolean tryLogin = false;
                    String option;

                    do {
                        System.out.println("\n");
                        if (tryLogin) {
                            writer.println("Authentication failed. Please try again or register a new account (r).");
                        }

                        option = reader.readLine();

                        if (option.equals("login")) {
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
                            else {
                                System.out.println("Authenticated succeded\n");
                                writer.println("Authentication succeeded.");
                            }
                        }
                        else if (option.equals("register")) {
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
                                System.out.println("Registration failed");
                                writer.println("Registration failed. Username already exists.");
                            }
                        }
                        else {
                            System.out.println("Invalid option. Please try again.");
                        }

                    } while (!authenticated);

                    String newToken = generateRandomToken();
                    updateTokenByUsername(username,newToken);
                    writer.println(newToken);
                }

                System.out.println("Client authenticated: " + username + "\n");
            }

        } catch (SocketException ex) {
            System.out.println("A conexão com o cliente foi interrompida.");
        } catch (IOException ex) {
            System.out.println("Ocorreu uma exceção de IO: " + ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Ocorreu uma exceção de algoritmo de hashing: " + ex.getMessage());
        }
    }

    private static boolean authenticate(String username, String password) {
        return Login.verify(username, password);
    }

    private static boolean register(String username, String password) {
        return Register.newUser(username, password);
    }

    public static String findUserByToken(String token) {
        String result = null;
        try {
            Scanner scanner = new Scanner(new File("users.txt"));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] fields = line.split(",");
                if (fields.length == 4 && fields[2].equals(token)) {
                    result = fields[0];
                    break;
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String generateRandomToken() {
        Random random = new Random();
        int randomInt = random.nextInt();
        String hexString = Integer.toHexString(randomInt);
        return hexString;
    }


    public static void updateTokenByUsername(String username, String newToken) {
        try {
            File file = new File("users.txt");
            Scanner scanner = new Scanner(file);

            List<String> updatedUsers = new ArrayList<>();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] fields = line.split(",");

                if (fields[0].equals(username)) {
                    // Atualizar o token do usuário com o novo valor
                    fields[2] = newToken;
                    line = String.join(",", fields);
                }

                updatedUsers.add(line);
            }

            scanner.close();

            FileWriter writer = new FileWriter(file);

            for (String line : updatedUsers) {
                writer.write(line + "\n");
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package server;

import server.models.User;
import server.parser.UsersParser;
import server.repository.UsersRepository;
import server.services.UserAuthenticator;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.ParseException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Server {

    private static int port;

    public static void main(String[] args) {
        if (args.length < 1) return;

        port = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server is listening on port " + port +"\n") ;

            while (true) {
                UsersParser.parse();

                Socket socket = serverSocket.accept();

                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                String tokenClient = reader.readLine();
                System.out.println("Token received: " + tokenClient);

                boolean validToken = false;

                Optional<User> userOptional = UsersRepository.getUserByToken(tokenClient);
                String username = null;

                if (!tokenClient.equals("null")){
                    if (userOptional.isPresent()) {
                        User user = userOptional.get();
                        if (!(user.getToken() == null)) {
                            validToken = true;
                        }
                    }
                }

                if(!userOptional.isPresent()){
                    writer.println("Token unauthorized");
                }

                else{
                    writer.println("Token authorized");
                }

                if(!validToken) {

                    String password;
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
                            System.out.println("server.Login -----------");
                            username = reader.readLine();
                            System.out.println("Username: " + username);
                            password = reader.readLine();
                            authenticated = UserAuthenticator.login(username, password);
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
                            if (UserAuthenticator.register(username, password)) {
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
                    updateTokenByUsername(username, newToken);
                    UsersRepository.getUserByName(username).ifPresent(u ->
                            u.setToken(newToken));
                    writer.println(newToken);
                }
                userOptional.ifPresent(u ->
                        System.out.println("Client authenticated: " + u.getUsername() + "\n"));
            }

        } catch (SocketException ex) {
            System.out.println("A conexão com o cliente foi interrompida.");
        } catch (IOException ex) {
            System.out.println("Ocorreu uma exceção de IO: " + ex.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static String generateRandomToken() {
        Random random = new Random();
        int randomInt = random.nextInt();
        String hexString = Integer.toHexString(randomInt);
        return hexString;
    }

    private static void updateTokenByUsername(String username, String newToken) throws IOException, ParseException {
        try {
            File file = new File("data/users.txt");
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

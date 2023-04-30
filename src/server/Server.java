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
import java.util.Optional;
import java.util.Random;

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
                Optional<User> user = null;

                if (!tokenClient.equals("null")){
                    user = UsersRepository.getUserByToken(tokenClient);
                    if (!(user.get().getToken() == null)) {
                        validToken = true;
                    }
                    else {
                        user = null;
                    }
                }

                if(user.get().getToken() == null){
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
                            System.out.println("server.Login -----------");
                            String username = reader.readLine();
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
                            String username = reader.readLine();
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
                    user.ifPresent(u ->
                            u.setToken(newToken));
                    writer.println(newToken);
                }

                System.out.println("Client authenticated: " + user.get().getUsername() + "\n");
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
}

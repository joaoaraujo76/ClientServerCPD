package client;

import protocol.Message;
import protocol.MessageType;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        if (args.length < 2) return;

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(hostname, port)) {
            Scanner scanner = new Scanner(System.in);
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            output.flush();

            System.out.println("WELCOME!");

            String token = getClientToken();

            output.writeObject(new Message(MessageType.LOGIN_TOKEN, token));
            output.flush();

            Message message = (Message) input.readObject();
            if (message.getType() == MessageType.AUTHENTICATED_TOKEN) {
                System.out.println("Authentication succeeded.");
            } else {
                System.out.println(message.getMessage());

                boolean authenticated = false;
                while (!authenticated) {

                    System.out.print("\nDo you want to register or login? (r/l) ");
                    String option = scanner.nextLine();

                    switch (option) {
                        case "r" -> {
                            System.out.println("Register -----------");
                            System.out.print("Username: ");
                            String username = scanner.nextLine();

                            System.out.print("Password: ");
                            String password = scanner.nextLine();

                            output.writeObject(new Message(MessageType.REGISTER, token, username, password));
                            output.flush();

                            System.out.println(((Message) input.readObject()).getMessage());
                        }

                        case "l" -> {
                            System.out.println("Login -----------");
                            System.out.print("Username: ");
                            String username = scanner.nextLine();

                            System.out.print("Password: ");
                            String password = scanner.nextLine();

                            output.writeObject(new Message(MessageType.LOGIN, token, username, password));
                            output.flush();

                            Message authResult = (Message) input.readObject();
                            authenticated = authResult.getType() == (MessageType.AUTHENTICATED);
                            System.out.println(authResult.getMessage());
                            if (authenticated) {
                                String newToken = authResult.getToken();
                                updateToken(newToken);
                            }
                        }
                        default -> System.out.println("Invalid option. Please try again.");
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            //TODO: handle exceptions
        }
    }

    private static String getClientToken() {
        String token = null;
        File file = new File("src/client/token.txt");
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line = br.readLine();
                if (line != null) {
                    token = line;
                } else {
                    System.out.println("O arquivo está vazio.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("O arquivo de token não existe na pasta atual.");
        }
        return token;
    }

    private static void updateToken(String newToken) {
        try {
            File file = new File("src/client/token.txt");
            if (file.exists()) {
                FileWriter writer = new FileWriter(file);
                writer.write(newToken);
                writer.close();
            } else {
                file.createNewFile();
                FileWriter writer = new FileWriter(file);
                writer.write(newToken);
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
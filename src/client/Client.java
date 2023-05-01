package client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        if (args.length < 2) return;

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(hostname, port)) {

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            System.out.println("Bem Vindo!");

            Scanner scanner = new Scanner(System.in);

            String token = getClientToken(); //Token do cliente
            writer.println(token); //Envia token para o servidor
            String validToken = reader.readLine(); //Resposta do servidor pra ver se o token é valido

            if (validToken.equals("Token authorized")){
                System.out.println("Authentication succeeded.");
            }

            else{
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
                    } else if (option.equals("l")) {
                        writer.println("login");

                        System.out.println("server.Login -----------");
                        System.out.print("Username: ");
                        String username = scanner.nextLine();
                        writer.println(username);

                        System.out.print("Password: ");
                        String password = scanner.nextLine();
                        writer.println(password);

                        String authResult = reader.readLine();
                        System.out.println(authResult);
                        authenticated = authResult.equals("Authentication succeeded.");
                    } else {
                        System.out.println("Invalid option. Please try again.");
                    }

                } while (!authenticated);

                String newToken = reader.readLine();
                updateToken(newToken);
            }
        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    public static String getClientToken() {
        String Token = null;
        File file = new File("src/client/token.txt");
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line = br.readLine();
                if (line != null) {
                    Token = line;
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
        return Token;
    }

    public static void updateToken(String newToken) {
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
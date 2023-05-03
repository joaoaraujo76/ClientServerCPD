package server;

import server.services.UserAuthenticator;
import server.repository.UsersRepository;
import server.models.User;

import java.io.*;
import java.net.Socket;
import java.text.ParseException;
import java.util.*;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Optional;

public class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        InputStream input = null;
        try {
            input = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = clientSocket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            String tokenClient = reader.readLine();
            System.out.println("Token received: " + tokenClient);

            boolean validToken = false;

            Optional<User> userOptional = UsersRepository.getUserByToken(tokenClient);
            String username = null;

            if (!tokenClient.equals("null")){
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    if (UserAuthenticator.validToken(user, tokenClient)) {
                        validToken = true;
                    }
                }
            }

            if(userOptional.isEmpty() || !validToken){
                writer.println("Token unauthorized");
            }

            if (validToken){
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
                writer.println(newToken);
            }
            userOptional.ifPresent(u ->
                    System.out.println("Client authenticated: " + u.getUsername() + "\n"));
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }


    }

    private static boolean authenticate(String username, String password) {
        return UserAuthenticator.login(username,password);
    }

    private static String generateRandomToken() {
        Random random = new Random();
        int randomInt = random.nextInt();
        return Integer.toHexString(randomInt);
    }

    private static void updateTokenByUsername(String username, String newToken) throws IOException {
        try {
            long nowInMillis = System.currentTimeMillis();
            long sevenDaysInMillis = 7 * 24 * 60 * 60 * 1000L;
            long sevenDaysFromNowInMillis = nowInMillis + sevenDaysInMillis;

            File file = new File("data/users.txt");
            Scanner scanner = new Scanner(file);

            List<String> updatedUsers = new ArrayList<>();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] fields = line.split(",");

                if (fields[0].equals(username)) {
                    fields[2] = newToken;
                    fields[3] = String.valueOf(sevenDaysFromNowInMillis);
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

            UsersRepository.getUserByName(username).ifPresent(u ->
                    u.setToken(newToken));
            UsersRepository.getUserByName(username).ifPresent(u ->
                    u.setExpiryDateToken(sevenDaysFromNowInMillis));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

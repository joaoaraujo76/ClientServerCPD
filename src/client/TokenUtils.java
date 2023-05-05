package client;

import java.io.*;

public class TokenUtils {
    private final static String TOKEN_FILE = "src/client/token.txt";
    public static void updateToken(String newToken) {
        try {
            File file = new File(TOKEN_FILE);
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

    public static String getClientToken() {
        String token = null;
        File file = new File(TOKEN_FILE);
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
}

import java.io.*;
import java.security.NoSuchAlgorithmException;

public class Register {

    public static boolean newUser(String username, String password) {
        try {
            FileWriter fileWriter = new FileWriter("users.txt", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            PrintWriter printWriter = new PrintWriter(bufferedWriter);

            // Verificar se o usuário já existe no arquivo
            if (verifyUser(username)) {
                printWriter.close();
                return false;
            } else {
                // Registrar o usuário no arquivo
                String hashedPassword = Hashing.hashPassword(password);
                printWriter.println(username + "," + hashedPassword + ",null" + ",null");
                printWriter.close();
                return true;
            }


        } catch (IOException | NoSuchAlgorithmException e) {
            System.out.println("Erro ao registrar usuário: " + e.getMessage());
            return false;
        }
    }
    public static boolean verifyUser(String username) {
        try {
            FileReader fileReader = new FileReader("users.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String linha;
            while ((linha = bufferedReader.readLine()) != null) {
                String[] fields = linha.split(",");
                String userId = fields[0];

                // Verificar se o username de usuário já existe no arquivo
                if (userId.equals(username)) {
                    bufferedReader.close();
                    return true;
                }
            }

            bufferedReader.close();
        } catch (IOException e) {
            System.out.println("Erro ao verificar existência de usuário: " + e.getMessage());
        }
        return false;
    }

}
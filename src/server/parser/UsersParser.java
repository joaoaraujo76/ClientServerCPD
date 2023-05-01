package server.parser;

import server.repository.UsersRepository;
import server.models.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

public class UsersParser implements Parser {
    public static void parse() throws IOException, ParseException {
        FileReader fileReader = new FileReader("data/users.txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] split = line.split(",");
            UsersRepository.getUserList().add(new User(split[0], split[1], split[2], Long.parseLong(split[3])));
        }
        bufferedReader.close();
    }
}

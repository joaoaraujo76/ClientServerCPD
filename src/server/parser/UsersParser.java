package server.parser;

import server.repository.UsersRepository;
import server.models.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UsersParser implements Parser {
    public static void parse() throws IOException, ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        FileReader fileReader = new FileReader("../data/users.txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] split = line.split(",");
            Date date = dateFormat.parse(split[3]);
            UsersRepository.getUserList().add(new User(split[0], split[1], split[2], date));
        }
        bufferedReader.close();
    }
}

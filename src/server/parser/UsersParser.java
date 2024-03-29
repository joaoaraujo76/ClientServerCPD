package server.parser;

import server.repository.UsersRepository;
import server.models.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UsersParser implements Parser {
    private static final String DATA_FILE = "src/server/data/users.txt";
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    public static void parse() throws IOException, ParseException {
        lock.readLock().lock();

        try {
            FileReader fileReader = new FileReader(DATA_FILE);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            UsersRepository.getUserList().clear();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] split = line.split(",");
                UsersRepository.getUserList().add(new User(split[0], split[1], split[2], Long.parseLong(split[3]), Integer.parseInt(split[4]), split[5], Long.parseLong(split[6])));
            }
            bufferedReader.close();
        } finally {
            lock.readLock().unlock();
        }
    }
}

package server.commands;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface Command {
    void execute() throws IOException, NoSuchAlgorithmException;
}

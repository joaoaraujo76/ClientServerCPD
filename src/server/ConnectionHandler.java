package server;

import protocol.Message;
import server.commands.*;
import server.parser.UsersParser;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ConnectionHandler implements Runnable {
    private final Socket clientSocket;

    public ConnectionHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
            output.flush();
            UsersParser.parse();

            while (true) {
                Object obj = input.readObject();

                if (obj instanceof Message message) {
                    String tokenClient = message.getToken();
                    System.out.println("Token received: " + tokenClient);

                    switch (message.getType()) {

                        case REGISTER -> {
                            Command registerCommand = new RegisterCommand(message, output);
                            registerCommand.execute();
                        }

                        case LOGIN -> {
                            Command loginCommand = new LoginCommand(message, output);
                            loginCommand.execute();
                        }

                        case LOGIN_TOKEN -> {
                            Command loginTokenCommand = new LoginTokenCommand(message, output);
                            loginTokenCommand.execute();
                        }

                        case JOIN_QUEUE -> {
                            Command joinQueueCommand = new JoinQueueCommand(message, output);
                            joinQueueCommand.execute();
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | ParseException e) {
            // TODO: handle exceptions;
        }
    }
}

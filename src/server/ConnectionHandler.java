package server;

import protocol.Message;
import server.commands.*;
import server.parser.UsersParser;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.io.IOException;

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

            while (true) {
                Object obj = input.readObject();
                UsersParser.parse();

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
                            Command loginTokenCommand = new LoginTokenCommand(message, input, output, clientSocket);
                            loginTokenCommand.execute();
                        }

                        case JOIN_RANKED_QUEUE -> {
                            Command joinRankedQueueCommand = new JoinRankedQueueCommand(message, input, output, clientSocket);
                            joinRankedQueueCommand.execute();
                        }

                        case JOIN_SIMPLE_QUEUE -> {
                            Command joinSimpleQueueCommand = new JoinSimpleQueueCommand(message, input, output, clientSocket);
                            joinSimpleQueueCommand.execute();
                        }

                        case CHANGE_PASSWORD -> {
                            Command changePasswordCommand = new ChangePasswordCommand(message, output);
                            changePasswordCommand.execute();
                        }

                        case SEE_ELO -> {
                            Command seeElo = new SeeEloCommand(message,output);
                            seeElo.execute();
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException | ParseException e) {
            // TODO: handle exceptions;
        }
    }
}

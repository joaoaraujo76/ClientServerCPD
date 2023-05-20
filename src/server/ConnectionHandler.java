package server;

import protocol.Message;
import server.commands.*;
import server.parser.UsersParser;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionHandler implements Runnable {
    private static final ExecutorService executor = Executors.newCachedThreadPool();
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
                        case REGISTER -> executor.submit(new RegisterCommand(message, output));
                        case LOGIN -> executor.submit(new LoginCommand(message, input, output, clientSocket));
                        case LOGIN_TOKEN -> executor.submit(new LoginTokenCommand(message, input, output, clientSocket));
                        case JOIN_RANKED_QUEUE -> executor.submit(new JoinRankedQueueCommand(message, input, output, clientSocket));
                        case JOIN_SIMPLE_QUEUE -> executor.submit(new JoinSimpleQueueCommand(message, input, output, clientSocket));
                        case CHANGE_PASSWORD -> executor.submit(new ChangePasswordCommand(message, output));
                        case SEE_ELO -> executor.submit(new SeeEloCommand(message, output));
                    }
                }
            }
        } catch (IOException | ClassNotFoundException | ParseException e) {
            // TODO: handle exceptions;
        }
    }
}

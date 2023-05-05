package client;

import client.states.ClientState;
import client.states.LoginTokenState;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        if (args.length < 2) return;

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(hostname, port)) {
            Scanner scanner = new Scanner(System.in);
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            output.flush();

            ClientState state = new LoginTokenState(scanner, input, output);
            while (true) {
                state = state.execute();
            }
        } catch (IOException e) {
            //TODO: handle exceptions
        }
    }
}
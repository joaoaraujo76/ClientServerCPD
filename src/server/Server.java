package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.*;


public class Server {

    private static int port;
    private static ExecutorService executor = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        if (args.length < 1) return;

        port = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server is listening on port " + port +"\n") ;

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from " + clientSocket.getInetAddress());

                executor.submit(new ConnectionHandler(clientSocket));
            }

        } catch (SocketException ex) {
            System.out.println("A conexão com o cliente foi interrompida.");
        } catch (IOException ex) {
            System.out.println("Ocorreu uma exceção de IO: " + ex.getMessage());
        }
    }
}

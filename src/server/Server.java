package server;

import server.queues.HighEloQueue;
import server.queues.LowEloQueue;
import server.queues.MediumEloQueue;
import server.queues.QueueTimeoutChecker;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        if (args.length < 1) return;

        int port = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server is listening on port " + port +"\n") ;

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from " + clientSocket.getInetAddress());

                executor.submit(new ConnectionHandler(clientSocket));
                executor.submit(new QueueTimeoutChecker(HighEloQueue.getInstance(), MediumEloQueue.getInstance(), 2));
                executor.submit(new QueueTimeoutChecker(MediumEloQueue.getInstance(), LowEloQueue.getInstance(), 1));
            }

        } catch (SocketException ex) {
            System.out.println("A conexão com o cliente foi interrompida.");
        } catch (IOException ex) {
            System.out.println("Ocorreu uma exceção de IO: " + ex.getMessage());
        }
    }
}

package server.models;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Player {
    private final User user;
    private final Socket socket;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;

    public Player(User user, Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        this.user = user;
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public User getUser() {
        return user;
    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectInputStream getInputStream() {
        return inputStream;
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    public Long getQueueJoinTime() {
        return user.getQueueJoinTime();
    }
}

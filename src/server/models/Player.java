package server.models;

import java.io.ObjectOutputStream;

public class Player {
    private final User user;
    private final ObjectOutputStream outputStream;

    public Player(User user, ObjectOutputStream outputStream) {
        this.user = user;
        this.outputStream = outputStream;
    }

    public User getUser() {
        return user;
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    public Long getQueueJoinTime() {
        return user.getQueueJoinTime();
    }
}

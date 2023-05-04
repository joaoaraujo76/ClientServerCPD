package protocol;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Message implements Serializable {
    private MessageType type;
    private String token;
    private String message;
    private String username;
    private String password;

    public Message(MessageType type, String token, String message) {
        this.type = type;
        this.token = token;
        this.message = message;
        this.username = null;
        this.password = null;
    }

    public Message(MessageType type, String token) {
        this.type = type;
        this.token = token;
        this.message = null;
        this.username = null;
        this.password = null;
    }

    public Message(MessageType type, String token, String username, String password) {
        this.type = type;
        this.token = token;
        this.message = null;
        this.username = username;
        this.password = password;
    }

    public MessageType getType() {
        return type;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }


    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(type);
        out.writeObject(token);
        out.writeObject(message);
        out.writeObject(username);
        out.writeObject(password);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        type = (MessageType) in.readObject();
        token = (String) in.readObject();
        message = (String) in.readObject();
        username = (String) in.readObject();
        password = (String) in.readObject();
    }

}

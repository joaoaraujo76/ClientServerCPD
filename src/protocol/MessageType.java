package protocol;

public enum MessageType {
    REGISTER,
    REGISTERED,
    LOGIN,
    LOGIN_TOKEN,
    AUTHENTICATED,
    AUTHENTICATED_TOKEN,
    JOIN_SIMPLE_QUEUE,
    JOIN_RANKED_QUEUE,
    QUEUED,
    LEAVE_QUEUE,
    QUEUE_LEFT,
    RESUME,
    ERROR
}



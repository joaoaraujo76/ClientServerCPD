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
    RESUME,
    CHECK,
    START_GAME,
    END_GAME,
    ERROR
}



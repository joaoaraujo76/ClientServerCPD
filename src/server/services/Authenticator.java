package server.services;

public interface Authenticator {

    static boolean login(String username, String password) {
        return false;
    }

    static boolean register(String username, String password) {
        return false;
    }

    private static String hashPassword(String password) {
        return null;
    }
}

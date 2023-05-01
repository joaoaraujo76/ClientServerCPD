package server.services;

import server.models.User;

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

    static boolean validToken(User user, String token) {
        return false;
    }
}

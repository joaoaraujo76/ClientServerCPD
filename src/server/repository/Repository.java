package server.repository;

import server.models.User;

import java.util.Optional;
import java.util.Set;

public interface Repository<T> {
    static <T> Set<T> getUserList() {
        return null;
    }

    static <T> Optional<T> getUserByName(String name) {
        return Optional.empty();
    }

    static <T> Optional<T> getUserByToken(String token) {
        return Optional.empty();
    }
}

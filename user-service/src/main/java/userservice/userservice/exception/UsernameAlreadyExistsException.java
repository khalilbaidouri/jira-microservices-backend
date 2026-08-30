package userservice.userservice.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super("Le nom d'utilisateur '" + username + "' existe déjà");
    }
}
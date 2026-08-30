package userservice.userservice.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Nom d'utilisateur ou mot de passe incorrect");
    }
}
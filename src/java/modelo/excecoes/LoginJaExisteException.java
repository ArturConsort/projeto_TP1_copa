package src.java.modelo.excecoes;

public class LoginJaExisteException extends RuntimeException {
    public LoginJaExisteException(String message) {
        super(message);
    }
}

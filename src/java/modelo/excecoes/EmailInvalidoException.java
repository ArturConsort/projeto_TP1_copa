package src.java.modelo.excecoes;

public class EmailInvalidoException extends RuntimeException {
    public EmailInvalidoException(String message) {
        super(message);
    }
}

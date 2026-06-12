package modelo.excecoes;

public class AutoDeleteException extends RuntimeException {
    public AutoDeleteException(String message) {
        super(message);
    }
}

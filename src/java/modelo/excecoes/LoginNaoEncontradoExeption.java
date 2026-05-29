package src.java.modelo.excecoes;

public class LoginNaoEncontradoExeption extends RuntimeException {
    public LoginNaoEncontradoExeption(String message) {
        super(message);
    }
}

package modelo.excecoes;

public class JogadorNaoEncontradoException extends RuntimeException {
    public JogadorNaoEncontradoException(String message) {
        super(message);
    }
}

package modelo.excecoes;

public class SelecaoNaoEncontradaException extends RuntimeException {
    public SelecaoNaoEncontradaException(String message) {
        super(message);
    }
}

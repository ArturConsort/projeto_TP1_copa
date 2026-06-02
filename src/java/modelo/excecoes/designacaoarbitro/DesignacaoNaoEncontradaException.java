package src.java.modelo.excecoes.designacaoarbitro;

public class DesignacaoNaoEncontradaException extends DesignacaoException {
    public DesignacaoNaoEncontradaException(int numpartida) {
        super("A designação da partida " + numPartida + " não foi encontrada.");
    }
}
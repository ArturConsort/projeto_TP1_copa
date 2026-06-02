package src.java.modelo.excecoes.designacaoarbitro;

public class DesignacaoJaCadastradaException extends DesignacaoException {
    public DesignacaoJaCadastradaException(int numPartida) {
        super("A designação da partida " + numPartida + " já foi cadastrada.");
    }
}
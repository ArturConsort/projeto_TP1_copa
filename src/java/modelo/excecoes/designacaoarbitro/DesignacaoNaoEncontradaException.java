package src.java.modelo.excecoes.designacaoarbitro;

public class DesignacaoNaoEncontrada extends DesignacaoException {
    public DesignacaoNaoEncontrada(int numpartida) {
        super("A designação da partida " + numPartida + " não foi encontrada.");
    }
}
package src.java.modelo.excecoes.designacaoarbitro;

public class DesignacaoNaoEncontrada extends DesignacaoException {
    public DesignacaoNaoEncontrada(String nomeArbitro) {
        super("O árbitro " + nomeArbitro + " não foi designado");
    }
}
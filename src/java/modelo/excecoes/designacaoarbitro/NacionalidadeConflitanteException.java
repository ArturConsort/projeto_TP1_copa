package src.java.modelo.excecoes.designacaoarbitro;

public class NacionalidadeConflitanteException extends DesignacaoException {
    public NacionalidadeConflitanteException(String nomeArbitro, String nomeSelecao) {
        super("O árbitro " + nomeArbitro + " possui conflito de nacionalidade com a seleção " + nomeSelecao + ".");
    }
}
package modelo.excecoes.designacaoarbitro;

public class ArbitroIguaisException extends DesignacaoException{
    public ArbitroIguaisException() {
        super("Não é permitido designar o mesmo árbitro para mais de uma função na mesma partida.");
    }
}
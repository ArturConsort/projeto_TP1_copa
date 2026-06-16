package modelo.excecoes.designacaoarbitro;

public class ArbitroIguaisException extends DesignacaoException{
    public ArbitroIguaisException() {
        super("Um ou mais arbitros que você inseriu são iguais");
    }
}
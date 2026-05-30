package src.java.modelo.excecoes.arbitro;

public class ArbitroNaoEncontradoException extends ArbitroException{
    public ArbitroNaoEncontradoException(String nome){
        super("Não existe um árbitro com o nome: " + nome)
    }
}
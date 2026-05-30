package src.java.modelo.excecoes.arbitro;

public class ArbitroJaCadastradoException extends ArbitroException {
    public ArbitroJaCadastradoException(String nome){
        super("Já existe um árbitro cadastrado com o nome: " + nome);
    }
}
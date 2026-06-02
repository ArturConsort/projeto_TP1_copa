package src.java.modelo.excecoes.estadio;

public class EstadioNaoEncontradoException extends EstadioException{
    public EstadioNaoEncontradoException(String nome){
        super("Não existe um estádio cadastrado com o nome: " + nome);
    }
}
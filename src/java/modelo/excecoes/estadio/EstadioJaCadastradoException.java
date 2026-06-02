package src.java.modelo.excecoes.estadio;

public class EstadioJaCadastradoException extends EstadioException{
    public EstadioJaCadastradoException(String nome){
        super("Já existe um estádio cadastrado com o nome: " + nome);
    }
}
package src.java.modelo.excecoes.estadio;

public class EstadioNaoEncontrado extends EstadioException{
    public EstadioNaoEncontrado(String nome){
        super("Não existe um estádio cadastrado com o nome: " + nome);
    }
}
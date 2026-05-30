package src.java.modelo.excecoes.estadio;

public class EstadioJaCadastrado extends EstadioException{
    public EstadioJaCadastrado(String nome){
        super("Já existe um estádio cadastrado com o nome: " + nome);
    }
}
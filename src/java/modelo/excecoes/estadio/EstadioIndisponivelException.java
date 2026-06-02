package src.java.modelo.excecoes.estadio;

public class EstadioIndisponivelException extends EstadioException{
    public EstadioIndisponivelException(String nome_estadio, String data, String horario){
        super("Estádio "+ nome_estadio +" está indisponível no dia " + data + " na hora " + horario);
    }
}
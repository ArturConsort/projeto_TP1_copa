package src.java.modelo.classes;
import java.util.List;

enum FasePartida{

    FASE_DE_GRUPOS,
    OITAVAS_DE_FINAL,
    QUARTAS_DE_FINAL,
    SEMIFINAL,
    FINAL,
    DISPUTA_DE_TERCEIRO_LUGAR
}

public class Partida {
    private Selecao timeCasa;
    private Selecao timeVisitante;
    private static int contPartidas;
    private int numeroPartidas;
    private String cidade;
    private String data;
    private String horario;
    private Estadio estadio;
    private Arbitro arbitroPrincipal;
    private List<Arbitro> listaAssistentes;
    private boolean finalizado = false;
    private FasePartida fase;

    public Partida(Selecao timeCasa, Selecao timeVisitante, int numeroPartidas, String cidade, String data, String horario, Estadio estadio, FasePartida fase) {
        this.timeCasa = timeCasa;
        this.timeVisitante = timeVisitante;
        this.numeroPartidas = numeroPartidas;
        this.cidade = cidade;
        this.data = data;
        this.horario = horario;
        this.estadio = estadio;
        this.fase = fase;
    }

    public Selecao getTimeCasa() {
        return timeCasa;
    }

    public void setTimeCasa(Selecao timeCasa) {
        this.timeCasa = timeCasa;
    }

    public Selecao getTimeVisitante() {
        return timeVisitante;
    }

    public void setTimeVisitante(Selecao timeVisitante) {
        this.timeVisitante = timeVisitante;
    }

    public static int getContPartidas() {
        return contPartidas;
    }

    public static void setContPartidas(int contPartidas) {
        Partida.contPartidas = contPartidas;
    }

    public int getNumeroPartidas() {
        return numeroPartidas;
    }

    public void setNumeroPartidas(int numeroPartidas) {
        this.numeroPartidas = numeroPartidas;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public Estadio getEstadio() {
        return estadio;
    }

    public void setEstadio(Estadio estadio) {
        this.estadio = estadio;
    }

    public Arbitro getArbitroPrincipal() {
        return arbitroPrincipal;
    }

    public void setArbitroPrincipal(Arbitro arbitroPrincipal) {
        this.arbitroPrincipal = arbitroPrincipal;
    }

    public List<Arbitro> getListaAssistentes() {
        return listaAssistentes;
    }

    public void setListaAssistentes(List<Arbitro> listaAssistentes) {
        this.listaAssistentes = listaAssistentes;
    }

    public FasePartida getFase() {
        return fase;
    }

    public void setFase(FasePartida fase) {
        this.fase = fase;
    }
}

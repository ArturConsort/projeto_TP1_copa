package modelo.classes;
import modelo.enumerations.FasePartida;
import modelo.enumerations.StatusPartida;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

public class Partida implements Serializable {
    private static final long serialVersionUID = 1L;

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
    private FasePartida fase;

    // 1 — "agendada" é o status inicial, conforme requisito "Atualizar status das partidas"
    private StatusPartida status = StatusPartida.AGENDADA;

    // 2 — composição: ResultadoPartida pertence à Partida
    private ResultadoPartida resultado;

    public Partida(Selecao timeCasa, Selecao timeVisitante, int numeroPartidas, String cidade, String data, String horario, Estadio estadio, FasePartida fase) {
        this.timeCasa = timeCasa;
        this.timeVisitante = timeVisitante;
        this.numeroPartidas = numeroPartidas;
        this.cidade = cidade;
        this.data = data;
        this.horario = horario;
        this.estadio = estadio;
        this.fase = fase;
        this.arbitroPrincipal = arbitroPrincipal;
        this.listaAssistentes = new ArrayList<>(); // inicializa lista vazia
    }

    // 3 — toString para o JComboBox exibir algo legível
    @Override
    public String toString() {
        return "Partida " + numeroPartidas + ": "
                + timeCasa.getPais() + " x " + timeVisitante.getPais()
                + " (" + data + " - " + fase + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Partida)) return false;
        Partida outra = (Partida) obj;
        return this.numeroPartidas == outra.numeroPartidas;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(numeroPartidas);
    }

    // --- getters e setters existentes ---
    public Selecao getTimeCasa() { return timeCasa; }
    public void setTimeCasa(Selecao timeCasa) { this.timeCasa = timeCasa; }

    public Selecao getTimeVisitante() { return timeVisitante; }
    public void setTimeVisitante(Selecao timeVisitante) { this.timeVisitante = timeVisitante; }

    public static int getContPartidas() { return contPartidas; }
    public static void setContPartidas(int contPartidas) { Partida.contPartidas = contPartidas; }

    public int getNumeroPartidas() { return numeroPartidas; }
    public void setNumeroPartidas(int numeroPartidas) { this.numeroPartidas = numeroPartidas; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public Estadio getEstadio() { return estadio; }
    public void setEstadio(Estadio estadio) { this.estadio = estadio; }

    public Arbitro getArbitroPrincipal() { return arbitroPrincipal; }
    public void setArbitroPrincipal(Arbitro arbitroPrincipal) { this.arbitroPrincipal = arbitroPrincipal; }

    public List<Arbitro> getListaAssistentes() { return listaAssistentes; }
    public void setListaAssistentes(List<Arbitro> listaAssistentes) { this.listaAssistentes = listaAssistentes; }

    public FasePartida getFase() { return fase; }
    public void setFase(FasePartida fase) { this.fase = fase; }

    // --- getters e setters novos ---
    public StatusPartida getStatus() { return status; }
    public void setStatus(StatusPartida status) { this.status = status; }

    public ResultadoPartida getResultado() { return resultado; }
    public void setResultado(ResultadoPartida resultado) { this.resultado = resultado; }

    public boolean isFinalizada() {
        return this.status == StatusPartida.FINALIZADA;
    }
}
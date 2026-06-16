package modelo.classes;

import java.io.Serializable;

public class PontuacaoSelecao implements Serializable {
    private static final long serialVersionUID = 1L;

    private Selecao selecao;
    private int pontos;
    private int vitorias;
    private int empates;
    private int derrotas;
    private int golsMarcados;
    private int golsSofridos;
    private int partidasJogadas;

    public PontuacaoSelecao(Selecao selecao) {
        this.selecao = selecao;
        // todos os contadores começam em zero
    }

    // Chamado quando a seleção VENCE uma partida
    public void registrarVitoria(int golsMarcados, int golsSofridos) {
        this.pontos += 3;
        this.vitorias++;
        this.golsMarcados += golsMarcados;
        this.golsSofridos += golsSofridos;
        this.partidasJogadas++;
    }

    // Chamado quando a seleção EMPATA uma partida
    public void registrarEmpate(int golsMarcados, int golsSofridos) {
        this.pontos += 1;
        this.empates++;
        this.golsMarcados += golsMarcados;
        this.golsSofridos += golsSofridos;
        this.partidasJogadas++;
    }

    // Chamado quando a seleção PERDE uma partida
    public void registrarDerrota(int golsMarcados, int golsSofridos) {
        this.derrotas++;
        this.golsMarcados += golsMarcados;
        this.golsSofridos += golsSofridos;
        this.partidasJogadas++;
    }

    public int getSaldoGols() {
        return golsMarcados - golsSofridos;
    }

    // Getters
    public Selecao getSelecao()       { return selecao; }
    public int getPontos()            { return pontos; }
    public int getVitorias()          { return vitorias; }
    public int getEmpates()           { return empates; }
    public int getDerrotas()          { return derrotas; }
    public int getGolsMarcados()      { return golsMarcados; }
    public int getGolsSofridos()      { return golsSofridos; }
    public int getPartidasJogadas()   { return partidasJogadas; }

    @Override
    public String toString() {
        return String.format("%-15s | PJ:%d | V:%d | E:%d | D:%d | GM:%d | GS:%d | SG:%d | Pts:%d",
                selecao.getPais(), partidasJogadas, vitorias, empates,
                derrotas, golsMarcados, golsSofridos, getSaldoGols(), pontos);
    }
}
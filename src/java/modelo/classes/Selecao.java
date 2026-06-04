package src.java.modelo.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Selecao implements Serializable {
    private static final long serialVersionUID = 1L;

    private String pais;
    private char grupo;
    private String confederacao;
    private String tecnico;
    private int rankingFIFA;
    private int titulos;
    private List<Jogador> jogadores;
    private List<Partida> partidas;

    public Selecao(String pais, char grupo, String confederacao, String tecnico, int rankingFIFA, int titulos) {
        this.pais = pais;
        this.grupo = grupo;
        this.confederacao = confederacao;
        this.tecnico = tecnico;
        this.rankingFIFA = rankingFIFA;
        this.titulos = titulos;
        this.jogadores = new ArrayList<>();
        this.partidas = new ArrayList<>();
    }

    public String getPais() {
        return pais;
    }
    public void setPais(String pais) {
        this.pais = pais;
    }

    public char getGrupo() {
        return grupo;
    }
    public void setGrupo(char grupo) {
        this.grupo = grupo;
    }

    public String getConfederacao() {
        return confederacao;
    }
    public void setConfederacao(String confederacao) {
        this.confederacao = confederacao;
    }


    public String getTecnico() {
        return tecnico;
    }
    public void setTecnico(String tecnico) {
        this.tecnico = tecnico;
    }

    public int getRankingFIFA() {
        return rankingFIFA;
    }
    public void setRankingFIFA(int rankingFIFA) {
        this.rankingFIFA = rankingFIFA;
    }

    public int getTitulos() {
        return titulos;
    }
    public void setTitulos(int titulos) {
        this.titulos = titulos;
    }

    public List<Jogador> getJogadores() {
        return jogadores;
    }
    public void addJogadores(Jogador jog) {
        jogadores.add(jog);
    }

    public List<Partida> getPartidas(){
        return partidas;
    }
    public void adicionarPartida(Partida partida) {
        partidas.add(partida);
    }

    @Override
    public String toString() {
        return pais;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Selecao)) return false;
        Selecao outra = (Selecao) obj;
        return this.pais.equalsIgnoreCase(outra.pais);
    }

    @Override
    public int hashCode() {
        return pais.toLowerCase().hashCode();
    }
}

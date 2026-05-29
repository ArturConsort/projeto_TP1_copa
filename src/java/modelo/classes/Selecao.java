package src.java.modelo.classes;

import java.util.ArrayList;
import java.util.List;

public class Selecao {
    private String pais;
    private char grupo;
    private String confederacao;
    private String tecnico;
    private int rankingFIFA;
    private int titulos;
    private List<Jogador> jogadores;
    private List<Partida> partidas = new ArrayList<>();

    public Selecao(String pais, char grupo, String confederacao, String tecnico, int rankingFIFA, int titulos, List<Jogador> jogadores, List<Partida> partidas) {
        this.pais = pais;
        this.grupo = grupo;
        this.confederacao = confederacao;
        this.tecnico = tecnico;
        this.rankingFIFA = rankingFIFA;
        this.titulos = titulos;
        this.jogadores = jogadores;
        this.partidas = partidas;
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
    public void setJogadores(List<Jogador> jogadores) {
        this.jogadores = jogadores;
    }

    public List<Partida> getPartidas(){
        return partidas;
    }
    public void adicionarPartida(Partida partida) {
        partidas.add(partida);
    }
}

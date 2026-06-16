package modelo.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Selecao implements Serializable {
    private static final long serialVersionUID = 1L;

    private String pais;
    private String grupo;
    private String confederacao;
    private String tecnico;
    private int rankingFIFA;
    private int titulos;
    private List<Jogador> jogadores;
    private List<Partida> partidas;

    // true = ainda está na competição
    // false = foi eliminada
    private boolean ativa = true;

    // true = perdeu a semifinal (pode disputar 3º lugar)
    // false = não perdeu semifinal
    private boolean perdeuSemifinal = false;

    public Selecao(String pais, String grupo, String confederacao, String tecnico, int rankingFIFA, int titulos) {
        this.pais = pais;
        this.grupo = grupo;
        this.confederacao = confederacao;
        this.tecnico = tecnico;
        this.rankingFIFA = rankingFIFA;
        this.titulos = titulos;
        this.jogadores = new ArrayList<>();
        this.partidas = new ArrayList<>();
    }

    // Getters e setters existentes
    public String getPais()             { return pais; }
    public void setPais(String pais)    { this.pais = pais; }

    public String getGrupo()              { return grupo; }
    public void setGrupo(String grupo)    { this.grupo = grupo; }

    public String getConfederacao()                  { return confederacao; }
    public void setConfederacao(String confederacao) { this.confederacao = confederacao; }

    public String getTecnico()               { return tecnico; }
    public void setTecnico(String tecnico)   { this.tecnico = tecnico; }

    public int getRankingFIFA()                  { return rankingFIFA; }
    public void setRankingFIFA(int rankingFIFA)  { this.rankingFIFA = rankingFIFA; }

    public int getTitulos()              { return titulos; }
    public void setTitulos(int titulos)  { this.titulos = titulos; }

    public List<Jogador> getJogadores()          { return jogadores; }
    public void addJogadores(Jogador jog)        { jogadores.add(jog); }

    public List<Partida> getPartidas()           { return partidas; }
    public void adicionarPartida(Partida partida){ partidas.add(partida); }

    // --- Novos getters/setters ---

    public boolean isAtiva()             { return ativa; }
    public void setAtiva(boolean ativa)  { this.ativa = ativa; }

    public boolean isPerdeuSemifinal()                    { return perdeuSemifinal; }
    public void setPerdeuSemifinal(boolean perdeuSemifinal) { this.perdeuSemifinal = perdeuSemifinal; }

    // Conveniência: elimina a seleção da competição
    public void eliminar() { this.ativa = false; }

    @Override
    public String toString() { return pais; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Selecao)) return false;
        Selecao outra = (Selecao) obj;
        return this.pais.equalsIgnoreCase(outra.pais);
    }

    @Override
    public int hashCode() { return pais.toLowerCase().hashCode(); }
}
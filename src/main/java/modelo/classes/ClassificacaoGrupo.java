package modelo.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ClassificacaoGrupo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String grupo; // 'A', 'B', ... 'H'
    private List<PontuacaoSelecao> pontuacoes;

    public ClassificacaoGrupo(String grupo) {
        this.grupo = grupo;
        this.pontuacoes = new ArrayList<>();
    }

    // Adiciona uma seleção ao grupo (chamado ao montar o grupo)
    public void adicionarSelecao(Selecao selecao) {
        // Evita duplicatas
        boolean jaExiste = pontuacoes.stream()
                .anyMatch(p -> p.getSelecao().equals(selecao));
        if (!jaExiste) {
            pontuacoes.add(new PontuacaoSelecao(selecao));
        }
    }

    // Retorna a pontuacao de uma selecao especifica
    public PontuacaoSelecao getPontuacao(Selecao selecao) {
        return pontuacoes.stream()
                .filter(p -> p.getSelecao().equals(selecao))
                .findFirst()
                .orElse(null);
    }

    // Retorna a tabela ordenada por pontos (sem desempate por ora)
    public List<PontuacaoSelecao> getClassificacao() {
        List<PontuacaoSelecao> ordenada = new ArrayList<>(pontuacoes);
        ordenada.sort(Comparator.comparingInt(PontuacaoSelecao::getPontos).reversed());
        return ordenada;
    }

    // As duas primeiras seleções classificadas avançam para oitavas
    public List<Selecao> getClassificadas() {
        List<Selecao> classificadas = new ArrayList<>();
        List<PontuacaoSelecao> tabela = getClassificacao();
        // Só classifica se todas jogaram as 3 partidas
        if (todasJogaramTresPartidas()) {
            classificadas.add(tabela.get(0).getSelecao()); // 1ª colocada
            classificadas.add(tabela.get(1).getSelecao()); // 2ª colocada
        }
        return classificadas;
    }

    // Verifica se todas as 4 seleções jogaram 3 partidas
    public boolean todasJogaramTresPartidas() {
        return pontuacoes.stream().allMatch(p -> p.getPartidasJogadas() == 3);
    }

    public String getGrupo()                         { return grupo; }
    public List<PontuacaoSelecao> getPontuacoes()  { return pontuacoes; }
}
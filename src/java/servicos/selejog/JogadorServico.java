package src.java.servicos.selejog;

import src.java.modelo.classes.Jogador;
import src.java.modelo.classes.Selecao;
import src.java.modelo.classes.StatusJogador;
import src.java.modelo.excecoes.JogadorNaoEncontradoException;
import src.java.modelo.excecoes.SelecaoJaExisteException;
import src.java.modelo.excecoes.SelecaoNaoEncontradaException;
import src.java.persistencia.SelecaoDAO;

import java.util.ArrayList;
import java.util.List;

public class JogadorServico {

    private SelecaoDAO dao;

    public JogadorServico() {
        this.dao = new SelecaoDAO();
    }


    // ------- manipulacao de selecoes pelo admin ------- //

    public void cadastrar(String nome, int idade, String numeracao, String posicao, Selecao selecao, StatusJogador status) {


        // --- validacoes de campos obrigatorios --- //
        if (selecao == null)
            throw new IllegalArgumentException("O nome da seleção não pode ser vazio");

        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("O nome do jogador não pode ser vazio");

        if (idade < 0)
            throw new IllegalArgumentException("A idade do jogador não pode ser negativa");

        // --- validacoes de valores numericos --- //
        if (numeracao == null || numeracao.isBlank())   // a FIFA tem 211 paises filiados
            throw new IllegalArgumentException("A numeração não pode ser vazia");

        if (posicao == null || posicao.isBlank())
            throw new IllegalArgumentException("A posição não pode ser vazia");

        // --- validacao de duplicata --- //
        if (dao.buscarPorPais(selecao.getPais()) == null)
            throw new SelecaoJaExisteException("Essa seleção não exite ainda: " + selecao.getPais());

        for (Jogador j : selecao.getJogadores()) {
            if (j.getNumeracao().equals(numeracao))
                throw new IllegalArgumentException("Já existe um jogador com a numeração " + numeracao + " nessa seleção");
        }

        Jogador jog = new Jogador(nome, idade, numeracao, posicao, selecao, status);
        dao.adicionarJog(jog);
    }

    public Jogador buscarPorNome(String nome) throws JogadorNaoEncontradoException {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("O nome não pode ser vazio");

        for (Selecao s : dao.carregaLista()) {
            for (Jogador j : s.getJogadores()) {
                if (j.getNome().equalsIgnoreCase(nome)) return j;
            }
        }

        throw new JogadorNaoEncontradoException("Jogador não encontrado: " + nome);
    }

    public void remover(String nome) throws JogadorNaoEncontradoException {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("O nome não pode ser vazio");

        for (Selecao s : dao.carregaLista()) {
            boolean removeu = s.getJogadores().removeIf(j -> j.getNome().equalsIgnoreCase(nome));
            if (removeu) {
                dao.atualizaSelecao(s); // salva a selecao com o jogador removido
                return;
            }
        }

        throw new JogadorNaoEncontradoException("Jogador não encontrado: " + nome);
    }
}

package src.java.servicos.selejog;

import src.java.modelo.classes.Jogador;
import src.java.modelo.enumerations.TipoPerfil;
import src.java.modelo.classes.Selecao;
import src.java.modelo.excecoes.AcessoNegadoException;
import src.java.modelo.excecoes.SelecaoJaExisteException;
import src.java.modelo.excecoes.SelecaoNaoEncontradaException;
import src.java.persistencia.SelecaoDAO;

import java.util.List;

public class SelecaoServico {

    private SelecaoDAO dao;

    public SelecaoServico() {
        this.dao = new SelecaoDAO();
    }


    // ------- manipulacao de selecoes pelo admin ------- //

    public void cadastrar(String pais, char grupo, String confederacao, String tecnico, int rankingFIFA, int titulos) {


        // --- validacoes de campos obrigatorios --- //
        if (pais == null || pais.isBlank())
            throw new IllegalArgumentException("O nome do país não pode ser vazio");

        if (confederacao == null || confederacao.isBlank())
            throw new IllegalArgumentException("A confederação não pode ser vazia");

        if (tecnico == null || tecnico.isBlank())
            throw new IllegalArgumentException("O nome do técnico não pode ser vazio");

        // --- validacoes de valores numericos --- //
        if (rankingFIFA < 1 || rankingFIFA > 211)   // a FIFA tem 211 paises filiados
            throw new IllegalArgumentException("Ranking FIFA inválido: deve estar entre 1 e 211");

        if (titulos < 0)
            throw new IllegalArgumentException("O número de títulos não pode ser negativo");

        // --- validacao de duplicata --- //
        if (dao.buscarPorPais(pais) != null)
            throw new SelecaoJaExisteException("Já existe uma seleção cadastrada para o país: " + pais);


        Selecao nova = new Selecao(pais, grupo, confederacao, tecnico, rankingFIFA, titulos);
        dao.salvar(nova);
    }

    public void remover(String nome) throws SelecaoNaoEncontradaException {
        if (dao.buscarPorPais(nome) == null) {
            throw new SelecaoNaoEncontradaException("Seleção não encontrada: " + nome);
        }

        dao.remover(nome);
    }

    public Selecao buscarPorNome(String nome) throws SelecaoNaoEncontradaException {
        Selecao s = dao.buscarPorPais(nome);
        if (s == null) throw new SelecaoNaoEncontradaException("Seleção não encontrada: " + nome);
        return s;
    }

    public List<Selecao> buscarPorGrupo(char grupo) throws SelecaoNaoEncontradaException {
        List<Selecao> s = dao.buscarPorGrupo(grupo);
        return s;
    }

    public void listarTodas() {
        List<Selecao> list = dao.carregaLista();
        System.out.printf("Lista seleções:\n\n");
        for(Selecao a : list){
            System.out.printf("%s\n\n", a.getPais());
        }
    }

    public void editarTecnicoSelecao(Selecao selecao, String novo_tecnico){
        Selecao s = dao.buscarPorPais(selecao.getPais());
        if (s == null) throw new SelecaoNaoEncontradaException("Seleção não encontrada: " + selecao);
        s.setTecnico(novo_tecnico);
        dao.atualizaSelecao(s);
    }

    public void editarGrupoSelecao(Selecao selecao, char novo_grupo){
        Selecao s = dao.buscarPorPais(selecao.getPais());
        if (s == null) throw new SelecaoNaoEncontradaException("Seleção não encontrada: " + selecao);
        s.setGrupo(novo_grupo);
        dao.atualizaSelecao(s);
    }

}
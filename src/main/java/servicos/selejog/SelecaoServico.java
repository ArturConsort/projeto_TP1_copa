package servicos.selejog;

import modelo.classes.Selecao;
import modelo.excecoes.SelecaoJaExisteException;
import modelo.excecoes.SelecaoNaoEncontradaException;
import persistencia.SelecaoDAO;

import java.util.List;

public class SelecaoServico {

    private SelecaoDAO dao;

    public SelecaoServico() {
        this.dao = new SelecaoDAO();
    }

    // ------- manipulação de seleções pelo admin ------- //

    public void cadastrar(String pais, String grupo, String confederacao,
                          String tecnico, int rankingFIFA, int titulos) {

        if (pais == null || pais.isBlank())
            throw new IllegalArgumentException("O nome do país não pode ser vazio");

        if (confederacao == null || confederacao.isBlank())
            throw new IllegalArgumentException("A confederação não pode ser vazia");

        if (tecnico == null || tecnico.isBlank())
            throw new IllegalArgumentException("O nome do técnico não pode ser vazio");

        if (rankingFIFA < 1 || rankingFIFA > 211)
            throw new IllegalArgumentException("Ranking FIFA inválido: deve estar entre 1 e 211");

        if (titulos < 0)
            throw new IllegalArgumentException("O número de títulos não pode ser negativo");

        if (dao.buscarPorPais(pais) != null)
            throw new SelecaoJaExisteException("Já existe uma seleção cadastrada para o país: " + pais);

        Selecao nova = new Selecao(pais, grupo, confederacao, tecnico, rankingFIFA, titulos);
        dao.salvar(nova);
    }

    /**
     * Edita todos os campos editáveis de uma seleção já existente.
     * O país não é alterado pois é a chave de identificação.
     */
    public void editar(String pais, String novoGrupo, String novoTecnico,
                       int novoRankingFIFA, int novosTitulos) {

        // --- validações --- //
        if (novoTecnico == null || novoTecnico.isBlank())
            throw new IllegalArgumentException("O nome do técnico não pode ser vazio");

        if (novoRankingFIFA < 1 || novoRankingFIFA > 211)
            throw new IllegalArgumentException("Ranking FIFA inválido: deve estar entre 1 e 211");

        if (novosTitulos < 0)
            throw new IllegalArgumentException("O número de títulos não pode ser negativo");

        Selecao s = dao.buscarPorPais(pais);
        if (s == null)
            throw new SelecaoNaoEncontradaException("Seleção não encontrada: " + pais);

        s.setGrupo(novoGrupo);
        s.setTecnico(novoTecnico);
        s.setRankingFIFA(novoRankingFIFA);
        s.setTitulos(novosTitulos);

        dao.atualizaSelecao(s);
    }

    public void remover(String nome) throws SelecaoNaoEncontradaException {
        if (dao.buscarPorPais(nome) == null)
            throw new SelecaoNaoEncontradaException("Seleção não encontrada: " + nome);
        dao.remover(nome);
    }

    public Selecao buscarPorNome(String nome) throws SelecaoNaoEncontradaException {
        Selecao s = dao.buscarPorPais(nome);
        if (s == null) throw new SelecaoNaoEncontradaException("Seleção não encontrada: " + nome);
        return s;
    }

    public List<Selecao> buscarPorGrupo(String grupo) {
        return dao.buscarPorGrupo(grupo);
    }

    public void listarTodas() {
        List<Selecao> list = dao.carregaLista();
        System.out.printf("Lista seleções:\n\n");
        for (Selecao a : list) {
            System.out.printf("%s\n\n", a.getPais());
        }
    }

    public void editarTecnicoSelecao(Selecao selecao, String novo_tecnico) {
        Selecao s = dao.buscarPorPais(selecao.getPais());
        if (s == null) throw new SelecaoNaoEncontradaException("Seleção não encontrada: " + selecao);
        s.setTecnico(novo_tecnico);
        dao.atualizaSelecao(s);
    }

    public void editarGrupoSelecao(Selecao selecao, String novo_grupo) {
        Selecao s = dao.buscarPorPais(selecao.getPais());
        if (s == null) throw new SelecaoNaoEncontradaException("Seleção não encontrada: " + selecao);
        s.setGrupo(novo_grupo);
        dao.atualizaSelecao(s);
    }
}
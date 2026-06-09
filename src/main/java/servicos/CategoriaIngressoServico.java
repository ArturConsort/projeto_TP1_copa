package servicos;

import modelo.classes.CategoriaIngresso;
import modelo.enumerations.TipoPerfil;
import persistencia.CategoriaIngressoDAO;
import servicos.usuario.SessaoUsuario;
import modelo.classes.Usuario;
import modelo.excecoes.AcessoNegadoException;

import java.util.ArrayList;
import java.util.List;

public class CategoriaIngressoServico {

    private CategoriaIngressoDAO dao;

    public CategoriaIngressoServico() {
        this.dao = new CategoriaIngressoDAO();
    }

    // ------- cadastro, edicao e remocao ------- //

    public void cadastrar(CategoriaIngresso novaCategoria) throws AcessoNegadoException {

        verificarPermissao(TipoPerfil.ADMINISTRADOR);

        if (dao.buscarPorNome(novaCategoria.getNome()) != null) {
            throw new IllegalArgumentException("Já existe uma categoria com o nome: " + novaCategoria.getNome());
        }

        if (novaCategoria.getPreco() < 0) {
            throw new IllegalArgumentException("O preço da categoria não pode ser negativo");
        }

        if (novaCategoria.getEstoque() < 0) {
            throw new IllegalArgumentException("O estoque da categoria não pode ser negativo");
        }

        dao.salvar(novaCategoria);
    }


    public void atualizarPreco(String nome, double novoPreco) throws AcessoNegadoException {

        verificarPermissao(TipoPerfil.ADMINISTRADOR);

        CategoriaIngresso categoria = dao.buscarPorNome(nome);
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria não encontrada: " + nome);
        }

        if (novoPreco < 0) {
            throw new IllegalArgumentException("O novo preço não pode ser negativo");
        }

        categoria.atualizarPreco(novoPreco);
        dao.atualizar(categoria);
    }


    public void remover(String nome) throws AcessoNegadoException {
        verificarPermissao(TipoPerfil.ADMINISTRADOR);
        dao.remover(nome);
    }




    // ------- metodos de busca ------- //

    public CategoriaIngresso buscarPorNome(String nome) {
        return dao.buscarPorNome(nome);
    }


    public List<CategoriaIngresso> pesquisar(String nome, Boolean comVagas) { // esses criterios sao opcionais, passar null para ignorar algum deles
        List<CategoriaIngresso> resultado = new ArrayList<>();

        for (CategoriaIngresso c : dao.carregaLista()) {
            boolean nomeOk     = nome      == null   ||   nome.equals(c.getNome());
            boolean vagasOk    = comVagas  == null   ||   comVagas.equals(c.temVagasDisponiveis());

            if (nomeOk && vagasOk) resultado.add(c);
        }

        return resultado;
    }




    // ------- metodos privados auxiliares ------- //

    private void verificarPermissao(TipoPerfil perfilRequisitado) {
        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (logado == null || logado.getPerfil() != perfilRequisitado) {
            throw new AcessoNegadoException("Acesso negado: esse usuario não tem permissao para fazer essa ação");
        }
    }
}
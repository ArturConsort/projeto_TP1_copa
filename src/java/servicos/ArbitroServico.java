package src.java.servicos;

import src.java.modelo.classes.Arbitro;
import src.java.modelo.classes.Usuario;
import src.java.modelo.enumerations.CategoriaArbitro;
import src.java.modelo.enumerations.TipoPerfil;
import src.java.modelo.excecoes.AcessoNegadoException;
import src.java.modelo.excecoes.arbitro.ArbitroJaCadastradoException;
import src.java.modelo.excecoes.arbitro.ArbitroNaoEncontradoException;
import src.java.persistencia.ArbitroDAO;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ArbitroServico {

    private final ArbitroDAO arbitroDAO;

    public ArbitroServico(ArbitroDAO arbitroDAO) {
        this.arbitroDAO = arbitroDAO;
    }

    public void cadastrarArbitro(Usuario solicitante, String nome, int idade,
                                 CategoriaArbitro categoria, int experiencia,
                                 String nacionalidade)
            throws AcessoNegadoException, ArbitroJaCadastradoException, IOException {

        verificarPermissao(solicitante, TipoPerfil.ADMINISTRADOR, TipoPerfil.ORGANIZADOR);

        Optional<Arbitro> existente = arbitroDAO.buscarPorNome(nome);
        if (existente.isPresent()) {
            throw new ArbitroJaCadastradoException(nome);
        }

        Arbitro arbitro = new Arbitro(nome, idade, categoria, experiencia, nacionalidade);
        arbitroDAO.salvar(arbitro);
    }

    public void removerArbitro(Usuario solicitante, String nome)
            throws AcessoNegadoException, ArbitroNaoEncontradoException, IOException {

        verificarPermissao(solicitante, TipoPerfil.ADMINISTRADOR, TipoPerfil.ORGANIZADOR);

        Optional<Arbitro> existente = arbitroDAO.buscarPorNome(nome);
        if (existente.isEmpty()) {
            throw new ArbitroNaoEncontradoException(nome);
        }

        arbitroDAO.remover(nome);
    }

    public Arbitro buscarPorNome(String nome)
            throws ArbitroNaoEncontradoException, IOException {

        return arbitroDAO.buscarPorNome(nome)
                .orElseThrow(() -> new ArbitroNaoEncontradoException(nome));
    }

    public List<Arbitro> listarArbitros() throws IOException {
        return arbitroDAO.carregaLista();
    }
}
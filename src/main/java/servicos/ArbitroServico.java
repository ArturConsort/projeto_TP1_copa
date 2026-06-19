package servicos;

import modelo.classes.Arbitro;
import modelo.classes.Usuario;
import modelo.enumerations.CategoriaArbitro;
import modelo.enumerations.TipoPerfil;
import modelo.excecoes.AcessoNegadoException;
import modelo.excecoes.arbitro.ArbitroJaCadastradoException;
import modelo.excecoes.arbitro.ArbitroNaoEncontradoException;
import persistencia.ArbitroDAO;
import servicos.usuario.SessaoUsuario;
import servicos.Servico;

import java.io.IOException;
import java.util.List;


public class ArbitroServico extends Servico{

    private final ArbitroDAO arbitroDAO;

    public ArbitroServico(ArbitroDAO arbitroDAO) {
        this.arbitroDAO = arbitroDAO;
    }

    public void cadastrarArbitro(String nome, int idade, CategoriaArbitro categoria, int experiencia, String nacionalidade) throws AcessoNegadoException, ArbitroJaCadastradoException, IOException {

        verificarPermissao(TipoPerfil.ADMINISTRADOR, TipoPerfil.ORGANIZADOR);

        Arbitro existente = arbitroDAO.buscarPorNome(nome);

        if (existente != null) {
            throw new ArbitroJaCadastradoException(nome);
        }

        Arbitro arbitro = new Arbitro(nome, idade, categoria, experiencia, nacionalidade);
        arbitroDAO.salvar(arbitro);
    }

    public void editarArbitro(String nomeOriginal, int idade, CategoriaArbitro categoria, int experiencia, String nacionalidade) throws AcessoNegadoException, ArbitroNaoEncontradoException, IOException {

        verificarPermissao(TipoPerfil.ADMINISTRADOR, TipoPerfil.ORGANIZADOR);

        Arbitro existente = arbitroDAO.buscarPorNome(nomeOriginal);

        if (existente == null) {
            throw new ArbitroNaoEncontradoException(nomeOriginal);
        }

        existente.setIdade(idade);
        existente.setCategoria(categoria);
        existente.setExperiencia(experiencia);
        existente.setNacionalidade(nacionalidade);

        arbitroDAO.atualizar(existente);
    }

    public void removerArbitro(String nome) throws AcessoNegadoException, ArbitroNaoEncontradoException, IOException {

        verificarPermissao(TipoPerfil.ADMINISTRADOR, TipoPerfil.ORGANIZADOR);

        Arbitro arbitro = arbitroDAO.buscarPorNome(nome);

        if (arbitro == null) {
            throw new ArbitroNaoEncontradoException(nome);
        }

        arbitroDAO.remover(nome);
    }

    public Arbitro buscarPorNome(String nome) throws ArbitroNaoEncontradoException, IOException {

        Arbitro arbitro = arbitroDAO.buscarPorNome(nome);

        if (arbitro == null) {
            throw new ArbitroNaoEncontradoException(nome);
        }

        return arbitro;
    }

    public List<Arbitro> listarArbitros() throws IOException {
        return arbitroDAO.carregaLista();
    }
}
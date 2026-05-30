package src.java.servicos;

import src.java.modelo.classes.Ingresso;
import src.java.modelo.classes.Usuario;
import src.java.modelo.classes.Venda;
import src.java.modelo.enumerations.TipoPerfil;
import src.java.persistencia.VendaDAO;
import src.java.servicos.usuario.SessaoUsuario;
import src.java.modelo.excecoes.AcessoNegadoException;

import java.util.ArrayList;
import java.util.List;

public class VendaServico {

    private VendaDAO dao;

    public VendaServico() {
        this.dao = new VendaDAO();
    }

    // ------- abertura e gerenciamento de venda ------- //

    public void cadastrar(Venda novaVenda) throws AcessoNegadoException {

        verificarPermissao(TipoPerfil.OPERADOR);

        if (novaVenda.getCliente() == null) {
            throw new IllegalArgumentException("A venda deve ter um cliente associado");
        }

        dao.salvar(novaVenda);
    }


    public void adicionarIngresso(String idVenda, Ingresso ingresso) throws AcessoNegadoException {

        verificarPermissao(TipoPerfil.OPERADOR);

        Venda venda = dao.buscarPorId(idVenda);
        if (venda == null) {
            throw new IllegalArgumentException("Venda não encontrada: " + idVenda);
        }

        if ("FINALIZADA".equals(venda.getStatus()) || "CANCELADA".equals(venda.getStatus())) {
            throw new IllegalStateException("Não é possível adicionar ingressos a uma venda " + venda.getStatus().toLowerCase());
        }

        if (ingresso.getCategoria() != null && !ingresso.getCategoria().temVagasDisponiveis()) {
            throw new IllegalStateException("Categoria sem vagas disponíveis: " + ingresso.getCategoria().getNome());
        }

        venda.adicionarIngresso(ingresso);
        dao.atualizar(venda);
    }


    public void finalizarVenda(String idVenda) throws AcessoNegadoException {

        verificarPermissao(TipoPerfil.OPERADOR);

        Venda venda = dao.buscarPorId(idVenda);
        if (venda == null) {
            throw new IllegalArgumentException("Venda não encontrada: " + idVenda);
        }

        if (venda.getIngressos().isEmpty()) {
            throw new IllegalStateException("Não é possível finalizar uma venda sem ingressos");
        }

        venda.finalizarVenda();
        dao.atualizar(venda);
    }


    public void cancelarVenda(String idVenda) throws AcessoNegadoException {

        verificarPermissao(TipoPerfil.ADMINISTRADOR);

        Venda venda = dao.buscarPorId(idVenda);
        if (venda == null) {
            throw new IllegalArgumentException("Venda não encontrada: " + idVenda);
        }

        venda.setStatus("CANCELADA");
        dao.atualizar(venda);
    }

    public void remover(String idVenda) throws AcessoNegadoException {
        verificarPermissao(TipoPerfil.ADMINISTRADOR);
        dao.remover(idVenda);
    }

    // ------- metodos de busca ------- //

    public Venda buscarPorId(String idVenda) {
        return dao.buscarPorId(idVenda);
    }

    public List<Venda> pesquisar(Usuario cliente, String status) { // esses criterios sao opcionais, passar null para ignorar algum deles
        List<Venda> resultado = new ArrayList<>();

        for (Venda v : dao.carregaLista()) {
            boolean clienteOk = cliente == null   ||   (v.getCliente() != null && v.getCliente().getLogin().equals(cliente.getLogin()));
            boolean statusOk  = status  == null   ||   status.equals(v.getStatus());

            if (clienteOk && statusOk) resultado.add(v);
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
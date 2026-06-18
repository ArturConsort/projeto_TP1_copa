package servicos;

import modelo.classes.Ingresso;
import modelo.classes.CategoriaIngresso;
import modelo.classes.Partida;
import modelo.classes.Usuario;
import modelo.classes.Venda;
import modelo.enumerations.TipoPerfil;
import modelo.excecoes.AcessoNegadoException;
import persistencia.PartidaDAO;
import persistencia.VendaDAO;
import servicos.usuario.SessaoUsuario;
import servicos.Servico;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VendaServico extends Servico{

    private VendaDAO dao;
    private PartidaDAO partidaDAO;

    public VendaServico() {
        this.dao = new VendaDAO();
        this.partidaDAO = new PartidaDAO();
    }

    // ------- abertura e gerenciamento de venda ------- //

    public void cadastrar(Venda novaVenda) throws AcessoNegadoException {

        verificarPermissaoMultipla(TipoPerfil.OPERADOR, TipoPerfil.ADMINISTRADOR);

        if (novaVenda.getCliente() == null) {
            throw new IllegalArgumentException("A venda deve ter um cliente associado");
        }

        dao.salvar(novaVenda);
    }


    public void adicionarIngresso(String idVenda, Ingresso ingresso) throws AcessoNegadoException {

        verificarPermissaoMultipla(TipoPerfil.OPERADOR, TipoPerfil.ADMINISTRADOR);

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

        // Regra: ingressos não podem ser vendidos após o início da partida
        Partida partida = ingresso.getPartida();
        if (partida != null && partida.getData() != null && partida.getHorario() != null) {
            try {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                LocalDateTime inicioPartida = LocalDateTime.parse(
                        partida.getData() + " " + partida.getHorario(), fmt);
                if (LocalDateTime.now().isAfter(inicioPartida)) {
                    throw new IllegalStateException(
                            "Não é possível vender ingressos após o início da partida ("
                            + partida.getData() + " " + partida.getHorario() + ").");
                }
            } catch (java.time.format.DateTimeParseException ignored) {
                // Se o formato de data/hora estiver inesperado, não bloqueia
            }
        }

        venda.adicionarIngresso(ingresso);
        dao.atualizar(venda);
    }


    public void finalizarVenda(String idVenda) throws AcessoNegadoException {

        verificarPermissaoMultipla(TipoPerfil.OPERADOR, TipoPerfil.ADMINISTRADOR);

        Venda venda = dao.buscarPorId(idVenda);
        if (venda == null) {
            throw new IllegalArgumentException("Venda não encontrada: " + idVenda);
        }

        if (venda.getIngressos().isEmpty()) {
            throw new IllegalStateException("Não é possível finalizar uma venda sem ingressos");
        }

        // finalizarVenda() reduz o estoque nos objetos CategoriaIngresso em memória.
        venda.finalizarVenda();

        // ── CORREÇÃO: persiste o estoque atualizado dentro da própria Partida ──
        // As CategoriaIngresso vivem dentro do objeto Partida (partidas.dat).
        // Precisamos recarregar a Partida do arquivo, encontrar a categoria pelo nome,
        // aplicar a redução e salvar a Partida atualizada de volta.
        for (Ingresso ingresso : venda.getIngressos()) {
            CategoriaIngresso categoriaVenda = ingresso.getCategoria();
            Partida partidaVenda = ingresso.getPartida();
            if (categoriaVenda == null || partidaVenda == null) continue;

            // Recarrega a partida do arquivo (estado persistido mais recente)
            Partida partidaPersistida = partidaDAO.buscarPorNumero(partidaVenda.getNumeroPartidas());
            if (partidaPersistida == null) continue;

            // Encontra a categoria correspondente dentro da partida pelo nome
            CategoriaIngresso categoriaPersistida =
                    partidaPersistida.buscarCategoriaPorNome(categoriaVenda.getNome());
            if (categoriaPersistida == null) continue;

            // Decrementa 1 por ingresso (já validado que há estoque antes da compra)
            if (categoriaPersistida.getEstoque() > 0) {
                categoriaPersistida.reduzirEstoque(1);
            }

            // Persiste a partida com o novo estoque
            partidaDAO.atualizar(partidaPersistida);
        }

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

    public List<Venda> pesquisar(Usuario cliente, String status) { // criterios opcionais, passar null para ignorar
        List<Venda> resultado = new ArrayList<>();

        for (Venda v : dao.carregaLista()) {
            boolean clienteOk = cliente == null || (v.getCliente() != null && v.getCliente().getLogin().equals(cliente.getLogin()));
            boolean statusOk  = status  == null || status.equals(v.getStatus());

            if (clienteOk && statusOk) resultado.add(v);
        }

        return resultado;
    }
}

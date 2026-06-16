package servicos.Partida;

import modelo.classes.*;
import persistencia.PartidaDAO;
import persistencia.ResultadoPartidaDAO;
import persistencia.SelecaoDAO;

import java.util.List;

public class ResultadoPartidaService {

    private ResultadoPartidaDAO resultadoDAO;
    private PartidaDAO partidaDAO;
    private ClassificacaoService classificacaoService = new ClassificacaoService();
    private SelecaoDAO selecaoDAO = new SelecaoDAO();
    public ResultadoPartidaService() {
        this.resultadoDAO = new ResultadoPartidaDAO();
        this.partidaDAO = new PartidaDAO();
    }

    // Chamado pela tela para popular o JComboBox de partidas pendentes
    public List<Partida> listarPartidasPendentes() {
        return partidaDAO.carregaLista(); // só as que ainda não têm resultado
    }

    public void cadastrarResultado(Partida partida,
                                   Selecao timeVencedor,
                                   Selecao timePerdedor,
                                   String placar,
                                   String placarPenaltis,
                                   String cartoesAmarelosStr,
                                   String cartoesVermelhosStr) throws Exception {

        // --- Validações ---
        if (partida == null) {
            throw new Exception("Selecione uma partida!");
        }
        if (!partida.isFinalizada()) {
            throw new Exception("Só é possível registrar resultado de partidas finalizadas!\n"
                    + "Status atual: " + partida.getStatus());
        }
        if (timeVencedor == null || timePerdedor == null) {
            throw new Exception("Selecione o time vencedor e o perdedor!");
        }
        if (timeVencedor.equals(timePerdedor)) {
            throw new Exception("Vencedor e perdedor não podem ser o mesmo time!");
        }

        // Vencedor e perdedor devem ser os times da partida selecionada
        boolean vencedorValido = timeVencedor.equals(partida.getTimeCasa())
                || timeVencedor.equals(partida.getTimeVisitante());
        boolean perdedorValido = timePerdedor.equals(partida.getTimeCasa())
                || timePerdedor.equals(partida.getTimeVisitante());

        if (!vencedorValido || !perdedorValido) {
            throw new Exception("Vencedor e perdedor devem ser os times da partida selecionada!");
        }
        if (placar == null || placar.trim().isEmpty()) {
            throw new Exception("O placar é obrigatório!");
        }
        if (cartoesAmarelosStr.trim().isEmpty()) {
            throw new Exception("Informe o número de cartões amarelos!");
        }
        if (cartoesVermelhosStr.trim().isEmpty()) {
            throw new Exception("Informe o número de cartões vermelhos!");
        }

        int cartoesAmarelos;
        int cartoesVermelhos;
        try {
            cartoesAmarelos = Integer.parseInt(cartoesAmarelosStr.trim());
            cartoesVermelhos = Integer.parseInt(cartoesVermelhosStr.trim());
        } catch (NumberFormatException e) {
            throw new Exception("Cartões devem ser números inteiros!");
        }

        if (cartoesAmarelos < 0 || cartoesVermelhos < 0) {
            throw new Exception("Número de cartões não pode ser negativo!");
        }

        // Verifica se essa partida já tem resultado cadastrado
        if (resultadoDAO.buscarPorNumeroPartida(partida.getNumeroPartidas()) != null) {
            throw new Exception("Essa partida já tem resultado cadastrado!");
        }

        // --- Monta e salva o resultado ---
        ResultadoPartida resultado = new ResultadoPartida(
                timeVencedor, timePerdedor, partida,
                placar.trim(),
                placarPenaltis != null ? placarPenaltis.trim() : "",
                cartoesAmarelos,
                cartoesVermelhos
        );

        resultadoDAO.salvar(resultado);
        // --- Remove a partida de partidas.dat pois já foi finalizada ---
        partidaDAO.remover(partida.getNumeroPartidas());
        classificacaoService.processarResultado(resultado);
        classificacaoService.processarEliminacao(resultado, selecaoDAO);

        if (partida.getFase() == modelo.enumerations.FasePartida.FASE_DE_GRUPOS) {
            try {
                classificacaoService.processarEliminacaoGrupos(selecaoDAO);
            } catch (Exception e) {
                // Grupos ainda não terminaram todos — sem problema, continua
            }
        }
    }

    public List<ResultadoPartida> listarResultados() {
        return resultadoDAO.carregaLista();
    }
}
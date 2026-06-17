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
                                   boolean empate,        // ← novo parâmetro
                                   String placar,
                                   String placarPenaltis,
                                   String cartoesAmarelosStr,
                                   String cartoesVermelhosStr) throws Exception {

        if (partida == null) {
            throw new Exception("Selecione uma partida!");
        }
        if (!partida.isFinalizada()) {
            throw new Exception("Só é possível registrar resultado de partidas finalizadas!\n"
                    + "Status atual: " + partida.getStatus());
        }

        // Valida formato do placar
        if (placar == null || placar.trim().isEmpty()) {
            throw new Exception("O placar é obrigatório!");
        }
        if (!placar.trim().matches("^\\d+x\\d+$")) {
            throw new Exception("Placar inválido! Use o formato '2x1' (ex: 2x1, 0x0).");
        }

        // Extrai gols do placar
        String[] partesP = placar.trim().split("x");
        int golsVencedor = Integer.parseInt(partesP[0].trim());
        int golsPerdedor = Integer.parseInt(partesP[1].trim());

        if (empate) {
            // Empate: placar deve ser igual
            if (golsVencedor != golsPerdedor) {
                throw new Exception(
                        "Em um empate o placar deve ser igual (ex: 1x1)!"
                );
            }
            // Empate só é permitido na fase de grupos
            if (partida.getFase() != modelo.enumerations.FasePartida.FASE_DE_GRUPOS) {
                throw new Exception(
                        "Empate não é permitido no mata-mata! " +
                                "Informe o vencedor e o placar de pênaltis se necessário."
                );
            }
        } else {
            if (timeVencedor == null || timePerdedor == null) {
                throw new Exception("Selecione o time vencedor e o perdedor!");
            }

            if (timeVencedor.equals(timePerdedor)) {
                throw new Exception("Vencedor e perdedor não podem ser o mesmo time!");
            }

            boolean vencedorValido =
                    timeVencedor.equals(partida.getTimeCasa()) ||
                            timeVencedor.equals(partida.getTimeVisitante());

            boolean perdedorValido =
                    timePerdedor.equals(partida.getTimeCasa()) ||
                            timePerdedor.equals(partida.getTimeVisitante());

            if (!vencedorValido || !perdedorValido) {
                throw new Exception(
                        "Vencedor e perdedor devem ser os times da partida selecionada!"
                );
            }

            if (partida.getFase() == modelo.enumerations.FasePartida.FASE_DE_GRUPOS) {

                if (golsVencedor <= golsPerdedor) {
                    throw new Exception(
                            "O placar do vencedor (" + golsVencedor +
                                    ") deve ser maior que o do perdedor (" +
                                    golsPerdedor + ")!"
                    );
                }

            } else {

                // Mata-mata: vencedor não pode ter MENOS gols
                if (golsVencedor < golsPerdedor) {
                    throw new Exception(
                            "O placar do vencedor não pode ser menor que o do perdedor!"
                    );
                }

                // Se empatou no tempo normal, precisa dos pênaltis
                if (golsVencedor == golsPerdedor &&
                        (placarPenaltis == null || placarPenaltis.trim().isEmpty())) {

                    throw new Exception(
                            "No mata-mata, em caso de empate no tempo normal, informe o placar dos pênaltis!"
                    );
                }
            }
        }

        // Valida pênaltis se houver
        if (placarPenaltis != null && !placarPenaltis.trim().isEmpty()) {

            if (!placarPenaltis.trim().matches("^\\d+x\\d+$")) {
                throw new Exception("Placar de pênaltis inválido! Use o formato '4x3'.");
            }

            if (partida.getFase() == modelo.enumerations.FasePartida.FASE_DE_GRUPOS) {
                throw new Exception("Não existe disputa de pênaltis na fase de grupos!");
            }

            if (golsVencedor != golsPerdedor) {
                throw new Exception(
                        "Só é permitido informar pênaltis quando houver empate no tempo normal!"
                );
            }

            String[] partesPen = placarPenaltis.trim().split("x");
            int penVencedor = Integer.parseInt(partesPen[0].trim());
            int penPerdedor = Integer.parseInt(partesPen[1].trim());

            if (penVencedor <= penPerdedor) {
                throw new Exception(
                        "O placar de pênaltis do vencedor (" + penVencedor +
                                ") deve ser maior que o do perdedor (" +
                                penPerdedor + ")!"
                );
            }
        }



        int cartoesAmarelos;
        int cartoesVermelhos;
        try {
            cartoesAmarelos  = Integer.parseInt(cartoesAmarelosStr.trim());
            cartoesVermelhos = Integer.parseInt(cartoesVermelhosStr.trim());
        } catch (NumberFormatException e) {
            throw new Exception("Cartões devem ser números inteiros!");
        }
        if (cartoesAmarelos < 0 || cartoesVermelhos < 0) {
            throw new Exception("Número de cartões não pode ser negativo!");
        }

        if (resultadoDAO.buscarPorNumeroPartida(partida.getNumeroPartidas()) != null) {
            throw new Exception("Essa partida já tem resultado cadastrado!");
        }

        // No empate, vencedor e perdedor são ambos os times (sem distinção)
        Selecao vencedorFinal = empate ? null : timeVencedor;
        Selecao perdedorFinal = empate ? null : timePerdedor;

        ResultadoPartida resultado = new ResultadoPartida(
                vencedorFinal, perdedorFinal, partida,
                placar.trim(),
                placarPenaltis != null ? placarPenaltis.trim() : "",
                cartoesAmarelos,
                cartoesVermelhos
        );

        resultadoDAO.salvar(resultado);
        partidaDAO.remover(partida.getNumeroPartidas());

        classificacaoService.processarResultado(resultado);

        if (partida.getFase() != modelo.enumerations.FasePartida.FASE_DE_GRUPOS) {
            classificacaoService.processarEliminacao(resultado, selecaoDAO);
        }

        if (partida.getFase() == modelo.enumerations.FasePartida.FASE_DE_GRUPOS) {
            try {
                classificacaoService.processarEliminacaoGrupos(selecaoDAO);
            } catch (Exception e) {
                // Grupos ainda não terminaram todos
            }
        }
    }

    public boolean copiaEncerrada() {
        for (ResultadoPartida r : resultadoDAO.carregaLista()) {
            if (r.getPartida().getFase() == modelo.enumerations.FasePartida.FINAL) {
                return true;
            }
        }
        return false;
    }

    // Apaga partidas e resultados — reinicia a copa
    public void reiniciarCopa() {
        new java.io.File("partidas.dat").delete();
        new java.io.File("resultados.dat").delete();
        new java.io.File("classificacao_grupos.dat").delete();
        Partida.setContPartidas(0);
    }

    public List<ResultadoPartida> listarResultados() {
        return resultadoDAO.carregaLista();
    }
}
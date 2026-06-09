package controllersJavaFX;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import modelo.classes.Partida;
import modelo.classes.ResultadoPartida;
import modelo.enumerations.StatusPartida;
import servicos.Partida.PartidaService;
import servicos.Partida.ResultadoPartidaService;

import java.util.List;
import java.util.stream.Collectors;

public class ListaPartidasController {

    @FXML private TextField campoBuscarTime;
    @FXML private TextField campoBuscarRodada;
    @FXML private TextField campoBuscarData;
    @FXML private VBox      listaPartidas;

    private final PartidaService          partidaService  = new PartidaService();
    private final ResultadoPartidaService resultadoService = new ResultadoPartidaService();

    @FXML
    public void initialize() {
        carregarTodas();
    }

    // Carrega e exibe todas as partidas (pendentes + finalizadas)
    private void carregarTodas() {
        listaPartidas.getChildren().clear();

        // Partidas pendentes (em partidas.dat)
        List<Partida> pendentes = partidaService.listarPartidas();
        for (Partida p : pendentes) {
            listaPartidas.getChildren().add(criarCardPendente(p));
        }

        // Partidas finalizadas (em resultados.dat)
        List<ResultadoPartida> finalizadas = resultadoService.listarResultados();
        for (ResultadoPartida r : finalizadas) {
            listaPartidas.getChildren().add(criarCardFinalizado(r));
        }
    }

    @FXML
    private void aoBuscar() {
        String time   = campoBuscarTime.getText().trim().toLowerCase();
        String rodada = campoBuscarRodada.getText().trim().toLowerCase();
        String data   = campoBuscarData.getText().trim();

        listaPartidas.getChildren().clear();

        // Filtra partidas pendentes
        List<Partida> pendentes = partidaService.listarPartidas().stream()
                .filter(p -> (time.isEmpty()
                        || p.getTimeCasa().getPais().toLowerCase().contains(time)
                        || p.getTimeVisitante().getPais().toLowerCase().contains(time)))
                .filter(p -> data.isEmpty() || p.getData().contains(data))
                .collect(Collectors.toList());

        for (Partida p : pendentes) {
            listaPartidas.getChildren().add(criarCardPendente(p));
        }

        // Filtra partidas finalizadas
        List<ResultadoPartida> finalizadas = resultadoService.listarResultados().stream()
                .filter(r -> (time.isEmpty()
                        || r.getPartida().getTimeCasa().getPais().toLowerCase().contains(time)
                        || r.getPartida().getTimeVisitante().getPais().toLowerCase().contains(time)))
                .filter(r -> data.isEmpty() || r.getPartida().getData().contains(data))
                .collect(Collectors.toList());

        for (ResultadoPartida r : finalizadas) {
            listaPartidas.getChildren().add(criarCardFinalizado(r));
        }
    }

    @FXML
    private void aoLimpar() {
        campoBuscarTime.clear();
        campoBuscarRodada.clear();
        campoBuscarData.clear();
        carregarTodas();
    }

    // Card para partida ainda sem resultado
    private HBox criarCardPendente(Partida p) {
        // Status no centro
        String statusTxt;
        String statusStyle;
        if (p.getStatus() == StatusPartida.EM_ANDAMENTO) {
            statusTxt   = "Em andamento";
            statusStyle = "card-status-andamento";
        } else {
            statusTxt   = "Agendado";
            statusStyle = "card-status-agendado";
        }

        Label lblStatus = new Label(statusTxt);
        lblStatus.getStyleClass().add(statusStyle);

        return montarCard(
                p.getTimeCasa().getPais(),
                statusTxt, statusStyle,
                p.getTimeVisitante().getPais(),
                p.getData() + "\n" + p.getHorario()
        );
    }

    // Card para partida finalizada com placar
    private HBox criarCardFinalizado(ResultadoPartida r) {
        Partida p = r.getPartida();
        return montarCard(
                p.getTimeCasa().getPais(),
                r.getPlacar(),
                "card-placar",
                p.getTimeVisitante().getPais(),
                p.getData() + "\n" + p.getHorario()
        );
    }

    // Monta o layout do card: Time Casa | Centro | Time Visitante | Data
    private HBox montarCard(String timeCasa, String centro, String centroStyle,
                            String timeVisitante, String dataHora) {
        HBox card = new HBox(16);
        card.getStyleClass().add("card-partida");
        card.setAlignment(Pos.CENTER_LEFT);

        Label lblCasa = new Label(timeCasa);
        lblCasa.getStyleClass().add("card-time");
        HBox.setHgrow(lblCasa, Priority.ALWAYS);

        Label lblCentro = new Label(centro);
        lblCentro.getStyleClass().add(centroStyle);
        lblCentro.setMinWidth(100);
        lblCentro.setAlignment(Pos.CENTER);

        Label lblVisitante = new Label(timeVisitante);
        lblVisitante.getStyleClass().add("card-time");
        lblVisitante.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(lblVisitante, Priority.ALWAYS);

        Label lblData = new Label(dataHora);
        lblData.getStyleClass().add("card-data");
        lblData.setMinWidth(80);
        lblData.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(lblCasa, lblCentro, lblVisitante, lblData);
        return card;
    }
}
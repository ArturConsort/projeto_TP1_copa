package controllersJavaFX;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelo.classes.Arbitro;
import modelo.classes.Estadio;
import modelo.classes.Partida;
import modelo.classes.Selecao;
import modelo.enumerations.FasePartida;
import servicos.Partida.PartidaService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CadastroPartidaController {

    @FXML private ComboBox<Selecao>     comboTimeCasa;
    @FXML private ComboBox<Selecao>     comboTimeVisitante;
    @FXML private ComboBox<Estadio>     comboEstadio;
    @FXML private ComboBox<Arbitro>     comboArbitro;
    @FXML private ComboBox<FasePartida> comboFase;
    @FXML private TextField             campoCidade;
    @FXML private DatePicker            campoData;   // ← calendário visual
    @FXML private TextField             campoHorario;
    @FXML private Label                 labelFeedback;

    private final PartidaService service = new PartidaService();
    private Partida partidaEmEdicao = null;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        comboTimeCasa.setItems(FXCollections.observableArrayList(service.listarSelecoes()));
        comboTimeVisitante.setItems(FXCollections.observableArrayList(service.listarSelecoes()));
        comboEstadio.setItems(FXCollections.observableArrayList(service.listarEstadios()));
        comboArbitro.setItems(FXCollections.observableArrayList(service.listarArbitros()));
        comboFase.setItems(FXCollections.observableArrayList(FasePartida.values()));

        // Calendário no formato brasileiro
        campoData.setConverter(new javafx.util.StringConverter<LocalDate>() {
            @Override public String toString(LocalDate d) {
                return d != null ? d.format(FMT) : "";
            }
            @Override public LocalDate fromString(String s) {
                return (s != null && !s.isEmpty()) ? LocalDate.parse(s, FMT) : null;
            }
        });
    }

    @FXML
    private void aoSalvar() {
        try {
            Selecao     timeCasa      = comboTimeCasa.getValue();
            Selecao     timeVisitante = comboTimeVisitante.getValue();
            Estadio     estadio       = comboEstadio.getValue();
            Arbitro     arbitro       = comboArbitro.getValue();
            FasePartida fase          = comboFase.getValue();
            String      cidade        = campoCidade.getText();
            String      horario       = campoHorario.getText();
            LocalDate   dataLocal     = campoData.getValue();

            // Converte LocalDate para String dd/MM/yyyy
            String data = dataLocal != null ? dataLocal.format(FMT) : "";

            if (partidaEmEdicao != null) {
                partidaEmEdicao.setTimeCasa(timeCasa);
                partidaEmEdicao.setTimeVisitante(timeVisitante);
                partidaEmEdicao.setEstadio(estadio);
                partidaEmEdicao.setArbitroPrincipal(arbitro);
                partidaEmEdicao.setFase(fase);
                partidaEmEdicao.setCidade(cidade);
                partidaEmEdicao.setData(data);
                partidaEmEdicao.setHorario(horario);
                service.atualizarPartida(partidaEmEdicao);
                feedback("Partida atualizada com sucesso!", true);
            } else {
                service.cadastrarPartida(timeCasa, timeVisitante, estadio,
                        cidade, data, horario, arbitro, fase);
                feedback("Partida cadastrada com sucesso!", true);
            }

            limpar();

        } catch (Exception e) {
            feedback(e.getMessage(), false);
        }
    }

    @FXML
    private void aoEditar() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Editar Partida");
        dialog.setHeaderText("Informe o número da partida:");
        dialog.showAndWait().ifPresent(str -> {
            try {
                int numero = Integer.parseInt(str.trim());
                Partida p = service.listarPartidas().stream()
                        .filter(x -> x.getNumeroPartidas() == numero)
                        .findFirst().orElse(null);

                if (p == null) { feedback("Partida não encontrada!", false); return; }

                partidaEmEdicao = p;
                comboTimeCasa.setValue(p.getTimeCasa());
                comboTimeVisitante.setValue(p.getTimeVisitante());
                comboEstadio.setValue(p.getEstadio());
                comboArbitro.setValue(p.getArbitroPrincipal());
                comboFase.setValue(p.getFase());
                campoCidade.setText(p.getCidade());
                campoHorario.setText(p.getHorario());
                // Converte String para LocalDate para o DatePicker
                if (p.getData() != null && !p.getData().isEmpty()) {
                    campoData.setValue(LocalDate.parse(p.getData(), FMT));
                }
                feedback("Campos preenchidos. Altere e clique em Salvar.", true);

            } catch (NumberFormatException e) {
                feedback("Número inválido!", false);
            }
        });
    }

    @FXML
    private void aoExcluir() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Excluir Partida");
        dialog.setHeaderText("Informe o número da partida a excluir:");
        dialog.showAndWait().ifPresent(str -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Tem certeza que deseja excluir esta partida?",
                    ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.YES) {
                    try {
                        service.removerPartida(Integer.parseInt(str.trim()));
                        feedback("Partida excluída com sucesso!", true);
                        limpar();
                    } catch (Exception e) {
                        feedback(e.getMessage(), false);
                    }
                }
            });
        });
    }

    private void limpar() {
        comboTimeCasa.setValue(null);
        comboTimeVisitante.setValue(null);
        comboEstadio.setValue(null);
        comboArbitro.setValue(null);
        comboFase.setValue(null);
        campoCidade.clear();
        campoData.setValue(null);
        campoHorario.clear();
        partidaEmEdicao = null;
    }

    private void feedback(String msg, boolean sucesso) {
        labelFeedback.setText(msg);
        labelFeedback.getStyleClass().setAll(sucesso ? "label-feedback-ok" : "label-feedback-erro");
    }
}
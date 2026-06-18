package controllersJavaFX;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
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
    @FXML private ComboBox<FasePartida> comboFase;
    @FXML private DatePicker            campoData;
    @FXML private TextField             campoHorario;
    @FXML private Label                 labelFeedback;

    private final PartidaService service = new PartidaService();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        comboTimeCasa.setItems(FXCollections.observableArrayList(service.listarSelecoes()));
        comboTimeVisitante.setItems(FXCollections.observableArrayList(service.listarSelecoes()));
        comboEstadio.setItems(FXCollections.observableArrayList(service.listarEstadios()));
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
            FasePartida fase          = comboFase.getValue();
            String      horario       = campoHorario.getText();
            LocalDate   dataLocal     = campoData.getValue();
            String      data          = dataLocal != null ? dataLocal.format(FMT) : "";

            // cidade vazia pois foi removida da tela — passa string vazia
            service.cadastrarPartida(timeCasa, timeVisitante, estadio,
                    "", data, horario, null, fase);
            feedback("Partida cadastrada com sucesso!", true);
            limpar();

        } catch (Exception e) {
            feedback(e.getMessage(), false);
        }
    }

    // Volta para a tela Home (menu principal)
    @FXML
    private void aoVoltar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu.fxml"));
            Stage stage = (Stage) comboTimeCasa.getScene().getWindow();
            double w = stage.getWidth();
            double h = stage.getHeight();
            stage.setScene(new Scene(loader.load()));
            stage.setWidth(w);
            stage.setHeight(h);
            stage.setTitle("Copa do Mundo 2026");
        } catch (Exception e) {
            feedback("Erro ao voltar: " + e.getMessage(), false);
        }
    }

    private void limpar() {
        comboTimeCasa.setValue(null);
        comboTimeVisitante.setValue(null);
        comboEstadio.setValue(null);
        comboFase.setValue(null);
        campoData.setValue(null);
        campoHorario.clear();
    }

    private void feedback(String msg, boolean sucesso) {
        labelFeedback.setText(msg);
        labelFeedback.getStyleClass().setAll(sucesso ? "label-feedback-ok" : "label-feedback-erro");
    }
}

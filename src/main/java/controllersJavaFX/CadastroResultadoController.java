package controllersJavaFX;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelo.classes.Partida;
import modelo.classes.Selecao;
import servicos.Partida.ResultadoPartidaService;

public class CadastroResultadoController {

    @FXML private ComboBox<Partida> comboPartida;
    @FXML private ComboBox<Selecao> comboVencedor;
    @FXML private ComboBox<Selecao> comboPerdedor;
    @FXML private TextField         campoPlacar;
    @FXML private TextField         campoPlacarPenaltis;
    @FXML private TextField         campoCartoesAmarelos;
    @FXML private TextField         campoCartoesVermelhos;
    @FXML private Label             labelFeedback;

    private final ResultadoPartidaService service = new ResultadoPartidaService();

    @FXML
    public void initialize() {
        carregarPartidas();
        // Quando trocar a partida, atualiza os times disponíveis
        comboPartida.setOnAction(e -> atualizarTimes());
    }

    private void carregarPartidas() {
        comboPartida.setItems(
                FXCollections.observableArrayList(service.listarPartidasPendentes())
        );
        if (!comboPartida.getItems().isEmpty()) {
            comboPartida.getSelectionModel().selectFirst();
            atualizarTimes();
        }
    }

    // Só os dois times da partida selecionada ficam disponíveis
    private void atualizarTimes() {
        Partida p = comboPartida.getValue();
        if (p == null) return;
        comboVencedor.setItems(FXCollections.observableArrayList(
                p.getTimeCasa(), p.getTimeVisitante()));
        comboPerdedor.setItems(FXCollections.observableArrayList(
                p.getTimeCasa(), p.getTimeVisitante()));
    }

    @FXML
    private void aoSalvar() {
        try {
            service.cadastrarResultado(
                    comboPartida.getValue(),
                    comboVencedor.getValue(),
                    comboPerdedor.getValue(),
                    campoPlacar.getText(),
                    campoPlacarPenaltis.getText(),
                    campoCartoesAmarelos.getText(),
                    campoCartoesVermelhos.getText()
            );
            feedback("Resultado registrado com sucesso!", true);
            limpar();
            carregarPartidas();

        } catch (Exception e) {
            feedback(e.getMessage(), false);
        }
    }

    @FXML
    private void aoVoltar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/lista_partidas.fxml"));
            Stage stage = (Stage) comboPartida.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 1200, 700));
        } catch (Exception e) {
            feedback("Erro ao navegar: " + e.getMessage(), false);
        }
    }

    private void limpar() {
        comboPartida.setValue(null);
        comboVencedor.setValue(null);
        comboPerdedor.setValue(null);
        campoPlacar.clear();
        campoPlacarPenaltis.clear();
        campoCartoesAmarelos.clear();
        campoCartoesVermelhos.clear();
    }

    private void feedback(String msg, boolean sucesso) {
        labelFeedback.setText(msg);
        labelFeedback.getStyleClass().setAll(sucesso ? "label-feedback-ok" : "label-feedback-erro");
    }
}
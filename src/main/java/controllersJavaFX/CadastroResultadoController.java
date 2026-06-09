package controllersJavaFX;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

    private final ResultadoPartidaService service = new ResultadoPartidaService();

    @FXML
    public void initialize() {
        carregarPartidas();

        // Quando o usuário troca a partida, atualiza os times disponíveis
        comboPartida.setOnAction(e -> atualizarTimes());
    }

    private void carregarPartidas() {
        comboPartida.setItems(
                FXCollections.observableArrayList(service.listarPartidasPendentes())
        );
        // Seleciona a primeira e já carrega os times
        if (!comboPartida.getItems().isEmpty()) {
            comboPartida.getSelectionModel().selectFirst();
            atualizarTimes();
        }
    }

    // Popula vencedor/perdedor apenas com os dois times da partida escolhida
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

            mostrarSucesso("Resultado registrado com sucesso!");
            limpar();
            carregarPartidas(); // recarrega pois a partida saiu da lista

        } catch (Exception e) {
            mostrarErro(e.getMessage());
        }
    }

    @FXML
    private void aoEditar() {
        mostrarErro("Para editar um resultado, exclua e cadastre novamente.");
    }

    @FXML
    private void aoExcluir() {
        mostrarErro("Exclusão de resultados não é permitida pelo sistema.");
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

    private void mostrarSucesso(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }

    private void mostrarErro(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
}
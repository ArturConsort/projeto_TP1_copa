package controllersJavaFX;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modelo.classes.Partida;
import modelo.enumerations.FasePartida;
import servicos.Partida.PartidaService;

import java.util.List;
import java.util.stream.Collectors;

public class ListaPartidasController {

    @FXML private TextField               campoBuscarTime;
    @FXML private ComboBox<FasePartida>   comboBuscarFase;
    @FXML private TextField               campoBuscarData;
    @FXML private TableView<Partida>      tabelaPartidas;
    @FXML private TableColumn<Partida, String> colNumero;
    @FXML private TableColumn<Partida, String> colTimeCasa;
    @FXML private TableColumn<Partida, String> colVisitante;
    @FXML private TableColumn<Partida, String> colEstadio;
    @FXML private TableColumn<Partida, String> colData;
    @FXML private TableColumn<Partida, String> colHorario;
    @FXML private TableColumn<Partida, String> colFase;
    @FXML private TableColumn<Partida, String> colStatus;
    @FXML private Label                   labelTotal;

    private final PartidaService service = new PartidaService();

    @FXML
    public void initialize() {
        // Popula ComboBox de fase com todos os valores + opção vazia
        ObservableList<FasePartida> fases = FXCollections.observableArrayList(FasePartida.values());
        comboBuscarFase.setItems(fases);
        comboBuscarFase.setPromptText("Todas as fases");

        // Liga cada coluna ao atributo certo de Partida
        colNumero.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getNumeroPartidas())));
        colTimeCasa.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTimeCasa().getPais()));
        colVisitante.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTimeVisitante().getPais()));
        colEstadio.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getEstadio().getNome()));
        colData.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getData()));
        colHorario.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getHorario()));
        colFase.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getFase().name()));

        // Coluna status com cor via CSS
        colStatus.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStatus().name()));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    // Aplica cor conforme o status
                    switch (status) {
                        case "AGENDADA"     -> getStyleClass().add("status-agendada");
                        case "EM_ANDAMENTO" -> getStyleClass().add("status-andamento");
                        case "FINALIZADA"   -> getStyleClass().add("status-finalizada");
                    }
                }
            }
        });

        carregarTodas();
    }

    private void carregarTodas() {
        List<Partida> lista = service.listarPartidas();
        tabelaPartidas.setItems(FXCollections.observableArrayList(lista));
        labelTotal.setText("Total: " + lista.size() + " partida(s)");
    }

    @FXML
    private void aoBuscar() {
        String time  = campoBuscarTime.getText().trim().toLowerCase();
        String data  = campoBuscarData.getText().trim();
        FasePartida fase = comboBuscarFase.getValue();

        List<Partida> filtrado = service.listarPartidas().stream()
                .filter(p -> time.isEmpty()
                        || p.getTimeCasa().getPais().toLowerCase().contains(time)
                        || p.getTimeVisitante().getPais().toLowerCase().contains(time))
                .filter(p -> data.isEmpty() || p.getData().contains(data))
                .filter(p -> fase == null || p.getFase() == fase)
                .collect(Collectors.toList());

        tabelaPartidas.setItems(FXCollections.observableArrayList(filtrado));
        labelTotal.setText("Total: " + filtrado.size() + " partida(s)");
    }

    @FXML
    private void aoLimpar() {
        campoBuscarTime.clear();
        campoBuscarData.clear();
        comboBuscarFase.setValue(null);
        carregarTodas();
    }

    @FXML
    private void aoExcluir() {
        Partida selecionada = tabelaPartidas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            new Alert(Alert.AlertType.WARNING,
                    "Selecione uma partida na tabela para excluir.", ButtonType.OK).showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Excluir partida " + selecionada.getNumeroPartidas() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    service.removerPartida(selecionada.getNumeroPartidas());
                    carregarTodas();
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK).showAndWait();
                }
            }
        });
    }

    @FXML
    private void aoAtualizar() {
        carregarTodas();
    }
}
package controllersJavaFX;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import modelo.classes.Partida;
import modelo.classes.ResultadoPartida;
import modelo.enumerations.FasePartida;
import modelo.enumerations.StatusPartida;
import servicos.Partida.PartidaService;
import servicos.Partida.ResultadoPartidaService;

import java.util.List;
import java.util.stream.Collectors;

public class ListaPartidasController {

    // --- Tabela de partidas ---
    @FXML private TextField                    campoBuscarTime;
    @FXML private ComboBox<FasePartida>        comboBuscarFase;
    @FXML private TextField                    campoBuscarData;
    @FXML private TableView<Partida>           tabelaPartidas;
    @FXML private TableColumn<Partida, String> colNumero;
    @FXML private TableColumn<Partida, String> colTimeCasa;
    @FXML private TableColumn<Partida, String> colVisitante;
    @FXML private TableColumn<Partida, String> colEstadio;
    @FXML private TableColumn<Partida, String> colData;
    @FXML private TableColumn<Partida, String> colHorario;
    @FXML private TableColumn<Partida, String> colFase;
    @FXML private TableColumn<Partida, String> colStatus;
    @FXML private TableColumn<Partida, Void>   colStatus2; // botão mudar status
    @FXML private TableColumn<Partida, Void>   colAcoes;   // editar / excluir
    @FXML private TableColumn<ResultadoPartida, Void> rColAcao;
    // --- Tabela de resultados ---
    @FXML private TableView<ResultadoPartida>           tabelaResultados;
    @FXML private TableColumn<ResultadoPartida, String> rColPartida;
    @FXML private TableColumn<ResultadoPartida, String> rColVencedor;
    @FXML private TableColumn<ResultadoPartida, String> rColPerdedor;
    @FXML private TableColumn<ResultadoPartida, String> rColPlacar;
    @FXML private TableColumn<ResultadoPartida, String> rColPenaltis;
    @FXML private TableColumn<ResultadoPartida, String> rColAmarelos;
    @FXML private TableColumn<ResultadoPartida, String> rColVermelhos;
    @FXML private TableColumn<ResultadoPartida, String> rColFase;

    @FXML private Label labelTotal;

    private final PartidaService          partidaService   = new PartidaService();
    private final ResultadoPartidaService resultadoService = new ResultadoPartidaService();

    // Ordem do ciclo de status
    private static final StatusPartida[] CICLO = {
            StatusPartida.AGENDADA,
            StatusPartida.EM_ANDAMENTO,
            StatusPartida.FINALIZADA
    };

    @FXML
    public void initialize() {
        comboBuscarFase.setItems(FXCollections.observableArrayList(FasePartida.values()));
        comboBuscarFase.setPromptText("Todas as fases");

        configurarColunasPart();
        configurarColunasResult();
        carregarTodas();
    }

    // ================================================================
    //  CONFIGURAÇÃO — tabela de partidas
    // ================================================================

    private void configurarColunasPart() {
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

        // Coluna status com cor
        colStatus.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStatus().name()));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                getStyleClass().removeAll("status-agendada","status-andamento","status-finalizada");
                if (empty || s == null) { setText(null); return; }
                setText(s);
                switch (s) {
                    case "AGENDADA"     -> getStyleClass().add("status-agendada");
                    case "EM_ANDAMENTO" -> getStyleClass().add("status-andamento");
                    case "FINALIZADA"   -> getStyleClass().add("status-finalizada");
                }
            }
        });

        // Coluna "Mudar Status" — botão que avança para o próximo status e salva
        colStatus2.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("▶ Avançar");
            {
                btn.getStyleClass().add("btn-status");
                btn.setOnAction(e -> {
                    Partida p = getTableView().getItems().get(getIndex());
                    avancarStatus(p);
                });
            }
            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                Partida p = getTableView().getItems().get(getIndex());
                // Esconde o botão se já está finalizada
                setGraphic(p.getStatus() == StatusPartida.FINALIZADA ? null : btn);
            }
        });

        // Coluna Ações — Editar (azul) e Excluir (vermelho)
        colAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("Editar");
            private final Button btnDel  = new Button("Excluir");
            private final HBox   box     = new HBox(6, btnEdit, btnDel);
            {
                btnEdit.setStyle(
                        "-fx-background-color:#1565c0;-fx-text-fill:white;" +
                                "-fx-font-weight:bold;-fx-background-radius:6;" +
                                "-fx-cursor:hand;-fx-padding:4 10;");
                btnDel.setStyle(
                        "-fx-background-color:#c62828;-fx-text-fill:white;" +
                                "-fx-font-weight:bold;-fx-background-radius:6;" +
                                "-fx-cursor:hand;-fx-padding:4 10;");
                btnEdit.setOnAction(e -> abrirDialogEditar(
                        getTableView().getItems().get(getIndex())));
                btnDel.setOnAction(e -> confirmarExcluir(
                        getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    // ================================================================
    //  CONFIGURAÇÃO — tabela de resultados
    // ================================================================

    private void configurarColunasResult() {
        rColPartida.setCellValueFactory(r ->
                new SimpleStringProperty("Partida " + r.getValue().getPartida().getNumeroPartidas()));
        rColPlacar.setCellValueFactory(r ->
                new SimpleStringProperty(r.getValue().getPlacar()));
        rColPenaltis.setCellValueFactory(r ->
                new SimpleStringProperty(r.getValue().getPlacarPenaltis()));
        rColAmarelos.setCellValueFactory(r ->
                new SimpleStringProperty(String.valueOf(r.getValue().getCartoesAmarelos())));
        rColVermelhos.setCellValueFactory(r ->
                new SimpleStringProperty(String.valueOf(r.getValue().getCartoesVermelhos())));
        rColFase.setCellValueFactory(r ->
                new SimpleStringProperty(r.getValue().getPartida().getFase().name()));

        // Vencedor em verde
        rColVencedor.setCellValueFactory(r -> {
            ResultadoPartida resultado = r.getValue();

            if (resultado.getTimeVencedor() == null) {
                return new SimpleStringProperty(
                        resultado.getPartida().getTimeCasa().getPais()
                );
            }

            return new SimpleStringProperty(
                    resultado.getTimeVencedor().getPais()
            );
        });
        rColVencedor.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);

                if (empty || s == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                ResultadoPartida resultado =
                        getTableView().getItems().get(getIndex());

                setText(s);

                if (resultado.getTimeVencedor() == null) {
                    setStyle("-fx-text-fill: #ffd54f; -fx-font-weight: bold;");
                } else {
                    setStyle("-fx-text-fill: #4caf50; -fx-font-weight: bold;");
                }
            }
        });
        // Perdedor em vermelho
        rColPerdedor.setCellValueFactory(r -> {
            ResultadoPartida resultado = r.getValue();

            if (resultado.getTimePerdedor() == null) {
                return new SimpleStringProperty(
                        resultado.getPartida().getTimeVisitante().getPais()
                );
            }

            return new SimpleStringProperty(
                    resultado.getTimePerdedor().getPais()
            );
        });
        rColPerdedor.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);

                if (empty || s == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                ResultadoPartida resultado =
                        getTableView().getItems().get(getIndex());

                setText(s);

                if (resultado.getTimePerdedor() == null) {
                    setStyle("-fx-text-fill: #ffd54f; -fx-font-weight: bold;");
                } else {
                    setStyle("-fx-text-fill: #ef5350; -fx-font-weight: bold;");
                }
            }
        });
        rColAcao.setCellFactory(col -> new TableCell<>() {
            private final Button btnReiniciar = new Button("🏆 Reiniciar Copa");

            {
                btnReiniciar.setStyle(
                        "-fx-background-color: #d4a32a;" +
                                "-fx-text-fill: black;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 8;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 4 10;"
                );
                btnReiniciar.setOnAction(e -> confirmarReiniciar());
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                ResultadoPartida r = getTableView().getItems().get(getIndex());
                boolean ehFinal = r.getPartida().getFase() == FasePartida.FINAL;
                setGraphic(ehFinal ? btnReiniciar : null);
            }
        }); // ← este era o que estava faltando
    }
    // ================================================================
    //  LÓGICA DE STATUS
    // ================================================================

    // Avança AGENDADA → EM_ANDAMENTO → FINALIZADA e salva imediatamente
    private void avancarStatus(Partida p) {
        StatusPartida atual = p.getStatus();
        for (int i = 0; i < CICLO.length - 1; i++) {
            if (CICLO[i] == atual) {
                StatusPartida proximo = CICLO[i + 1];
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                        "Mudar status de " + atual.name() + " → " + proximo.name() + "?",
                        ButtonType.YES, ButtonType.NO);
                confirm.showAndWait().ifPresent(btn -> {
                    if (btn == ButtonType.YES) {
                        try {
                            partidaService.atualizarStatus(p.getNumeroPartidas(), proximo);
                            carregarTodas(); // recarrega para refletir
                        } catch (Exception ex) {
                            new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
                        }
                    }
                });
                return;
            }
        }
    }

    // ================================================================
    //  EDITAR E EXCLUIR
    // ================================================================

    private void abrirDialogEditar(Partida p) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar Partida Nº " + p.getNumeroPartidas());
        dialog.setHeaderText("Altere os campos e clique em Salvar.");

        TextField campoData    = new TextField(p.getData());
        TextField campoHorario = new TextField(p.getHorario());
        ComboBox<FasePartida>   campoFase   = new ComboBox<>(
                FXCollections.observableArrayList(FasePartida.values()));
        campoFase.setValue(p.getFase());

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(15));
        grid.addRow(0, new Label("Data (dd/MM/yyyy):"), campoData);
        grid.addRow(1, new Label("Horário (hh:mm):"),   campoHorario);
        grid.addRow(2, new Label("Fase:"),               campoFase);

        dialog.getDialogPane().setContent(grid);
        ButtonType btnSalvar = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnVoltar = new ButtonType("Voltar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalvar, btnVoltar);

        dialog.showAndWait().ifPresent(result -> {
            if (result == btnSalvar) {
                try {
                    p.setData(campoData.getText().trim());
                    p.setHorario(campoHorario.getText().trim());
                    p.setFase(campoFase.getValue());
                    partidaService.atualizarPartida(p);
                    carregarTodas();
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK).showAndWait();
                }
            }
        });
    }

    private void confirmarExcluir(Partida p) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Excluir Partida Nº " + p.getNumeroPartidas() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    partidaService.removerPartida(p.getNumeroPartidas());
                    carregarTodas();
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK).showAndWait();
                }
            }
        });
    }

    // ================================================================
    //  CARREGAR DADOS
    // ================================================================

    private void carregarTodas() {
        // Tabela de partidas
        List<Partida> partidas = partidaService.listarPartidas();
        tabelaPartidas.setItems(FXCollections.observableArrayList(partidas));

        List<ResultadoPartida> resultados = FXCollections.observableArrayList();

        try {
            resultados = resultadoService.listarResultados();

            System.out.println("Resultados encontrados: " + resultados.size());

            for (ResultadoPartida r : resultados) {
                System.out.println(
                        "Partida " +
                                r.getPartida().getNumeroPartidas() +
                                " - Placar: " +
                                r.getPlacar()
                );
            }

            tabelaResultados.setItems(
                    FXCollections.observableArrayList(resultados)
            );

        } catch (Exception e) {
            e.printStackTrace();

            new Alert(Alert.AlertType.ERROR,
                    "Erro ao carregar resultados:\n" + e.getMessage(),
                    ButtonType.OK).showAndWait();
        }

        labelTotal.setText("Partidas: " + partidas.size() +
                "   |   Resultados: " + resultados.size());
    }

    // ================================================================
    //  BUSCA E NAVEGAÇÃO
    // ================================================================

    @FXML
    private void aoBuscar() {
        String time = campoBuscarTime.getText().trim().toLowerCase();
        String data = campoBuscarData.getText().trim();
        FasePartida fase = comboBuscarFase.getValue();

        List<Partida> filtrado = partidaService.listarPartidas().stream()
                .filter(p -> time.isEmpty()
                        || p.getTimeCasa().getPais().toLowerCase().contains(time)
                        || p.getTimeVisitante().getPais().toLowerCase().contains(time))
                .filter(p -> data.isEmpty() || p.getData().contains(data))
                .filter(p -> fase == null || p.getFase() == fase)
                .collect(Collectors.toList());

        tabelaPartidas.setItems(FXCollections.observableArrayList(filtrado));
        labelTotal.setText("Partidas filtradas: " + filtrado.size());
    }

    @FXML private void aoLimpar()    { campoBuscarTime.clear(); campoBuscarData.clear(); comboBuscarFase.setValue(null); carregarTodas(); }
    @FXML private void aoAtualizar() { carregarTodas(); }

    @FXML
    private void aoVoltar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu.fxml"));
            Stage stage = (Stage) tabelaPartidas.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 1200, 700));
            stage.setTitle("Copa do Mundo 2026");
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao voltar: " + e.getMessage(), ButtonType.OK).showAndWait();
        }
    }

        private void confirmarReiniciar() {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Isso apagará todas as partidas e resultados.\n" +
                            "Seleções, estádios e árbitros serão mantidos.\n\n" +
                            "Tem certeza que deseja reiniciar a Copa?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Reiniciar Copa");
            confirm.setHeaderText("⚠ Esta ação não pode ser desfeita!");

            confirm.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.YES) {
                    try {
                        ResultadoPartidaService service = new ResultadoPartidaService();
                        service.reiniciarCopa();

                        tabelaPartidas.getItems().clear();
                        tabelaResultados.getItems().clear();
                        labelTotal.setText("Copa reiniciada! Cadastre novas partidas.");

                    } catch (Exception e) {
                        new Alert(Alert.AlertType.ERROR,
                                "Erro ao reiniciar: " + e.getMessage(),
                                ButtonType.OK).showAndWait();
                    }
                }
            });
        }
}
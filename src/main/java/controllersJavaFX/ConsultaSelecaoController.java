package controllersJavaFX;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import modelo.classes.Selecao;
import modelo.classes.Usuario;
import persistencia.SelecaoDAO;
import servicos.usuario.SessaoUsuario;

import java.util.List;
import java.util.Optional;

public class ConsultaSelecaoController {

    @FXML private Label labelUsuarioLogado;

    @FXML private ComboBox<String> comboGrupo;
    @FXML private TextField campoTecnico;
    @FXML private TextField campoPais;
    @FXML private Label labelTotal;

    @FXML private TableView<Selecao> tabelaSelecoes;
    @FXML private TableColumn<Selecao, String>  colPais;
    @FXML private TableColumn<Selecao, String>  colGrupo;
    @FXML private TableColumn<Selecao, String>  colTecnico;
    @FXML private TableColumn<Selecao, Integer> colRanking;
    @FXML private TableColumn<Selecao, Integer> colTitulos;

    private final SelecaoDAO selecaoDAO = new SelecaoDAO();
    private ObservableList<Selecao> selecoes;

    // ------------------------------------------------------------------ //
    //  inicialização
    // ------------------------------------------------------------------ //

    @FXML
    public void initialize() {

        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (logado != null) {
            labelUsuarioLogado.setText(logado.getNome() + " · " + logado.getPerfil());
        }

        comboGrupo.setItems(FXCollections.observableArrayList(
                "A","B","C","D","E","F","G","H"));

        colPais   .setCellValueFactory(new PropertyValueFactory<>("pais"));
        colGrupo  .setCellValueFactory(new PropertyValueFactory<>("grupo"));
        colTecnico.setCellValueFactory(new PropertyValueFactory<>("tecnico"));
        colRanking.setCellValueFactory(new PropertyValueFactory<>("rankingFIFA"));
        colTitulos.setCellValueFactory(new PropertyValueFactory<>("titulos"));

        carregarDados();
    }

    // ------------------------------------------------------------------ //
    //  carregamento / atualização
    // ------------------------------------------------------------------ //

    private void carregarDados() {
        List<Selecao> lista = selecaoDAO.carregaLista();
        selecoes = FXCollections.observableArrayList(lista);
        tabelaSelecoes.setItems(selecoes);
        atualizarTotal();
    }

    private void atualizarTotal() {
        labelTotal.setText("Total: " + tabelaSelecoes.getItems().size() + " seleções");
    }

    // ------------------------------------------------------------------ //
    //  pesquisa / limpar / atualizar
    // ------------------------------------------------------------------ //

    @FXML
    private void handlePesquisar() {

        List<Selecao> resultado = selecaoDAO.carregaLista();

        String pais    = campoPais   .getText().trim().toLowerCase();
        String grupo   = comboGrupo  .getValue();
        String tecnico = campoTecnico.getText().trim().toLowerCase();

        if (!pais.isBlank())
            resultado = resultado.stream()
                    .filter(s -> s.getPais().toLowerCase().contains(pais))
                    .toList();

        if (grupo != null && !grupo.isBlank())
            resultado = resultado.stream()
                    .filter(s -> s.getGrupo().equalsIgnoreCase(grupo))
                    .toList();

        if (!tecnico.isBlank())
            resultado = resultado.stream()
                    .filter(s -> s.getTecnico().toLowerCase().contains(tecnico))
                    .toList();

        tabelaSelecoes.setItems(FXCollections.observableArrayList(resultado));
        labelTotal.setText("Total: " + resultado.size() + " seleções");
    }

    @FXML
    private void handleLimpar() {
        comboGrupo.setValue(null);
        campoPais.clear();
        campoTecnico.clear();
        tabelaSelecoes.setItems(selecoes);
        atualizarTotal();
    }

    @FXML
    private void handleAtualizar() {
        carregarDados();
    }

    // ------------------------------------------------------------------ //
    //  EDITAR
    // ------------------------------------------------------------------ //

    @FXML
    private void handleEditar() {

        Selecao selecionada = tabelaSelecoes.getSelectionModel().getSelectedItem();

        if (selecionada == null) {
            new Alert(Alert.AlertType.WARNING,
                    "Selecione uma seleção para editar.", ButtonType.OK)
                    .showAndWait();
            return;
        }

        // ---------- monta o Dialog ---------- //
        Dialog<Selecao> dialog = new Dialog<>();
        dialog.setTitle("Editar Seleção");
        dialog.setHeaderText("Editando: " + selecionada.getPais());

        ButtonType btnSalvar = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalvar, ButtonType.CANCEL);

        // ---------- campos do formulário ---------- //
        TextField tfPais     = new TextField(selecionada.getPais());
        TextField tfGrupo    = new TextField(selecionada.getGrupo());
        TextField tfTecnico  = new TextField(selecionada.getTecnico());
        TextField tfRanking  = new TextField(String.valueOf(selecionada.getRankingFIFA()));
        TextField tfTitulos  = new TextField(String.valueOf(selecionada.getTitulos()));

        // país é exibido mas não editável (é a chave de busca no DAO)
        tfPais.setDisable(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("País:"),         0, 0); grid.add(tfPais,    1, 0);
        grid.add(new Label("Grupo:"),        0, 1); grid.add(tfGrupo,   1, 1);
        grid.add(new Label("Técnico:"),      0, 2); grid.add(tfTecnico, 1, 2);
        grid.add(new Label("Ranking FIFA:"), 0, 3); grid.add(tfRanking, 1, 3);
        grid.add(new Label("Títulos:"),      0, 4); grid.add(tfTitulos, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // ---------- validação ao clicar em Salvar ---------- //
        dialog.getDialogPane()
                .lookupButton(btnSalvar)
                .addEventFilter(javafx.event.ActionEvent.ACTION, event -> {

                    String grupo   = tfGrupo  .getText().trim();
                    String tecnico = tfTecnico.getText().trim();
                    String rankStr = tfRanking.getText().trim();
                    String titStr  = tfTitulos.getText().trim();

                    if (grupo.isBlank() || tecnico.isBlank()
                            || rankStr.isBlank() || titStr.isBlank()) {
                        mostrarErro("Todos os campos são obrigatórios.");
                        event.consume();
                        return;
                    }

                    try {
                        int ranking = Integer.parseInt(rankStr);
                        int titulos = Integer.parseInt(titStr);

                        if (ranking < 1 || ranking > 211) {
                            mostrarErro("Ranking FIFA deve estar entre 1 e 211.");
                            event.consume();
                            return;
                        }
                        if (titulos < 0) {
                            mostrarErro("O número de títulos não pode ser negativo.");
                            event.consume();
                        }

                    } catch (NumberFormatException e) {
                        mostrarErro("Ranking e Títulos devem ser números inteiros.");
                        event.consume();
                    }
                });

        // ---------- converte resultado do Dialog em Selecao ---------- //
        dialog.setResultConverter(botao -> {
            if (botao == btnSalvar) {
                // cria um novo objeto em vez de reutilizar o mesmo da tabela
                // assim o JavaFX detecta a mudança e re-renderiza corretamente
                Selecao atualizada = new Selecao(
                        selecionada.getPais(),
                        tfGrupo  .getText().trim(),
                        selecionada.getConfederacao(),
                        tfTecnico.getText().trim(),
                        Integer.parseInt(tfRanking.getText().trim()),
                        Integer.parseInt(tfTitulos.getText().trim())
                );
                return atualizada;
            }
            return null;
        });

        // ---------- persiste e recarrega se o usuário confirmou ---------- //
        Optional<Selecao> resultado = dialog.showAndWait();

        resultado.ifPresent(selecaoEditada -> {
            selecaoDAO.atualizaSelecao(selecaoEditada);
            carregarDados();
            tabelaSelecoes.refresh(); // força o JavaFX a re-renderizar as células

            Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
            sucesso.setTitle("Sucesso");
            sucesso.setHeaderText(null);
            sucesso.setContentText("Seleção atualizada com sucesso.");
            sucesso.showAndWait();
        });
    }

    // ------------------------------------------------------------------ //
    //  EXCLUIR
    // ------------------------------------------------------------------ //

    @FXML
    private void handleExcluir() {

        Selecao selecionada = tabelaSelecoes.getSelectionModel().getSelectedItem();

        if (selecionada == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Aviso");
            alerta.setHeaderText(null);
            alerta.setContentText("Selecione uma seleção para excluir.");
            alerta.showAndWait();
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmação");
        confirmacao.setHeaderText("Excluir seleção");
        confirmacao.setContentText("Deseja realmente excluir " + selecionada.getPais() + "?");

        if (confirmacao.showAndWait().get() == ButtonType.OK) {
            selecaoDAO.remover(selecionada.getPais());
            carregarDados();

            Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
            sucesso.setTitle("Sucesso");
            sucesso.setHeaderText(null);
            sucesso.setContentText("Seleção removida com sucesso.");
            sucesso.showAndWait();
        }
    }

    // ------------------------------------------------------------------ //
    //  utilitário
    // ------------------------------------------------------------------ //

    private void mostrarErro(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Erro de validação");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    @FXML
    private void handleVoltar() {
        navegarPara("/fxml/equipes.fxml", "Equipes");
    }

    @FXML
    private void handleLogout() {
        SessaoUsuario.getInstancia().encerrarSessao();
        navegarPara("/fxml/login.fxml", "Login — Copa do Mundo 2026");
    }

    private void navegarPara(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = (Stage) labelUsuarioLogado.getScene().getWindow();
            double w = stage.getWidth();
            double h = stage.getHeight();
            stage.setScene(new Scene(loader.load()));
            stage.setWidth(w);
            stage.setHeight(h);
            stage.setTitle(titulo);
        } catch (Exception e) {
            System.out.println("Tela ainda não implementada: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
package controllersJavaFX;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import modelo.classes.Jogador;
import modelo.classes.Selecao;
import modelo.classes.StatusJogador;
import modelo.classes.Usuario;
import persistencia.SelecaoDAO;
import servicos.usuario.SessaoUsuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConsultaJogadorController {

    @FXML private Label labelUsuarioLogado;

    @FXML private TextField campoNome;
    @FXML private TextField campoSelecao;
    @FXML private ComboBox<String> comboPosicao;
    @FXML private ComboBox<String> comboStatus;

    @FXML private Label labelTotal;

    @FXML private TableView<Jogador> tabelaJogadores;
    @FXML private TableColumn<Jogador, String>  colNome;
    @FXML private TableColumn<Jogador, String>  colSelecao;
    @FXML private TableColumn<Jogador, Integer> colIdade;
    @FXML private TableColumn<Jogador, String>  colNumeracao;
    @FXML private TableColumn<Jogador, String>  colPosicao;
    @FXML private TableColumn<Jogador, String>  colStatus;

    private final SelecaoDAO selecaoDAO = new SelecaoDAO();
    private ObservableList<Jogador> todosJogadores;

    // ------------------------------------------------------------------ //
    //  inicialização
    // ------------------------------------------------------------------ //

    @FXML
    public void initialize() {

        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (logado != null) {
            labelUsuarioLogado.setText(logado.getNome() + " · " + logado.getPerfil());
        }

        comboPosicao.setItems(FXCollections.observableArrayList(
                "Goleiro", "Defensor", "Meio-Campo", "Atacante"
        ));

        comboStatus.setItems(FXCollections.observableArrayList(
                "ATIVO", "LESIONADO", "SUSPENSO"
        ));

        colNome.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getNome()));

        colSelecao.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getSelecao().getPais()));

        colIdade.setCellValueFactory(
                data -> new SimpleIntegerProperty(data.getValue().getIdade()).asObject());

        colNumeracao.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getNumeracao()));

        colPosicao.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getPosicao()));

        colStatus.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getStatus().toString()));

        carregarDados();
    }

    // ------------------------------------------------------------------ //
    //  carregamento
    // ------------------------------------------------------------------ //

    private List<Jogador> coletarTodosJogadores() {
        List<Jogador> todos = new ArrayList<>();
        for (Selecao s : selecaoDAO.carregaLista()) {
            todos.addAll(s.getJogadores());
        }
        return todos;
    }

    private void carregarDados() {
        todosJogadores = FXCollections.observableArrayList(coletarTodosJogadores());
        tabelaJogadores.setItems(todosJogadores);
        atualizarTotal();
    }

    private void atualizarTotal() {
        labelTotal.setText("Total: " + tabelaJogadores.getItems().size() + " jogadores");
    }

    // ------------------------------------------------------------------ //
    //  pesquisa / limpar / atualizar
    // ------------------------------------------------------------------ //

    @FXML
    private void handlePesquisar() {

        String nome    = campoNome.getText().trim().toLowerCase();
        String selecao = campoSelecao.getText().trim().toLowerCase();
        String posicao = comboPosicao.getValue();
        String status  = comboStatus.getValue();

        List<Jogador> resultado = coletarTodosJogadores();

        if (!nome.isBlank())
            resultado = resultado.stream()
                    .filter(j -> j.getNome().toLowerCase().contains(nome))
                    .toList();

        if (!selecao.isBlank())
            resultado = resultado.stream()
                    .filter(j -> j.getSelecao().getPais().toLowerCase().contains(selecao))
                    .toList();

        if (posicao != null && !posicao.isBlank())
            resultado = resultado.stream()
                    .filter(j -> j.getPosicao().equalsIgnoreCase(posicao))
                    .toList();

        if (status != null && !status.isBlank())
            resultado = resultado.stream()
                    .filter(j -> j.getStatus() == StatusJogador.valueOf(status))
                    .toList();

        tabelaJogadores.setItems(FXCollections.observableArrayList(resultado));
        labelTotal.setText("Total: " + resultado.size() + " jogadores");
    }

    @FXML
    private void handleLimpar() {
        campoNome.clear();
        campoSelecao.clear();
        comboPosicao.setValue(null);
        comboStatus.setValue(null);
        tabelaJogadores.setItems(todosJogadores);
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

        Jogador selecionado = tabelaJogadores.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            new Alert(Alert.AlertType.WARNING,
                    "Selecione um jogador para editar.", ButtonType.OK)
                    .showAndWait();
            return;
        }

        // ---------- monta o Dialog ---------- //
        Dialog<Jogador> dialog = new Dialog<>();
        dialog.setTitle("Editar Jogador");
        dialog.setHeaderText("Editando: " + selecionado.getNome());

        ButtonType btnSalvar = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalvar, ButtonType.CANCEL);

        // ---------- campos do formulário ---------- //
        // nome é exibido mas não editável (é a chave de busca)
        TextField tfNome      = new TextField(selecionado.getNome());
        tfNome.setDisable(true);

        // seleção também não é editável aqui (mudança de seleção é operação separada)
        TextField tfSelecao   = new TextField(selecionado.getSelecao().getPais());
        tfSelecao.setDisable(true);

        TextField tfIdade     = new TextField(String.valueOf(selecionado.getIdade()));
        TextField tfNumeracao = new TextField(selecionado.getNumeracao());

        ComboBox<String> cbPosicao = new ComboBox<>();
        cbPosicao.setItems(FXCollections.observableArrayList(
                "Goleiro", "Defensor", "Meio-Campo", "Atacante"));
        cbPosicao.setValue(selecionado.getPosicao());

        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.setItems(FXCollections.observableArrayList(
                "ATIVO", "LESIONADO", "SUSPENSO"));
        cbStatus.setValue(selecionado.getStatus().toString());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Nome:"),      0, 0); grid.add(tfNome,      1, 0);
        grid.add(new Label("Seleção:"),   0, 1); grid.add(tfSelecao,   1, 1);
        grid.add(new Label("Idade:"),     0, 2); grid.add(tfIdade,     1, 2);
        grid.add(new Label("Nº Camisa:"), 0, 3); grid.add(tfNumeracao, 1, 3);
        grid.add(new Label("Posição:"),   0, 4); grid.add(cbPosicao,   1, 4);
        grid.add(new Label("Status:"),    0, 5); grid.add(cbStatus,    1, 5);

        dialog.getDialogPane().setContent(grid);

        // ---------- validação ao clicar em Salvar ---------- //
        dialog.getDialogPane()
                .lookupButton(btnSalvar)
                .addEventFilter(javafx.event.ActionEvent.ACTION, event -> {

                    String idadeStr    = tfIdade.getText().trim();
                    String numeracao   = tfNumeracao.getText().trim();
                    String posicao     = cbPosicao.getValue();
                    String status      = cbStatus.getValue();

                    if (idadeStr.isBlank() || numeracao.isBlank()
                            || posicao == null || status == null) {
                        mostrarErro("Todos os campos são obrigatórios.");
                        event.consume();
                        return;
                    }

                    try {
                        int idade = Integer.parseInt(idadeStr);
                        if (idade < 0) {
                            mostrarErro("A idade não pode ser negativa.");
                            event.consume();
                            return;
                        }
                    } catch (NumberFormatException e) {
                        mostrarErro("Idade deve ser um número inteiro.");
                        event.consume();
                        return;
                    }

                    // verifica se a numeração nova já existe em outro jogador da mesma seleção
                    // (só valida se a numeração foi alterada)
                    if (!numeracao.equals(selecionado.getNumeracao())) {
                        Selecao selecao = selecaoDAO.buscarPorPais(selecionado.getSelecao().getPais());
                        if (selecao != null) {
                            boolean numeracaoEmUso = selecao.getJogadores().stream()
                                    .anyMatch(j -> j.getNumeracao().equals(numeracao)
                                            && !j.getNome().equals(selecionado.getNome()));
                            if (numeracaoEmUso) {
                                mostrarErro("Já existe um jogador com o número " + numeracao + " nessa seleção.");
                                event.consume();
                            }
                        }
                    }
                });

        // ---------- converte resultado do Dialog em Jogador ---------- //
        dialog.setResultConverter(botao -> {
            if (botao == btnSalvar) {
                // cria novo objeto para forçar re-renderização do JavaFX
                Jogador atualizado = new Jogador(
                        selecionado.getNome(),
                        Integer.parseInt(tfIdade.getText().trim()),
                        tfNumeracao.getText().trim(),
                        cbPosicao.getValue(),
                        selecionado.getSelecao(),
                        StatusJogador.valueOf(cbStatus.getValue())
                );
                return atualizado;
            }
            return null;
        });

        // ---------- persiste e recarrega ---------- //
        Optional<Jogador> resultado = dialog.showAndWait();

        resultado.ifPresent(jogadorEditado -> {

            // busca a seleção no DAO, substitui o jogador antigo pelo novo e salva
            Selecao selecao = selecaoDAO.buscarPorPais(selecionado.getSelecao().getPais());
            if (selecao != null) {
                List<Jogador> jogadores = selecao.getJogadores();
                for (int i = 0; i < jogadores.size(); i++) {
                    if (jogadores.get(i).getNome().equals(selecionado.getNome())) {
                        jogadores.set(i, jogadorEditado);
                        break;
                    }
                }
                selecaoDAO.atualizaSelecao(selecao);
            }

            carregarDados();
            tabelaJogadores.refresh(); // força re-renderização das células

            Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
            sucesso.setTitle("Sucesso");
            sucesso.setHeaderText(null);
            sucesso.setContentText("Jogador atualizado com sucesso.");
            sucesso.showAndWait();
        });
    }

    // ------------------------------------------------------------------ //
    //  EXCLUIR
    // ------------------------------------------------------------------ //

    @FXML
    private void handleExcluir() {

        Jogador selecionado = tabelaJogadores.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            Alert aviso = new Alert(Alert.AlertType.WARNING);
            aviso.setTitle("Aviso");
            aviso.setHeaderText(null);
            aviso.setContentText("Selecione um jogador para excluir.");
            aviso.showAndWait();
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmação");
        confirmacao.setHeaderText("Excluir jogador");
        confirmacao.setContentText("Deseja realmente excluir " + selecionado.getNome() + "?");

        if (confirmacao.showAndWait().get() == ButtonType.OK) {

            Selecao selecao = selecaoDAO.buscarPorPais(selecionado.getSelecao().getPais());
            if (selecao != null) {
                selecao.getJogadores().removeIf(j -> j.getNome().equals(selecionado.getNome()));
                selecaoDAO.atualizaSelecao(selecao);
            }

            carregarDados();

            Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
            sucesso.setTitle("Sucesso");
            sucesso.setHeaderText(null);
            sucesso.setContentText("Jogador removido com sucesso.");
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

    // ------------------------------------------------------------------ //
    //  navegação
    // ------------------------------------------------------------------ //

    @FXML private void handleVoltar() { navegarPara("/fxml/jogadores.fxml", "Jogadores"); }

    @FXML private void handleLogout() {
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
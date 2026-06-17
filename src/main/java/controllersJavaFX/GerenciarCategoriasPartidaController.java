package controllersJavaFX;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import modelo.classes.CategoriaIngresso;
import modelo.classes.Partida;
import servicos.Partida.PartidaService;

import java.util.List;

/**
 * Tela de administrador para gerenciar as categorias de ingresso de uma partida.
 * Permite: adicionar nova categoria, remover categoria existente e alterar preço/estoque.
 */
public class GerenciarCategoriasPartidaController {

    // ── HUD ────────────────────────────────────────────────
    @FXML private Button btnHome;

    // ── Seleção de partida ──────────────────────────────────
    @FXML private ComboBox<Partida> comboPartida;

    // ── Tabela de categorias ────────────────────────────────
    @FXML private TableView<CategoriaIngresso>           tabelaCategorias;
    @FXML private TableColumn<CategoriaIngresso, String> colNome;
    @FXML private TableColumn<CategoriaIngresso, String> colPreco;
    @FXML private TableColumn<CategoriaIngresso, String> colEstoque;

    // ── Formulário ──────────────────────────────────────────
    @FXML private TextField campoNome;
    @FXML private TextField campoPreco;
    @FXML private TextField campoEstoque;

    // ── Feedback ────────────────────────────────────────────
    @FXML private Label labelFeedback;

    private final PartidaService partidaService = new PartidaService();
    private Partida partidaAtual = null;

    // ── Inicialização ───────────────────────────────────────

    @FXML
    public void initialize() {
        // Configura as colunas da tabela
        colNome.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getNome()));

        colPreco.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        String.format("R$ %.2f", cell.getValue().getPreco())));

        colEstoque.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(cell.getValue().getEstoque())));

        // Preenche o combo de partidas
        comboPartida.setItems(FXCollections.observableArrayList(partidaService.listarPartidas()));

        // Ao selecionar uma partida, carrega as categorias dela
        comboPartida.getSelectionModel().selectedItemProperty().addListener((obs, old, nova) -> {
            if (nova != null) {
                partidaAtual = nova;
                recarregarTabela();
            }
        });

        // Ao clicar em uma linha da tabela, preenche o formulário
        tabelaCategorias.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                campoNome.setText(sel.getNome());
                campoPreco.setText(String.valueOf(sel.getPreco()));
                campoEstoque.setText(String.valueOf(sel.getEstoque()));
            }
        });
    }

    // ── Ações do formulário ─────────────────────────────────

    /** Adiciona uma nova categoria à partida selecionada. */
    @FXML
    private void aoAdicionar() {
        if (partidaAtual == null) { feedback("Selecione uma partida primeiro.", false); return; }

        try {
            String nome   = campoNome.getText().trim();
            double preco  = Double.parseDouble(campoPreco.getText().trim());
            int estoque   = Integer.parseInt(campoEstoque.getText().trim());

            if (nome.isEmpty()) throw new IllegalArgumentException("O nome não pode ser vazio.");
            if (preco < 0)      throw new IllegalArgumentException("O preço não pode ser negativo.");
            if (estoque < 0)    throw new IllegalArgumentException("O estoque não pode ser negativo.");

            if (partidaAtual.buscarCategoriaPorNome(nome) != null) {
                throw new IllegalArgumentException("Já existe uma categoria com o nome \"" + nome + "\" nesta partida.");
            }

            partidaAtual.adicionarCategoria(new CategoriaIngresso(nome, preco, estoque));
            partidaService.atualizarPartida(partidaAtual);
            recarregarTabela();
            limpar();
            feedback("Categoria \"" + nome + "\" adicionada com sucesso!", true);

        } catch (NumberFormatException e) {
            feedback("Preço e estoque devem ser valores numéricos válidos.", false);
        } catch (Exception e) {
            feedback(e.getMessage(), false);
        }
    }

    /** Atualiza o preço e/ou estoque da categoria selecionada na tabela. */
    @FXML
    private void aoAtualizar() {
        if (partidaAtual == null) { feedback("Selecione uma partida primeiro.", false); return; }

        CategoriaIngresso selecionada = tabelaCategorias.getSelectionModel().getSelectedItem();
        if (selecionada == null) { feedback("Selecione uma categoria na tabela.", false); return; }

        try {
            double novoPreco   = Double.parseDouble(campoPreco.getText().trim());
            int novoEstoque    = Integer.parseInt(campoEstoque.getText().trim());

            if (novoPreco < 0)   throw new IllegalArgumentException("O preço não pode ser negativo.");
            if (novoEstoque < 0) throw new IllegalArgumentException("O estoque não pode ser negativo.");

            selecionada.atualizarPreco(novoPreco);
            selecionada.setEstoque(novoEstoque);
            partidaService.atualizarPartida(partidaAtual);
            recarregarTabela();
            limpar();
            feedback("Categoria atualizada com sucesso!", true);

        } catch (NumberFormatException e) {
            feedback("Preço e estoque devem ser valores numéricos válidos.", false);
        } catch (Exception e) {
            feedback(e.getMessage(), false);
        }
    }

    /** Remove a categoria selecionada na tabela da partida atual. */
    @FXML
    private void aoRemover() {
        if (partidaAtual == null) { feedback("Selecione uma partida primeiro.", false); return; }

        CategoriaIngresso selecionada = tabelaCategorias.getSelectionModel().getSelectedItem();
        if (selecionada == null) { feedback("Selecione uma categoria na tabela.", false); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Deseja remover a categoria \"" + selecionada.getNome() + "\"?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    partidaAtual.removerCategoria(selecionada.getNome());
                    partidaService.atualizarPartida(partidaAtual);
                    recarregarTabela();
                    limpar();
                    feedback("Categoria removida com sucesso!", true);
                } catch (Exception e) {
                    feedback(e.getMessage(), false);
                }
            }
        });
    }

    /** Restaura as três categorias padrão (Superior, Inferior, VIP) para a partida. */
    @FXML
    private void aoRestaurarPadrao() {
        if (partidaAtual == null) { feedback("Selecione uma partida primeiro.", false); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Isso substituirá todas as categorias atuais pelas categorias padrão " +
                "(Superior, Inferior e VIP). Deseja continuar?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    int capacidade = partidaAtual.getEstadio() != null
                            ? partidaAtual.getEstadio().getCapacidade() : 0;

                    List<CategoriaIngresso> novas = new java.util.ArrayList<>();
                    novas.add(new CategoriaIngresso("Superior", 500.00,  (int) Math.round(capacidade * 0.60)));
                    novas.add(new CategoriaIngresso("Inferior", 900.00,  (int) Math.round(capacidade * 0.30)));
                    novas.add(new CategoriaIngresso("VIP",     1200.00,  (int) Math.round(capacidade * 0.10)));

                    partidaAtual.setCategoriasIngresso(novas);
                    partidaService.atualizarPartida(partidaAtual);
                    recarregarTabela();
                    limpar();
                    feedback("Categorias padrão restauradas com sucesso!", true);
                } catch (Exception e) {
                    feedback(e.getMessage(), false);
                }
            }
        });
    }

    // ── Utilitários ─────────────────────────────────────────

    private void recarregarTabela() {
        if (partidaAtual == null) return;
        tabelaCategorias.setItems(FXCollections.observableArrayList(
                partidaAtual.getCategoriasIngresso()));
    }

    private void limpar() {
        campoNome.clear();
        campoPreco.clear();
        campoEstoque.clear();
        tabelaCategorias.getSelectionModel().clearSelection();
    }

    private void feedback(String msg, boolean sucesso) {
        labelFeedback.setText(msg);
        labelFeedback.getStyleClass().setAll(sucesso ? "label-feedback-ok" : "label-feedback-erro");
    }

    // ── Navegação ────────────────────────────────────────────

    @FXML private void irHome() { navegarPara("/fxml/menu.fxml", "Home"); }

    private void navegarPara(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = (Stage) btnHome.getScene().getWindow();
            double w = stage.getWidth();
            double h = stage.getHeight();
            stage.setScene(new Scene(loader.load()));
            stage.setWidth(w);
            stage.setHeight(h);
            stage.setTitle(titulo);
        } catch (Exception e) {
            System.out.println("Tela não encontrada: " + fxmlPath);
            e.printStackTrace();
        }
    }
}

package controllersJavaFX;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import modelo.classes.Arbitro;
import modelo.enumerations.CategoriaArbitro;
import modelo.enumerations.TipoPerfil;
import modelo.excecoes.AcessoNegadoException;
import modelo.excecoes.arbitro.ArbitroNaoEncontradoException;
import servicos.ArbitroServico;
import servicos.usuario.SessaoUsuario;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ConsultaArbitroController {

    @FXML
    private TextField campoNome;

    @FXML
    private TextField campoNacionalidade;

    @FXML
    private ComboBox<CategoriaArbitro> comboCategoria;

    @FXML
    private TableView<Arbitro> tabelaArbitros;

    @FXML
    private TableColumn<Arbitro, String> colNome;

    @FXML
    private TableColumn<Arbitro, Integer> colIdade;

    @FXML
    private TableColumn<Arbitro, CategoriaArbitro> colCategoria;

    @FXML
    private TableColumn<Arbitro, Integer> colExperiencia;

    @FXML
    private TableColumn<Arbitro, String> colNacionalidade;

    @FXML
    private Label labelTotal;

    @FXML
    private Button btnVoltar;

    @FXML
    private Button btnEditar;

    private final ObservableList<Arbitro> listaArbitros = FXCollections.observableArrayList();

    private ArbitroServico arbitroServico;

    public void setServico(ArbitroServico arbitroServico) {
        this.arbitroServico = arbitroServico;
    }

    public void carregarDadosIniciais() {
        // Controle de acesso: ocultar botão Editar para perfil ARBITRO
        TipoPerfil perfil = SessaoUsuario.getInstancia().getUsuarioLogado().getPerfil();
        if (perfil == TipoPerfil.ARBITRO) {
            btnEditar.setVisible(false);
            btnEditar.setManaged(false);
        }
        recarregarTodos();
    }

    @FXML
    public void initialize() {

        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colIdade.setCellValueFactory(new PropertyValueFactory<>("idade"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colExperiencia.setCellValueFactory(new PropertyValueFactory<>("experiencia"));
        colNacionalidade.setCellValueFactory(new PropertyValueFactory<>("nacionalidade"));

        tabelaArbitros.setItems(listaArbitros);
        tabelaArbitros.setPlaceholder(new Label("Nenhum árbitro encontrado."));

        comboCategoria.getItems().setAll(CategoriaArbitro.values());

        atualizarTotal(0);
    }

    @FXML
    private void handlePesquisar() {

        String nome = campoNome.getText();
        String nacionalidade = campoNacionalidade.getText();
        CategoriaArbitro categoria = comboCategoria.getValue();

        try {
            List<Arbitro> todos = arbitroServico.listarArbitros();

            final String filtroNome = nome != null ? nome.trim() : "";
            final String filtroNacionalidade = nacionalidade != null ? nacionalidade.trim() : "";

            List<Arbitro> resultado = todos.stream()
                    .filter(a -> filtroNome.isEmpty()
                            || a.getNome().toLowerCase().contains(filtroNome.toLowerCase()))
                    .filter(a -> filtroNacionalidade.isEmpty()
                            || a.getNacionalidade().toLowerCase().contains(filtroNacionalidade.toLowerCase()))
                    .filter(a -> categoria == null
                            || a.getCategoria() == categoria)
                    .collect(Collectors.toList());

            listaArbitros.setAll(resultado);
            atualizarTotal(resultado.size());

        } catch (AcessoNegadoException e) {
            mostrarErro("Acesso negado: " + e.getMessage());
        } catch (IOException e) {
            mostrarErro("Erro ao pesquisar árbitros: " + e.getMessage());
        } catch (Exception e) {
            mostrarErro("Erro inesperado: " + e.getMessage());
        }
    }

    @FXML
    private void handleLimpar() {

        campoNome.clear();
        campoNacionalidade.clear();
        comboCategoria.getSelectionModel().clearSelection();

        recarregarTodos();
    }

    @FXML
    private void handleAtualizar() {
        recarregarTodos();
    }

    @FXML
    private void handleEditar() {

        Arbitro selecionado = tabelaArbitros.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            mostrarErro("Selecione um árbitro na tabela antes de editar.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cadastro_arbitro.fxml"));
            Parent root = loader.load();

            CadastroArbitroController controller = loader.getController();
            controller.setServico(arbitroServico);
            controller.preencherParaEdicao(selecionado);

            Stage stage = (Stage) tabelaArbitros.getScene().getWindow();
            double w = stage.getWidth();
            double h = stage.getHeight();
            stage.setScene(new Scene(root));
            stage.setWidth(w);
            stage.setHeight(h);
            stage.show();

        } catch (IOException e) {
            mostrarErro("Erro ao abrir tela de edição: " + e.getMessage());
        }
    }

    @FXML
    private void handleExcluir() {

        Arbitro selecionado = tabelaArbitros.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            mostrarErro("Selecione um árbitro na tabela antes de remover.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Remoção");
        confirmacao.setHeaderText("Remover árbitro?");
        confirmacao.setContentText(
                "Deseja remover '" + selecionado.getNome() + "'?\nEssa ação não pode ser desfeita."
        );

        confirmacao.showAndWait().ifPresent(resposta -> {
            if (resposta == ButtonType.OK) {
                executarRemocao(selecionado);
            }
        });
    }

    @FXML
    private void handleVoltar() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnVoltar.getScene().getWindow();
            double w = stage.getWidth();
            double h = stage.getHeight();
            stage.setScene(new Scene(root));
            stage.setWidth(w);
            stage.setHeight(h);
            stage.show();

        } catch (IOException e) {
            Stage stage = (Stage) btnVoltar.getScene().getWindow();
            stage.close();
        }
    }

    private void recarregarTodos() {

        try {
            List<Arbitro> todos = arbitroServico.listarArbitros();

            listaArbitros.setAll(todos);
            atualizarTotal(todos.size());

        } catch (AcessoNegadoException e) {
            mostrarErro("Acesso negado: " + e.getMessage());
        } catch (IOException e) {
            mostrarErro("Erro ao carregar árbitros: " + e.getMessage());
        } catch (Exception e) {
            mostrarErro("Erro inesperado: " + e.getMessage());
        }
    }

    private void executarRemocao(Arbitro arbitro) {

        try {
            arbitroServico.removerArbitro(arbitro.getNome());

            recarregarTodos();

            mostrarSucesso("Árbitro '" + arbitro.getNome() + "' removido com sucesso!");

        } catch (ArbitroNaoEncontradoException e) {
            mostrarErro("Árbitro não encontrado: " + e.getMessage());
        } catch (AcessoNegadoException e) {
            mostrarErro("Acesso negado: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            mostrarErro("Dados inválidos: " + e.getMessage());
        } catch (IOException e) {
            mostrarErro("Erro ao remover árbitro: " + e.getMessage());
        } catch (Exception e) {
            mostrarErro("Erro inesperado: " + e.getMessage());
        }
    }

    private void atualizarTotal(int quantidade) {
        labelTotal.setText("Total: " + quantidade + " árbitro(s)");
    }

    private void mostrarErro(String mensagem) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText("Ocorreu um erro");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarSucesso(String mensagem) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
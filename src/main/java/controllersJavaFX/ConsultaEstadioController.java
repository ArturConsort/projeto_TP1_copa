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

import modelo.classes.Estadio;
import modelo.enumerations.TipoGramado;
import modelo.enumerations.TipoPerfil;
import modelo.excecoes.AcessoNegadoException;
import modelo.excecoes.estadio.EstadioNaoEncontradoException;
import servicos.EstadioServico;
import servicos.usuario.SessaoUsuario;

import java.io.IOException;
import java.util.List;

public class ConsultaEstadioController {

    @FXML
    private TextField campoNomeEstadio;

    @FXML
    private TextField campoCidade;

    @FXML
    private TextField campoEstado;

    @FXML
    private ComboBox<TipoGramado> comboGramado;

    @FXML
    private TableView<Estadio> tabelaEstadios;

    @FXML
    private TableColumn<Estadio, String> colNome;

    @FXML
    private TableColumn<Estadio, String> colCidade;

    @FXML
    private TableColumn<Estadio, String> colEstado;

    @FXML
    private TableColumn<Estadio, Integer> colCapacidade;

    @FXML
    private TableColumn<Estadio, TipoGramado> colTipoGramado;

    @FXML
    private Label labelTotal;

    @FXML
    private Button btnVoltar;

    @FXML
    private Button btnEditar;

    private final ObservableList<Estadio> listaEstadios = FXCollections.observableArrayList();

    private EstadioServico estadioServico;

    public void setServico(EstadioServico estadioServico) {
        this.estadioServico = estadioServico;
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
        colCidade.setCellValueFactory(new PropertyValueFactory<>("cidade"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colCapacidade.setCellValueFactory(new PropertyValueFactory<>("capacidade"));
        colTipoGramado.setCellValueFactory(new PropertyValueFactory<>("tipoGramado"));

        tabelaEstadios.setItems(listaEstadios);
        tabelaEstadios.setPlaceholder(new Label("Nenhum estádio encontrado."));

        comboGramado.getItems().setAll(TipoGramado.values());

        atualizarTotal(0);
    }

    @FXML
    private void handlePesquisar() {

        String nome = campoNomeEstadio.getText();
        String cidade = campoCidade.getText();
        String estado = campoEstado.getText();
        TipoGramado tipo = comboGramado.getValue();

        try {
            List<Estadio> resultado = estadioServico.filtrar(nome, cidade, estado, tipo);
            listaEstadios.setAll(resultado);
            atualizarTotal(resultado.size());

        } catch (AcessoNegadoException e) {
            mostrarErro("Acesso negado: " + e.getMessage());
        } catch (IOException e) {
            mostrarErro("Erro ao pesquisar estádios: " + e.getMessage());
        } catch (Exception e) {
            mostrarErro("Erro inesperado: " + e.getMessage());
        }
    }

    @FXML
    private void handleLimpar() {

        campoNomeEstadio.clear();
        campoCidade.clear();
        campoEstado.clear();
        comboGramado.getSelectionModel().clearSelection();

        recarregarTodos();
    }

    @FXML
    private void handleAtualizar() {
        recarregarTodos();
    }

    @FXML
    private void handleEditar() {

        Estadio selecionado = tabelaEstadios.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            mostrarErro("Selecione um estádio na tabela antes de editar.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cadastro_estadio.fxml"));
            Parent root = loader.load();

            CadastroEstadioController controller = loader.getController();
            controller.setServico(estadioServico);
            controller.preencherParaEdicao(selecionado);

            Stage stage = (Stage) tabelaEstadios.getScene().getWindow();
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

        Estadio selecionado = tabelaEstadios.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            mostrarErro("Selecione um estádio na tabela antes de remover.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Remoção");
        confirmacao.setHeaderText("Remover estádio?");
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

            Stage stage = (Stage) tabelaEstadios.getScene().getWindow();
            double w = stage.getWidth();
            double h = stage.getHeight();
            stage.setScene(new Scene(root));
            stage.setWidth(w);
            stage.setHeight(h);
            stage.show();

        } catch (IOException e) {
            Stage stage = (Stage) tabelaEstadios.getScene().getWindow();
            stage.close();
        }
    }

    private void recarregarTodos() {

        try {
            List<Estadio> todos = estadioServico.listarEstadios();

            listaEstadios.setAll(todos);
            atualizarTotal(todos.size());

        } catch (AcessoNegadoException e) {
            mostrarErro("Acesso negado: " + e.getMessage());
        } catch (IOException e) {
            mostrarErro("Erro ao carregar estádios: " + e.getMessage());
        } catch (Exception e) {
            mostrarErro("Erro inesperado: " + e.getMessage());
        }
    }

    private void executarRemocao(Estadio estadio) {

        try {
            estadioServico.removerEstadio(estadio.getNome());

            recarregarTodos();

            mostrarSucesso("Estádio '" + estadio.getNome() + "' removido com sucesso!");

        } catch (EstadioNaoEncontradoException e) {
            mostrarErro("Estádio não encontrado: " + e.getMessage());
        } catch (AcessoNegadoException e) {
            mostrarErro("Acesso negado: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            mostrarErro("Dados inválidos: " + e.getMessage());
        } catch (IOException e) {
            mostrarErro("Erro ao remover estádio: " + e.getMessage());
        } catch (Exception e) {
            mostrarErro("Erro inesperado: " + e.getMessage());
        }
    }

    private void atualizarTotal(int quantidade) {
        labelTotal.setText("Total: " + quantidade + " estádio(s)");
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
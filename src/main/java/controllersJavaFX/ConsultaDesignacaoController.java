package controllersJavaFX;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import modelo.classes.Arbitro;
import modelo.classes.DesignacaoArbitro;
import modelo.enumerations.TipoPerfil;
import modelo.excecoes.AcessoNegadoException;
import modelo.excecoes.designacaoarbitro.DesignacaoNaoEncontradaException;
import servicos.ArbitroServico;
import servicos.DesignacaoArbitroServico;
import servicos.Partida.PartidaService;
import servicos.usuario.SessaoUsuario;

import java.io.IOException;
import java.util.List;

public class ConsultaDesignacaoController {

    @FXML
    private TextField campoPartida;

    @FXML
    private TextField campoArbitro;

    @FXML
    private TextField campoAssistente1;

    @FXML
    private TextField campoAssistente2;

    @FXML
    private TableView<DesignacaoArbitro> tabelaDesignacoes;

    @FXML
    private TableColumn<DesignacaoArbitro, String> colPartida;

    @FXML
    private TableColumn<DesignacaoArbitro, String> colArbitroPrincipal;

    @FXML
    private TableColumn<DesignacaoArbitro, String> colAssistente1;

    @FXML
    private TableColumn<DesignacaoArbitro, String> colAssistente2;

    @FXML
    private Label labelTotal;

    @FXML
    private Button btnVoltar;

    @FXML
    private Button btnLimpar;

    @FXML
    private Button btnEditar;

    private final ObservableList<DesignacaoArbitro> listaDesignacoes = FXCollections.observableArrayList();

    private DesignacaoArbitroServico designacaoServico;
    private ArbitroServico arbitroServico;
    private PartidaService partidaService;

    public void setServico(DesignacaoArbitroServico designacaoServico) {
        this.designacaoServico = designacaoServico;
    }

    public void setServicos(DesignacaoArbitroServico designacaoServico, ArbitroServico arbitroServico, PartidaService partidaService) {
        this.designacaoServico = designacaoServico;
        this.arbitroServico = arbitroServico;
        this.partidaService = partidaService;
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

        colPartida.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPartida().toString())
        );

        colArbitroPrincipal.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPrincipalArbitro().getNome())
        );

        colAssistente1.setCellValueFactory(data -> {
            List<Arbitro> assistentes = data.getValue().getAssistentes();
            return new SimpleStringProperty(assistentes.size() > 0 ? assistentes.get(0).getNome() : "");
        });

        colAssistente2.setCellValueFactory(data -> {
            List<Arbitro> assistentes = data.getValue().getAssistentes();
            return new SimpleStringProperty(assistentes.size() > 1 ? assistentes.get(1).getNome() : "");
        });

        tabelaDesignacoes.setItems(listaDesignacoes);
        tabelaDesignacoes.setPlaceholder(new Label("Nenhuma designação encontrada."));

        atualizarTotal(0);
    }

    @FXML
    private void handlePesquisar() {

        String filtroPartida = campoPartida.getText();
        String filtroArbitro = campoArbitro.getText();
        String filtroAssistente1 = campoAssistente1 != null ? campoAssistente1.getText() : "";
        String filtroAssistente2 = campoAssistente2 != null ? campoAssistente2.getText() : "";

        try {
            List<DesignacaoArbitro> resultado = designacaoServico.pesquisarDesignacoes(
                    filtroPartida, filtroArbitro, filtroAssistente1, filtroAssistente2);

            listaDesignacoes.setAll(resultado);
            atualizarTotal(resultado.size());

        } catch (AcessoNegadoException e) {
            mostrarErro("Acesso negado: " + e.getMessage());
        } catch (IOException e) {
            mostrarErro("Erro ao pesquisar designações: " + e.getMessage());
        } catch (Exception e) {
            mostrarErro("Erro inesperado: " + e.getMessage());
        }
    }

    @FXML
    private void handleLimpar() {

        campoPartida.clear();
        campoArbitro.clear();
        if (campoAssistente1 != null) campoAssistente1.clear();
        if (campoAssistente2 != null) campoAssistente2.clear();

        recarregarTodos();
    }

    @FXML
    private void handleAtualizar() {
        recarregarTodos();
    }

    @FXML
    private void handleEditar() {

        DesignacaoArbitro selecionado = tabelaDesignacoes.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            mostrarErro("Selecione uma designação na tabela antes de editar.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cadastro_designacao.fxml"));
            Parent root = loader.load();

            CadastroDesignacaoController controller = loader.getController();
            controller.setServicos(
                    designacaoServico,
                    arbitroServico != null ? arbitroServico : new servicos.ArbitroServico(new persistencia.ArbitroDAO()),
                    partidaService != null ? partidaService : new servicos.Partida.PartidaService()
            );
            controller.carregarDadosIniciais();
            controller.preencherParaEdicao(selecionado);

            Stage stage = (Stage) tabelaDesignacoes.getScene().getWindow();
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

        DesignacaoArbitro selecionado = tabelaDesignacoes.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            mostrarErro("Selecione uma designação na tabela antes de remover.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Remoção");
        confirmacao.setHeaderText("Remover designação?");
        confirmacao.setContentText(
                "Deseja remover a designação da partida "
                        + selecionado.getPartida().getNumeroPartidas()
                        + "?\nEssa ação não pode ser desfeita."
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
            List<DesignacaoArbitro> resultado = designacaoServico.listarDesignacoes();

            listaDesignacoes.setAll(resultado);
            atualizarTotal(resultado.size());

        } catch (AcessoNegadoException e) {
            mostrarErro("Acesso negado: " + e.getMessage());
        } catch (IOException e) {
            mostrarErro("Erro ao carregar designações: " + e.getMessage());
        } catch (Exception e) {
            mostrarErro("Erro inesperado: " + e.getMessage());
        }
    }

    private void executarRemocao(DesignacaoArbitro designacao) {

        try {
            designacaoServico.removerDesignacao(designacao.getPartida().getNumeroPartidas());

            recarregarTodos();

            mostrarSucesso("Designação da partida "
                    + designacao.getPartida().getNumeroPartidas()
                    + " removida com sucesso!");

        } catch (DesignacaoNaoEncontradaException e) {
            mostrarErro("Designação não encontrada: " + e.getMessage());
        } catch (AcessoNegadoException e) {
            mostrarErro("Acesso negado: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            mostrarErro("Dados inválidos: " + e.getMessage());
        } catch (IOException e) {
            mostrarErro("Erro ao remover designação: " + e.getMessage());
        } catch (Exception e) {
            mostrarErro("Erro inesperado: " + e.getMessage());
        }
    }

    private void atualizarTotal(int quantidade) {
        labelTotal.setText("Total: " + quantidade + " designação(ões)");
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
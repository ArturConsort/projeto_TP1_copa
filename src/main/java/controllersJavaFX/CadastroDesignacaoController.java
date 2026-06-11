package controllersJavaFX;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import modelo.classes.Arbitro;
import modelo.classes.Partida;
import modelo.excecoes.AcessoNegadoException;
import modelo.excecoes.designacaoarbitro.DesignacaoJaCadastradaException;
import modelo.excecoes.designacaoarbitro.NacionalidadeConflitanteException;
import servicos.ArbitroServico;
import servicos.DesignacaoArbitroServico;
import servicos.Partida.PartidaService;

import java.io.IOException;
import java.util.List;

public class CadastroDesignacaoController {

    @FXML
    private ComboBox<Partida> cbPartida;

    @FXML
    private ComboBox<Arbitro> cbArbitroPrincipal;

    @FXML
    private ComboBox<Arbitro> cbAssistente1;

    @FXML
    private ComboBox<Arbitro> cbAssistente2;

    @FXML
    private Button btnVoltar;

    private DesignacaoArbitroServico designacaoServico;
    private ArbitroServico arbitroServico;
    private PartidaService partidaService;

    public void setServicos(DesignacaoArbitroServico designacaoServico,
                            ArbitroServico arbitroServico,
                            PartidaService partidaService) {
        this.designacaoServico = designacaoServico;
        this.arbitroServico = arbitroServico;
        this.partidaService = partidaService;
    }

    public void carregarDadosIniciais() {

        try {
            List<Partida> partidas = partidaService.listarPartidas();
            cbPartida.getItems().setAll(partidas);
        } catch (Exception e) {
            mostrarErro("Erro ao carregar partidas: " + e.getMessage());
        }

        try {
            List<Arbitro> arbitros = arbitroServico.listarArbitros();
            cbArbitroPrincipal.getItems().setAll(arbitros);
            cbAssistente1.getItems().setAll(arbitros);
            cbAssistente2.getItems().setAll(arbitros);
        } catch (Exception e) {
            mostrarErro("Erro ao carregar árbitros: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
    }

    @FXML
    public void cadastrar() {

        Partida partida = cbPartida.getValue();
        Arbitro principal = cbArbitroPrincipal.getValue();
        Arbitro assistente1 = cbAssistente1.getValue();
        Arbitro assistente2 = cbAssistente2.getValue();

        if (partida == null) {
            mostrarErro("Selecione a partida.");
            cbPartida.requestFocus();
            return;
        }

        if (principal == null) {
            mostrarErro("Selecione o árbitro principal.");
            cbArbitroPrincipal.requestFocus();
            return;
        }

        if (assistente1 == null) {
            mostrarErro("Selecione o árbitro assistente 1.");
            cbAssistente1.requestFocus();
            return;
        }

        if (assistente2 == null) {
            mostrarErro("Selecione o árbitro assistente 2.");
            cbAssistente2.requestFocus();
            return;
        }

        try {
            designacaoServico.criarDesignacao(partida, principal, List.of(assistente1, assistente2));

            mostrarSucesso("Designação da partida " + partida.getNumeroPartidas() + " cadastrada com sucesso!");

            limpar();

        } catch (DesignacaoJaCadastradaException e) {
            mostrarErro("Designação já cadastrada: " + e.getMessage());

        } catch (NacionalidadeConflitanteException e) {
            mostrarErro("Conflito de nacionalidade: " + e.getMessage());

        } catch (AcessoNegadoException e) {
            mostrarErro("Acesso negado: " + e.getMessage());

        } catch (IllegalArgumentException e) {
            mostrarErro("Dados inválidos: " + e.getMessage());

        } catch (IOException e) {
            mostrarErro("Erro ao salvar os dados: " + e.getMessage());

        } catch (Exception e) {
            mostrarErro("Erro inesperado: " + e.getMessage());
        }
    }

    @FXML
    public void limpar() {

        cbPartida.getSelectionModel().clearSelection();
        cbArbitroPrincipal.getSelectionModel().clearSelection();
        cbAssistente1.getSelectionModel().clearSelection();
        cbAssistente2.getSelectionModel().clearSelection();

        cbPartida.requestFocus();
    }

    @FXML
    public void voltar() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/menu.fxml")
            );

            Parent root = loader.load();

            Stage stage = (Stage) btnVoltar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            Stage stage = (Stage) btnVoltar.getScene().getWindow();
            stage.close();
        }
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
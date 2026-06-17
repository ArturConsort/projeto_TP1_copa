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
import modelo.excecoes.AcessoNegadoException;
import modelo.excecoes.designacaoarbitro.DesignacaoNaoEncontradaException;
import modelo.enumerations.TipoPerfil;
import servicos.DesignacaoArbitroServico;
import servicos.usuario.SessaoUsuario;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ConsultaDesignacaoController {

    @FXML
    private TextField campoPartida;

    @FXML
    private TextField campoArbitro;

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

    /** true quando o usuário logado é árbitro — o filtro fica fixo no nome dele */
    private boolean filtroArbitroFixo = false;

    private final ObservableList<DesignacaoArbitro> listaDesignacoes = FXCollections.observableArrayList();

    private DesignacaoArbitroServico designacaoServico;

    public void setServico(DesignacaoArbitroServico designacaoServico) {
        this.designacaoServico = designacaoServico;
    }

    public void carregarDadosIniciais() {

        modelo.classes.Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();

        if (logado != null && logado.getPerfil() == TipoPerfil.ARBITRO) {
            filtroArbitroFixo = true;
            campoArbitro.setText(logado.getNome());
            campoArbitro.setEditable(false);
            campoArbitro.setStyle("-fx-opacity: 0.7;");
            btnLimpar.setVisible(false);
            btnLimpar.setManaged(false);
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

        try {
            List<DesignacaoArbitro> todos = designacaoServico.listarDesignacoes();

            final String fPartida = filtroPartida != null ? filtroPartida.trim() : "";
            final String fArbitro = filtroArbitro != null ? filtroArbitro.trim() : "";

            List<DesignacaoArbitro> resultado = todos.stream()
                    .filter(d -> fPartida.isEmpty()
                            || d.getPartida().toString().toLowerCase().contains(fPartida.toLowerCase()))
                    .filter(d -> fArbitro.isEmpty()
                            || d.getPrincipalArbitro().getNome().toLowerCase().contains(fArbitro.toLowerCase()))
                    .collect(Collectors.toList());

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
        if (!filtroArbitroFixo) {
            campoArbitro.clear();
        }

        recarregarTodos();
    }

    @FXML
    private void handleAtualizar() {
        recarregarTodos();
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
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            Stage stage = (Stage) btnVoltar.getScene().getWindow();
            stage.close();
        }
    }

    private void recarregarTodos() {

        try {
            List<DesignacaoArbitro> todos = designacaoServico.listarDesignacoes();

            List<DesignacaoArbitro> resultado;

            if (filtroArbitroFixo) {
                String nomeArbitro = campoArbitro.getText().trim().toLowerCase();
                resultado = todos.stream()
                        .filter(d -> d.getPrincipalArbitro().getNome().toLowerCase().contains(nomeArbitro))
                        .collect(Collectors.toList());
            } else {
                resultado = todos;
            }

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
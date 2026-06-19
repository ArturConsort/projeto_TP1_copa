package controllersJavaFX;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import modelo.classes.Estadio;
import modelo.enumerations.TipoGramado;
import modelo.excecoes.AcessoNegadoException;
import modelo.excecoes.estadio.EstadioJaCadastradoException;
import modelo.excecoes.estadio.EstadioNaoEncontradoException;
import servicos.EstadioServico;

import java.io.IOException;

public class CadastroEstadioController {

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtCidade;

    @FXML
    private TextField txtEstado;

    @FXML
    private TextField txtCapacidade;

    @FXML
    private ComboBox<TipoGramado> cbTipoGramado;

    @FXML
    private Button btnCadastrar;

    @FXML
    private Button btnLimpar;

    @FXML
    private Button btnVoltar;

    private EstadioServico estadioServico;
    private boolean modoEdicao = false;
    private String nomeOriginal;

    public void setServico(EstadioServico estadioServico) {
        this.estadioServico = estadioServico;
    }

    public void preencherParaEdicao(Estadio estadio) {
        this.modoEdicao = true;
        this.nomeOriginal = estadio.getNome();
        txtNome.setText(estadio.getNome());
        txtNome.setEditable(false);
        txtCidade.setText(estadio.getCidade());
        txtEstado.setText(estadio.getEstado());
        txtCapacidade.setText(String.valueOf(estadio.getCapacidade()));
        cbTipoGramado.setValue(estadio.getTipoGramado());
        btnCadastrar.setText("Salvar Alterações");
    }

    @FXML
    public void initialize() {
        cbTipoGramado.getItems().addAll(TipoGramado.values());
    }

    @FXML
    public void cadastrar() {

        String nome = txtNome.getText().trim();
        String cidade = txtCidade.getText().trim();
        String estado = txtEstado.getText().trim();
        String capTexto = txtCapacidade.getText().trim();
        TipoGramado tipo = cbTipoGramado.getValue();

        if (nome.isEmpty()) {
            mostrarErro("O campo 'Nome do Estádio' é obrigatório.");
            txtNome.requestFocus();
            return;
        }

        if (cidade.isEmpty()) {
            mostrarErro("O campo 'Cidade' é obrigatório.");
            txtCidade.requestFocus();
            return;
        }

        if (estado.isEmpty()) {
            mostrarErro("O campo 'Estado' é obrigatório.");
            txtEstado.requestFocus();
            return;
        }

        if (capTexto.isEmpty()) {
            mostrarErro("O campo 'Capacidade' é obrigatório.");
            txtCapacidade.requestFocus();
            return;
        }

        if (tipo == null) {
            mostrarErro("Selecione o tipo de gramado.");
            cbTipoGramado.requestFocus();
            return;
        }

        int capacidade;

        try {
            capacidade = Integer.parseInt(capTexto);

        } catch (NumberFormatException e) {
            mostrarErro("Capacidade inválida: informe apenas números inteiros.");
            txtCapacidade.requestFocus();
            return;
        }

        try {
            if (modoEdicao) {
                estadioServico.editarEstadio(nomeOriginal, cidade, estado, capacidade, tipo);
                mostrarSucesso("Estádio '" + nomeOriginal + "' atualizado com sucesso!");
            } else {
                estadioServico.cadastrarEstadio(nome, cidade, estado, capacidade, tipo);
                mostrarSucesso("Estádio '" + nome + "' cadastrado com sucesso!");
            }

            limpar();

        } catch (EstadioJaCadastradoException e) {
            mostrarErro("Estádio já cadastrado: " + e.getMessage());

        } catch (EstadioNaoEncontradoException e) {
            mostrarErro("Estádio não encontrado: " + e.getMessage());

        } catch (AcessoNegadoException e) {
            mostrarErro("Acesso negado: " + e.getMessage());

        } catch (IllegalArgumentException e) {
            mostrarErro("Dados inválidos: " + e.getMessage());

        } catch (IOException e) {
            mostrarErro("Erro ao salvar os dados: " + e.getMessage());
        }
    }

    @FXML
    public void limpar() {

        txtNome.clear();
        txtCidade.clear();
        txtEstado.clear();
        txtCapacidade.clear();

        cbTipoGramado.getSelectionModel().clearSelection();

        txtNome.requestFocus();
    }

    @FXML
    public void voltar() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/menu.fxml")
            );

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
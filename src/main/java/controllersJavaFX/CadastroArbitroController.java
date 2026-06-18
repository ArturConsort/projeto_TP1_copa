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

import modelo.enumerations.CategoriaArbitro;
import modelo.excecoes.AcessoNegadoException;
import modelo.excecoes.arbitro.ArbitroJaCadastradoException;
import servicos.ArbitroServico;

import java.io.IOException;

public class CadastroArbitroController {

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtIdade;

    @FXML
    private TextField txtExperiencia;

    @FXML
    private TextField txtNacionalidade;

    @FXML
    private ComboBox<CategoriaArbitro> cbCategoria;

    @FXML
    private Button btnVoltar;

    private ArbitroServico arbitroServico;

    public void setServico(ArbitroServico arbitroServico) {
        this.arbitroServico = arbitroServico;
    }

    @FXML
    public void initialize() {
        cbCategoria.getItems().addAll(CategoriaArbitro.values());
    }

    @FXML
    public void cadastrar() {

        String nome = txtNome.getText().trim();
        String idadeTexto = txtIdade.getText().trim();
        String experienciaTexto = txtExperiencia.getText().trim();
        String nacionalidade = txtNacionalidade.getText().trim();
        CategoriaArbitro categoria = cbCategoria.getValue();

        if (nome.isEmpty()) {
            mostrarErro("O campo 'Nome' é obrigatório.");
            txtNome.requestFocus();
            return;
        }

        if (idadeTexto.isEmpty()) {
            mostrarErro("O campo 'Idade' é obrigatório.");
            txtIdade.requestFocus();
            return;
        }

        if (experienciaTexto.isEmpty()) {
            mostrarErro("O campo 'Experiência' é obrigatório.");
            txtExperiencia.requestFocus();
            return;
        }

        if (nacionalidade.isEmpty()) {
            mostrarErro("O campo 'Nacionalidade' é obrigatório.");
            txtNacionalidade.requestFocus();
            return;
        }

        if (categoria == null) {
            mostrarErro("Selecione a categoria do árbitro.");
            cbCategoria.requestFocus();
            return;
        }

        int idade;
        int experiencia;

        try {
            idade = Integer.parseInt(idadeTexto);
        } catch (NumberFormatException e) {
            mostrarErro("Idade inválida: informe apenas números inteiros.");
            txtIdade.requestFocus();
            return;
        }

        try {
            experiencia = Integer.parseInt(experienciaTexto);
        } catch (NumberFormatException e) {
            mostrarErro("Experiência inválida: informe apenas números inteiros.");
            txtExperiencia.requestFocus();
            return;
        }

        try {
            arbitroServico.cadastrarArbitro(nome, idade, categoria, experiencia, nacionalidade);

            mostrarSucesso("Árbitro '" + nome + "' cadastrado com sucesso!");

            limpar();

        } catch (ArbitroJaCadastradoException e) {
            mostrarErro("Árbitro já cadastrado: " + e.getMessage());

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
        txtIdade.clear();
        txtExperiencia.clear();
        txtNacionalidade.clear();

        cbCategoria.getSelectionModel().clearSelection();

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
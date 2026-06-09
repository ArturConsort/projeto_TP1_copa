package controllersJavaFX;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelo.classes.Usuario;
import modelo.enumerations.TipoPerfil;
import servicos.usuario.UsuarioServico;

public class CadastroUsuarioController {

    @FXML private TextField     campoNome;
    @FXML private TextField     campoCpf;
    @FXML private TextField     campoEmail;
    @FXML private TextField     campoPais;
    @FXML private TextField     campoLogin;
    @FXML private PasswordField campoSenha;
    @FXML private PasswordField campoConfirmarSenha;
    @FXML private ComboBox<TipoPerfil> comboPerfil;
    @FXML private Label         labelFeedback;

    private final UsuarioServico usuarioServico = new UsuarioServico();

    @FXML
    public void initialize() {
        comboPerfil.setItems(FXCollections.observableArrayList(TipoPerfil.values()));
    }

    @FXML
    private void handleCadastrar() {
        labelFeedback.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 12; -fx-font-family: 'Segoe UI';");

        if (campoNome.getText().isBlank()) {
            labelFeedback.setText("Informe o nome completo.");
            return;
        }
        if (campoCpf.getText().isBlank()) {
            labelFeedback.setText("Informe o CPF.");
            return;
        }
        if (campoEmail.getText().isBlank()) {
            labelFeedback.setText("Informe o e-mail.");
            return;
        }
        if (campoPais.getText().isBlank()) {
            labelFeedback.setText("Informe o país.");
            return;
        }
        if (campoLogin.getText().isBlank()) {
            labelFeedback.setText("Informe o login.");
            return;
        }
        if (comboPerfil.getValue() == null) {
            labelFeedback.setText("Selecione um perfil de acesso.");
            return;
        }
        if (campoSenha.getText().isBlank()) {
            labelFeedback.setText("Informe a senha.");
            return;
        }
        if (!campoSenha.getText().equals(campoConfirmarSenha.getText())) {
            labelFeedback.setText("As senhas não coincidem.");
            return;
        }

        try {
            Usuario novo = new Usuario(
                    campoNome.getText().trim(),
                    campoCpf.getText().trim(),
                    campoEmail.getText().trim(),
                    campoPais.getText().trim(),
                    campoLogin.getText().trim(),
                    campoSenha.getText(),
                    comboPerfil.getValue()
            );
            usuarioServico.cadastrar(novo);

            labelFeedback.setStyle("-fx-text-fill: #4caf82; -fx-font-size: 12; -fx-font-family: 'Segoe UI';");
            labelFeedback.setText("Usuário cadastrado com sucesso!");
            limparCampos();

        } catch (Exception e) {
            labelFeedback.setText("Erro: " + e.getMessage());
        }
    }

    @FXML
    private void handleVoltar() {
        navegarPara("/fxml/menu.fxml", "Menu — Copa do Mundo 2026");
    }

    // ===== Utilitários =====

    private void limparCampos() {
        campoNome.clear();
        campoCpf.clear();
        campoEmail.clear();
        campoPais.clear();
        campoLogin.clear();
        campoSenha.clear();
        campoConfirmarSenha.clear();
        comboPerfil.setValue(null);
    }

    private void navegarPara(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = (Stage) campoNome.getScene().getWindow();

            // Salva o estado atual ANTES de trocar a cena
            boolean eraMaximized  = stage.isMaximized();
            boolean eraFullScreen = stage.isFullScreen();

            stage.setScene(new Scene(loader.load()));
            stage.setTitle(titulo);

            // Restaura o estado: fullscreen tem prioridade sobre maximized
            if (eraFullScreen) {
                stage.setFullScreen(true);
            } else if (eraMaximized) {
                stage.setMaximized(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package controllersJavaFX;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import servicos.usuario.UsuarioServico;

public class LoginController {



    @FXML private TextField campoLogin;
    @FXML private PasswordField campoSenha;
    @FXML private Label labelErro;

    private UsuarioServico usuarioServico = new UsuarioServico();

    @FXML
    private void handleLogin() {
        String login = campoLogin.getText();
        String senha = campoSenha.getText();

        try {
            usuarioServico.login(login, senha);
            irParaMenu();
        } catch (Exception e) {
            labelErro.setText("Login ou senha inválidos.");
            System.out.println("Erro: " + e.getMessage());
            System.out.println("Diretório atual: " + System.getProperty("user.dir"));
        }
    }

    private void irParaMenu() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/menu.fxml"));
        Stage stage = (Stage) campoLogin.getScene().getWindow();
        double w = stage.getWidth();
            double h = stage.getHeight();
            stage.setScene(new Scene(loader.load()));
            stage.setWidth(w);
            stage.setHeight(h);
        stage.setTitle("Menu Principal — Copa do Mundo 2026");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setMaximized(true);
    }
}
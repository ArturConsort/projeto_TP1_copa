package controllersJavaFX;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import modelo.classes.Usuario;
import servicos.usuario.SessaoUsuario;

public class JogadoresInicialController {

    @FXML private Label labelUsuarioLogado;

    @FXML
    public void initialize() {
        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (logado != null) {
            labelUsuarioLogado.setText(logado.getNome() + " · " + logado.getPerfil());
        }
    }

    @FXML
    private void handleCadastro() {
        navegarPara("/fxml/cadastro_jogador.fxml", "Jogadores — Cadastro");
    }

    @FXML
    private void handleConsulta() {
        navegarPara("/fxml/consulta_jogador.fxml", "Jogadores — Consulta");
    }

    @FXML
    private void handleVoltar() {
        navegarPara("/fxml/menu.fxml", "Menu — Copa do Mundo 2026");
    }

    @FXML
    private void handleLogout() {
        SessaoUsuario.getInstancia().encerrarSessao();
        navegarPara("/fxml/login.fxml", "Login — Copa do Mundo 2026");
    }

    private void navegarPara(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = (Stage) labelUsuarioLogado.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(titulo);
        } catch (Exception e) {
            System.out.println("Tela ainda não implementada: " + fxmlPath);
            e.printStackTrace();
        }
    }
}

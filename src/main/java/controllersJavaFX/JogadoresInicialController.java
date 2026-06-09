package controllersJavaFX;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class JogadoresInicialController {
    // ── Botões do HUD ───────────────────────────────────────
    @FXML
    private Button btnHome;
    @FXML private Button btnJogadores;
    @FXML private Button btnEquipes;
    @FXML private Button btnPartidas;
    @FXML private Button btnEstadios;
    @FXML private Button btnArbitros;
    @FXML private Button btnIngressos;

    // ── Inicialização ───────────────────────────────────────

    @FXML
    public void initialize() {
        // Marca "Equipes" como ativo visualmente
        btnEquipes.getStyleClass().add("nav-btn-active");
    }

    // ── Navegação pelo HUD ──────────────────────────────────

    @FXML private void irHome()      { navegarPara("/fxml/menu.fxml",      "Home");      }
    @FXML private void irJogadores() { }
    @FXML private void irEquipes()   { navegarPara("/fxml/equipes.fxml", "Equipes"); /* já estamos aqui */ }
    @FXML private void irPartidas()  { navegarPara("/fxml/partidas.fxml",  "Partidas");  }
    @FXML private void irEstadios()  { navegarPara("/fxml/estadios.fxml",  "Estádios");  }
    @FXML private void irArbitros()  { navegarPara("/fxml/arbitros.fxml",  "Árbitros");  }
    @FXML private void irIngressos() { navegarPara("/fxml/ingressos.fxml", "Ingressos"); }

    // ── Seleção de atividade ────────────────────────────────

    @FXML
    private void handleCadastro() {
        navegarPara("/fxml/cadastro_jogador.fxml", "Jogadores — Cadastro");
    }

    @FXML
    private void handleConsulta() {
        navegarPara("/fxml/consulta_jogador.fxml", "Jogadores — Consulta");
    }

    // ── Utilitário ──────────────────────────────────────────

    private void navegarPara(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = (Stage) btnHome.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 900, 600));
            stage.setTitle(titulo);
        } catch (Exception e) {
            System.out.println("Tela ainda não implementada: " + fxmlPath);
            e.printStackTrace();
        }
    }
}

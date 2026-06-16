package controllersJavaFX;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelo.classes.Ingresso;
import modelo.classes.Partida;
import modelo.classes.Usuario;
import modelo.classes.Venda;
import modelo.enumerations.TipoPerfil;
import servicos.VendaServico;
import servicos.usuario.SessaoUsuario;

import java.util.ArrayList;
import java.util.List;

public class MeusIngressosController {

    // ── Botões do HUD ───────────────────────────────────────
    @FXML private Button btnHome;
    @FXML private Button btnJogadores;
    @FXML private Button btnEquipes;
    @FXML private Button btnPartidas;
    @FXML private Button btnEstadios;
    @FXML private Button btnArbitros;
    @FXML private Button btnIngressos;
    @FXML private Button btnRelatorios;

    @FXML private FlowPane painelIngressos;
    @FXML private Label labelTotalIngressos;

    private final VendaServico vendaServico = new VendaServico();

    @FXML
    public void initialize() {
        ajustarNavbarPorPerfil();
        carregarMeusIngressos();
    }

    private void carregarMeusIngressos() {

        painelIngressos.getChildren().clear();

        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();

        if (logado == null) {
            Label aviso = new Label("Você precisa estar logado para ver seus ingressos.");
            aviso.getStyleClass().add("setor-info");
            painelIngressos.getChildren().add(aviso);
            labelTotalIngressos.setText("0 ingresso(s)");
            return;
        }

        List<Venda> minhasVendas = vendaServico.pesquisar(logado, "FINALIZADA");

        List<Ingresso> todosIngressos = new ArrayList<>();
        for (Venda v : minhasVendas) {
            if (v.getIngressos() != null) todosIngressos.addAll(v.getIngressos());
        }

        if (todosIngressos.isEmpty()) {
            Label vazio = new Label("Você ainda não possui ingressos.");
            vazio.getStyleClass().add("setor-info");
            painelIngressos.getChildren().add(vazio);
            labelTotalIngressos.setText("0 ingresso(s)");
            return;
        }

        for (Ingresso ingresso : todosIngressos) {
            painelIngressos.getChildren().add(criarCardIngresso(ingresso));
        }

        labelTotalIngressos.setText(todosIngressos.size() + " ingresso(s)");
    }

    private VBox criarCardIngresso(Ingresso ingresso) {

        VBox card = new VBox(10);
        card.getStyleClass().add("partida-card");
        card.setAlignment(Pos.TOP_LEFT);
        card.setPrefWidth(280);

        // ── ID do ingresso ───────────────────────────────────
        Label idLabel = new Label("Ingresso #" + ingresso.getIdIngresso());
        idLabel.getStyleClass().add("partida-card-titulo");

        // ── Partida ──────────────────────────────────────────
        Partida p = ingresso.getPartida();
        String confronto = "Partida não disponível";
        String dataHora = "";
        String estadio = "";
        if (p != null) {
            String casa = p.getTimeCasa() != null ? p.getTimeCasa().getPais() : "?";
            String visitante = p.getTimeVisitante() != null ? p.getTimeVisitante().getPais() : "?";
            confronto = casa + " x " + visitante;
            dataHora = p.getData() + " às " + p.getHorario();
            estadio = p.getEstadio() != null ? p.getEstadio().getNome() : "";
        }

        Label confrontoLabel = new Label(confronto);
        confrontoLabel.getStyleClass().add("partida-card-info");
        confrontoLabel.setWrapText(true);

        Label dataLabel = new Label(dataHora);
        dataLabel.getStyleClass().add("partida-card-info");

        // ── Setor e preço ────────────────────────────────────
        String nomeSetor = ingresso.getCategoria() != null ? ingresso.getCategoria().getNome() : "-";
        String preco = ingresso.getCategoria() != null
                ? String.format("R$ %.2f", ingresso.getCategoria().getPreco()) : "-";

        Label setorLabel = new Label("Setor: " + nomeSetor);
        setorLabel.getStyleClass().add("partida-card-info");

        Label precoLabel = new Label("Preço: " + preco);
        precoLabel.getStyleClass().add("partida-card-info");

        // ── Status de validação ──────────────────────────────
        Label statusLabel = new Label(ingresso.isFoiValidado() ? "✔ Utilizado" : "✔ Válido");
        statusLabel.getStyleClass().add(ingresso.isFoiValidado() ? "status-finalizada" : "status-agendada");

        if (!estadio.isBlank()) {
            Label estadioLabel = new Label("📍 " + estadio);
            estadioLabel.getStyleClass().add("partida-card-info");
            card.getChildren().addAll(idLabel, confrontoLabel, dataLabel, estadioLabel, setorLabel, precoLabel, statusLabel);
        } else {
            card.getChildren().addAll(idLabel, confrontoLabel, dataLabel, setorLabel, precoLabel, statusLabel);
        }

        VBox.setMargin(idLabel, new Insets(0, 0, 4, 0));

        return card;
    }

    // ── Controle de visibilidade por perfil ─────────────────

    private void ajustarNavbarPorPerfil() {
        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (logado == null || logado.getPerfil() != TipoPerfil.OPERADOR) return;
        for (Button b : new Button[]{ btnJogadores, btnEquipes, btnPartidas, btnEstadios, btnArbitros }) {
            b.setVisible(false);
            b.setManaged(false);
        }
    }

    // ── Navegação pelo HUD ──────────────────────────────────

    @FXML private void irHome()      { navegarPara("/fxml/menu.fxml",      "Home");      }
    @FXML private void irJogadores() { navegarPara("/fxml/jogadores.fxml", "Jogadores"); }
    @FXML private void irEquipes()   { navegarPara("/fxml/equipes.fxml",   "Equipes");   }
    @FXML private void irPartidas()  { navegarPara("/fxml/partidas.fxml",  "Partidas");  }
    @FXML private void irEstadios()  { navegarPara("/fxml/estadios.fxml",  "Estádios");  }
    @FXML private void irArbitros()  { navegarPara("/fxml/arbitros.fxml",  "Árbitros");  }
    @FXML private void irRelatorios(){ navegarPara("/fxml/relatorios.fxml", "Relatórios"); }

    @FXML private void irIngressos() { navegarPara("/fxml/ingressos.fxml", "Ingressos"); }

    // ── Utilitário ──────────────────────────────────────────

    private void navegarPara(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = (Stage) btnHome.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 1200, 700));
            stage.setTitle(titulo);
        } catch (Exception e) {
            System.out.println("Tela ainda não implementada: " + fxmlPath);
            e.printStackTrace();
        }
    }
}

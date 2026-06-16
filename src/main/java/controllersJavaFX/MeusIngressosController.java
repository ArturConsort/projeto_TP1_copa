package controllersJavaFX;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelo.classes.Ingresso;
import modelo.classes.Partida;
import modelo.classes.Usuario;
import modelo.classes.Venda;
import servicos.VendaServico;
import servicos.usuario.SessaoUsuario;

import java.util.ArrayList;
import java.util.List;

public class MeusIngressosController {

    @FXML private Label labelUsuarioLogado;
    @FXML private FlowPane painelIngressos;
    @FXML private Label labelTotalIngressos;

    private final VendaServico vendaServico = new VendaServico();

    @FXML
    public void initialize() {
        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (logado != null) {
            labelUsuarioLogado.setText(logado.getNome() + " · " + logado.getPerfil());
        }
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

        Label idLabel = new Label("Ingresso #" + ingresso.getIdIngresso());
        idLabel.getStyleClass().add("partida-card-titulo");

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

        String nomeSetor = ingresso.getCategoria() != null ? ingresso.getCategoria().getNome() : "-";
        String preco = ingresso.getCategoria() != null
                ? String.format("R$ %.2f", ingresso.getCategoria().getPreco()) : "-";

        Label setorLabel = new Label("Setor: " + nomeSetor);
        setorLabel.getStyleClass().add("partida-card-info");

        Label precoLabel = new Label("Preço: " + preco);
        precoLabel.getStyleClass().add("partida-card-info");

        Label statusLabel = new Label(ingresso.isFoiValidado() ? "✘ Entrada utilizada" : "✔ Ingresso válido");
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

    // ── Navegação ────────────────────────────────────────────

    @FXML
    private void irIngressos() {
        navegarPara("/fxml/ingressos.fxml", "Ingressos");
    }

    @FXML
    private void handleVoltar() {
        navegarPara("/fxml/ingressos.fxml", "Ingressos");
    }

    @FXML
    private void handleLogout() {
        SessaoUsuario.getInstancia().encerrarSessao();
        navegarPara("/fxml/login.fxml", "Login — Copa do Mundo 2026");
    }

    private void navegarPara(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = (Stage) painelIngressos.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 1200, 700));
            stage.setTitle(titulo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

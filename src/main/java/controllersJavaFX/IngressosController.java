package controllersJavaFX;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelo.classes.Partida;
import servicos.Partida.PartidaService;
import servicos.usuario.SessaoCompra;
import modelo.enumerations.TipoPerfil;
import servicos.usuario.SessaoUsuario;

import java.util.List;
import java.util.stream.Collectors;

public class IngressosController {

    // ── Botões do HUD ───────────────────────────────────────
    @FXML private Button btnHome;
    @FXML private Button btnJogadores;
    @FXML private Button btnEquipes;
    @FXML private Button btnPartidas;
    @FXML private Button btnEstadios;
    @FXML private Button btnArbitros;
    @FXML private Button btnIngressos;
    @FXML private Button btnRelatorios;

    @FXML private FlowPane painelPartidas;
    @FXML private TextField campoBusca;

    private final PartidaService partidaService = new PartidaService();
    private List<Partida> todasAsPartidas;

    // ── Inicialização ───────────────────────────────────────

    @FXML
    public void initialize() {
        ajustarNavbarPorPerfil();
        todasAsPartidas = partidaService.listarPartidas();
        renderizarPartidas(todasAsPartidas);

        // busca em tempo real ao digitar
        if (campoBusca != null) {
            campoBusca.textProperty().addListener((obs, antigo, novo) -> filtrarPartidas(novo));
        }
    }

    private void filtrarPartidas(String texto) {
        if (texto == null || texto.isBlank()) {
            renderizarPartidas(todasAsPartidas);
            return;
        }
        String termo = texto.trim().toLowerCase();
        List<Partida> filtradas = todasAsPartidas.stream()
                .filter(p -> {
                    String casa = p.getTimeCasa() != null ? p.getTimeCasa().getPais().toLowerCase() : "";
                    String visitante = p.getTimeVisitante() != null ? p.getTimeVisitante().getPais().toLowerCase() : "";
                    String data = p.getData() != null ? p.getData().toLowerCase() : "";
                    String estadio = p.getEstadio() != null ? p.getEstadio().getNome().toLowerCase() : "";
                    return casa.contains(termo) || visitante.contains(termo)
                            || data.contains(termo) || estadio.contains(termo);
                })
                .collect(Collectors.toList());
        renderizarPartidas(filtradas);
    }

    @FXML
    private void aoBuscar() {
        if (campoBusca != null) filtrarPartidas(campoBusca.getText());
    }

    @FXML
    private void aoLimparBusca() {
        if (campoBusca != null) campoBusca.clear();
        renderizarPartidas(todasAsPartidas);
    }

    private void renderizarPartidas(List<Partida> partidas) {

        painelPartidas.getChildren().clear();

        if (partidas == null || partidas.isEmpty()) {
            Label vazio = new Label("Nenhuma partida encontrada.");
            vazio.getStyleClass().add("setor-info");
            painelPartidas.getChildren().add(vazio);
            return;
        }

        for (Partida partida : partidas) {
            painelPartidas.getChildren().add(criarCardPartida(partida));
        }
    }

    private VBox criarCardPartida(Partida partida) {

        VBox card = new VBox(12);
        card.getStyleClass().add("partida-card");
        card.setAlignment(Pos.TOP_LEFT);

        // bandeiras / selecoes
        HBox bandeiras = new HBox(20);
        bandeiras.getStyleClass().add("partida-card-bandeiras");
        bandeiras.setAlignment(Pos.CENTER);

        String emojiCasa = obterBandeira(partida.getTimeCasa() != null ? partida.getTimeCasa().getPais() : "");
        String emojiVisitante = obterBandeira(partida.getTimeVisitante() != null ? partida.getTimeVisitante().getPais() : "");

        Label labelCasa = new Label(emojiCasa);
        labelCasa.getStyleClass().add("partida-card-bandeira");

        Label labelVisitante = new Label(emojiVisitante);
        labelVisitante.getStyleClass().add("partida-card-bandeira");

        bandeiras.getChildren().addAll(labelCasa, labelVisitante);

        // titulo do confronto
        String nomeCasa = partida.getTimeCasa() != null ? partida.getTimeCasa().getPais() : "?";
        String nomeVisitante = partida.getTimeVisitante() != null ? partida.getTimeVisitante().getPais() : "?";

        Label titulo = new Label(nomeCasa + " x " + nomeVisitante);
        titulo.getStyleClass().add("partida-card-titulo");
        titulo.setWrapText(true);

        // data e horario
        Label dataLabel = new Label(partida.getData());
        dataLabel.getStyleClass().add("partida-card-info");

        Label horarioLabel = new Label(partida.getHorario());
        horarioLabel.getStyleClass().add("partida-card-info");

        VBox.setMargin(bandeiras, new Insets(0, 0, 4, 0));

        card.getChildren().addAll(bandeiras, titulo, dataLabel, horarioLabel);

        card.setOnMouseClicked(e -> {
            SessaoCompra.getInstancia().selecionarPartida(partida);
            navegarPara("/fxml/selecao_setor.fxml", "Ingressos — Seleção de Setor");
        });

        return card;
    }

    // converte o nome do pais em uma bandeira emoji simples
    private String obterBandeira(String pais) {

        if (pais == null) return "🏳";

        return switch (pais.trim().toLowerCase()) {
            case "brasil" -> "🇧🇷";
            case "méxico", "mexico" -> "🇲🇽";
            case "áfrica do sul", "africa do sul" -> "🇿🇦";
            case "coreia do sul", "coréia do sul" -> "🇰🇷";
            case "tchéquia", "tchequia", "republica tcheca", "república tcheca" -> "🇨🇿";
            case "canadá", "canada" -> "🇨🇦";
            case "bósnia", "bosnia" -> "🇧🇦";
            case "estados unidos", "eua" -> "🇺🇸";
            case "paraguai" -> "🇵🇾";
            case "catar", "qatar" -> "🇶🇦";
            case "suíça", "suica" -> "🇨🇭";
            case "croácia", "croacia" -> "🇭🇷";
            default -> "🏳";
        };
    }

    // ── Controle de visibilidade por perfil ─────────────────

    private void ajustarNavbarPorPerfil() {
        modelo.classes.Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
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

    @FXML private void irIngressos() { /* já estamos aqui */ }

    @FXML private void irMeusIngressos() {
        navegarPara("/fxml/meus_ingressos.fxml", "Meus Ingressos");
    }

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

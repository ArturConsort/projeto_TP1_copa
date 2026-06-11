package controllersJavaFX;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelo.classes.CategoriaIngresso;
import modelo.classes.Partida;
import servicos.usuario.SessaoCompra;

import java.util.List;

public class SelecaoSetorController {

    // ── Botões do HUD ───────────────────────────────────────
    @FXML private Button btnHome;
    @FXML private Button btnJogadores;
    @FXML private Button btnEquipes;
    @FXML private Button btnPartidas;
    @FXML private Button btnEstadios;
    @FXML private Button btnArbitros;
    @FXML private Button btnIngressos;

    @FXML private HBox painelBandeiras;
    @FXML private Label labelConfronto;
    @FXML private Label labelData;
    @FXML private Label labelHorario;

    @FXML private VBox painelSetores;
    @FXML private FlowPane painelLegenda;

    private VBox itemSelecionado;

    // ── Inicialização ───────────────────────────────────────

    @FXML
    public void initialize() {

        Partida partida = SessaoCompra.getInstancia().getPartidaSelecionada();

        if (partida == null) {
            labelConfronto.setText("Nenhuma partida selecionada");
            labelData.setText("");
            labelHorario.setText("");
            return;
        }

        carregarDadosPartida(partida);
        carregarSetores(partida);
        carregarLegenda(partida);
    }

    private void carregarDadosPartida(Partida partida) {

        String nomeCasa = partida.getTimeCasa() != null ? partida.getTimeCasa().getPais() : "?";
        String nomeVisitante = partida.getTimeVisitante() != null ? partida.getTimeVisitante().getPais() : "?";

        labelConfronto.setText(nomeCasa + " x " + nomeVisitante);
        labelData.setText("Data: " + partida.getData());
        labelHorario.setText("Horário: " + partida.getHorario());

        Label bandeiraCasa = new Label(obterBandeira(nomeCasa));
        bandeiraCasa.getStyleClass().add("setor-bandeira");

        Label bandeiraVisitante = new Label(obterBandeira(nomeVisitante));
        bandeiraVisitante.getStyleClass().add("setor-bandeira");

        painelBandeiras.getChildren().setAll(bandeiraCasa, bandeiraVisitante);
    }

    /**
     * Carrega apenas as categorias vinculadas à partida selecionada,
     * em vez de buscar todas as categorias do sistema.
     */
    private void carregarSetores(Partida partida) {

        painelSetores.getChildren().clear();

        List<CategoriaIngresso> categorias = partida.getCategoriasIngresso();

        if (categorias == null || categorias.isEmpty()) {
            Label vazio = new Label("Nenhum setor disponível para esta partida.");
            vazio.getStyleClass().add("setor-info");
            painelSetores.getChildren().add(vazio);
            return;
        }

        for (CategoriaIngresso categoria : categorias) {
            painelSetores.getChildren().add(criarItemSetor(categoria));
        }
    }

    private VBox criarItemSetor(CategoriaIngresso categoria) {

        VBox item = new VBox(6);
        item.getStyleClass().add("setor-item");
        item.setAlignment(Pos.CENTER_LEFT);

        Label nome = new Label(categoria.getNome());
        nome.getStyleClass().add("setor-item-nome");

        Label preco = new Label(String.format("Preço: R$ %.2f", categoria.getPreco()));
        preco.getStyleClass().add("setor-item-preco");

        Label disponibilidade = new Label(
                categoria.temVagasDisponiveis()
                        ? "Disponível (" + categoria.getEstoque() + " vagas)"
                        : "Esgotado"
        );
        disponibilidade.getStyleClass().add("setor-item-obs");

        item.getChildren().addAll(nome, preco, disponibilidade);

        item.setOnMouseClicked(e -> {

            if (!categoria.temVagasDisponiveis()) return;

            if (itemSelecionado != null) {
                itemSelecionado.getStyleClass().remove("setor-item-selecionado");
            }

            item.getStyleClass().add("setor-item-selecionado");
            itemSelecionado = item;

            SessaoCompra.getInstancia().selecionarCategoria(categoria);

            navegarPara("/fxml/compra_ingresso.fxml", "Ingressos — Finalizar Compra");
        });

        return item;
    }

    /**
     * Preenche o painel de legenda do mapa apenas com as categorias
     * que realmente existem na partida selecionada.
     */
    private void carregarLegenda(Partida partida) {
        if (painelLegenda == null) return;
        painelLegenda.getChildren().clear();

        List<CategoriaIngresso> categorias = partida.getCategoriasIngresso();
        if (categorias == null || categorias.isEmpty()) return;

        for (CategoriaIngresso categoria : categorias) {
            String cssClass = obterCssCorCategoria(categoria.getNome());

            Region cor = new Region();
            cor.getStyleClass().addAll(cssClass);

            Label nome = new Label(categoria.getNome());

            HBox item = new HBox(6);
            item.setAlignment(Pos.CENTER_LEFT);
            item.getStyleClass().add("legenda-item");
            item.getChildren().addAll(cor, nome);

            painelLegenda.getChildren().add(item);
        }
    }

    /**
     * Mapeia o nome da categoria para a classe CSS da cor correspondente.
     * Categorias desconhecidas recebem a cor padrão (superior).
     */
    private String obterCssCorCategoria(String nomeCategoria) {
        if (nomeCategoria == null) return "legenda-cor-superior";
        return switch (nomeCategoria.trim().toLowerCase()) {
            case "pcr"      -> "legenda-cor-pcr";
            case "gold"     -> "legenda-cor-gold";
            case "premium"  -> "legenda-cor-premium";
            case "especial" -> "legenda-cor-especial";
            case "vip"      -> "legenda-cor-vip";
            case "inferior" -> "legenda-cor-gold";
            default         -> "legenda-cor-superior";
        };
    }

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

    // ── Navegação pelo HUD ──────────────────────────────────

    @FXML private void irHome()      { navegarPara("/fxml/menu.fxml",      "Home");      }
    @FXML private void irJogadores() { navegarPara("/fxml/jogadores.fxml", "Jogadores"); }
    @FXML private void irEquipes()   { navegarPara("/fxml/equipes.fxml",   "Equipes");   }
    @FXML private void irPartidas()  { navegarPara("/fxml/partidas.fxml",  "Partidas");  }
    @FXML private void irEstadios()  { navegarPara("/fxml/estadios.fxml",  "Estádios");  }
    @FXML private void irArbitros()  { navegarPara("/fxml/arbitros.fxml",  "Árbitros");  }
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

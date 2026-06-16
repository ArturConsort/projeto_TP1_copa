package controllersJavaFX;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelo.classes.Ingresso;
import modelo.classes.Usuario;
import modelo.enumerations.TipoPerfil;
import modelo.excecoes.AcessoNegadoException;
import servicos.IngressoServico;
import servicos.usuario.SessaoUsuario;

public class ValidarIngressoController {

    // ── Botões do HUD ───────────────────────────────────────
    @FXML private Button btnHome;
    @FXML private Button btnJogadores;
    @FXML private Button btnEquipes;
    @FXML private Button btnPartidas;
    @FXML private Button btnEstadios;
    @FXML private Button btnArbitros;
    @FXML private Button btnIngressos;
    @FXML private Button btnRelatorios;

    @FXML private TextField campoIdIngresso;
    @FXML private VBox painelResultado;
    @FXML private Label labelResultado;
    @FXML private Label labelDetalhe;

    private final IngressoServico ingressoServico = new IngressoServico();

    // ── Inicialização ────────────────────────────────────────

    @FXML
    public void initialize() {
        ajustarNavbarPorPerfil();
        painelResultado.setVisible(false);
        painelResultado.setManaged(false);
    }

    // ── Ação de validar ──────────────────────────────────────

    @FXML
    private void aoValidar() {
        String texto = campoIdIngresso.getText();

        if (texto == null || texto.isBlank()) {
            exibirResultado(false, "Campo vazio", "Informe o número do ingresso.");
            return;
        }

        int idIngresso;
        try {
            idIngresso = Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            exibirResultado(false, "ID inválido", "O número do ingresso deve conter apenas dígitos.");
            return;
        }

        try {
            Ingresso ingresso = ingressoServico.buscarPorId(idIngresso);

            if (ingresso == null) {
                exibirResultado(false, "Ingresso não encontrado", "Nenhum ingresso com ID #" + idIngresso + " foi localizado.");
                return;
            }

            if (ingresso.isFoiValidado()) {
                String detalhes = montarDetalhes(ingresso);
                exibirResultado(false, "⚠ Ingresso já utilizado", "Este ingresso já foi validado anteriormente.\n" + detalhes);
                return;
            }

            boolean sucesso = ingressoServico.validarEntrada(idIngresso);

            if (sucesso) {
                String detalhes = montarDetalhes(ingresso);
                exibirResultado(true, "✔ Entrada liberada!", "Ingresso #" + idIngresso + " validado com sucesso.\n" + detalhes);
                campoIdIngresso.clear();
            } else {
                exibirResultado(false, "Falha na validação", "Não foi possível validar o ingresso #" + idIngresso + ".");
            }

        } catch (AcessoNegadoException e) {
            exibirResultado(false, "Acesso negado", e.getMessage());
        } catch (IllegalArgumentException e) {
            exibirResultado(false, "Erro", e.getMessage());
        }
    }

    @FXML
    private void aoLimpar() {
        campoIdIngresso.clear();
        painelResultado.setVisible(false);
        painelResultado.setManaged(false);
    }

    // ── Helpers ──────────────────────────────────────────────

    private String montarDetalhes(Ingresso ingresso) {
        StringBuilder sb = new StringBuilder();
        if (ingresso.getPartida() != null) {
            modelo.classes.Partida p = ingresso.getPartida();
            String casa = p.getTimeCasa() != null ? p.getTimeCasa().getPais() : "?";
            String visitante = p.getTimeVisitante() != null ? p.getTimeVisitante().getPais() : "?";
            sb.append("Partida: ").append(casa).append(" x ").append(visitante);
            if (p.getData() != null) sb.append("  |  ").append(p.getData());
        }
        if (ingresso.getCategoria() != null) {
            sb.append("\nSetor: ").append(ingresso.getCategoria().getNome());
        }
        return sb.toString();
    }

    private void exibirResultado(boolean sucesso, String titulo, String detalhe) {
        labelResultado.setText(titulo);
        labelDetalhe.setText(detalhe);
        labelResultado.getStyleClass().removeAll("resultado-sucesso", "resultado-erro");
        labelResultado.getStyleClass().add(sucesso ? "resultado-sucesso" : "resultado-erro");
        painelResultado.setVisible(true);
        painelResultado.setManaged(true);
    }

    // ── Controle de visibilidade por perfil ──────────────────

    private void ajustarNavbarPorPerfil() {
        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (logado == null || logado.getPerfil() != TipoPerfil.OPERADOR) return;
        for (Button b : new Button[]{ btnJogadores, btnEquipes, btnPartidas, btnEstadios, btnArbitros }) {
            b.setVisible(false);
            b.setManaged(false);
        }
    }

    // ── Navegação pelo HUD ───────────────────────────────────

    @FXML private void irHome()      { navegarPara("/fxml/menu.fxml",      "Home");      }
    @FXML private void irJogadores() { navegarPara("/fxml/jogadores.fxml", "Jogadores"); }
    @FXML private void irEquipes()   { navegarPara("/fxml/equipes.fxml",   "Equipes");   }
    @FXML private void irPartidas()  { navegarPara("/fxml/partidas.fxml",  "Partidas");  }
    @FXML private void irEstadios()  { navegarPara("/fxml/estadios.fxml",  "Estádios");  }
    @FXML private void irArbitros()  { navegarPara("/fxml/arbitros.fxml",  "Árbitros");  }
    @FXML private void irIngressos() { navegarPara("/fxml/ingressos.fxml", "Ingressos"); }
    @FXML private void irRelatorios(){ navegarPara("/fxml/relatorios.fxml","Relatórios");}

    // ── Utilitário ───────────────────────────────────────────

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

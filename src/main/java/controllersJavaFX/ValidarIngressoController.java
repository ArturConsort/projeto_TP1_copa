package controllersJavaFX;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelo.classes.Ingresso;
import modelo.classes.Usuario;
import modelo.excecoes.AcessoNegadoException;
import servicos.IngressoServico;
import servicos.usuario.SessaoUsuario;

public class ValidarIngressoController {

    @FXML private Label labelUsuarioLogado;
    @FXML private TextField campoIdIngresso;
    @FXML private VBox painelResultado;
    @FXML private Label labelResultado;
    @FXML private Label labelDetalhe;

    private final IngressoServico ingressoServico = new IngressoServico();

    // ── Inicialização ────────────────────────────────────────

    @FXML
    public void initialize() {
        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (logado != null) {
            labelUsuarioLogado.setText(logado.getNome() + " · " + logado.getPerfil());
        }
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
                exibirResultado(false, "⚠ Ingresso já utilizado",
                        "Este ingresso já foi validado anteriormente.\n" + montarDetalhes(ingresso));
                return;
            }

            boolean sucesso = ingressoServico.validarEntrada(idIngresso);

            if (sucesso) {
                exibirResultado(true, "✔ Entrada liberada!",
                        "Ingresso #" + idIngresso + " validado com sucesso.\n" + montarDetalhes(ingresso));
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

    // ── Navegação ────────────────────────────────────────────

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
            Stage stage = (Stage) campoIdIngresso.getScene().getWindow();
            double w = stage.getWidth();
            double h = stage.getHeight();
            stage.setScene(new Scene(loader.load()));
            stage.setWidth(w);
            stage.setHeight(h);
            stage.setTitle(titulo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

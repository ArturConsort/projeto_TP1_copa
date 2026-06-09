package controllersJavaFX;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import modelo.classes.Usuario;
import modelo.enumerations.TipoPerfil;
import servicos.usuario.SessaoUsuario;
import servicos.usuario.UsuarioServico;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RelatoriosController {

    @FXML private Label   labelUsuarioLogado;
    @FXML private Label   labelTituloRelatorio;
    @FXML private TextArea areaRelatorio;

    private final UsuarioServico usuarioServico = new UsuarioServico();
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @FXML
    public void initialize() {
        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (logado != null)
            labelUsuarioLogado.setText(logado.getNome() + " · " + logado.getPerfil());

        // Carrega o relatório de usuários automaticamente ao abrir
        handleRelatorioUsuarios();
    }

    // ===== Relatório de Usuários =====

    @FXML
    private void handleRelatorioUsuarios() {
        labelTituloRelatorio.setText("relatório · usuários");

        List<Usuario> todos = usuarioServico.pesquisar(null, null, null);

        // Contagem por perfil
        Map<TipoPerfil, Long> porPerfil = todos.stream()
                .collect(Collectors.groupingBy(Usuario::getPerfil, Collectors.counting()));

        StringBuilder sb = new StringBuilder();

        // Cabeçalho
        sb.append(linha('=', 52)).append("\n");
        sb.append("  RELATÓRIO DE USUÁRIOS\n");
        sb.append("  Gerado em: ").append(LocalDateTime.now().format(FMT)).append("\n");
        sb.append(linha('=', 52)).append("\n\n");

        // Resumo por perfil
        sb.append("  RESUMO POR PERFIL\n");
        sb.append(linha('-', 52)).append("\n");
        sb.append(String.format("  %-20s %s%n", "Perfil", "Quantidade"));
        sb.append(linha('-', 52)).append("\n");

        for (TipoPerfil p : TipoPerfil.values()) {
            long qtd = porPerfil.getOrDefault(p, 0L);
            sb.append(String.format("  %-20s %d%n", p.name(), qtd));
        }

        sb.append(linha('-', 52)).append("\n");
        sb.append(String.format("  %-20s %d%n", "TOTAL", todos.size()));
        sb.append("\n");

        // Lista completa
        sb.append("  LISTA COMPLETA\n");
        sb.append(linha('-', 52)).append("\n");

        if (todos.isEmpty()) {
            sb.append("  Nenhum usuário cadastrado.\n");
        } else {
            // Agrupa por perfil para exibir organizado
            for (TipoPerfil p : TipoPerfil.values()) {
                List<Usuario> doGrupo = todos.stream()
                        .filter(u -> u.getPerfil() == p)
                        .toList();
                if (doGrupo.isEmpty()) continue;

                sb.append("\n  > ").append(p.name()).append("\n");
                for (Usuario u : doGrupo) {
                    sb.append(String.format("    · %-22s login: %-14s país: %s%n",
                            u.getNome(), u.getLogin(), u.getPais()));
                }
            }
        }

        sb.append("\n").append(linha('=', 52)).append("\n");

        areaRelatorio.setText(sb.toString());
        // Rola para o topo
        areaRelatorio.setScrollTop(0);
    }

    // ===== Limpar =====

    @FXML
    private void handleLimpar() {
        areaRelatorio.clear();
        labelTituloRelatorio.setText("relatório · —");
    }

    // ===== Navegação =====

    @FXML
    private void handleVoltar() {
        navegarPara("/fxml/menu.fxml", "Menu — Copa do Mundo 2026");
    }

    @FXML
    private void handleLogout() {
        SessaoUsuario.getInstancia().encerrarSessao();
        navegarPara("/fxml/login.fxml", "Login — Copa do Mundo 2026");
    }

    // ===== Utilitários =====

    private String linha(char c, int tamanho) {
        return "  " + String.valueOf(c).repeat(tamanho);
    }

    private void navegarPara(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = (Stage) areaRelatorio.getScene().getWindow();
            boolean fullScreen = stage.isFullScreen();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(titulo);
            stage.setFullScreen(fullScreen);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
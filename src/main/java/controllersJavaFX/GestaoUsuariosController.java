package controllersJavaFX;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelo.classes.Usuario;
import modelo.enumerations.TipoPerfil;
import servicos.usuario.SessaoUsuario;
import servicos.usuario.UsuarioServico;

import java.util.List;
import java.util.Optional;

public class GestaoUsuariosController {

    // ── Navbar ──────────────────────────────────────────────
    @FXML private Label labelUsuarioLogado;

    // ── Filtros ─────────────────────────────────────────────
    @FXML private TextField    campoBuscaNome;
    @FXML private TextField    campoBuscaPais;
    @FXML private TextField    campoBuscaLogin;
    @FXML private ComboBox<String> comboBuscaAcesso;

    // ── Lista ────────────────────────────────────────────────
    @FXML private ListView<String> listaUsuarios;

    // ── Painel: dados atuais ─────────────────────────────────
    @FXML private VBox      painelDados;
    @FXML private TextField dadoNome;
    @FXML private TextField dadoCpf;
    @FXML private TextField dadoEmail;
    @FXML private TextField dadoPais;
    @FXML private TextField dadoLogin;
    @FXML private TextField dadoPerfil;
    @FXML private TextField dadoSenha;

    // ── Painel: edição ───────────────────────────────────────
    @FXML private VBox           painelEdicao;
    @FXML private TextField      editNome;
    @FXML private TextField      editCpf;
    @FXML private TextField      editEmail;
    @FXML private TextField      editPais;
    @FXML private ComboBox<TipoPerfil> editPerfil;
    @FXML private TextField      editSenha;
    @FXML private Label          labelFeedback;

    // ── Estado interno ───────────────────────────────────────
    private final UsuarioServico usuarioServico = new UsuarioServico();
    private List<Usuario>        todosUsuarios;
    private ObservableList<String> nomesExibidos = FXCollections.observableArrayList();
    private Usuario              usuarioSelecionado;

    // ════════════════════════════════════════════════════════
    //   INICIALIZAÇÃO
    // ════════════════════════════════════════════════════════

    @FXML
    public void initialize() {

        // Navbar
        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (logado != null)
            labelUsuarioLogado.setText(logado.getNome() + " · " + logado.getPerfil());

        // Opções do filtro de acesso
        comboBuscaAcesso.setItems(FXCollections.observableArrayList(
                "Todos", "ADMINISTRADOR", "ORGANIZADOR", "OPERADOR", "ARBITRO"
        ));
        comboBuscaAcesso.setValue("Todos");

        // Opções do combo de edição de perfil
        editPerfil.setItems(FXCollections.observableArrayList(TipoPerfil.values()));

        // Configura a lista com seleção por clique
        listaUsuarios.setItems(nomesExibidos);
        listaUsuarios.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, selecionado) -> {
                    if (selecionado != null) selecionarUsuario(selecionado);
                }
        );

        carregarLista();
    }

    // ════════════════════════════════════════════════════════
    //   CARREGAMENTO E FILTRO
    // ════════════════════════════════════════════════════════

    private void carregarLista() {
        todosUsuarios = usuarioServico.pesquisar(null, null, null);
        atualizarExibicao(todosUsuarios);
    }

    private void atualizarExibicao(List<Usuario> lista) {
        nomesExibidos.clear();
        lista.stream()
                .map(u -> u.getNome() + "  ·  " + u.getPerfil().name())
                .forEach(nomesExibidos::add);
    }

    @FXML
    private void handleFiltrar() {
        String nome   = campoBuscaNome.getText().trim().toLowerCase();
        String pais   = campoBuscaPais.getText().trim().toLowerCase();
        String login  = campoBuscaLogin.getText().trim().toLowerCase();
        String perfStr = comboBuscaAcesso.getValue();

        TipoPerfil perfil = null;
        if (perfStr != null && !perfStr.equals("Todos")) {
            try { perfil = TipoPerfil.valueOf(perfStr); } catch (Exception ignored) {}
        }

        final TipoPerfil perfilFinal = perfil;

        List<Usuario> filtrados = todosUsuarios.stream()
                .filter(u -> nome.isBlank()  || u.getNome().toLowerCase().contains(nome))
                .filter(u -> pais.isBlank()  || u.getPais().toLowerCase().contains(pais))
                .filter(u -> login.isBlank() || u.getLogin().toLowerCase().contains(login))
                .filter(u -> perfilFinal == null || u.getPerfil() == perfilFinal)
                .toList();

        atualizarExibicao(filtrados);

        // Fecha painéis se o usuário selecionado sumiu dos resultados
        if (usuarioSelecionado != null) {
            boolean aindaVisivel = filtrados.stream()
                    .anyMatch(u -> u.getLogin().equals(usuarioSelecionado.getLogin()));
            if (!aindaVisivel) fecharPaineis();
        }
    }

    // ════════════════════════════════════════════════════════
    //   SELEÇÃO DE USUÁRIO
    // ════════════════════════════════════════════════════════

    private void selecionarUsuario(String itemExibido) {
        // O item exibido tem formato "Nome  ·  PERFIL" — extrai o nome para localizar
        String nomeExtraido = itemExibido.contains("  ·  ")
                ? itemExibido.substring(0, itemExibido.indexOf("  ·  ")).trim()
                : itemExibido.trim();

        // Busca na lista atual pelo nome (pode haver homônimos — usa o primeiro encontrado)
        usuarioSelecionado = todosUsuarios.stream()
                .filter(u -> u.getNome().equals(nomeExtraido))
                .findFirst()
                .orElse(null);

        if (usuarioSelecionado == null) return;

        preencherDadosAtuais(usuarioSelecionado);
        preencherCamposEdicao(usuarioSelecionado);
        limparFeedback();

        mostrarPainel(painelDados,   true);
        mostrarPainel(painelEdicao,  true);
    }

    // ════════════════════════════════════════════════════════
    //   PREENCHIMENTO DOS PAINÉIS
    // ════════════════════════════════════════════════════════

    private void preencherDadosAtuais(Usuario u) {
        dadoNome.setText(u.getNome());
        dadoCpf.setText(u.getCpf());
        dadoEmail.setText(u.getEmail());
        dadoPais.setText(u.getPais());
        dadoLogin.setText(u.getLogin());
        dadoPerfil.setText(u.getPerfil().name());
        dadoSenha.setText(u.getSenha());
    }

    private void preencherCamposEdicao(Usuario u) {
        editNome.setText(u.getNome());
        editCpf.setText(u.getCpf());
        editEmail.setText(u.getEmail());
        editPais.setText(u.getPais());
        editPerfil.setValue(u.getPerfil());
        editSenha.setText(u.getSenha());
    }

    // ════════════════════════════════════════════════════════
    //   SALVAR MUDANÇAS
    // ════════════════════════════════════════════════════════

    @FXML
    private void handleSalvar() {
        if (usuarioSelecionado == null) return;

        String novoNome  = editNome.getText().trim();
        String novoCpf   = editCpf.getText().trim();
        String novoEmail = editEmail.getText().trim();
        String novoPais  = editPais.getText().trim();
        TipoPerfil novoPerfil = editPerfil.getValue();
        String novaSenha = editSenha.getText().trim();

        // Converte vazios para null (o serviço ignora campos null)
        novoNome  = novoNome.isEmpty()  ? null : novoNome;
        novoCpf   = novoCpf.isEmpty()   ? null : novoCpf;
        novoEmail = novoEmail.isEmpty()  ? null : novoEmail;
        novoPais  = novoPais.isEmpty()   ? null : novoPais;
        novaSenha = novaSenha.isEmpty()  ? null : novaSenha;

        try {
            List<String> warnings = usuarioServico.editar(
                    usuarioSelecionado.getLogin(),
                    novoNome, novoCpf, novoEmail, novoPais, novaSenha, novoPerfil
            );

            String mensagem = "";

            if (warnings.isEmpty()) {
                mensagem = "Operação realizada com sucesso";
            } else {
                for (String warning : warnings) {
                    mensagem += "• " + warning + "\n";
                }

            }
            mostrarFeedbackSucesso(mensagem);

            // Recarrega lista e atualiza dados exibidos
            carregarLista();
            handleFiltrar();

            // Atualiza o painel de dados atuais com os novos valores
            Usuario atualizado = usuarioServico.buscarPorLogin(usuarioSelecionado.getLogin());
            if (atualizado != null) {
                usuarioSelecionado = atualizado;
                preencherDadosAtuais(atualizado);
            }

        } catch (Exception e) {
            mostrarFeedbackErro("Erro: " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════
    //   EXCLUIR CONTA
    // ════════════════════════════════════════════════════════

    @FXML
    private void handleExcluir() {
        if (usuarioSelecionado == null) return;

        // Popup de confirmação
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Excluir conta");
        confirm.setHeaderText(null);
        confirm.setContentText(
                "Você está prestes a deletar uma conta permanentemente.\nDeseja continuar?"
        );

        // Estiliza os botões do dialog
        confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> resultado = confirm.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.YES) {
            try {
                usuarioServico.excluir(usuarioSelecionado.getLogin());

                mostrarFeedbackSucesso("Operação realizada com sucesso");

                fecharPaineis();
                usuarioSelecionado = null;
                carregarLista();
                handleFiltrar();

            } catch (Exception e) {
                mostrarFeedbackErro("Erro: " + e.getMessage());
            }
        }
    }

    // ════════════════════════════════════════════════════════
    //   NAVEGAÇÃO
    // ════════════════════════════════════════════════════════

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
            Stage stage = (Stage) listaUsuarios.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 900, 600));
            stage.setTitle(titulo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ════════════════════════════════════════════════════════
    //   UTILITÁRIOS
    // ════════════════════════════════════════════════════════

    private void mostrarPainel(VBox painel, boolean visivel) {
        painel.setVisible(visivel);
        painel.setManaged(visivel);
    }

    private void fecharPaineis() {
        mostrarPainel(painelDados,  false);
        mostrarPainel(painelEdicao, false);
    }

    private void mostrarFeedbackSucesso(String msg) {
        labelFeedback.getStyleClass().removeAll("feedback-erro", "feedback-sucesso");
        labelFeedback.getStyleClass().add("feedback-sucesso");
        labelFeedback.setText(msg);
    }

    private void mostrarFeedbackErro(String msg) {
        labelFeedback.getStyleClass().removeAll("feedback-sucesso", "feedback-erro");
        labelFeedback.getStyleClass().add("feedback-erro");
        labelFeedback.setText(msg);
    }

    private void limparFeedback() {
        labelFeedback.setText("");
        labelFeedback.getStyleClass().removeAll("feedback-sucesso", "feedback-erro");
    }
}
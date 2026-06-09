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

public class GestaoUsuariosController {

    // Navbar
    @FXML private Label labelUsuarioLogado;

    // Filtros
    @FXML private TextField campoBuscaNome;
    @FXML private TextField campoBuscaPais;
    @FXML private ComboBox<String> comboBuscaPerfil;
    @FXML private ListView<String> listaUsuarios;

    // Painel de ações
    @FXML private VBox  painelAcoes;
    @FXML private Label labelNomeSelecionado;
    @FXML private Button btnEditarAcesso;

    // Submenu de perfis
    @FXML private VBox submenuPerfil;

    private final UsuarioServico usuarioServico = new UsuarioServico();

    // Lista completa carregada do serviço
    private List<Usuario> todosUsuarios;
    // Lista filtrada exibida no ListView
    private ObservableList<String> nomesExibidos = FXCollections.observableArrayList();
    // Usuário atualmente selecionado
    private Usuario usuarioSelecionado;

    // ===== Inicialização =====

    @FXML
    public void initialize() {
        // Navbar
        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (logado != null)
            labelUsuarioLogado.setText(logado.getNome() + " · " + logado.getPerfil());

        // Configura o ListView com células customizadas
        listaUsuarios.setItems(nomesExibidos);
        listaUsuarios.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String nome, boolean empty) {
                super.updateItem(nome, empty);
                if (empty || nome == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent; -fx-padding: 0;");
                } else {
                    Button btn = new Button(nome);
                    btn.getStyleClass().add("lista-item");
                    btn.setMaxWidth(Double.MAX_VALUE);

                    // Destaca se for o selecionado
                    if (usuarioSelecionado != null
                            && usuarioSelecionado.getNome().equals(nome)) {
                        btn.getStyleClass().add("lista-item-selecionado");
                    }

                    btn.setOnAction(e -> selecionarUsuario(nome));
                    setGraphic(btn);
                    setText(null);
                    setStyle("-fx-background-color: transparent; -fx-padding: 0;");
                }
            }
        });

        // Opções do filtro de perfil (primeiro item = "Todos" para limpar filtro)
        comboBuscaPerfil.setItems(FXCollections.observableArrayList(
                "Todos", "ADMINISTRADOR", "ORGANIZADOR", "OPERADOR", "ARBITRO"
        ));
        comboBuscaPerfil.setValue("Todos");

        carregarLista();
    }

    // ===== Carregamento e filtro =====

    private void carregarLista() {
        todosUsuarios = usuarioServico.pesquisar(null, null, null);
        atualizarExibicao(todosUsuarios);
    }

    private void atualizarExibicao(List<Usuario> lista) {
        nomesExibidos.clear();
        lista.stream()
                .map(Usuario::getNome)
                .forEach(nomesExibidos::add);
        listaUsuarios.refresh();
    }

    @FXML
    private void handleFiltrar() {
        String nome  = campoBuscaNome.getText().trim().toLowerCase();
        String pais  = campoBuscaPais.getText().trim().toLowerCase();
        String perfStr = comboBuscaPerfil.getValue();

        TipoPerfil perfil = null;
        if (perfStr != null && !perfStr.equals("Todos")) {
            try { perfil = TipoPerfil.valueOf(perfStr); } catch (Exception ignored) {}
        }

        final TipoPerfil perfilFinal = perfil;

        List<Usuario> filtrados = todosUsuarios.stream()
                .filter(u -> nome.isBlank()  || u.getNome().toLowerCase().contains(nome))
                .filter(u -> pais.isBlank()  || u.getPais().toLowerCase().contains(pais))
                .filter(u -> perfilFinal == null || u.getPerfil() == perfilFinal)
                .toList();

        atualizarExibicao(filtrados);
        fecharPaineis();
    }

    // ===== Seleção de usuário =====

    private void selecionarUsuario(String nome) {
        // Fecha submenu se estava aberto
        mostrarSubmenu(false);

        // Encontra o usuario pelo nome na lista atual
        usuarioSelecionado = todosUsuarios.stream()
                .filter(u -> u.getNome().equals(nome))
                .findFirst()
                .orElse(null);

        if (usuarioSelecionado == null) return;

        // Atualiza painel de ações
        labelNomeSelecionado.setText(usuarioSelecionado.getNome());
        mostrarPainel(true);

        // Refresh para destacar o item selecionado
        listaUsuarios.refresh();
    }

    // ===== Ações do painel =====

    @FXML
    private void handleAbrirSubmenuPerfil() {
        // Alterna visibilidade do submenu
        boolean aberto = submenuPerfil.isVisible();
        mostrarSubmenu(!aberto);
    }

    @FXML private void handleSetAdministrador() { aplicarPerfil(TipoPerfil.ADMINISTRADOR); }
    @FXML private void handleSetOrganizador()   { aplicarPerfil(TipoPerfil.ORGANIZADOR);   }
    @FXML private void handleSetArbitro()       { aplicarPerfil(TipoPerfil.ARBITRO);       }
    @FXML private void handleSetOperador()      { aplicarPerfil(TipoPerfil.OPERADOR);      }

    private void aplicarPerfil(TipoPerfil novoPerfil) {
        if (usuarioSelecionado == null) return;
        try {
            usuarioServico.editar(
                    usuarioSelecionado.getLogin(),
                    null, null, null, null, null,
                    novoPerfil
            );
            mostrarAlerta(Alert.AlertType.INFORMATION,
                    "Perfil atualizado",
                    usuarioSelecionado.getNome() + " agora é " + novoPerfil.name() + ".");
            fecharPaineis();
            carregarLista();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", e.getMessage());
        }
    }

    @FXML
    private void handleRemover() {
        if (usuarioSelecionado == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar remoção");
        confirm.setHeaderText("Remover: " + usuarioSelecionado.getNome());
        confirm.setContentText("Essa ação é permanente. Deseja continuar?");

        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    usuarioServico.excluir(usuarioSelecionado.getLogin());
                    fecharPaineis();
                    usuarioSelecionado = null;
                    carregarLista();
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", e.getMessage());
                }
            }
        });
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

    private void mostrarPainel(boolean visivel) {
        painelAcoes.setVisible(visivel);
        painelAcoes.setManaged(visivel);
    }

    private void mostrarSubmenu(boolean visivel) {
        submenuPerfil.setVisible(visivel);
        submenuPerfil.setManaged(visivel);
    }

    private void fecharPaineis() {
        mostrarPainel(false);
        mostrarSubmenu(false);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void navegarPara(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = (Stage) listaUsuarios.getScene().getWindow();
            boolean fullScreen = stage.isFullScreen();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(titulo);
            stage.setFullScreen(fullScreen);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package controllersJavaFX;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import modelo.classes.Usuario;
import modelo.enumerations.TipoPerfil;
import servicos.usuario.SessaoUsuario;

public class MenuController {

    @FXML private Button btnHome;
    @FXML private Button btnJogadores;
    @FXML private Button btnEquipes;
    @FXML private Button btnPartidas;
    @FXML private Button btnEstadios;
    @FXML private Button btnArbitros;
    @FXML private Button btnIngressos;
    @FXML private Button btnVendas; 
    @FXML private Button btnDesignacoes;
    @FXML private Button btnUsuarios;
    @FXML private Label  labelUsuario;

    // Popup do submenu de Usuários
    private Popup subMenuUsuarios;

    @FXML
    public void initialize() {
        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (logado == null) return;

        TipoPerfil perfil = logado.getPerfil();
        labelUsuario.setText(logado.getNome() + " · " + perfil.name());

        esconderTodos();
        mostrar(btnHome);

        switch (perfil) {
            case ADMINISTRADOR -> mostrar(btnJogadores, btnEquipes, btnPartidas,
                    btnEstadios, btnArbitros, btnIngressos,
                    btnVendas, btnDesignacoes, btnUsuarios);
            case ORGANIZADOR   -> mostrar(btnJogadores, btnEquipes, btnPartidas,
                    btnEstadios, btnArbitros, btnDesignacoes);
            case OPERADOR      -> mostrar(btnIngressos, btnVendas);
            case ARBITRO       -> mostrar(btnPartidas);
        }

        // Submenu de Usuários só aparece se o botão estiver visível
        if (btnUsuarios.isVisible()) {
            configurarSubMenuUsuarios();
        }
    }

    // ===== Submenu Usuários =====

    private void configurarSubMenuUsuarios() {
        // Botão "Cadastrar usuário"
        Button btnCadastrar = new Button("Cadastrar usuário");
        btnCadastrar.getStyleClass().add("submenu-item");
        btnCadastrar.setOnAction(e -> {
            subMenuUsuarios.hide();
            navegarPara("/fxml/cadastro_usuario.fxml", "Cadastro de Usuário");
        });

        // Botão "Gerir usuários"
        Button btnGerir = new Button("Gerir usuários");
        btnGerir.getStyleClass().add("submenu-item");
        btnGerir.setOnAction(e -> {
            subMenuUsuarios.hide();
            navegarPara("/fxml/gestao_usuarios.fxml", "Gestão de Usuários");
        });

        VBox conteudo = new VBox(btnCadastrar, btnGerir);
        conteudo.getStyleClass().add("submenu-popup");
        conteudo.getStylesheets().add(getClass().getResource("/css/menu.css").toExternalForm());

        subMenuUsuarios = new Popup();
        subMenuUsuarios.getContent().add(conteudo);
        subMenuUsuarios.setAutoHide(true); // fecha ao clicar fora

        // Abre ao passar o mouse sobre o botão Usuários
        btnUsuarios.setOnMouseEntered(e -> {
            double x = btnUsuarios.localToScreen(0, 0).getX();
            double y = btnUsuarios.localToScreen(0, 0).getY() + btnUsuarios.getHeight();
            subMenuUsuarios.show(btnUsuarios, x, y);
        });

        // Mantém aberto enquanto o mouse está no popup
        conteudo.setOnMouseExited(e -> subMenuUsuarios.hide());
    }

    // ===== Handlers de navegação =====

    @FXML private void irHome()        { System.out.println("Home"); }
    @FXML private void irJogadores()   { navegarPara("/fxml/jogadores.fxml",   "Jogadores");   }
    @FXML private void irEquipes()     { navegarPara("/fxml/equipes.fxml",     "Equipes");     }
    @FXML private void irPartidas()    { navegarPara("/fxml/partidas.fxml",    "Partidas");    }
    @FXML private void irEstadios()    { navegarPara("/fxml/estadios.fxml",    "Estádios");    }
    @FXML private void irArbitros()    { navegarPara("/fxml/arbitros.fxml",    "Árbitros");    }
    @FXML private void irIngressos()   { navegarPara("/fxml/ingressos.fxml",   "Ingressos");   }
    @FXML private void irVendas()      { navegarPara("/fxml/vendas.fxml",      "Vendas");      }
    @FXML private void irDesignacoes() { navegarPara("/fxml/designacoes.fxml", "Designações"); }

    // btnUsuarios não tem @FXML handler direto — o clique abre o submenu pelo hover
    @FXML private void irUsuarios() {} // necessário para o FXML não reclamar do onAction

    @FXML
    private void handleLogout() {
        SessaoUsuario.getInstancia().encerrarSessao();
        navegarPara("/fxml/login.fxml", "Login — Copa do Mundo 2026");
    }

    // ===== Utilitários =====

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

    private void esconderTodos() {
        for (Button b : new Button[]{
                btnJogadores, btnEquipes, btnPartidas, btnEstadios,
                btnArbitros, btnIngressos, btnVendas, btnDesignacoes, btnUsuarios
        }) {
            b.setVisible(false);
            b.setManaged(false);
        }
    }

    private void mostrar(Button... botoes) {
        for (Button b : botoes) {
            b.setVisible(true);
            b.setManaged(true);
        }
    }
}
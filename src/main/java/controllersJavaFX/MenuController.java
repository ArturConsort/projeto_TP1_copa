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
    @FXML private Label labelUsuario;

    private Popup subMenuUsuarios;
    private Popup subMenuPartidas;

    @FXML
    public void initialize() {

        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();

        if (logado == null) {
            return;
        }

        TipoPerfil perfil = logado.getPerfil();

        labelUsuario.setText(
                logado.getNome() + " · " + perfil.name()
        );

        esconderTodos();
        mostrar(btnHome);

        switch (perfil) {

            case ADMINISTRADOR ->
                    mostrar(
                            btnJogadores,
                            btnEquipes,
                            btnPartidas,
                            btnEstadios,
                            btnArbitros,
                            btnIngressos,
                            btnVendas,
                            btnDesignacoes,
                            btnUsuarios
                    );

            case ORGANIZADOR ->
                    mostrar(
                            btnJogadores,
                            btnEquipes,
                            btnPartidas,
                            btnEstadios,
                            btnArbitros,
                            btnDesignacoes
                    );

            case OPERADOR ->
                    mostrar(
                            btnIngressos,
                            btnVendas
                    );

            case ARBITRO ->
                    mostrar(
                            btnPartidas
                    );
        }

        if (btnUsuarios.isVisible()) {
            configurarSubMenuUsuarios();
        }

        if (btnPartidas.isVisible()) {
            configurarSubMenuPartidas();
        }
    }

    // =========================================================
    // SUBMENU USUÁRIOS
    // =========================================================

    private void configurarSubMenuUsuarios() {

        Button btnCadastrar = new Button("Cadastrar usuário");

        btnCadastrar.getStyleClass().add("submenu-item");
        btnCadastrar.setOnAction(e -> {
            subMenuUsuarios.hide();
            navegarPara(
                    "/fxml/cadastro_usuario.fxml",
                    "Cadastro de Usuário"
            );
        });

        Button btnGerir = new Button("Gerir usuários");

        btnGerir.getStyleClass().add("submenu-item");
        btnGerir.setOnAction(e -> {
            subMenuUsuarios.hide();
            navegarPara(
                    "/fxml/gestao_usuarios.fxml",
                    "Gestão de Usuários"
            );
        });

        subMenuUsuarios = criarPopup(
                btnUsuarios,
                btnCadastrar,
                btnGerir
        );
    }

    // =========================================================
    // SUBMENU PARTIDAS
    // =========================================================

    private void configurarSubMenuPartidas() {

        Button btnCadastrarPartida =
                new Button("Cadastrar Partida");

        btnCadastrarPartida.getStyleClass().add("submenu-item");
        btnCadastrarPartida.setOnAction(e -> {
            subMenuPartidas.hide();
            navegarPara(
                    "/fxml/cadastro_partida.fxml",
                    "Cadastro de Partida"
            );
        });

        Button btnResultado =
                new Button("Registrar Resultado");

        btnResultado.getStyleClass().add("submenu-item");
        btnResultado.setOnAction(e -> {
            subMenuPartidas.hide();
            navegarPara(
                    "/fxml/cadastro_resultado.fxml",
                    "Registro de Resultado"
            );
        });

        Button btnConsulta =
                new Button("Consultar Partidas");

        btnConsulta.getStyleClass().add("submenu-item");
        btnConsulta.setOnAction(e -> {
            subMenuPartidas.hide();
            navegarPara(
                    "/fxml/lista_partidas.fxml",
                    "Consulta de Partidas"
            );
        });

        subMenuPartidas = criarPopup(
                btnPartidas,
                btnCadastrarPartida,
                btnResultado,
                btnConsulta
        );
    }

    // =========================================================
    // MÉTODO GENÉRICO PARA POPUPS
    // =========================================================

    private Popup criarPopup(Button botaoPai, Button... itens) {

        VBox conteudo = new VBox(itens);

        conteudo.getStyleClass().add("submenu-popup");
        conteudo.getStylesheets().add(
                getClass()
                        .getResource("/css/menu.css")
                        .toExternalForm()
        );

        Popup popup = new Popup();

        popup.getContent().add(conteudo);
        popup.setAutoHide(true);

        botaoPai.setOnMouseEntered(e -> {

            double x =
                    botaoPai.localToScreen(0, 0).getX();

            double y =
                    botaoPai.localToScreen(0, 0).getY()
                            + botaoPai.getHeight();

            popup.show(botaoPai, x, y);
        });

        return popup;
    }

    // =========================================================
    // NAVEGAÇÃO
    // =========================================================

    @FXML
    private void irHome() {
        System.out.println("Home");
    }

    @FXML
    private void irJogadores() {
        navegarPara("/fxml/jogadores.fxml", "Jogadores");
    }

    @FXML
    private void irEquipes() {
        navegarPara("/fxml/equipes.fxml", "Equipes");
    }

    @FXML
    private void irPartidas() {
        // submenu
    }

    @FXML
    private void irEstadios() {
        navegarPara("/fxml/estadios.fxml", "Estádios");
    }

    @FXML
    private void irArbitros() {
        navegarPara("/fxml/arbitros.fxml", "Árbitros");
    }

    @FXML
    private void irIngressos() {
        navegarPara("/fxml/ingressos.fxml", "Ingressos");
    }

    @FXML
    private void irVendas() {
        navegarPara("/fxml/vendas.fxml", "Vendas");
    }

    @FXML
    private void irDesignacoes() {
        navegarPara("/fxml/designacoes.fxml", "Designações");
    }

    @FXML
    private void irUsuarios() {
        // submenu
    }

    @FXML
    private void handleLogout() {

        SessaoUsuario
                .getInstancia()
                .encerrarSessao();

        navegarPara(
                "/fxml/login.fxml",
                "Login — Copa do Mundo 2026"
        );
    }

    // =========================================================
    // UTILITÁRIOS
    // =========================================================

    private void navegarPara(
            String fxmlPath,
            String titulo
    ) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(fxmlPath)
                    );

            Stage stage =
                    (Stage) btnHome.getScene().getWindow();

            stage.setScene(
                    new Scene(loader.load(), 900, 600)
            );

            stage.setTitle(titulo);

        } catch (Exception e) {

            System.out.println(
                    "Tela ainda não implementada: "
                            + fxmlPath
            );

            e.printStackTrace();
        }
    }

    private void esconderTodos() {

        for (Button b : new Button[]{
                btnJogadores,
                btnEquipes,
                btnPartidas,
                btnEstadios,
                btnArbitros,
                btnIngressos,
                btnVendas,
                btnDesignacoes,
                btnUsuarios
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
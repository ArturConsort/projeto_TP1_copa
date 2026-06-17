package controllersJavaFX;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import modelo.classes.Usuario;
import modelo.enumerations.TipoPerfil;
import servicos.usuario.SessaoUsuario;

public class MenuController {

    @FXML private ImageView imagemFundo;

    @FXML private Button btnHome;
    @FXML private Button btnJogadores;
    @FXML private Button btnEquipes;
    @FXML private Button btnPartidas;
    @FXML private Button btnEstadios;
    @FXML private Button btnArbitros;
    @FXML private Button btnIngressos;
    @FXML private Button btnValidarIngresso;
    @FXML private Button btnDesignacoes;
    @FXML private Button btnUsuarios;
    @FXML private Label labelUsuario;
    @FXML private Button btnRelatorios;

    private Popup subMenuUsuarios;
    private Popup subMenuPartidas;
    private Popup subMenuEstadios;
    private Popup subMenuArbitros;
    private Popup subMenuDesignacoes;

    @FXML
    public void initialize() {

        // Limita a imagem ao tamanho da janela, mantendo a proporção
        imagemFundo.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                StackPane pai = (StackPane) imagemFundo.getParent();
                imagemFundo.fitWidthProperty().bind(pai.widthProperty());
                imagemFundo.fitHeightProperty().bind(pai.heightProperty());
                imagemFundo.setPreserveRatio(true);
            }
        });

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
                            btnValidarIngresso,
                            btnDesignacoes,
                            btnUsuarios,
                            btnRelatorios
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
                            btnValidarIngresso,
                            btnRelatorios
                    );

            case ARBITRO ->
                    mostrar(
                            btnDesignacoes
                    );
        }

        if (btnUsuarios.isVisible()) {
            configurarSubMenuUsuarios();
        }

        if (btnPartidas.isVisible()) {
            configurarSubMenuPartidas();
        }
        if (btnEstadios.isVisible()) {
            configurarSubMenuEstadios();
        }
        if (btnArbitros.isVisible()) {
            configurarSubMenuArbitros();
        }
        if (btnDesignacoes.isVisible()) {
            configurarSubMenuDesignacoes();
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

        Button btnCategorias =
                new Button("Categorias de Ingresso");

        btnCategorias.getStyleClass().add("submenu-item");
        btnCategorias.setOnAction(e -> {
            subMenuPartidas.hide();
            navegarPara(
                    "/fxml/gerenciar_categorias_partida.fxml",
                    "Gerenciar Categorias de Ingresso por Partida"
            );
        });

        // Mostrar "Categorias de Ingresso" apenas para administradores
        Usuario logadoAdmin = SessaoUsuario.getInstancia().getUsuarioLogado();
        btnCategorias.setVisible(logadoAdmin != null &&
                logadoAdmin.getPerfil() == TipoPerfil.ADMINISTRADOR);

        subMenuPartidas = criarPopup(
                btnPartidas,
                btnCadastrarPartida,
                btnResultado,
                btnConsulta,
                btnCategorias
        );
    }

    // =========================================================
    // SUBMENU ESTÁDIOS
    // =========================================================

    private void configurarSubMenuEstadios() {

        Button btnCadastrar = new Button("Cadastrar Estádio");
        btnCadastrar.getStyleClass().add("submenu-item");
        btnCadastrar.setOnAction(e -> {
            subMenuEstadios.hide();
            navegarParaEstadios("/fxml/cadastro_estadio.fxml", "Cadastro de Estádio");
        });

        Button btnConsultar = new Button("Consultar Estádios");
        btnConsultar.getStyleClass().add("submenu-item");
        btnConsultar.setOnAction(e -> {
            subMenuEstadios.hide();
            navegarParaEstadios("/fxml/consulta_estadio.fxml", "Consulta de Estádios");
        });

        subMenuEstadios = criarPopup(btnEstadios, btnCadastrar, btnConsultar);
    }

    // =========================================================
    // SUBMENU ÁRBITROS
    // =========================================================

    private void configurarSubMenuArbitros() {

        Button btnCadastrar = new Button("Cadastrar Árbitro");

        btnCadastrar.getStyleClass().add("submenu-item");
        btnCadastrar.setOnAction(e -> {
            subMenuArbitros.hide();
            navegarParaArbitros(
                    "/fxml/cadastro_arbitro.fxml",
                    "Cadastro de Árbitro"
            );
        });

        Button btnConsultar = new Button("Consultar Árbitros");

        btnConsultar.getStyleClass().add("submenu-item");
        btnConsultar.setOnAction(e -> {
            subMenuArbitros.hide();
            navegarParaArbitros(
                    "/fxml/consulta_arbitro.fxml",
                    "Consulta de Árbitros"
            );
        });

        subMenuArbitros = criarPopup(
                btnArbitros,
                btnCadastrar,
                btnConsultar
        );
    }

    // =========================================================
    // SUBMENU DESIGNAÇÕES
    // =========================================================

    private void configurarSubMenuDesignacoes() {

        Button btnCadastrar = new Button("Cadastrar Designação");

        btnCadastrar.getStyleClass().add("submenu-item");
        btnCadastrar.setOnAction(e -> {
            subMenuDesignacoes.hide();
            navegarParaDesignacoes(
                    "/fxml/cadastro_designacao.fxml",
                    "Cadastro de Designação"
            );
        });

        Button btnConsultar = new Button("Consultar Designações");

        btnConsultar.getStyleClass().add("submenu-item");
        btnConsultar.setOnAction(e -> {
            subMenuDesignacoes.hide();
            navegarParaDesignacoes(
                    "/fxml/consulta_designacao.fxml",
                    "Consulta de Designações"
            );
        });

        subMenuDesignacoes = criarPopup(
                btnDesignacoes,
                btnCadastrar,
                btnConsultar
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

        PauseTransition delay = new PauseTransition(Duration.millis(120));
        delay.setOnFinished(e -> {
            if (!conteudo.isHover() && !botaoPai.isHover()) {
                popup.hide();
            }
        });

        botaoPai.setOnMouseExited(e -> delay.playFromStart());

        conteudo.setOnMouseExited(e -> {
            if (!botaoPai.isHover()) {
                popup.hide();
            }
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
        // submenu
    }

    @FXML
    private void irArbitros() {
        // submenu
    }

    @FXML
    private void irIngressos() {
        navegarPara("/fxml/ingressos.fxml", "Ingressos");
    }

    @FXML
    private void irValidarIngresso() {
        navegarPara("/fxml/validar_ingresso.fxml", "Validar Ingresso");
    }


    @FXML
    private void irDesignacoes() {
        // submenu
    }

    @FXML
    private void irUsuarios() {
        // submenu
    }

    @FXML
    private void irRelatorios() { navegarPara("/fxml/relatorios.fxml", "Relatórios"); }

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

    private void navegarPara(String fxmlPath, String titulo) {

        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource(fxmlPath));

            Stage stage = (Stage) btnHome.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 900, 600));
            stage.setTitle(titulo);

        } catch (Exception e) {
            System.out.println("Tela ainda não implementada: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void navegarParaEstadios(String fxmlPath, String titulo) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = (Stage) btnHome.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 900, 600));
            stage.setTitle(titulo);

            Object controller = loader.getController();
            if (controller instanceof CadastroEstadioController c) {
                c.setServico(new servicos.EstadioServico(
                        new persistencia.EstadioDAO(),
                        new persistencia.PartidaDAO()
                ));
            } else if (controller instanceof ConsultaEstadioController c) {
                servicos.EstadioServico svc = new servicos.EstadioServico(
                        new persistencia.EstadioDAO(),
                        new persistencia.PartidaDAO()
                );
                c.setServico(svc);
                c.carregarDadosIniciais();
            }

        } catch (Exception e) {
            System.out.println("Erro ao carregar tela de estádios: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void navegarParaArbitros(String fxmlPath, String titulo) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = (Stage) btnHome.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 900, 600));
            stage.setTitle(titulo);

            Object controller = loader.getController();
            if (controller instanceof CadastroArbitroController c) {
                c.setServico(new servicos.ArbitroServico(
                        new persistencia.ArbitroDAO()
                ));
            } else if (controller instanceof ConsultaArbitroController c) {
                servicos.ArbitroServico svc = new servicos.ArbitroServico(
                        new persistencia.ArbitroDAO()
                );
                c.setServico(svc);
                c.carregarDadosIniciais();
            }

        } catch (Exception e) {
            System.out.println("Erro ao carregar tela de árbitros: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void navegarParaDesignacoes(String fxmlPath, String titulo) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = (Stage) btnHome.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 900, 600));
            stage.setTitle(titulo);

            Object controller = loader.getController();
            if (controller instanceof CadastroDesignacaoController c) {
                c.setServicos(
                        new servicos.DesignacaoArbitroServico(new persistencia.DesignacaoArbitroDAO()),
                        new servicos.ArbitroServico(new persistencia.ArbitroDAO()),
                        new servicos.Partida.PartidaService()
                );
                c.carregarDadosIniciais();
            } else if (controller instanceof ConsultaDesignacaoController c) {
                c.setServico(new servicos.DesignacaoArbitroServico(new persistencia.DesignacaoArbitroDAO()));
                c.carregarDadosIniciais();
            }

        } catch (Exception e) {
            System.out.println("Erro ao carregar tela de designações: " + fxmlPath);
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
                btnValidarIngresso,
                btnDesignacoes,
                btnUsuarios,
                btnRelatorios
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
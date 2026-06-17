package controllersJavaFX;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import modelo.classes.Jogador;
import modelo.classes.Selecao;
import modelo.classes.StatusJogador;
import modelo.classes.Usuario;
import persistencia.SelecaoDAO;
import servicos.usuario.SessaoUsuario;

import java.util.List;

public class CadastroJogadorController {

    @FXML private Label labelUsuarioLogado;
    @FXML private TextField campoNome;
    @FXML private TextField campoIdade;
    @FXML private TextField campoNumeracao;

    @FXML private ComboBox<String> comboSelecao;
    @FXML private ComboBox<String> comboPosicao;
    @FXML private ComboBox<String> comboStatus;

    @FXML private Label labelFeedback;

    private SelecaoDAO selecaoDAO = new SelecaoDAO();

    @FXML
    public void initialize() {

        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (logado != null) {
            labelUsuarioLogado.setText(logado.getNome() + " · " + logado.getPerfil());
        }

        // carrega as selecoes cadastradas no combo
        List<Selecao> selecoes = selecaoDAO.carregaLista();
        List<String> nomesSelecoes = selecoes.stream()
                .map(Selecao::getPais)
                .toList();
        comboSelecao.setItems(FXCollections.observableArrayList(nomesSelecoes));

        comboPosicao.setItems(FXCollections.observableArrayList(
                "Goleiro", "Defensor", "Meio-Campo", "Atacante"
        ));

        comboStatus.setItems(FXCollections.observableArrayList(
                "ATIVO", "LESIONADO", "SUSPENSO"
        ));
    }

    @FXML
    private void handleCadastrar() {

        // --- validacoes de campos vazios --- //
        if (campoNome.getText().isBlank()) {
            mostrarErro("Informe o nome do jogador.");
            return;
        }

        if (comboSelecao.getValue() == null) {
            mostrarErro("Selecione uma seleção.");
            return;
        }

        if (campoIdade.getText().isBlank()) {
            mostrarErro("Informe a idade.");
            return;
        }

        if (campoNumeracao.getText().isBlank()) {
            mostrarErro("Informe o número da camisa.");
            return;
        }

        if (comboPosicao.getValue() == null) {
            mostrarErro("Selecione uma posição.");
            return;
        }

        if (comboStatus.getValue() == null) {
            mostrarErro("Selecione um status.");
            return;
        }

        try {
            String nome      = campoNome.getText().trim();
            int idade        = Integer.parseInt(campoIdade.getText().trim());
            String numeracao = campoNumeracao.getText().trim();
            String posicao   = comboPosicao.getValue();
            StatusJogador status = StatusJogador.valueOf(comboStatus.getValue());

            // busca a selecao escolhida
            Selecao selecao = selecaoDAO.buscarPorPais(comboSelecao.getValue());
            if (selecao == null) {
                mostrarErro("Seleção não encontrada.");
                return;
            }

            // --- validacoes de regra de negocio --- //
            if (idade < 0) {
                mostrarErro("Idade inválida: deve ser positiva.");
                return;
            }

            int numero;
            try {
                numero = Integer.parseInt(numeracao);
                if (numero < 1 || numero > 26) {
                    mostrarErro("Número da camisa deve estar entre 1 e 26.");
                    return;
                }
            } catch (NumberFormatException e) {
                mostrarErro("Número da camisa deve ser um valor numérico.");
                return;
            }

            if (selecao.getJogadores().size() >= 26) {
                mostrarErro("Essa seleção já atingiu o limite de 26 jogadores.");
                return;
            }

            for (Jogador j : selecao.getJogadores()) {
                if (j.getNumeracao().equals(numeracao)) {
                    mostrarErro("Já existe um jogador com o número " + numeracao + " nessa seleção.");
                    return;
                }
            }

            // --- cadastro --- //
            Jogador novoJogador = new Jogador(nome, idade, numeracao, posicao, selecao, status);
            selecao.addJogadores(novoJogador);
            selecaoDAO.atualizaSelecao(selecao);

            labelFeedback.setStyle("-fx-text-fill: #4caf82;");
            labelFeedback.setText("Jogador cadastrado com sucesso!");
            limparCampos();

        } catch (NumberFormatException e) {
            mostrarErro("Idade deve ser um valor numérico.");
        }
    }

    @FXML
    private void handleVoltar() {
        navegarPara("/fxml/jogadores.fxml", "Jogadores");
    }

    @FXML
    private void handleLogout() {
        SessaoUsuario.getInstancia().encerrarSessao();
        navegarPara("/fxml/login.fxml", "Login — Copa do Mundo 2026");
    }

    private void mostrarErro(String mensagem) {
        labelFeedback.setStyle("-fx-text-fill: #ff6b6b;");
        labelFeedback.setText(mensagem);
    }

    private void limparCampos() {
        campoNome.clear();
        campoIdade.clear();
        campoNumeracao.clear();
        comboSelecao.setValue(null);
        comboPosicao.setValue(null);
        comboStatus.setValue(null);
    }

    private void navegarPara(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = (Stage) campoNome.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(titulo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

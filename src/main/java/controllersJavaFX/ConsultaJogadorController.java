package controllersJavaFX;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelo.classes.Jogador;
import modelo.classes.Selecao;
import modelo.classes.StatusJogador;
import persistencia.SelecaoDAO;

import java.util.ArrayList;
import java.util.List;

public class ConsultaJogadorController {

    @FXML private Button btnHome;
    @FXML private Button btnJogadores;
    @FXML private Button btnEquipes;
    @FXML private Button btnPartidas;
    @FXML private Button btnEstadios;
    @FXML private Button btnArbitros;
    @FXML private Button btnIngressos;

    @FXML private TextField campoNome;
    @FXML private TextField campoSelecao;
    @FXML private ComboBox<String> comboPosicao;
    @FXML private ComboBox<String> comboStatus;

    @FXML private Label labelTotal;

    @FXML private TableView<Jogador> tabelaJogadores;
    @FXML private TableColumn<Jogador, String>  colNome;
    @FXML private TableColumn<Jogador, String>  colSelecao;
    @FXML private TableColumn<Jogador, Integer> colIdade;
    @FXML private TableColumn<Jogador, String>  colNumeracao;
    @FXML private TableColumn<Jogador, String>  colPosicao;
    @FXML private TableColumn<Jogador, String>  colStatus;

    private final SelecaoDAO selecaoDAO = new SelecaoDAO();
    private ObservableList<Jogador> todosJogadores;

    @FXML
    public void initialize() {

        comboPosicao.setItems(FXCollections.observableArrayList(
                "Goleiro", "Defensor", "Meio-Campo", "Atacante"
        ));

        comboStatus.setItems(FXCollections.observableArrayList(
                "ATIVO", "LESIONADO", "SUSPENSO"
        ));

        // --- mapeamento das colunas --- //
        // como Jogador nao tem getSelecaoNome(), usamos cellValueFactory manual
        colNome.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getNome()));

        colSelecao.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getSelecao().getPais()));

        colIdade.setCellValueFactory(
                data -> new SimpleIntegerProperty(data.getValue().getIdade()).asObject());

        colNumeracao.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getNumeracao()));

        colPosicao.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getPosicao()));

        colStatus.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getStatus().toString()));

        carregarDados();
    }

    // percorre todas as selecoes e coleta os jogadores delas
    private List<Jogador> coletarTodosJogadores() {
        List<Jogador> todos = new ArrayList<>();
        for (Selecao s : selecaoDAO.carregaLista()) {
            todos.addAll(s.getJogadores());
        }
        return todos;
    }

    private void carregarDados() {
        todosJogadores = FXCollections.observableArrayList(coletarTodosJogadores());
        tabelaJogadores.setItems(todosJogadores);
        atualizarTotal();
    }

    @FXML
    private void handlePesquisar() {

        String nome    = campoNome.getText().trim().toLowerCase();
        String selecao = campoSelecao.getText().trim().toLowerCase();
        String posicao = comboPosicao.getValue();
        String status  = comboStatus.getValue();

        List<Jogador> resultado = coletarTodosJogadores();

        if (!nome.isBlank())
            resultado = resultado.stream()
                    .filter(j -> j.getNome().toLowerCase().contains(nome))
                    .toList();

        if (!selecao.isBlank())
            resultado = resultado.stream()
                    .filter(j -> j.getSelecao().getPais().toLowerCase().contains(selecao))
                    .toList();

        if (posicao != null && !posicao.isBlank())
            resultado = resultado.stream()
                    .filter(j -> j.getPosicao().equalsIgnoreCase(posicao))
                    .toList();

        if (status != null && !status.isBlank())
            resultado = resultado.stream()
                    .filter(j -> j.getStatus() == StatusJogador.valueOf(status))
                    .toList();

        tabelaJogadores.setItems(FXCollections.observableArrayList(resultado));
        labelTotal.setText("Total: " + resultado.size() + " jogadores");
    }

    @FXML
    private void handleLimpar() {
        campoNome.clear();
        campoSelecao.clear();
        comboPosicao.setValue(null);
        comboStatus.setValue(null);
        tabelaJogadores.setItems(todosJogadores);
        atualizarTotal();
    }

    @FXML
    private void handleAtualizar() {
        carregarDados();
    }

    @FXML
    private void handleExcluir() {

        Jogador selecionado = tabelaJogadores.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            Alert aviso = new Alert(Alert.AlertType.WARNING);
            aviso.setTitle("Aviso");
            aviso.setHeaderText(null);
            aviso.setContentText("Selecione um jogador para excluir.");
            aviso.showAndWait();
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmação");
        confirmacao.setHeaderText("Excluir jogador");
        confirmacao.setContentText("Deseja realmente excluir " + selecionado.getNome() + "?");

        if (confirmacao.showAndWait().get() == ButtonType.OK) {

            // remove o jogador da lista da selecao e salva
            Selecao selecao = selecaoDAO.buscarPorPais(selecionado.getSelecao().getPais());
            if (selecao != null) {
                selecao.getJogadores().removeIf(j -> j.getNome().equals(selecionado.getNome()));
                selecaoDAO.atualizaSelecao(selecao);
            }

            carregarDados();

            Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
            sucesso.setTitle("Sucesso");
            sucesso.setHeaderText(null);
            sucesso.setContentText("Jogador removido com sucesso.");
            sucesso.showAndWait();
        }
    }

    private void atualizarTotal() {
        labelTotal.setText("Total: " + tabelaJogadores.getItems().size() + " jogadores");
    }

    @FXML private void irHome()      { navegarPara("/fxml/menu.fxml",      "Home");      }
    @FXML private void irJogadores() { navegarPara("/fxml/jogadores.fxml", "Jogadores"); }
    @FXML private void irEquipes()   { navegarPara("/fxml/equipes.fxml",   "Equipes");   }
    @FXML private void irPartidas()  { navegarPara("/fxml/partidas.fxml",  "Partidas");  }
    @FXML private void irEstadios()  { navegarPara("/fxml/estadios.fxml",  "Estádios");  }
    @FXML private void irArbitros()  { navegarPara("/fxml/arbitros.fxml",  "Árbitros");  }
    @FXML private void irIngressos() { navegarPara("/fxml/ingressos.fxml", "Ingressos"); }

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

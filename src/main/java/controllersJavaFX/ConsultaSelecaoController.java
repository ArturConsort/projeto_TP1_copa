package controllersJavaFX;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.stage.Stage;
import modelo.classes.Selecao;
import persistencia.SelecaoDAO;

import java.util.List;
import java.util.stream.Collectors;

public class ConsultaSelecaoController {

    @FXML private Button btnHome;
    @FXML private Button btnJogadores;
    @FXML private Button btnEquipes;
    @FXML private Button btnPartidas;
    @FXML private Button btnEstadios;
    @FXML private Button btnArbitros;
    @FXML private Button btnIngressos;

    @FXML
    private ComboBox<String> comboGrupo;

    @FXML
    private TextField campoTecnico;

    @FXML
    private TextField campoPais;

    @FXML
    private Label labelTotal;

    @FXML
    private TableView<Selecao> tabelaSelecoes;

    @FXML
    private TableColumn<Selecao, String> colPais;

    @FXML
    private TableColumn<Selecao, String> colGrupo;

    @FXML
    private TableColumn<Selecao, String> colTecnico;

    @FXML
    private TableColumn<Selecao, Integer> colRanking;

    @FXML
    private TableColumn<Selecao, Integer> colTitulos;

    private final SelecaoDAO selecaoDAO = new SelecaoDAO();

    private ObservableList<Selecao> selecoes;

    @FXML
    public void initialize() {

        comboGrupo.setItems(
                FXCollections.observableArrayList(
                        "A","B","C","D",
                        "E","F","G","H"
                )
        );

        colPais.setCellValueFactory(
                new PropertyValueFactory<>("pais"));

        colGrupo.setCellValueFactory(
                new PropertyValueFactory<>("grupo"));

        colTecnico.setCellValueFactory(
                new PropertyValueFactory<>("tecnico"));

        colRanking.setCellValueFactory(
                new PropertyValueFactory<>("rankingFIFA"));

        colTitulos.setCellValueFactory(
                new PropertyValueFactory<>("titulos"));

        carregarDados();
    }

    private void carregarDados() {

        List<Selecao> lista = selecaoDAO.carregaLista();

        selecoes = FXCollections.observableArrayList(lista);

        tabelaSelecoes.setItems(selecoes);

        atualizarTotal();
    }

    @FXML
    private void handlePesquisar() {

        List<Selecao> resultado = selecaoDAO.carregaLista();

        String pais = campoPais.getText().trim().toLowerCase();
        String grupo = comboGrupo.getValue();
        String tecnico = campoTecnico.getText().trim().toLowerCase();

        if (!pais.isBlank()) {

            resultado = resultado.stream()
                    .filter(s -> s.getPais()
                            .toLowerCase()
                            .contains(pais))
                    .toList();
        }

        if (grupo != null && !grupo.isBlank()) {

            resultado = resultado.stream()
                    .filter(s -> s.getGrupo()
                            .equalsIgnoreCase(grupo))
                    .toList();
        }

        if (!tecnico.isBlank()) {

            resultado = resultado.stream()
                    .filter(s -> s.getTecnico()
                            .toLowerCase()
                            .contains(tecnico))
                    .toList();
        }

        tabelaSelecoes.setItems(
                FXCollections.observableArrayList(resultado));

        labelTotal.setText(
                "Total: " + resultado.size() + " seleções");
    }
    @FXML
    private void handleLimpar() {

        comboGrupo.setValue(null);
        campoPais.clear();
        campoTecnico.clear();

        tabelaSelecoes.setItems(selecoes);

        atualizarTotal();
    }

    @FXML
    private void handleAtualizar() {

        carregarDados();
    }

    @FXML
    private void handleExcluir() {

        Selecao selecionada =
                tabelaSelecoes.getSelectionModel()
                        .getSelectedItem();

        if (selecionada == null) {

            Alert alerta = new Alert(
                    Alert.AlertType.WARNING);

            alerta.setTitle("Aviso");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "Selecione uma seleção para excluir.");

            alerta.showAndWait();
            return;
        }

        Alert confirmacao =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmacao.setTitle("Confirmação");
        confirmacao.setHeaderText("Excluir seleção");
        confirmacao.setContentText(
                "Deseja realmente excluir "
                        + selecionada.getPais() + "?");

        if (confirmacao.showAndWait().get()
                == ButtonType.OK) {

            selecaoDAO.remover(
                    selecionada.getPais());

            carregarDados();

            Alert sucesso =
                    new Alert(Alert.AlertType.INFORMATION);

            sucesso.setTitle("Sucesso");
            sucesso.setHeaderText(null);
            sucesso.setContentText(
                    "Seleção removida com sucesso.");

            sucesso.showAndWait();
        }
    }

    private void atualizarTotal() {

        labelTotal.setText(
                "Total: " +
                        tabelaSelecoes.getItems().size() +
                        " seleções");
    }

    @FXML private void irHome()      { navegarPara("/fxml/menu.fxml",      "Home");      }
    @FXML private void irJogadores() { navegarPara("/fxml/jogadores.fxml", "Jogadores"); }
    @FXML private void irEquipes()   { navegarPara("/fxml/equipes.fxml", "Jogadores"); }
    @FXML private void irPartidas()  { navegarPara("/fxml/partidas.fxml",  "Partidas");  }
    @FXML private void irEstadios()  { navegarPara("/fxml/estadios.fxml",  "Estádios");  }
    @FXML private void irArbitros()  { navegarPara("/fxml/arbitros.fxml",  "Árbitros");  }
    @FXML private void irIngressos() { navegarPara("/fxml/ingressos.fxml", "Ingressos"); }

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
}
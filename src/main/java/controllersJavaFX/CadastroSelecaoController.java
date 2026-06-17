package controllersJavaFX;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelo.classes.Selecao;
import modelo.classes.Usuario;
import persistencia.SelecaoDAO;
import servicos.usuario.SessaoUsuario;

public class CadastroSelecaoController {

    @FXML private Label labelUsuarioLogado;

    @FXML private TextField campoPais;
    @FXML private TextField campoTecnico;
    @FXML private TextField campoRanking;
    @FXML private TextField campoTitulos;

    @FXML private ComboBox<String> comboGrupo;

    @FXML private Label labelFeedback;

    @FXML
    public void initialize() {

        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (logado != null) {
            labelUsuarioLogado.setText(logado.getNome() + " · " + logado.getPerfil());
        }

        comboGrupo.setItems(FXCollections.observableArrayList(
                "A", "B", "C", "D", "E", "F", "G", "H"
        ));
    }

    @FXML
    private void handleCadastrar() {

        if (campoPais.getText().isBlank()) {
            labelFeedback.setText("Informe o país.");
            return;
        }
        if (comboGrupo.getValue() == null) {
            labelFeedback.setText("Selecione um grupo.");
            return;
        }
        if (campoTecnico.getText().isBlank()) {
            labelFeedback.setText("Informe o técnico.");
            return;
        }
        if (campoRanking.getText().isBlank()) {
            labelFeedback.setText("Informe o ranking FIFA.");
            return;
        }
        if (campoTitulos.getText().isBlank()) {
            labelFeedback.setText("Informe a quantidade de títulos.");
            return;
        }

        try {
            String pais    = campoPais.getText();
            String grupo   = comboGrupo.getValue();
            String tecnico = campoTecnico.getText();
            int ranking    = Integer.parseInt(campoRanking.getText());
            int titulos    = Integer.parseInt(campoTitulos.getText());

            Selecao add = new Selecao(pais, grupo, "teste", tecnico, ranking, titulos);
            SelecaoDAO dao = new SelecaoDAO();
            dao.salvar(add);

            labelFeedback.setStyle("-fx-text-fill: #4caf82;");
            labelFeedback.setText("Seleção cadastrada com sucesso!");
            limparCampos();

        } catch (NumberFormatException e) {
            labelFeedback.setStyle("-fx-text-fill: #ff6b6b;");
            labelFeedback.setText("Ranking FIFA e Títulos devem ser números.");
        }
    }

    @FXML
    private void handleVoltar() {
        navegarPara("/fxml/equipes.fxml", "Equipes");
    }

    @FXML
    private void handleLogout() {
        SessaoUsuario.getInstancia().encerrarSessao();
        navegarPara("/fxml/login.fxml", "Login — Copa do Mundo 2026");
    }

    private void limparCampos() {
        campoPais.clear();
        campoTecnico.clear();
        campoRanking.clear();
        campoTitulos.clear();
        comboGrupo.setValue(null);
    }

    private void navegarPara(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = (Stage) campoPais.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(titulo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package controllersJavaFX;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modelo.classes.Estadio;
import modelo.classes.Partida;
import modelo.classes.Selecao;
import modelo.enumerations.FasePartida;
import servicos.Partida.PartidaService;

public class CadastroPartidaController {

    @FXML private ComboBox<Selecao>     comboTimeCasa;
    @FXML private ComboBox<Selecao>     comboTimeVisitante;
    @FXML private ComboBox<Estadio>     comboEstadio;
    @FXML private ComboBox<FasePartida> comboFase;
    @FXML private TextField             campoCidade;
    @FXML private TextField             campoRodada;
    @FXML private TextField             campoData;
    @FXML private TextField             campoHorario;

    private final PartidaService service = new PartidaService();

    // Partida selecionada para edição — null quando estiver cadastrando
    private Partida partidaEmEdicao = null;

    @FXML
    public void initialize() {
        // Popula ComboBoxes com dados dos arquivos ao abrir a tela
        comboTimeCasa.setItems(FXCollections.observableArrayList(service.listarSelecoes()));
        comboTimeVisitante.setItems(FXCollections.observableArrayList(service.listarSelecoes()));
        comboEstadio.setItems(FXCollections.observableArrayList(service.listarEstadios()));
        comboFase.setItems(FXCollections.observableArrayList(FasePartida.values()));
    }

    @FXML
    private void aoSalvar() {
        try {
            Selecao     timeCasa      = comboTimeCasa.getValue();
            Selecao     timeVisitante = comboTimeVisitante.getValue();
            Estadio     estadio       = comboEstadio.getValue();
            FasePartida fase          = comboFase.getValue();
            String      cidade        = campoCidade.getText();
            String      rodada        = campoRodada.getText();
            String      data          = campoData.getText();
            String      horario       = campoHorario.getText();

            if (partidaEmEdicao != null) {
                // Modo edição — atualiza a partida existente
                partidaEmEdicao.setTimeCasa(timeCasa);
                partidaEmEdicao.setTimeVisitante(timeVisitante);
                partidaEmEdicao.setEstadio(estadio);
                partidaEmEdicao.setFase(fase);
                partidaEmEdicao.setCidade(cidade);
                partidaEmEdicao.setData(data);
                partidaEmEdicao.setHorario(horario);
                service.atualizarPartida(partidaEmEdicao);
                mostrarSucesso("Partida atualizada com sucesso!");
            } else {
                // Modo cadastro — cria nova partida
                service.cadastrarPartida(timeCasa, timeVisitante, estadio,
                        cidade, data, horario, rodada, fase);
                mostrarSucesso("Partida cadastrada com sucesso!");
            }

            limpar();

        } catch (Exception e) {
            mostrarErro(e.getMessage());
        }
    }

    @FXML
    private void aoEditar() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Editar Partida");
        dialog.setHeaderText("Informe o número da partida:");
        dialog.showAndWait().ifPresent(numeroStr -> {
            try {
                int numero = Integer.parseInt(numeroStr.trim());
                Partida p = service.listarPartidas()
                        .stream()
                        .filter(x -> x.getNumeroPartidas() == numero)
                        .findFirst()
                        .orElse(null);

                if (p == null) {
                    mostrarErro("Partida não encontrada!");
                    return;
                }

                // Preenche os campos com os dados da partida para edição
                partidaEmEdicao = p;
                comboTimeCasa.setValue(p.getTimeCasa());
                comboTimeVisitante.setValue(p.getTimeVisitante());
                comboEstadio.setValue(p.getEstadio());
                comboFase.setValue(p.getFase());
                campoCidade.setText(p.getCidade());
                campoData.setText(p.getData());
                campoHorario.setText(p.getHorario());

            } catch (NumberFormatException e) {
                mostrarErro("Número de partida inválido!");
            }
        });
    }

    @FXML
    private void aoExcluir() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Excluir Partida");
        dialog.setHeaderText("Informe o número da partida a excluir:");
        dialog.showAndWait().ifPresent(numeroStr -> {
            try {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                        "Tem certeza que deseja excluir esta partida?",
                        ButtonType.YES, ButtonType.NO);
                confirm.showAndWait().ifPresent(btn -> {
                    if (btn == ButtonType.YES) {
                        try {
                            service.removerPartida(Integer.parseInt(numeroStr.trim()));
                            mostrarSucesso("Partida excluída com sucesso!");
                            limpar();
                        } catch (Exception e) {
                            mostrarErro(e.getMessage());
                        }
                    }
                });
            } catch (NumberFormatException e) {
                mostrarErro("Número de partida inválido!");
            }
        });
    }

    private void limpar() {
        comboTimeCasa.setValue(null);
        comboTimeVisitante.setValue(null);
        comboEstadio.setValue(null);
        comboFase.setValue(null);
        campoCidade.clear();
        campoRodada.clear();
        campoData.clear();
        campoHorario.clear();
        partidaEmEdicao = null;
    }

    private void mostrarSucesso(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }

    private void mostrarErro(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
}

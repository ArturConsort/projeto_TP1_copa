package controllersJavaFX;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelo.classes.*;
import modelo.enumerations.TipoPerfil;
import servicos.IngressoServico;
import servicos.VendaServico;
import servicos.usuario.SessaoCompra;
import servicos.usuario.SessaoUsuario;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CompraIngressoController {

    // ── Botões do HUD ───────────────────────────────────────
    @FXML private Button btnHome;
    @FXML private Button btnJogadores;
    @FXML private Button btnEquipes;
    @FXML private Button btnPartidas;
    @FXML private Button btnEstadios;
    @FXML private Button btnArbitros;
    @FXML private Button btnIngressos;

    // ── Resumo do pedido ────────────────────────────────────
    @FXML private Label labelConfrontoResumo;
    @FXML private Label labelDataResumo;
    @FXML private Label labelSetorResumo;
    @FXML private Label labelPrecoResumo;
    @FXML private Label labelTotal;
    @FXML private Spinner<Integer> spinnerQuantidade;

    // ── Dados do comprador ──────────────────────────────────
    @FXML private TextField campoNome;
    @FXML private TextField campoCpf;
    @FXML private TextField campoEmail;
    @FXML private TextField campoPais;

    // ── Pagamento ────────────────────────────────────────────
    @FXML private ComboBox<String> comboPagamento;
    @FXML private VBox painelDadosCartao;
    @FXML private TextField campoNumeroCartao;
    @FXML private TextField campoNomeCartao;
    @FXML private TextField campoValidadeCartao;
    @FXML private TextField campoCvvCartao;
    @FXML private Label labelPagamentoInfo;

    private final IngressoServico ingressoServico = new IngressoServico();
    private final VendaServico vendaServico = new VendaServico();

    // ── Inicialização ───────────────────────────────────────

    @FXML
    public void initialize() {

        Partida partida = SessaoCompra.getInstancia().getPartidaSelecionada();
        CategoriaIngresso categoria = SessaoCompra.getInstancia().getCategoriaSelecionada();

        if (partida == null || categoria == null) {
            labelConfrontoResumo.setText("Nenhum ingresso selecionado");
            spinnerQuantidade.setDisable(true);
            return;
        }

        String nomeCasa = partida.getTimeCasa() != null ? partida.getTimeCasa().getPais() : "?";
        String nomeVisitante = partida.getTimeVisitante() != null ? partida.getTimeVisitante().getPais() : "?";

        labelConfrontoResumo.setText(nomeCasa + " x " + nomeVisitante);
        labelDataResumo.setText(partida.getData() + " - " + partida.getHorario());
        labelSetorResumo.setText("Setor: " + categoria.getNome());
        labelPrecoResumo.setText(String.format("Preço unitário: R$ %.2f", categoria.getPreco()));

        int maximo = Math.max(categoria.getEstoque(), 1);

        spinnerQuantidade.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maximo, 1)
        );

        spinnerQuantidade.valueProperty().addListener((obs, antigo, novo) -> atualizarTotal());

        atualizarTotal();

        // pre-preenche dados do usuario logado, se houver
        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (logado != null) {
            campoNome.setText(logado.getNome());
            campoCpf.setText(logado.getCpf());
            campoEmail.setText(logado.getEmail());
            campoPais.setText(logado.getPais());
        }

        // meios de pagamento
        comboPagamento.setItems(FXCollections.observableArrayList(
                "Cartão de crédito",
                "Cartão de débito",
                "Pix",
                "Boleto bancário"
        ));

        comboPagamento.valueProperty().addListener((obs, antigo, novo) -> atualizarPainelPagamento(novo));

        // estado inicial: nenhum meio selecionado, oculta dados do cartao
        painelDadosCartao.setVisible(false);
        painelDadosCartao.setManaged(false);
    }

    private void atualizarTotal() {

        CategoriaIngresso categoria = SessaoCompra.getInstancia().getCategoriaSelecionada();
        if (categoria == null) return;

        int quantidade = spinnerQuantidade.getValue();
        double total = categoria.getPreco() * quantidade;

        labelTotal.setText(String.format("Total: R$ %.2f", total));
    }

    private void atualizarPainelPagamento(String meio) {

        if (meio == null) {
            painelDadosCartao.setVisible(false);
            painelDadosCartao.setManaged(false);
            labelPagamentoInfo.setText("");
            return;
        }

        boolean exibirCartao = meio.equals("Cartão de crédito") || meio.equals("Cartão de débito");

        painelDadosCartao.setVisible(exibirCartao);
        painelDadosCartao.setManaged(exibirCartao);

        switch (meio) {
            case "Pix" -> labelPagamentoInfo.setText("Um QR Code para pagamento via Pix será gerado após a confirmação.");
            case "Boleto bancário" -> labelPagamentoInfo.setText("O boleto será enviado para o e-mail informado e deve ser pago em até 2 dias úteis.");
            default -> labelPagamentoInfo.setText("");
        }
    }

    // ── Ações ────────────────────────────────────────────────

    @FXML
    private void aoConfirmarCompra() {

        Partida partida = SessaoCompra.getInstancia().getPartidaSelecionada();
        CategoriaIngresso categoria = SessaoCompra.getInstancia().getCategoriaSelecionada();

        if (partida == null || categoria == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecione uma partida e um setor antes de continuar.");
            return;
        }

        if (campoNome.getText().isBlank()
                || campoCpf.getText().isBlank()
                || campoEmail.getText().isBlank()
                || campoPais.getText().isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Preencha todos os dados do comprador.");
            return;
        }

        String meioPagamento = comboPagamento.getValue();
        if (meioPagamento == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecione um meio de pagamento.");
            return;
        }

        boolean exigeCartao = meioPagamento.equals("Cartão de crédito") || meioPagamento.equals("Cartão de débito");
        if (exigeCartao && (
                campoNumeroCartao.getText().isBlank()
                        || campoNomeCartao.getText().isBlank()
                        || campoValidadeCartao.getText().isBlank()
                        || campoCvvCartao.getText().isBlank())) {
            mostrarAlerta(Alert.AlertType.WARNING, "Preencha os dados do cartão.");
            return;
        }

        int quantidade = spinnerQuantidade.getValue();

        if (!categoria.temVagasDisponiveis() || categoria.getEstoque() < quantidade) {
            mostrarAlerta(Alert.AlertType.WARNING, "Não há vagas suficientes nesse setor.");
            return;
        }

        try {

            Usuario cliente = SessaoUsuario.getInstancia().getUsuarioLogado();

            if (cliente == null) {
                cliente = new Usuario(
                        campoNome.getText(),
                        campoCpf.getText(),
                        campoEmail.getText(),
                        campoPais.getText(),
                        campoCpf.getText(),
                        "",
                        TipoPerfil.OPERADOR
                );
            }

            String idVenda = "VND-" + System.currentTimeMillis();
            String dataVenda = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            Venda venda = new Venda(idVenda, dataVenda, cliente, 0.0, "ABERTA");
            vendaServico.cadastrar(venda);

            for (int i = 0; i < quantidade; i++) {
                Ingresso ingresso = new Ingresso(partida, categoria);
                ingressoServico.cadastrar(ingresso);
                vendaServico.adicionarIngresso(idVenda, ingresso);
            }

            vendaServico.finalizarVenda(idVenda);

            mostrarAlerta(Alert.AlertType.INFORMATION,
                    "Compra realizada com sucesso!\nPagamento via " + meioPagamento + ".\nID da venda: " + idVenda);

            SessaoCompra.getInstancia().encerrar();
            navegarPara("/fxml/ingressos.fxml", "Ingressos");

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao concluir a compra: " + e.getMessage());
        }
    }

    @FXML
    private void aoVoltar() {
        navegarPara("/fxml/selecao_setor.fxml", "Ingressos — Seleção de Setor");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Compra de ingresso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    // ── Navegação pelo HUD ──────────────────────────────────

    @FXML private void irHome()      { navegarPara("/fxml/menu.fxml",      "Home");      }
    @FXML private void irJogadores() { navegarPara("/fxml/jogadores.fxml", "Jogadores"); }
    @FXML private void irEquipes()   { navegarPara("/fxml/equipes.fxml",   "Equipes");   }
    @FXML private void irPartidas()  { navegarPara("/fxml/partidas.fxml",  "Partidas");  }
    @FXML private void irEstadios()  { navegarPara("/fxml/estadios.fxml",  "Estádios");  }
    @FXML private void irArbitros()  { navegarPara("/fxml/arbitros.fxml",  "Árbitros");  }
    @FXML private void irIngressos() { navegarPara("/fxml/ingressos.fxml", "Ingressos"); }

    // ── Utilitário ──────────────────────────────────────────

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

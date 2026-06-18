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
import persistencia.IngressoDAO;
import servicos.IngressoServico;
import servicos.VendaServico;
import servicos.usuario.SessaoCompra;
import servicos.usuario.SessaoUsuario;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class CompraIngressoController {

    // ── Navbar ──────────────────────────────────────────────
    @FXML private Label labelUsuarioLogado;

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

        // ── Sincroniza o contador de IDs antes de qualquer coisa ────────────
        // Garante que novos Ingresso() criados nesta sessão nunca repitam IDs
        // de ingressos já gravados em execuções anteriores.
        new IngressoDAO().carregaLista();

        Usuario usuarioLogadoNavbar = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (usuarioLogadoNavbar != null) {
            labelUsuarioLogado.setText(usuarioLogadoNavbar.getNome() + " · " + usuarioLogadoNavbar.getPerfil());
        }

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

        // ── Formatação automática do CPF ────────────────────
        campoCpf.textProperty().addListener((obs, antigo, novo) -> {
            if (novo == null) return;
            String digitos = novo.replaceAll("[^0-9]", "");
            if (digitos.length() > 11) digitos = digitos.substring(0, 11);
            String formatado = formatarCpf(digitos);
            if (!formatado.equals(novo)) {
                campoCpf.setText(formatado);
                campoCpf.positionCaret(formatado.length());
            }
        });

        // ── Formatação automática do número do cartão ───────
        campoNumeroCartao.textProperty().addListener((obs, antigo, novo) -> {
            if (novo == null) return;
            String digitos = novo.replaceAll("[^0-9]", "");
            if (digitos.length() > 16) digitos = digitos.substring(0, 16);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digitos.length(); i++) {
                if (i > 0 && i % 4 == 0) sb.append(' ');
                sb.append(digitos.charAt(i));
            }
            String formatado = sb.toString();
            if (!formatado.equals(novo)) {
                campoNumeroCartao.setText(formatado);
                campoNumeroCartao.positionCaret(formatado.length());
            }
        });

        // ── Formatação automática de validade (MM/AA) ───────
        campoValidadeCartao.textProperty().addListener((obs, antigo, novo) -> {
            if (novo == null) return;
            String digitos = novo.replaceAll("[^0-9]", "");
            if (digitos.length() > 4) digitos = digitos.substring(0, 4);
            String formatado = digitos.length() > 2
                    ? digitos.substring(0, 2) + "/" + digitos.substring(2)
                    : digitos;
            if (!formatado.equals(novo)) {
                campoValidadeCartao.setText(formatado);
                campoValidadeCartao.positionCaret(formatado.length());
            }
        });

        // ── Apenas dígitos no CVV ────────────────────────────
        campoCvvCartao.textProperty().addListener((obs, antigo, novo) -> {
            if (novo == null) return;
            String digitos = novo.replaceAll("[^0-9]", "");
            if (digitos.length() > 4) digitos = digitos.substring(0, 4);
            if (!digitos.equals(novo)) {
                campoCvvCartao.setText(digitos);
                campoCvvCartao.positionCaret(digitos.length());
            }
        });

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

    // ── Formatação de CPF ────────────────────────────────────

    private String formatarCpf(String digitos) {
        StringBuilder sb = new StringBuilder(digitos);
        if (sb.length() > 9) sb.insert(9, '-');
        if (sb.length() > 6) sb.insert(6, '.');
        if (sb.length() > 3) sb.insert(3, '.');
        return sb.toString();
    }

    // ── Validações ───────────────────────────────────────────

    private boolean cpfValido(String cpf) {
        String digitos = cpf.replaceAll("[^0-9]", "");
        return digitos.length() == 11;
    }

    private boolean emailValido(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private boolean cartaoValido(String numero) {
        String digitos = numero.replaceAll("[^0-9]", "");
        if (digitos.length() < 13 || digitos.length() > 19) return false;
        int soma = 0;
        boolean alternar = false;
        for (int i = digitos.length() - 1; i >= 0; i--) {
            int d = Character.getNumericValue(digitos.charAt(i));
            if (alternar) { d *= 2; if (d > 9) d -= 9; }
            soma += d;
            alternar = !alternar;
        }
        return soma % 10 == 0;
    }

    private boolean validadeCartaoValida(String validade) {
        if (validade == null || !validade.matches("\\d{2}/\\d{2}")) return false;
        try {
            int mes = Integer.parseInt(validade.substring(0, 2));
            int ano = Integer.parseInt(validade.substring(3)) + 2000;
            if (mes < 1 || mes > 12) return false;
            return !YearMonth.of(ano, mes).isBefore(YearMonth.now());
        } catch (Exception e) {
            return false;
        }
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

        if (!cpfValido(campoCpf.getText())) {
            mostrarAlerta(Alert.AlertType.WARNING, "CPF inválido. Verifique o número informado.");
            campoCpf.requestFocus();
            return;
        }

        if (!emailValido(campoEmail.getText())) {
            mostrarAlerta(Alert.AlertType.WARNING, "E-mail inválido. Use o formato: usuario@dominio.com");
            campoEmail.requestFocus();
            return;
        }

        String meioPagamento = comboPagamento.getValue();
        if (meioPagamento == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecione um meio de pagamento.");
            return;
        }

        boolean exigeCartao = meioPagamento.equals("Cartão de crédito") || meioPagamento.equals("Cartão de débito");
        if (exigeCartao) {
            if (campoNumeroCartao.getText().isBlank()
                    || campoNomeCartao.getText().isBlank()
                    || campoValidadeCartao.getText().isBlank()
                    || campoCvvCartao.getText().isBlank()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Preencha todos os dados do cartão.");
                return;
            }
            if (!cartaoValido(campoNumeroCartao.getText())) {
                mostrarAlerta(Alert.AlertType.WARNING, "Número de cartão inválido.");
                campoNumeroCartao.requestFocus();
                return;
            }
            if (!validadeCartaoValida(campoValidadeCartao.getText())) {
                mostrarAlerta(Alert.AlertType.WARNING, "Validade do cartão inválida ou expirada. Use o formato MM/AA.");
                campoValidadeCartao.requestFocus();
                return;
            }
            if (campoCvvCartao.getText().replaceAll("[^0-9]", "").length() < 3) {
                mostrarAlerta(Alert.AlertType.WARNING, "CVV inválido. Deve conter 3 ou 4 dígitos.");
                campoCvvCartao.requestFocus();
                return;
            }
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

            // finalizarVenda persiste o estoque atualizado dentro de partidas.dat
            vendaServico.finalizarVenda(idVenda);

            mostrarAlerta(Alert.AlertType.INFORMATION,
                    "Compra realizada com sucesso!\nPagamento via " + meioPagamento + ".\nID da venda: " + idVenda);

            SessaoCompra.getInstancia().encerrar();
            navegarPara("/fxml/meus_ingressos.fxml", "Meus Ingressos");

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

    // ── Navegação ───────────────────────────────────────────

    @FXML
    private void handleLogout() {
        SessaoUsuario.getInstancia().encerrarSessao();
        navegarPara("/fxml/login.fxml", "Login — Copa do Mundo 2026");
    }

    // ── Utilitário ──────────────────────────────────────────

    private void navegarPara(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = (Stage) labelUsuarioLogado.getScene().getWindow();
            double w = stage.getWidth();
            double h = stage.getHeight();
            stage.setScene(new Scene(loader.load()));
            stage.setWidth(w);
            stage.setHeight(h);
            stage.setTitle(titulo);
        } catch (Exception e) {
            System.out.println("Tela ainda não implementada: " + fxmlPath);
            e.printStackTrace();
        }
    }
}

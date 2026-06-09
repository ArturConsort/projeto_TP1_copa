package app;

import controlers.UsuarioControle;
import modelo.classes.*;
import modelo.enumerations.CategoriaArbitro;
import modelo.enumerations.TipoGramado;
import modelo.enumerations.TipoPerfil;
import persistencia.ArbitroDAO;
import persistencia.DesignacaoArbitroDAO;
import persistencia.EstadioDAO;
import persistencia.PartidaDAO;
import servicos.ArbitroServico;
import servicos.CategoriaIngressoServico;
import servicos.DesignacaoArbitroServico;
import servicos.EstadioServico;
import servicos.IngressoServico;
import servicos.Partida.PartidaService;
import servicos.VendaServico;
import servicos.usuario.SessaoUsuario;
import servicos.usuario.UsuarioServico;
import modelo.classes.Selecao;
import modelo.classes.Partida;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import modelo.enumerations.FasePartida;

public class Programa {

    private static Scanner entrada = new Scanner(System.in);

    private static UsuarioServico servico = new UsuarioServico();
    private static CategoriaIngressoServico categoriaServico = new CategoriaIngressoServico();
    private static IngressoServico ingressoServico = new IngressoServico();
    private static VendaServico vendaServico = new VendaServico();
    private static ArbitroServico arbitroServico = new ArbitroServico(new ArbitroDAO());
    private static DesignacaoArbitroServico designacaoServico = new DesignacaoArbitroServico(new DesignacaoArbitroDAO());
    private static EstadioServico estadioServico = new EstadioServico(new EstadioDAO(), new PartidaDAO());
    private static PartidaService partidaServico = new PartidaService();

    private static UsuarioControle usuarioControle = new UsuarioControle(entrada);


    public static void main(String[] args){


        // +++++ CASO SEJA A PRIMEIRA VEZ RODANDO O CODIGO, DESCOMENTE ESSA PERTE, RODE UMA VEZ, COMENTE, E RODE DE NOVO//
        // O USUARIO ADM É CRIADO, LOGIN:0000, SENHA: SENHA123

        //Usuario novo = new Usuario("ADM", "000.000.000-00", "adm@gmail.com", "Brasil", "0000", "senha123", TipoPerfil.ADMINISTRADOR);
        //SessaoUsuario.getInstancia().iniciarSessao(novo);
        //servico.cadastrar(novo);
        //SessaoUsuario.getInstancia().encerrarSessao();
        //return;




        limparTela();


        // ------- login ------- //
        while(true) {
            System.out.println("=== ENTRAR ===");
            System.out.print("login: ");
            String login = entrada.nextLine();
            System.out.print("senha: ");
            String senha = entrada.nextLine();

            try {
                servico.login(login, senha);
                break;
            } catch (Exception e) {
                limparTela();
                System.out.println("Erro ao entrar: " + e.getMessage());
            }
        }



        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
        TipoPerfil perfil = logado.getPerfil();

        boolean opInvalida = false;
        boolean rodando = true;
        while (rodando){

            limparTela();
            if(opInvalida == true) System.out.println("= opção invalida =");
            opInvalida = false;
            System.out.println("=== Bem vindo " + logado.getNome() + " ===");
            exibeMenu(logado.getPerfil());

            String op = entrada.nextLine();
            switch(op){

                case "1" -> usuarioControle.listarUsuarios();
                case "2" -> usuarioControle.gerarRelatorio();

                case "3" -> {
                    if (perfil == TipoPerfil.ADMINISTRADOR || perfil == TipoPerfil.ORGANIZADOR)
                        gerirPartida();
                    else opInvalida = true;
                }

                case "4" -> {
                    if (perfil == TipoPerfil.ADMINISTRADOR || perfil == TipoPerfil.ORGANIZADOR)
                        gerirSelecao();
                    else opInvalida = true;
                }

                case "5" -> {
                    if (perfil == TipoPerfil.ADMINISTRADOR || perfil == TipoPerfil.ARBITRO)
                        visualizarPartida();
                    else opInvalida = true;
                }

                case "6" -> {
                    if (perfil == TipoPerfil.ADMINISTRADOR || perfil == TipoPerfil.OPERADOR)
                        menuIngressos();
                    else opInvalida = true;
                }

                case "7" -> {
                    if (perfil == TipoPerfil.ADMINISTRADOR || perfil == TipoPerfil.OPERADOR)
                        menuVendas();
                    else opInvalida = true;
                }

                case "8" -> {
                    if (perfil == TipoPerfil.ADMINISTRADOR || perfil == TipoPerfil.ORGANIZADOR)
                        menuEstadios();
                    else opInvalida = true;
                }

                case "9" -> {
                    if (perfil == TipoPerfil.ADMINISTRADOR || perfil == TipoPerfil.ORGANIZADOR)
                        menuArbitros();
                    else opInvalida = true;
                }

                case "10" -> {
                    if (perfil == TipoPerfil.ADMINISTRADOR || perfil == TipoPerfil.ORGANIZADOR)
                        menuDesignacoes();
                    else opInvalida = true;
                }

                case "11" -> {
                    if (perfil == TipoPerfil.ADMINISTRADOR)
                        usuarioControle.criarConta();
                    else opInvalida = true;
                }

                case "12" -> {
                    if (perfil == TipoPerfil.ADMINISTRADOR)
                        usuarioControle.removerConta();
                    else opInvalida = true;
                }

                case "13" -> {
                    if (perfil == TipoPerfil.ADMINISTRADOR)
                        usuarioControle.editaConta();
                    else opInvalida = true;
                }

                case "0" ->{
                    entrada.close();
                    servico.logout();
                    rodando = false;
                    System.out.println("Sessão encerrada");
                }

                default -> opInvalida = true;
            }
        }
    }




    // ================================================================
    //   MENU INGRESSOS  (opção 6)
    // ================================================================

    private static void menuIngressos() {
        boolean rodando = true;
        while (rodando) {
            limparTela();
            System.out.println("=== INGRESSOS ===");
            System.out.println("(1) Cadastrar categoria de ingresso");
            System.out.println("(2) Listar categorias de ingresso");
            System.out.println("(3) Atualizar preço de categoria");
            System.out.println("(4) Cadastrar ingresso");
            System.out.println("(5) Buscar ingresso por ID");
            System.out.println("(6) Validar entrada de ingresso");
            System.out.println("(0) Voltar ao menu");
            System.out.print("\nEscolha uma opção: ");
            String op = entrada.nextLine();

            switch (op) {
                case "1" -> cadastrarCategoria();
                case "2" -> listarCategorias();
                case "3" -> atualizarPrecoCategoria();
                case "4" -> cadastrarIngresso();
                case "5" -> buscarIngresso();
                case "6" -> validarIngresso();
                case "0" -> rodando = false;
                default  -> System.out.println("Opção inválida.");
            }
        }
    }


    private static void cadastrarCategoria() {
        limparTela();
        System.out.println("=== CADASTRAR CATEGORIA DE INGRESSO ===");
        try {
            System.out.print("Nome da categoria: ");
            String nome = entrada.nextLine();

            System.out.print("Preço (ex: 350.00): ");
            double preco = Double.parseDouble(entrada.nextLine().replace(",", "."));

            System.out.print("Estoque (quantidade): ");
            int estoque = Integer.parseInt(entrada.nextLine());

            CategoriaIngresso cat = new CategoriaIngresso(nome, preco, estoque);
            categoriaServico.cadastrar(cat);
            System.out.println("\nCategoria cadastrada com sucesso!");
        } catch (NumberFormatException e) {
            System.out.println("Erro: valor inválido. Use apenas números.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }


    private static void listarCategorias() {
        limparTela();
        System.out.println("=== CATEGORIAS DE INGRESSO ===");
        List<CategoriaIngresso> lista = categoriaServico.pesquisar(null, null);
        if (lista.isEmpty()) {
            System.out.println("Nenhuma categoria cadastrada.");
        } else {
            lista.forEach(c -> System.out.println(
                    "Nome: " + c.getNome() +
                            " | Preço: R$" + String.format("%.2f", c.getPreco()) +
                            " | Estoque: " + c.getEstoque() +
                            " | Vagas: " + (c.temVagasDisponiveis() ? "sim" : "esgotado")
            ));
        }
        pausar();
    }


    private static void atualizarPrecoCategoria() {
        limparTela();
        System.out.println("=== ATUALIZAR PREÇO DE CATEGORIA ===");
        try {
            System.out.print("Nome da categoria: ");
            String nome = entrada.nextLine();

            System.out.print("Novo preço (ex: 400.00): ");
            double novoPreco = Double.parseDouble(entrada.nextLine().replace(",", "."));

            categoriaServico.atualizarPreco(nome, novoPreco);
            System.out.println("\nPreço atualizado com sucesso!");
        } catch (NumberFormatException e) {
            System.out.println("Erro: valor inválido.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }


    private static void cadastrarIngresso() {
        limparTela();
        System.out.println("=== CADASTRAR INGRESSO ===");
        try {
            System.out.print("Nome da categoria: ");
            String nomeCategoria = entrada.nextLine();

            CategoriaIngresso cat = categoriaServico.buscarPorNome(nomeCategoria);
            if (cat == null) {
                System.out.println("Categoria não encontrada: " + nomeCategoria);
                pausar();
                return;
            }

            Ingresso ing = new Ingresso(null, cat);
            ingressoServico.cadastrar(ing);
            System.out.println("\nIngresso cadastrado com sucesso!");
            System.out.println("ID gerado: " + ing.getIdIngresso());
            System.out.println("Preço efetivo: R$" + String.format("%.2f", ing.getPrecoEfetivo()));
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }


    private static void buscarIngresso() {
        limparTela();
        System.out.println("=== BUSCAR INGRESSO ===");
        try {
            System.out.print("ID do ingresso: ");
            int id = Integer.parseInt(entrada.nextLine());

            Ingresso ing = ingressoServico.buscarPorId(id);
            if (ing == null) {
                System.out.println("Ingresso não encontrado.");
            } else {
                System.out.println("\nIngresso encontrado:");
                System.out.println("ID:        " + ing.getIdIngresso());
                System.out.println("Categoria: " + (ing.getCategoria() != null ? ing.getCategoria().getNome() : "sem categoria"));
                System.out.println("Preço:     R$" + String.format("%.2f", ing.getPrecoEfetivo()));
                System.out.println("Validado:  " + (ing.isFoiValidado() ? "sim" : "não"));
            }
        } catch (NumberFormatException e) {
            System.out.println("Erro: ID inválido. Digite apenas números.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }


    private static void validarIngresso() {
        limparTela();
        System.out.println("=== VALIDAR ENTRADA ===");
        try {
            System.out.print("ID do ingresso: ");
            int id = Integer.parseInt(entrada.nextLine());

            boolean resultado = ingressoServico.validarEntrada(id);
            if (resultado) {
                System.out.println("\nEntrada validada com sucesso! ✓");
            } else {
                System.out.println("\nIngresso já foi validado anteriormente.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Erro: ID inválido. Digite apenas números.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }




    // ================================================================
    //   MENU VENDAS  (opção 7)
    // ================================================================

    private static void menuVendas() {
        boolean rodando = true;
        while (rodando) {
            limparTela();
            System.out.println("=== VENDAS ===");
            System.out.println("(1) Abrir nova venda");
            System.out.println("(2) Adicionar ingresso a venda");
            System.out.println("(3) Finalizar venda");
            System.out.println("(4) Cancelar venda");
            System.out.println("(5) Buscar venda por ID");
            System.out.println("(6) Listar vendas");
            System.out.println("(0) Voltar ao menu");
            System.out.print("\nEscolha uma opção: ");
            String op = entrada.nextLine();

            switch (op) {
                case "1" -> abrirVenda();
                case "2" -> adicionarIngressoVenda();
                case "3" -> finalizarVenda();
                case "4" -> cancelarVenda();
                case "5" -> buscarVenda();
                case "6" -> listarVendas();
                case "0" -> rodando = false;
                default  -> System.out.println("Opção inválida.");
            }
        }
    }


    private static void abrirVenda() {
        limparTela();
        System.out.println("=== ABRIR NOVA VENDA ===");
        try {
            System.out.print("ID da venda (ex: VND-001): ");
            String id = entrada.nextLine();

            System.out.print("Data (ex: 2025-06-15): ");
            String data = entrada.nextLine();

            System.out.print("Login do cliente: ");
            String loginCliente = entrada.nextLine();

            Usuario cliente = servico.buscarPorLogin(loginCliente);
            if (cliente == null) {
                System.out.println("Cliente não encontrado: " + loginCliente);
                pausar();
                return;
            }

            Venda venda = new Venda(id, data, cliente, 0.0, "ABERTA");
            vendaServico.cadastrar(venda);
            System.out.println("\nVenda aberta com sucesso!");
            System.out.println("ID: " + id + " | Cliente: " + cliente.getNome() + " | Status: ABERTA");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }


    private static void adicionarIngressoVenda() {
        limparTela();
        System.out.println("=== ADICIONAR INGRESSO À VENDA ===");
        try {
            System.out.print("ID da venda: ");
            String idVenda = entrada.nextLine();

            System.out.print("ID do ingresso: ");
            int idIngresso = Integer.parseInt(entrada.nextLine());

            Ingresso ing = ingressoServico.buscarPorId(idIngresso);
            if (ing == null) {
                System.out.println("Ingresso não encontrado: " + idIngresso);
                pausar();
                return;
            }

            vendaServico.adicionarIngresso(idVenda, ing);
            System.out.println("\nIngresso adicionado com sucesso!");
            System.out.println("Ingresso: " + idIngresso + " | Preço: R$" + String.format("%.2f", ing.getPrecoEfetivo()));
        } catch (NumberFormatException e) {
            System.out.println("Erro: ID do ingresso inválido. Digite apenas números.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }


    private static void finalizarVenda() {
        limparTela();
        System.out.println("=== FINALIZAR VENDA ===");
        try {
            System.out.print("ID da venda: ");
            String id = entrada.nextLine();

            vendaServico.finalizarVenda(id);

            Venda v = vendaServico.buscarPorId(id);
            System.out.println("\nVenda finalizada com sucesso!");
            System.out.println("Total: R$" + String.format("%.2f", v.getValorTotal()));
            System.out.println("Status: " + v.getStatus());
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }


    private static void cancelarVenda() {
        limparTela();
        System.out.println("=== CANCELAR VENDA ===");
        try {
            System.out.print("ID da venda: ");
            String id = entrada.nextLine();

            System.out.print("Tem certeza que deseja cancelar? (1) sim  (2) não: ");
            String conf = entrada.nextLine();
            if (!conf.equals("1")) {
                System.out.println("Cancelamento abortado.");
                pausar();
                return;
            }

            vendaServico.cancelarVenda(id);
            System.out.println("\nVenda cancelada.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }


    private static void buscarVenda() {
        limparTela();
        System.out.println("=== BUSCAR VENDA ===");
        try {
            System.out.print("ID da venda: ");
            String id = entrada.nextLine();

            Venda v = vendaServico.buscarPorId(id);
            if (v == null) {
                System.out.println("Venda não encontrada.");
            } else {
                exibeVenda(v);
            }
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }


    private static void listarVendas() {
        limparTela();
        System.out.println("=== LISTAR VENDAS ===");
        System.out.println("Filtrar por status (ABERTA, FINALIZADA, CANCELADA) ou Enter para todas: ");
        String statusFiltro = entrada.nextLine();
        if (statusFiltro.isBlank()) statusFiltro = null;

        List<Venda> lista = vendaServico.pesquisar(null, statusFiltro);
        if (lista.isEmpty()) {
            System.out.println("Nenhuma venda encontrada.");
        } else {
            lista.forEach(v -> System.out.println(
                    "ID: " + v.getIdVenda() +
                            " | Data: " + v.getDataVenda() +
                            " | Cliente: " + (v.getCliente() != null ? v.getCliente().getNome() : "-") +
                            " | Total: R$" + String.format("%.2f", v.getValorTotal()) +
                            " | Status: " + v.getStatus() +
                            " | Ingressos: " + v.getIngressos().size()
            ));
        }
        pausar();
    }


    private static void exibeVenda(Venda v) {
        System.out.println("\n--- Detalhes da venda ---");
        System.out.println("ID:        " + v.getIdVenda());
        System.out.println("Data:      " + v.getDataVenda());
        System.out.println("Cliente:   " + (v.getCliente() != null ? v.getCliente().getNome() : "-"));
        System.out.println("Status:    " + v.getStatus());
        System.out.println("Total:     R$" + String.format("%.2f", v.getValorTotal()));
        System.out.println("Ingressos: " + v.getIngressos().size());
        v.getIngressos().forEach(i -> System.out.println(
                "  - " + i.getIdIngresso() +
                        " | " + (i.getCategoria() != null ? i.getCategoria().getNome() : "sem categoria") +
                        " | R$" + String.format("%.2f", i.getPrecoEfetivo()) +
                        " | validado: " + (i.isFoiValidado() ? "sim" : "não")
        ));
    }




    // ================================================================
    //   MENU ESTÁDIOS  (opção 8)
    // ================================================================

    private static void menuEstadios() {
        boolean rodando = true;
        while (rodando) {
            limparTela();
            System.out.println("=== ESTÁDIOS ===");
            System.out.println("(1) Cadastrar estádio");
            System.out.println("(2) Listar estádios");
            System.out.println("(3) Buscar estádio por nome");
            System.out.println("(4) Verificar disponibilidade");
            System.out.println("(5) Remover estádio");
            System.out.println("(0) Voltar ao menu");
            System.out.print("\nEscolha uma opção: ");
            String op = entrada.nextLine();

            switch (op) {
                case "1" -> cadastrarEstadio();
                case "2" -> listarEstadios();
                case "3" -> buscarEstadio();
                case "4" -> verificarDisponibilidadeEstadio();
                case "5" -> removerEstadio();
                case "0" -> rodando = false;
                default  -> System.out.println("Opção inválida.");
            }
        }
    }


    private static void cadastrarEstadio() {
        limparTela();
        System.out.println("=== CADASTRAR ESTÁDIO ===");
        try {
            System.out.print("Nome: ");
            String nome = entrada.nextLine();

            System.out.print("Cidade: ");
            String cidade = entrada.nextLine();

            System.out.print("Estado: ");
            String estado = entrada.nextLine();

            System.out.print("Capacidade: ");
            int capacidade = Integer.parseInt(entrada.nextLine());

            System.out.println("Tipo de gramado (" + tiposGramadoDisponiveis() + "): ");
            String tipoStr = entrada.nextLine().toUpperCase();
            TipoGramado tipoGramado = TipoGramado.valueOf(tipoStr);

            estadioServico.cadastrarEstadio(nome, cidade, estado, capacidade, tipoGramado);
            System.out.println("\nEstádio cadastrado com sucesso!");
        } catch (NumberFormatException e) {
            System.out.println("Erro: capacidade inválida. Use apenas números.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: tipo de gramado inválido.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }


    private static void listarEstadios() {
        limparTela();
        System.out.println("=== LISTA DE ESTÁDIOS ===");
        try {
            List<Estadio> lista = estadioServico.listarEstadios();
            if (lista.isEmpty()) {
                System.out.println("Nenhum estádio cadastrado.");
            } else {
                lista.forEach(e -> System.out.println(
                        "Nome: " + e.getNome() +
                                " | Localização: " + e.getLocalizacao() +
                                " | Capacidade: " + e.getCapacidade() +
                                " | Gramado: " + e.getTipoGramado()
                ));
            }
        }
        catch (Exception e){
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }


    private static void buscarEstadio() {
        limparTela();
        System.out.println("=== BUSCAR ESTÁDIO ===");
        try {
            System.out.print("Nome do estádio: ");
            String nome = entrada.nextLine();

            Estadio e = estadioServico.buscarPorNome(nome);
            if (e == null) {
                System.out.println("Estádio não encontrado.");
            } else {
                exibeEstadio(e);
            }
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }


    private static void verificarDisponibilidadeEstadio() {
        limparTela();
        System.out.println("=== VERIFICAR DISPONIBILIDADE DE ESTÁDIO ===");
        try {
            System.out.print("Nome do estádio: ");
            String nome = entrada.nextLine();

            Estadio estadio = estadioServico.buscarPorNome(nome);
            if (estadio == null) {
                System.out.println("Estádio não encontrado: " + nome);
                pausar();
                return;
            }

            System.out.print("Data (ex: 2025-06-15): ");
            String data = entrada.nextLine();

            System.out.print("Horário (ex: 16:00): ");
            String horario = entrada.nextLine();

            estadioServico.verificarDisponibilidade(estadio, data, horario);
            System.out.println("\nEstádio disponível na data e horário informados!");
        } catch (Exception e) {
            System.out.println("Estádio indisponível: " + e.getMessage());
        }
        pausar();
    }


    private static void removerEstadio() {
        limparTela();
        System.out.println("=== REMOVER ESTÁDIO ===");
        try {
            System.out.print("Nome do estádio: ");
            String nome = entrada.nextLine();

            Estadio encontrado = estadioServico.buscarPorNome(nome);
            if (encontrado == null) {
                System.out.println("Estádio não encontrado: " + nome);
                pausar();
                return;
            }

            exibeEstadio(encontrado);
            System.out.println("\nATENÇÃO: você está prestes a remover este estádio permanentemente.");
            System.out.print("(1) confirmar remoção  (2) cancelar: ");
            String conf = entrada.nextLine();
            if (!conf.equals("1")) {
                System.out.println("Remoção cancelada.");
                pausar();
                return;
            }

            estadioServico.removerEstadio(nome);
            System.out.println("\nEstádio removido com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }


    private static void exibeEstadio(Estadio e) {
        System.out.println("\n--- Detalhes do estádio ---");
        System.out.println("Nome:        " + e.getNome());
        System.out.println("Cidade:      " + e.getCidade());
        System.out.println("Estado:      " + e.getEstado());
        System.out.println("Localização: " + e.getLocalizacao());
        System.out.println("Capacidade:  " + e.getCapacidade());
        System.out.println("Gramado:     " + e.getTipoGramado());
    }


    private static String tiposGramadoDisponiveis() {
        StringBuilder sb = new StringBuilder();
        TipoGramado[] valores = TipoGramado.values();
        for (int i = 0; i < valores.length; i++) {
            sb.append(valores[i].name());
            if (i < valores.length - 1) sb.append(", ");
        }
        return sb.toString();
    }




    // ================================================================
    //   MENU ÁRBITROS  (opção 9)
    // ================================================================

    private static void menuArbitros() {
        boolean rodando = true;
        while (rodando) {
            limparTela();
            System.out.println("=== ÁRBITROS ===");
            System.out.println("(1) Cadastrar árbitro");
            System.out.println("(2) Listar árbitros");
            System.out.println("(3) Buscar árbitro por nome");
            System.out.println("(4) Remover árbitro");
            System.out.println("(0) Voltar ao menu");
            System.out.print("\nEscolha uma opção: ");
            String op = entrada.nextLine();

            switch (op) {
                case "1" -> cadastrarArbitro();
                case "2" -> listarArbitros();
                case "3" -> buscarArbitro();
                case "4" -> removerArbitro();
                case "0" -> rodando = false;
                default  -> System.out.println("Opção inválida.");
            }
        }
    }


    private static void cadastrarArbitro() {
        limparTela();
        System.out.println("=== CADASTRAR ÁRBITRO ===");
        try {
            System.out.print("Nome: ");
            String nome = entrada.nextLine();

            System.out.print("Idade: ");
            int idade = Integer.parseInt(entrada.nextLine());

            System.out.println("Categoria (" + categoriasArbitroDisponiveis() + "): ");
            String catStr = entrada.nextLine().toUpperCase();
            CategoriaArbitro categoria = CategoriaArbitro.valueOf(catStr);

            System.out.print("Anos de experiência: ");
            int experiencia = Integer.parseInt(entrada.nextLine());

            System.out.print("Nacionalidade: ");
            String nacionalidade = entrada.nextLine();

            Usuario solicitante = SessaoUsuario.getInstancia().getUsuarioLogado();
            arbitroServico.cadastrarArbitro(nome, idade, categoria, experiencia, nacionalidade);
            System.out.println("\nÁrbitro cadastrado com sucesso!");
        }
        catch (NumberFormatException e) {
            System.out.println("Erro: valor numérico inválido.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: categoria de árbitro inválida.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }


    private static void listarArbitros() {
        limparTela();
        System.out.println("=== LISTA DE ÁRBITROS ===");
        try {
            List<Arbitro> lista = arbitroServico.listarArbitros();
            if (lista.isEmpty()) {
                System.out.println("Nenhum árbitro cadastrado.");
            } else {
                lista.forEach(a -> System.out.println(
                        "Nome: " + a.getNome() +
                                " | Idade: " + a.getIdade() +
                                " | Categoria: " + a.getCategoria() +
                                " | Experiência: " + a.getExperiencia() + " anos" +
                                " | Nacionalidade: " + a.getNacionalidade()
                ));
            }
        }
        catch (Exception e){
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }


    private static void buscarArbitro() {
        limparTela();
        System.out.println("=== BUSCAR ÁRBITRO ===");
        try {
            System.out.print("Nome do árbitro: ");
            String nome = entrada.nextLine();

            Arbitro a = arbitroServico.buscarPorNome(nome);
            if (a == null) {
                System.out.println("Árbitro não encontrado.");
            } else {
                exibeArbitro(a);
            }
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }


    private static void removerArbitro() {
        limparTela();
        System.out.println("=== REMOVER ÁRBITRO ===");
        try {
            System.out.print("Nome do árbitro: ");
            String nome = entrada.nextLine();

            Arbitro encontrado = arbitroServico.buscarPorNome(nome);
            if (encontrado == null) {
                System.out.println("Árbitro não encontrado: " + nome);
                pausar();
                return;
            }

            exibeArbitro(encontrado);
            System.out.println("\nATENÇÃO: você está prestes a remover este árbitro permanentemente.");
            System.out.print("(1) confirmar remoção  (2) cancelar: ");
            String conf = entrada.nextLine();
            if (!conf.equals("1")) {
                System.out.println("Remoção cancelada.");
                pausar();
                return;
            }

            Usuario solicitante = SessaoUsuario.getInstancia().getUsuarioLogado();
            arbitroServico.removerArbitro(nome);
            System.out.println("\nÁrbitro removido com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }


    private static void exibeArbitro(Arbitro a) {
        System.out.println("\n--- Detalhes do árbitro ---");
        System.out.println("Nome:          " + a.getNome());
        System.out.println("Idade:         " + a.getIdade());
        System.out.println("Categoria:     " + a.getCategoria());
        System.out.println("Experiência:   " + a.getExperiencia() + " anos");
        System.out.println("Nacionalidade: " + a.getNacionalidade());
    }


    private static String categoriasArbitroDisponiveis() {
        StringBuilder sb = new StringBuilder();
        CategoriaArbitro[] valores = CategoriaArbitro.values();
        for (int i = 0; i < valores.length; i++) {
            sb.append(valores[i].name());
            if (i < valores.length - 1) sb.append(", ");
        }
        return sb.toString();
    }




    // ================================================================
    //   MENU DESIGNAÇÕES  (opção 10)
    // ================================================================

    private static void menuDesignacoes() {
        boolean rodando = true;
        while (rodando) {
            limparTela();
            System.out.println("=== DESIGNAÇÕES DE ÁRBITROS ===");
            System.out.println("(1) Criar designação");
            System.out.println("(2) Listar designações");
            System.out.println("(3) Buscar designação por partida");
            System.out.println("(4) Remover designação");
            System.out.println("(0) Voltar ao menu");
            System.out.print("\nEscolha uma opção: ");
            String op = entrada.nextLine();

            switch (op) {
                case "1" -> criarDesignacao();
                case "2" -> listarDesignacoes();
                case "3" -> buscarDesignacao();
                case "4" -> removerDesignacao();
                case "0" -> rodando = false;
                default  -> System.out.println("Opção inválida.");
            }
        }
    }


    private static void criarDesignacao() {
        limparTela();
        System.out.println("=== CRIAR DESIGNAÇÃO DE ÁRBITRO ===");
        try {
            System.out.print("Número da partida: ");
            int numeroPartida = Integer.parseInt(entrada.nextLine());

            // Busca a partida pelo número
            Partida partida = buscarPartidaPorNumero(numeroPartida);
            if (partida == null) {
                System.out.println("Partida não encontrada: " + numeroPartida);
                pausar();
                return;
            }

            System.out.print("Nome do árbitro principal: ");
            String nomePrincipal = entrada.nextLine();
            Arbitro principal = arbitroServico.buscarPorNome(nomePrincipal);
            if (principal == null) {
                System.out.println("Árbitro não encontrado: " + nomePrincipal);
                pausar();
                return;
            }

            List<Arbitro> assistentes = new ArrayList<>();
            System.out.println("Adicionar árbitros assistentes (Enter em branco para finalizar):");
            while (true) {
                System.out.print("Nome do assistente: ");
                String nomeAssistente = entrada.nextLine();
                if (nomeAssistente.isBlank()) break;

                Arbitro assistente = arbitroServico.buscarPorNome(nomeAssistente);
                if (assistente == null) {
                    System.out.println("Árbitro não encontrado: " + nomeAssistente + ". Ignorando.");
                } else {
                    assistentes.add(assistente);
                    System.out.println("Assistente adicionado: " + assistente.getNome());
                }
            }

            designacaoServico.criarDesignacao(partida, principal, assistentes);
            System.out.println("\nDesignação criada com sucesso!");
            System.out.println("Partida: " + numeroPartida +
                    " | Principal: " + principal.getNome() +
                    " | Assistentes: " + assistentes.size());
        } catch (NumberFormatException e) {
            System.out.println("Erro: número de partida inválido. Digite apenas números.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }


    private static void listarDesignacoes() {
        limparTela();
        System.out.println("=== LISTA DE DESIGNAÇÕES ===");
        try {
            List<DesignacaoArbitro> lista = designacaoServico.listarDesignacoes();
            if (lista.isEmpty()) {
                System.out.println("Nenhuma designação cadastrada.");
            } else {
                lista.forEach(d -> {
                    Partida p = d.getPartida();
                    String infoPartida = (p != null) ? "Partida " + p.getNumeroPartidas() : "Partida desconhecida";
                    String principal = (d.getPrincipalArbitro() != null) ? d.getPrincipalArbitro().getNome() : "-";
                    int qtdAssistentes = (d.getAssistentes() != null) ? d.getAssistentes().size() : 0;
                    System.out.println(infoPartida +
                            " | Principal: " + principal +
                            " | Assistentes: " + qtdAssistentes);
                });
            }
        }
        catch (Exception e){
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }


    private static void buscarDesignacao() {
        limparTela();
        System.out.println("=== BUSCAR DESIGNAÇÃO POR PARTIDA ===");
        try {
            System.out.print("Número da partida: ");
            int numeroPartida = Integer.parseInt(entrada.nextLine());

            DesignacaoArbitro designacao = designacaoServico.buscarPorPartida(numeroPartida);
            if (designacao == null) {
                System.out.println("Designação não encontrada para a partida " + numeroPartida + ".");
            } else {
                exibeDesignacao(designacao);
            }
        } catch (NumberFormatException e) {
            System.out.println("Erro: número inválido. Digite apenas números.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }


    private static void removerDesignacao() {
        limparTela();
        System.out.println("=== REMOVER DESIGNAÇÃO ===");
        try {
            System.out.print("Número da partida: ");
            int numeroPartida = Integer.parseInt(entrada.nextLine());

            DesignacaoArbitro encontrada = designacaoServico.buscarPorPartida(numeroPartida);
            if (encontrada == null) {
                System.out.println("Designação não encontrada para a partida " + numeroPartida + ".");
                pausar();
                return;
            }

            exibeDesignacao(encontrada);
            System.out.println("\nATENÇÃO: você está prestes a remover esta designação permanentemente.");
            System.out.print("(1) confirmar remoção  (2) cancelar: ");
            String conf = entrada.nextLine();
            if (!conf.equals("1")) {
                System.out.println("Remoção cancelada.");
                pausar();
                return;
            }

            designacaoServico.removerDesignacao(numeroPartida);
            System.out.println("\nDesignação removida com sucesso.");
        } catch (NumberFormatException e) {
            System.out.println("Erro: número inválido. Digite apenas números.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }


    private static void exibeDesignacao(DesignacaoArbitro d) {
        Partida p = d.getPartida();
        System.out.println("\n--- Detalhes da designação ---");
        System.out.println("Partida:    " + (p != null ? "Nº " + p.getNumeroPartidas() : "-"));
        System.out.println("Principal:  " + (d.getPrincipalArbitro() != null ? d.getPrincipalArbitro().getNome() : "-"));

        List<Arbitro> assistentes = d.getAssistentes();
        if (assistentes == null || assistentes.isEmpty()) {
            System.out.println("Assistentes: nenhum");
        } else {
            System.out.println("Assistentes:");
            assistentes.forEach(a -> System.out.println(
                    "  - " + a.getNome() +
                            " | " + a.getCategoria() +
                            " | " + a.getNacionalidade()
            ));
        }
    }


    /**
     * Busca uma Partida pelo número a partir das designações existentes,
     * já que PartidaDAO não está disponível diretamente nesta classe.
     * Retorna null se não encontrada.
     */
    private static Partida buscarPartidaPorNumero(int numeroPartida) {
        try {
            DesignacaoArbitro d = designacaoServico.buscarPorPartida(numeroPartida);
            if (d != null) return d.getPartida();
        } catch (Exception ignored) {}
        return null;
    }




    // ================================================================
    //   MÉTODOS AUXILIARES
    // ================================================================

    private static void pausar() {
        System.out.println("\nPressione Enter para continuar...");
        entrada.nextLine();
    }

    public static void limparTela() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }


    private static void exibeMenu(TipoPerfil perfil) {

        System.out.println("(1): Listar usuários");
        System.out.println("(2): Gerar relatório");

        if (perfil == TipoPerfil.ADMINISTRADOR || perfil == TipoPerfil.ORGANIZADOR) {
            System.out.println("(3): Gerir partidas");
            System.out.println("(4): Gerir seleções");
        }
        if (perfil == TipoPerfil.ADMINISTRADOR || perfil == TipoPerfil.ARBITRO) {
            System.out.println("(5): Visualizar partidas designadas");
        }
        if (perfil == TipoPerfil.ADMINISTRADOR || perfil == TipoPerfil.OPERADOR) {
            System.out.println("(6): Ingressos");
            System.out.println("(7): Vendas");
        }
        if (perfil == TipoPerfil.ADMINISTRADOR || perfil == TipoPerfil.ORGANIZADOR) {
            System.out.println("(8): Estádios");
            System.out.println("(9): Árbitros");
            System.out.println("(10): Designações");
        }
        if (perfil == TipoPerfil.ADMINISTRADOR) {
            System.out.println("(11): Criar conta");
            System.out.println("(12): Remover conta");
            System.out.println("(13): Editar conta");
        }

        System.out.println("(0): Sair");
        System.out.print("\nEscolha uma opção: ");
    }




    private static void gerirSelecao(){
        limparTela();
        System.out.println("SESSAO EM DESENVOLVIMENTO");
        System.out.println();
        System.out.print("voltar ao menu");
        entrada.nextLine();
    }







    private static void gerirPartida() {
        boolean rodando = true;
        while (rodando) {
            limparTela();
            System.out.println("=== GERIR PARTIDAS ===");
            System.out.println("(1) Cadastrar partida");
            System.out.println("(2) Listar partidas");
            System.out.println("(3) Remover partida");
            System.out.println("(0) Voltar ao menu");
            System.out.print("\nEscolha uma opção: ");
            String op = entrada.nextLine();

            switch (op) {
                case "1" -> cadastrarPartida();
                case "2" -> listarPartidas();
                case "3" -> removerPartida();
                case "0" -> rodando = false;
                default  -> System.out.println("Opção inválida.");
            }
        }
    }


    private static void cadastrarPartida() {
        limparTela();
        System.out.println("=== CADASTRAR PARTIDA ===");
        try {

            // exibe seleções disponíveis para facilitar a escolha
            List<Selecao> selecoes = partidaServico.listarSelecoes();
            if (selecoes.isEmpty()) {
                System.out.println("Nenhuma seleção cadastrada. Cadastre seleções antes de criar uma partida.");
                pausar();
                return;
            }
            System.out.println("Seleções disponíveis:");
            selecoes.forEach(s -> System.out.println(
                    "  " + s.getPais() +
                            " | Grupo: " + s.getGrupo() +
                            " | Confederação: " + s.getConfederacao() +
                            " | Ranking FIFA: " + s.getRankingFIFA()
            ));
            System.out.println();

            System.out.print("País do time da casa: ");
            String paisCasa = entrada.nextLine();
            Selecao timeCasa = selecoes.stream()
                    .filter(s -> s.getPais().equalsIgnoreCase(paisCasa))
                    .findFirst()
                    .orElse(null);
            if (timeCasa == null) {
                System.out.println("Seleção não encontrada: " + paisCasa);
                pausar();
                return;
            }

            System.out.print("País do time visitante: ");
            String paisVisitante = entrada.nextLine();
            Selecao timeVisitante = selecoes.stream()
                    .filter(s -> s.getPais().equalsIgnoreCase(paisVisitante))
                    .findFirst()
                    .orElse(null);
            if (timeVisitante == null) {
                System.out.println("Seleção não encontrada: " + paisVisitante);
                pausar();
                return;
            }

            // exibe estádios disponíveis para facilitar a escolha
            List<Estadio> estadios = estadioServico.listarEstadios();
            if (estadios.isEmpty()) {
                System.out.println("Nenhum estádio cadastrado. Cadastre um estádio antes de criar uma partida.");
                pausar();
                return;
            }
            System.out.println("\nEstádios disponíveis:");
            estadios.forEach(e -> System.out.println(
                    "  " + e.getNome() +
                            " | " + e.getLocalizacao() +
                            " | Capacidade: " + e.getCapacidade()
            ));
            System.out.println();

            System.out.print("Nome do estádio: ");
            String nomeEstadio = entrada.nextLine();
            Estadio estadio = estadioServico.buscarPorNome(nomeEstadio);
            if (estadio == null) {
                System.out.println("Estádio não encontrado: " + nomeEstadio);
                pausar();
                return;
            }

            System.out.print("Cidade: ");
            String cidade = entrada.nextLine();

            System.out.print("Data (ex: 2025-06-15): ");
            String data = entrada.nextLine();

            System.out.print("Horário (ex: 16:00): ");
            String horario = entrada.nextLine();

            System.out.print("Rodada: ");
            String rodada = entrada.nextLine();

            System.out.println("Fase (" + fasesDisponiveis() + "): ");
            String faseStr = entrada.nextLine().toUpperCase();
            FasePartida fase = FasePartida.valueOf(faseStr);

            partidaServico.cadastrarPartida(timeCasa, timeVisitante, estadio, cidade, data, horario, rodada, fase);
            System.out.println("\nPartida cadastrada com sucesso!");

        } catch (IllegalArgumentException e) {
            System.out.println("Erro: fase inválida.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }


    private static void listarPartidas() {
        limparTela();
        System.out.println("=== LISTA DE PARTIDAS ===");
        List<Partida> lista = partidaServico.listarPartidas();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma partida cadastrada.");
        } else {
            lista.forEach(p -> {
                System.out.println("----------------------------------------");
                System.out.println("Partida Nº:   " + p.getNumeroPartidas());
                System.out.println("Data:         " + p.getData());
                System.out.println("Horário:      " + p.getHorario());
                System.out.println("Cidade:       " + p.getCidade());
                System.out.println("Fase:         " + p.getFase());
                System.out.println("Estádio:      " + (p.getEstadio() != null ? p.getEstadio().getNome() : "-"));
                System.out.println("Time Casa:    " + (p.getTimeCasa() != null ? p.getTimeCasa().getPais() : "-"));
                System.out.println("Visitante:    " + (p.getTimeVisitante() != null ? p.getTimeVisitante().getPais() : "-"));
            });
            System.out.println("----------------------------------------");
            System.out.println("\nTotal de partidas: " + lista.size());
        }
        pausar();
    }


    private static void removerPartida() {
        limparTela();
        System.out.println("=== REMOVER PARTIDA ===");
        try {
            listarPartidas();

            System.out.print("Número da partida a remover: ");
            int numero = Integer.parseInt(entrada.nextLine());

            System.out.print("Tem certeza que deseja remover? (1) sim  (2) não: ");
            String conf = entrada.nextLine();
            if (!conf.equals("1")) {
                System.out.println("Remoção cancelada.");
                pausar();
                return;
            }

            partidaServico.removerPartida(numero);
            System.out.println("\nPartida removida com sucesso.");
        } catch (NumberFormatException e) {
            System.out.println("Erro: número inválido. Digite apenas números.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }


    private static String fasesDisponiveis() {
        StringBuilder sb = new StringBuilder();
        FasePartida[] valores = FasePartida.values();
        for (int i = 0; i < valores.length; i++) {
            sb.append(valores[i].name());
            if (i < valores.length - 1) sb.append(", ");
        }
        return sb.toString();
    }

    private static void visualizarPartida(){
        limparTela();
        System.out.println("=== MINHAS PARTIDAS DESIGNADAS ===");
        try {
            Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
            String nomeLogado = logado.getNome();

            List<DesignacaoArbitro> todasDesignacoes = designacaoServico.listarDesignacoes();

            List<DesignacaoArbitro> minhasDesignacoes = todasDesignacoes.stream()
                    .filter(d -> {
                        boolean isPrincipal = d.getPrincipalArbitro() != null &&
                                d.getPrincipalArbitro().getNome().equalsIgnoreCase(nomeLogado);

                        boolean isAssistente = d.getAssistentes() != null &&
                                d.getAssistentes().stream()
                                        .anyMatch(a -> a.getNome().equalsIgnoreCase(nomeLogado));

                        return isPrincipal || isAssistente;
                    })
                    .toList();

            if (minhasDesignacoes.isEmpty()) {
                System.out.println("Nenhuma partida designada para você.");
            } else {
                minhasDesignacoes.forEach(d -> {
                    Partida p = d.getPartida();
                    System.out.println("----------------------------------------");
                    System.out.println("Partida Nº:   " + p.getNumeroPartidas());
                    System.out.println("Data:         " + p.getData());
                    System.out.println("Horário:      " + p.getHorario());
                    System.out.println("Cidade:       " + p.getCidade());
                    System.out.println("Fase:         " + p.getFase());
                    System.out.println("Estádio:      " + (p.getEstadio() != null ? p.getEstadio().getNome() : "-"));
                    System.out.println("Time Casa:    " + (p.getTimeCasa() != null ? p.getTimeCasa().getPais() : "-"));
                    System.out.println("Visitante:    " + (p.getTimeVisitante() != null ? p.getTimeVisitante().getPais() : "-"));

                    boolean isPrincipal = d.getPrincipalArbitro() != null &&
                            d.getPrincipalArbitro().getNome().equalsIgnoreCase(nomeLogado);
                    System.out.println("Sua função:   " + (isPrincipal ? "Árbitro Principal" : "Árbitro Assistente"));
                });
                System.out.println("----------------------------------------");
                System.out.println("\nTotal de partidas: " + minhasDesignacoes.size());
            }

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        pausar();
    }


}
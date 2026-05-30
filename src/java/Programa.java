package src.java;

import src.java.modelo.classes.*;
import src.java.modelo.enumerations.TipoPerfil;
import src.java.persistencia.UsuarioDAO;
import src.java.servicos.CategoriaIngressoServico;
import src.java.servicos.IngressoServico;
import src.java.servicos.VendaServico;
import src.java.servicos.usuario.Relatorio;
import src.java.servicos.usuario.SessaoUsuario;
import src.java.servicos.usuario.UsuarioServico;

import java.util.List;
import java.util.Scanner;

public class Programa {

    private static Scanner entrada = new Scanner(System.in);
    private static UsuarioServico servico = new UsuarioServico();
    private static CategoriaIngressoServico categoriaServico = new CategoriaIngressoServico();
    private static IngressoServico ingressoServico = new IngressoServico();
    private static VendaServico vendaServico = new VendaServico();



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

                case "1" -> listarUsuarios();
                case "2" -> gerarRelatorio();

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
                    if (perfil == TipoPerfil.ADMINISTRADOR)
                        criarConta();
                    else opInvalida = true;
                }


                case "9" -> {
                    if (perfil == TipoPerfil.ADMINISTRADOR)
                        removerConta();
                    else opInvalida = true;
                }


                case "10" -> {
                    if (perfil == TipoPerfil.ADMINISTRADOR)
                        editaConta();
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
            System.out.print("ID do ingresso (ex: ING-001): ");
            String id = entrada.nextLine();

            System.out.print("Nome da categoria: ");
            String nomeCategoria = entrada.nextLine();

            CategoriaIngresso cat = categoriaServico.buscarPorNome(nomeCategoria);
            if (cat == null) {
                System.out.println("Categoria não encontrada: " + nomeCategoria);
                pausar();
                return;
            }

            Ingresso ing = new Ingresso(id, null, cat, false);
            ingressoServico.cadastrar(ing);
            System.out.println("\nIngresso cadastrado com sucesso!");
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
            String id = entrada.nextLine();

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
            String id = entrada.nextLine();

            boolean resultado = ingressoServico.validarEntrada(id);
            if (resultado) {
                System.out.println("\nEntrada validada com sucesso! ✓");
            } else {
                System.out.println("\nIngresso já foi validado anteriormente.");
            }
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
            String idIngresso = entrada.nextLine();

            Ingresso ing = ingressoServico.buscarPorId(idIngresso);
            if (ing == null) {
                System.out.println("Ingresso não encontrado: " + idIngresso);
                pausar();
                return;
            }

            vendaServico.adicionarIngresso(idVenda, ing);
            System.out.println("\nIngresso adicionado com sucesso!");
            System.out.println("Ingresso: " + idIngresso + " | Preço: R$" + String.format("%.2f", ing.getPrecoEfetivo()));
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
        if (perfil == TipoPerfil.ADMINISTRADOR) {
            System.out.println("(8): Criar conta");
            System.out.println("(9): Remover conta");
            System.out.println("(10): Editar conta");
        }

        System.out.println("(0): Sair");
        System.out.print("\nEscolha uma opção: ");
    }




    private static void listarUsuarios(){

        try {
            while(true) {
                limparTela();
                System.out.println("== Digite os parâmetros da busca ==");
                System.out.println("== caso nao queira especificar um parametro aperte enter ==");

                System.out.println();

                System.out.println("Nome: ");
                String nome = entrada.nextLine();
                if (nome.isBlank()) nome = null;

                System.out.println("País: ");
                String pais = entrada.nextLine();
                if (pais.isBlank()) pais = null;

                System.out.println("Perfil: ");
                String temp = entrada.nextLine().toUpperCase();
                TipoPerfil perfil = null;
                if (!temp.isBlank()) {
                    try{
                        perfil = TipoPerfil.valueOf(temp.toUpperCase());
                    }
                    catch(IllegalArgumentException e){
                        System.out.println("Perfil inválido, ignorando filtro.");
                    }
                }

                try {
                    List<Usuario> listaFiltrada = servico.pesquisar(nome, pais, perfil);

                    limparTela();
                    if (listaFiltrada.isEmpty()) {
                        System.out.println("Nenhum usuário atende a esses parâmetros");
                    } else {
                        listaFiltrada.forEach(u -> System.out.println(u.getNome() + " | " + u.getPais() + " | " + u.getPerfil()));
                    }
                }
                catch(Exception e){
                    System.out.println("Erro:" + e.getMessage());
                }


                System.out.println();
                System.out.println();
                System.out.println("(1) fazer outra busca");
                System.out.println("(2) voltar ao menu");
                System.out.println("Escolha uma opção:");
                String op = entrada.nextLine();
                if (op.equals("2")) break;

            }

        }
        catch(Exception e){
            System.out.println("Erro: " + e.getMessage());
        }

    }



    private static void gerarRelatorio(){

        Relatorio relatorio = new Relatorio();
        limparTela();

        while(true) {

            limparTela();

            System.out.println("(1) Gerar relatório de Usuarios");
            System.out.println("(2) Gerar relatório geral");
            System.out.print("Escolha uma opção: ");
            String op = entrada.nextLine();

            limparTela();
            if (op.equals("1")) {
                relatorio.exibirRelatorioUsuarios();
            } else {
                relatorio.exibirRelatorioGeral();
            }

            System.out.println();
            System.out.println("(1) Gerar outro relatório");
            System.out.println("(2) Voltar ao menu");
            System.out.print("Escolha uma opção: ");
            op = entrada.nextLine();

            if (op.equals("1")) {
                relatorio.exibirRelatorioUsuarios();
            } else if (op.equals("2")) {
                relatorio.exibirRelatorioGeral();
                break;
            }
        }
    }


    private static void gerirPartida(){
        limparTela();
        System.out.println("SESSAO EM DESENVOLVIMENTO");
        System.out.println();
        System.out.print("voltar ao menu");
        entrada.nextLine();
    }

    private static void gerirSelecao(){
        limparTela();
        System.out.println("SESSAO EM DESENVOLVIMENTO");
        System.out.println();
        System.out.print("voltar ao menu");
        entrada.nextLine();
    }

    private static void visualizarPartida(){
        limparTela();
        System.out.println("SESSAO EM DESENVOLVIMENTO");
        System.out.println();
        System.out.print("voltar ao menu");
        entrada.nextLine();
    }

    private static void criarConta(){

        limparTela();

        while(true) {



            System.out.println("=== DIGITE OS DADOS DO USUÁRIO A SER CADASTRADO ===");
            System.out.print("Nome: ");
            String nome = entrada.nextLine();
            System.out.print("CPF: ");
            String cpf = entrada.nextLine();
            System.out.print("Email: ");
            String email = entrada.nextLine();
            System.out.print("Pais: ");
            String pais = entrada.nextLine();
            System.out.print("Login: ");
            String login = entrada.nextLine();
            System.out.print("Senha: ");
            String senha = entrada.nextLine();
            System.out.print("Perfil (ADMINISTRADOR, ORGANIZADOR, OPERADOR, ARBITRO): ");
            String temp = entrada.nextLine().toUpperCase();

            System.out.println();

            try {
                TipoPerfil perfil = TipoPerfil.valueOf(temp);
                Usuario novo = new Usuario(nome, cpf, email, pais, login, senha, perfil);
                servico.cadastrar(novo);
                limparTela();
                System.out.println("Usuário cadastrado com sucesso");
            }
            catch (IllegalArgumentException e){
                limparTela();
                System.out.println("Erro no cadastro: Perfil inválido");
                System.out.println("Tentar novamente? (1)sim    (2)nao");
                String resp = entrada.nextLine();
                if(resp.equals("2")) break;
                limparTela();
                continue;
            }
            catch (Exception e) {
                limparTela();
                System.out.println("Erro no cadastro: " + e.getMessage());
                System.out.println("Tentar novamente? (1)sim    (2)nao");
                String resp = entrada.nextLine();
                if(resp.equals("2")) break;
                limparTela();
                continue;
            }

            System.out.println("(1) Cadastrar outro usuário");
            System.out.println("(2) Voltar ao menu");
            System.out.print("Escolha uma opção: ");
            String op = entrada.nextLine();
            limparTela();

            if (op.equals("2")) {
                break;
            }

        }
    }

    private static void removerConta(){
        limparTela();

        while(true) {
            System.out.print("Login do usuário a remover: ");
            String login = entrada.nextLine();
            limparTela();

            Usuario encontrado = servico.buscarPorLogin(login);

            if(encontrado == null){
                System.out.println("Nenhum usuário encontrado com o login \"" + login + "\".");
                System.out.println("(1) Tentar outro login");
                System.out.println("(2) Voltar ao menu");
                System.out.print("Escolha uma opção: ");
                String op = entrada.nextLine();
                limparTela();
                if (op.equals("2")) break;
                continue;
            }

            System.out.println();

            try {
                System.out.println("Usuário encontrado: " + encontrado.getNome() + " | " + encontrado.getPerfil());
                System.out.println("ATENÇÃO, VOCÊ ESTÁ PRESTES A REMOVER UMA CONTA PERMANENTEMENTE.");
                System.out.println("(1) desistir de remover a conta");
                System.out.println("(2) remover a conta permanentemente");
                String op = entrada.nextLine();
                limparTela();

                if (op.equals("2")){
                    servico.excluir(login);
                    System.out.println("Conta removida");
                }
                else System.out.println("Remoção cancelada");
            }
            catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }

            System.out.println("(1) Remover usuário");
            System.out.println("(2) Voltar ao menu");
            System.out.print("Escolha uma opção: ");
            String op = entrada.nextLine();
            limparTela();

            if (op.equals("2")) {
                break;
            }
        }
    }

    public static void editaConta(){
        limparTela();

        while(true){
            System.out.print("Login do usuário a editar: ");
            String login = entrada.nextLine();
            limparTela();

            Usuario encontrado = servico.buscarPorLogin(login);

            if(encontrado == null){
                System.out.println("Nenhum usuário encontrado com o login \"" + login + "\".");
                System.out.println("(1) Tentar outro login");
                System.out.println("(2) Voltar ao menu");
                System.out.print("Escolha uma opção: ");
                String op = entrada.nextLine();
                limparTela();
                if (op.equals("2")) break;
                continue;
            }

            System.out.println("=== DADOS ATUAIS ===");
            System.out.println("Nome:   " + encontrado.getNome());
            System.out.println("CPF:    " + encontrado.getCpf());
            System.out.println("Email:  " + encontrado.getEmail());
            System.out.println("País:   " + encontrado.getPais());
            System.out.println("Senha:  " + encontrado.getSenha());
            System.out.println("Perfil: " + encontrado.getPerfil());
            System.out.println();
            System.out.println("Digite o novo valor para cada campo.");
            System.out.println("Deixe em branco para manter o valor atual.");
            System.out.println();

            System.out.print("Nome:   ");
            String nome = entrada.nextLine();
            System.out.print("CPF:    ");
            String cpf = entrada.nextLine();
            System.out.print("Email:  ");
            String email = entrada.nextLine();
            System.out.print("País:   ");
            String pais = entrada.nextLine();
            System.out.print("Senha:  ");
            String senha = entrada.nextLine();
            System.out.print("Perfil (ADMINISTRADOR, ORGANIZADOR, OPERADOR, ARBITRO): ");
            String temp = entrada.nextLine().toUpperCase();

            TipoPerfil perfil = null;
            if (!temp.isBlank()) {
                try{
                    perfil = TipoPerfil.valueOf(temp.toUpperCase());
                }
                catch(IllegalArgumentException e){
                    System.out.println("Perfil inválido, ignorando filtro.");
                }
            }

            if(nome.isBlank()) nome = null;
            if(cpf.isBlank()) cpf = null;
            if(email.isBlank()) email = null;
            if(pais.isBlank()) pais = null;
            if(senha.isBlank()) senha = null;

            try{
                List<String> avisos = servico.editar(login, nome, cpf, email, pais, senha, perfil);
                limparTela();
                System.out.println("= Edição concluída =");
                if(!avisos.isEmpty()) avisos.forEach(a -> System.out.println("aviso: " + a));
            }
            catch(Exception e){
                System.out.println("Erro: " + e.getMessage());
            }

            System.out.println();
            System.out.println("(1) Editar outro usuário");
            System.out.println("(2) Voltar ao menu");
            System.out.print("Escolha uma opção: ");
            String op = entrada.nextLine();
            limparTela();

            if (op.equals("2")) break;
        }
    }
}
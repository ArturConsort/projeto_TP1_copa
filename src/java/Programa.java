package src.java;

import src.java.modelo.classes.Usuario;
import src.java.modelo.enumerations.TipoPerfil;
import src.java.persistencia.UsuarioDAO;
import src.java.servicos.usuario.Relatorio;
import src.java.servicos.usuario.SessaoUsuario;
import src.java.servicos.usuario.UsuarioServico;

import java.util.List;
import java.util.Scanner;

public class Programa {

    private static Scanner entrada = new Scanner(System.in);
    private static UsuarioServico servico = new UsuarioServico();

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
                        comprarIngressos();
                    else opInvalida = true;
                }


                case "7" -> {
                    if (perfil == TipoPerfil.ADMINISTRADOR || perfil == TipoPerfil.OPERADOR)
                        verRegistros();
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
            System.out.println("(6): Comprar ingressos");
            System.out.println("(7): Ver registros");
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

    private static void comprarIngressos(){
        limparTela();
        System.out.println("SESSAO EM DESENVOLVIMENTO");
        System.out.println();
        System.out.print("voltar ao menu");
        entrada.nextLine();
    }

    private static void verRegistros(){
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
                System.out.println("Usuário cadastrado com sussesso");
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
                System.out.println("ANTENÇÃO, VOCÊ ESTÁ PRESTES A REMOVER UMA CONTA PERMANENTEMENTE,");
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
            System.out.println("senha:   " + encontrado.getSenha());
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

            System.out.print("Senha:   ");
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
            if(pais.isBlank())pais = null;
            if(senha.isBlank())senha = null;

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

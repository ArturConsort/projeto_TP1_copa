package src.java;

import src.java.modelo.classes.Usuario;
import src.java.modelo.enumerations.TipoPerfil;
import src.java.servicos.usuario.Relatorio;
import src.java.servicos.usuario.SessaoUsuario;
import src.java.servicos.usuario.UsuarioServico;

import java.util.List;
import java.util.Scanner;

public class Programa {

    public static void main(String[] args){

        Scanner entrada = new Scanner(System.in);
        UsuarioServico servico = new UsuarioServico();
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

        boolean rodando = true;
        while (rodando){

            limparTela();
            System.out.println("=== Bem vindo " + logado.getNome() + " ===");
            exibeMenu(logado.getPerfil());

            String op = entrada.nextLine();
            switch(op){

                case "1" -> listarUsuarios();
                case "2" -> gerarRelatorio();
                case "3" -> gerirPartida();
                case "4" -> gerirSelecao();
                case "5" -> visualizarPartida();
                case "6" -> comprarIngressos();
                case "7" -> verRegistros();
                case "8" -> criarConta();
                case "9" -> removerConta();

                case "0" -> servico.logout();

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

        Scanner entrada = new Scanner(System.in);
        UsuarioServico servico = new UsuarioServico();

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

        Scanner entrada = new Scanner(System.in);
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
        Scanner entrada = new Scanner(System.in);
        limparTela();
        System.out.println("SESSAO EM DESENVOLVIMENTO");
        System.out.println();
        System.out.print("voltar ao menu");
        entrada.nextLine();
    }

    private static void gerirSelecao(){
        Scanner entrada = new Scanner(System.in);
        limparTela();
        System.out.println("SESSAO EM DESENVOLVIMENTO");
        System.out.println();
        System.out.print("voltar ao menu");
        entrada.nextLine();
    }

    private static void visualizarPartida(){
        Scanner entrada = new Scanner(System.in);
        limparTela();
        System.out.println("SESSAO EM DESENVOLVIMENTO");
        System.out.println();
        System.out.print("voltar ao menu");
        entrada.nextLine();
    }

    private static void comprarIngressos(){
        Scanner entrada = new Scanner(System.in);
        limparTela();
        System.out.println("SESSAO EM DESENVOLVIMENTO");
        System.out.println();
        System.out.print("voltar ao menu");
        entrada.nextLine();
    }

    private static void verRegistros(){
        Scanner entrada = new Scanner(System.in);
        limparTela();
        System.out.println("SESSAO EM DESENVOLVIMENTO");
        System.out.println();
        System.out.print("voltar ao menu");
        entrada.nextLine();
    }

    private static void criarConta(){

        Scanner entrada = new Scanner(System.in);
        UsuarioServico servico = new UsuarioServico();
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

        Scanner entrada = new Scanner(System.in);
        UsuarioServico servico = new UsuarioServico();
        limparTela();

        while(true) {



            System.out.print("Login do usuário a remover: ");
            String login = entrada.nextLine();

            System.out.println();

            try {

                System.out.println("ANTENÇÃO, VOCÊ ESTÁ PRESTES A REMOVER UMA CONTA PERMANENTEMENTE,");
                System.out.println("(1) desistir de remover a conta");
                System.out.println("(2) remover a conta permanentemente");
                String op = entrada.nextLine();

                if (op.equals("2")){
                    servico.excluir(login);
                    System.out.println("conta removida");
                }
            }
            catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }

            System.out.println("(1) Remover outro usuário");
            System.out.println("(2) Voltar ao menu");
            System.out.print("Escolha uma opção: ");
            String op = entrada.nextLine();
            limparTela();

            if (op.equals("2")) {
                break;
            }

        }

    }












}

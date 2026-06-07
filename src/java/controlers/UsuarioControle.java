package src.java.controlers;

import src.java.modelo.classes.Usuario;
import src.java.modelo.enumerations.TipoPerfil;
import src.java.servicos.usuario.Relatorio;
import src.java.servicos.usuario.UsuarioServico;

import java.util.List;
import java.util.Scanner;

public class UsuarioControle {

    private final UsuarioServico servico;
    private final Relatorio relatorio;
    private final Scanner entrada;

    public UsuarioControle(Scanner entrada) {
        this.servico   = new UsuarioServico();
        this.relatorio = new Relatorio();
        this.entrada   = entrada;
    }



    // ================================================================
    //   LISTAR USUÁRIOS
    // ================================================================

    public void listarUsuarios(){

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

                System.out.println("Perfil (ADMINISTRADOR, ORGANIZADOR, OPERADOR, ARBITRO): ");
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


    // ================================================================
    //   GERAR RELATÓRIO
    // ================================================================

    public void gerarRelatorio() {
        while (true) {
            limparTela();
            System.out.println("(1) Gerar relatório de usuários");
            System.out.println("(2) Gerar relatório geral");
            System.out.print("Escolha uma opção: ");
            String op = entrada.nextLine();

            limparTela();
            if (op.equals("1")) {
                relatorio.exibirRelatorioUsuarios();
            } else {
                relatorio.exibirRelatorioPartidas();
            }

            System.out.println();
            System.out.println("(1) Gerar outro relatório");
            System.out.println("(2) Voltar ao menu");
            System.out.print("Escolha uma opção: ");
            op = entrada.nextLine();

            if (!op.equals("1")) break;
        }
    }



    // ================================================================
    //   CRIAR CONTA
    // ================================================================

    public void criarConta() {
        limparTela();

        while (true) {
            System.out.println("=== CRIAR CONTA ===");
            System.out.print("Nome: ");
            String nome = entrada.nextLine();
            System.out.print("CPF: ");
            String cpf = entrada.nextLine();
            System.out.print("Email: ");
            String email = entrada.nextLine();
            System.out.print("País: ");
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
                System.out.println("Usuário cadastrado com sucesso.");
            } catch (IllegalArgumentException e) {
                limparTela();
                System.out.println("Erro: perfil inválido.");
                if (perguntarTentarNovamente()) continue;
                break;
            } catch (Exception e) {
                limparTela();
                System.out.println("Erro: " + e.getMessage());
                if (perguntarTentarNovamente()) continue;
                break;
            }

            System.out.println("(1) Cadastrar outro usuário");
            System.out.println("(2) Voltar ao menu");
            System.out.print("Escolha uma opção: ");
            String op = entrada.nextLine();
            limparTela();
            if (op.equals("2")) break;
        }
    }




    // ================================================================
    //   REMOVER CONTA
    // ================================================================

    public void removerConta() {
        limparTela();

        while (true) {
            System.out.print("Login do usuário a remover: ");
            String login = entrada.nextLine();
            limparTela();

            Usuario encontrado = servico.buscarPorLogin(login);

            if (encontrado == null) {
                System.out.println("Nenhum usuário encontrado com o login \"" + login + "\".");
                System.out.println("(1) Tentar outro login");
                System.out.println("(2) Voltar ao menu");
                System.out.print("Escolha uma opção: ");
                String op = entrada.nextLine();
                limparTela();
                if (op.equals("2")) break;
                continue;
            }

            try {
                System.out.println("Usuário encontrado: " + encontrado.getNome() + " | " + encontrado.getPerfil());
                System.out.println("ATENÇÃO: você está prestes a remover esta conta permanentemente.");
                System.out.println("(1) Desistir");
                System.out.println("(2) Confirmar remoção");
                System.out.print("Escolha uma opção: ");
                String op = entrada.nextLine();
                limparTela();

                if (op.equals("2")) {
                    servico.excluir(login);
                    System.out.println("Conta removida com sucesso.");
                } else {
                    System.out.println("Remoção cancelada.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }

            System.out.println();
            System.out.println("(1) Remover outro usuário");
            System.out.println("(2) Voltar ao menu");
            System.out.print("Escolha uma opção: ");
            String op = entrada.nextLine();
            limparTela();
            if (op.equals("2")) break;
        }
    }



    // ================================================================
    //   EDITAR CONTA
    // ================================================================

    public void editaConta() {
        limparTela();

        while (true) {
            System.out.print("Login do usuário a editar: ");
            String login = entrada.nextLine();
            limparTela();

            Usuario encontrado = servico.buscarPorLogin(login);

            if (encontrado == null) {
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
            String nome = lerOpcional();
            System.out.print("CPF:    ");
            String cpf = lerOpcional();
            System.out.print("Email:  ");
            String email = lerOpcional();
            System.out.print("País:   ");
            String pais = lerOpcional();
            System.out.print("Senha:  ");
            String senha = lerOpcional();
            System.out.print("Perfil (ADMINISTRADOR, ORGANIZADOR, OPERADOR, ARBITRO): ");
            String temp = entrada.nextLine().toUpperCase();

            TipoPerfil perfil = null;
            if (!temp.isBlank()) {
                try {
                    perfil = TipoPerfil.valueOf(temp);
                } catch (IllegalArgumentException e) {
                    System.out.println("Perfil inválido, campo ignorado.");
                }
            }

            try {
                List<String> avisos = servico.editar(login, nome, cpf, email, pais, senha, perfil);
                limparTela();
                System.out.println("=== Edição concluída ===");
                avisos.forEach(a -> System.out.println("Aviso: " + a));
            } catch (Exception e) {
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




    // ================================================================
    //   MÉTODOS AUXILIARES PRIVADOS
    // ================================================================

    /**
     * Lê uma linha e retorna null se estiver em branco.
     * Usado nos campos opcionais da edição de conta.
     */
    private String lerOpcional() {
        String valor = entrada.nextLine();
        return valor.isBlank() ? null : valor;
    }

    /**
     * Pergunta ao usuário se quer tentar novamente.
     * Retorna true para sim, false para não.
     */
    private boolean perguntarTentarNovamente() {
        System.out.println("Tentar novamente? (1) Sim   (2) Não");
        String resp = entrada.nextLine();
        limparTela();
        return resp.equals("1");
    }

    private void limparTela() {
        for (int i = 0; i < 50; i++) System.out.println();
    }



}

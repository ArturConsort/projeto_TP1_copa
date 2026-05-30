package src.java.servicos.usuario;

import src.java.modelo.enumerations.TipoPerfil;
import src.java.modelo.classes.Usuario;
import src.java.modelo.excecoes.*;
import src.java.persistencia.UsuarioDAO;

import java.util.ArrayList;
import java.util.List;

public class UsuarioServico {

    private UsuarioDAO dao;

    public UsuarioServico(){
        this.dao =  new UsuarioDAO();
    }










            // ------- login e logout ------- //
    public Usuario login(String login, String senha) throws UsuarioNaoEncontradoException{
        for(Usuario u : dao.carregaLista()){
            if(u.getLogin().equals(login) && u.getSenha().equals(senha)){
                SessaoUsuario.getInstancia().iniciarSessao(u);
                return u;
            }
        }
        throw new UsuarioNaoEncontradoException("Login ou senha inválidos");
    }


    public void logout(){
        SessaoUsuario.getInstancia().encerrarSessao();
    }













            // ------- manipulacao de contas pelo admin ------- //

    public void cadastrar(Usuario novoUsuario) throws AcessoNegadoException, EmailInvalidoException, SenhaFracaException, LoginJaExisteException, IllegalArgumentException {


        verificarPermissao(TipoPerfil.ADMINISTRADOR);
        validarEmail(novoUsuario.getEmail());
        validarSenha(novoUsuario.getSenha());

        for(Usuario u : dao.carregaLista()){
            if(u.getLogin().equals(novoUsuario.getLogin())){
                throw new LoginJaExisteException("Já existe um usuário com esse login");
            }
        }



        dao.salvar(novoUsuario);

    }


    public List<String> editar(String login, String novoNome, String novoCpf, String novoEmail, String novoPais, String novaSenha, TipoPerfil novoTipo) throws AcessoNegadoException, UsuarioNaoEncontradoException{

        verificarPermissao(TipoPerfil.ADMINISTRADOR);
        Usuario u = dao.buscarPorLogin(login);
        if(u == null) throw new UsuarioNaoEncontradoException("Usuário não encontrado: " + login);
        List<String> avisos = new ArrayList<>();

        if(novoNome != null) u.setNome(novoNome);
        if(novoCpf != null) u.setCpf(novoCpf);
        if(novoPais != null) u.setPais(novoPais);
        if(novoTipo != null) u.setPerfil(novoTipo);

        if(novoEmail != null){
            try {
                validarEmail(novoEmail);
                u.setEmail(novoEmail);
            }
            catch(EmailInvalidoException e){
                avisos.add("e-mail inválido: " + e.getMessage());
            }
        }

        if(novaSenha != null) {
            try {
                validarSenha(novaSenha);
                u.setSenha(novaSenha);
            }
            catch (SenhaFracaException e){
                avisos.add("Senha inválida:" + e.getMessage());
            }
        }

        dao.atualizaUsuario(u);
        avisos.add("todos os campos válido preenchidos foram atualizados");
        return avisos;

    }


    public void excluir(String login) throws AcessoNegadoException, UsuarioNaoEncontradoException{
        verificarPermissao(TipoPerfil.ADMINISTRADOR);
        dao.remover(login);
    }













            // ------- metodo de busca ------- //
   public List<Usuario> pesquisar(String nome, String pais, TipoPerfil perfil){ // esse metodo busca por esses 3 criterios, eles nao sao obrigatorios, da pra preencher eles com nul se nao quiser pesquisar por algum deles
        List<Usuario> resultado = new ArrayList<>();

        for(Usuario u : dao.carregaLista()){
            boolean nomeOk =     nome == null     ||    nome.equals(u.getNome());
            boolean paisOK =     pais == null     ||    pais.equals(u.getPais());
            boolean perfilOK =   perfil == null   ||    perfil.equals(u.getPerfil());


            if(nomeOk && paisOK && perfilOK) resultado.add(u);
        }

        return resultado;

   }

    public Usuario buscarPorLogin(String login) {
        return dao.buscarPorLogin(login);
    }

















    // ------- metodos privados auxiliares ------- //
    private void verificarPermissao(TipoPerfil perfilRequisitado){
        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if(logado == null || logado.getPerfil() != perfilRequisitado){
            throw new AcessoNegadoException("Acesso negado: esse usuario não tem permissao para fazer essa ação");
        }
    }

    private void validarEmail(String email){
        // !email.maches(...) -> verifica se o email NAO segue o padrao de texto a seguir
        // ^...$ -> abrem e fecham o padrao de texto
        // [\w.+\-] -> sequencia de 1 ou mais caracteres incluindo letras e outros como(1._+-), exemplo: joao, joao_junior, joao+junior
        // @[\w\-] -> '@' + sequencia de 1 ou mais caracteres, mas dessa vez so letras, pois representa o dominio (email, hotmail)
        // \.[a-z]{2,}$ -> '.' + 2 ou mais letras minusculas (.com, .br)
        if(email == null || !email.matches("^[\\w.+\\-]+@[\\w\\-]+\\.[a-z]{2,}$")){
            throw new EmailInvalidoException("Email invalido:" + email);
        }
    }

    private void validarSenha(String senha){
        if(senha == null || senha.length()<8 || !senha.matches(".*[a-zA-Z].*") || !senha.matches(".*[0-9].*")){
            throw new SenhaFracaException("Senha fraca. A senha deve conter letras, numeros e ao menos 8 caracteres");
        }
    }





















}

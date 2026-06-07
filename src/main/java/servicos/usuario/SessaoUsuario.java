package servicos.usuario;

import modelo.classes.Usuario;

public class SessaoUsuario {

    //padrao singleton
    private static SessaoUsuario instancia;     // faz com que toda SessaoUsuario se refira sempre ao mesmo objeto, pois o objeto "instancia" eh unico pra toda a classe
    private Usuario usuarioLogado;


    private SessaoUsuario(){}                   // o construtor eh privado para impedir que outra sessao seja criada


    public static SessaoUsuario getInstancia(){                        // so eh possivel criar uma nova instancia por esse metodo. se uma sessao ja tiver sido criada ele nao cria outra, pra que sempre exista uma unica secao
        if (instancia == null) instancia = new SessaoUsuario();
        return instancia;      // unica forma de acessar o objeto instancia. fazer SessaoUsuario.getInstancia.metodo
    }


    public void iniciarSessao(Usuario usuarioLogado){              // atribui um usuario a sessao
        this.usuarioLogado = usuarioLogado;
    }


    public void encerrarSessao(){               // disatribui um usuario a sessao
        this.usuarioLogado = null;
    }


    public Usuario getUsuarioLogado(){
        return  usuarioLogado;
    }





}

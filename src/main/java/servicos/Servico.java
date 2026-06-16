package servicos;

import modelo.classes.Usuario;
import modelo.enumerations.TipoPerfil;
import modelo.excecoes.AcessoNegadoException;
import servicos.usuario.SessaoUsuario;

public abstract class Servico {

    protected void verificarPermissao(TipoPerfil... perfisAceitos) {
        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();

        if (logado == null) {
            throw new AcessoNegadoException("Nenhum usuário logado.");
        }

        for (TipoPerfil perfil : perfisAceitos) {
            if (logado.getPerfil() == perfil) {
                return;
            }
        }

        throw new AcessoNegadoException("Acesso negado: permissão insuficiente.");
    }

    protected void verificarPermissaoMultipla(TipoPerfil... perfisAceitos) {
        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();

        if (logado == null) {
            throw new AcessoNegadoException("Nenhum usuario logado");
        }

        for (TipoPerfil perfil : perfisAceitos) {
            if (logado.getPerfil() == perfil) {
                return;
            }
        }

        throw new AcessoNegadoException(
                "Acesso negado: esse usuario não tem permissao para fazer essa ação"
        );
    }
}
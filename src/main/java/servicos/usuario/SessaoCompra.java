package servicos.usuario;

import modelo.classes.CategoriaIngresso;
import modelo.classes.Partida;

public class SessaoCompra {

    // padrao singleton, igual a SessaoUsuario
    private static SessaoCompra instancia;

    private Partida partidaSelecionada;
    private CategoriaIngresso categoriaSelecionada;
    private int quantidade = 1;

    private SessaoCompra() {}

    public static SessaoCompra getInstancia() {
        if (instancia == null) instancia = new SessaoCompra();
        return instancia;
    }

    public void selecionarPartida(Partida partida) {
        this.partidaSelecionada = partida;
        this.categoriaSelecionada = null;
        this.quantidade = 1;
    }

    public void selecionarCategoria(CategoriaIngresso categoria) {
        this.categoriaSelecionada = categoria;
    }

    public Partida getPartidaSelecionada() {
        return partidaSelecionada;
    }

    public CategoriaIngresso getCategoriaSelecionada() {
        return categoriaSelecionada;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public void encerrar() {
        this.partidaSelecionada = null;
        this.categoriaSelecionada = null;
        this.quantidade = 1;
    }
}

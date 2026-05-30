package src.java.modelo.classes;

import java.io.Serializable;

public class Ingresso implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idIngresso;
    private Partida partida;
    private CategoriaIngresso categoria;
    private boolean foiValidado;

    public Ingresso(String idIngresso, Partida partida, CategoriaIngresso categoria, boolean foiValidado) {
        this.idIngresso = idIngresso;
        this.partida = partida;
        this.categoria = categoria;
        this.foiValidado = foiValidado;
    }

    // ------- metodos do diagrama ------- //

    public boolean validarEntrada() {
        if (!foiValidado) {
            this.foiValidado = true;
            return true;
        }
        return false; // ingresso ja foi validado anteriormente
    }

    public double getPrecoEfetivo() {
        if (categoria == null) return 0.0;
        return categoria.getPreco();
    }

    // ------- getters e setters ------- //

    public String getIdIngresso() {
        return idIngresso;
    }
    public void setIdIngresso(String idIngresso) {
        this.idIngresso = idIngresso;
    }

    public Partida getPartida() {
        return partida;
    }
    public void setPartida(Partida partida) {
        this.partida = partida;
    }

    public CategoriaIngresso getCategoria() {
        return categoria;
    }
    public void setCategoria(CategoriaIngresso categoria) {
        this.categoria = categoria;
    }

    public boolean isFoiValidado() {
        return foiValidado;
    }
    public void setFoiValidado(boolean foiValidado) {
        this.foiValidado = foiValidado;
    }
}
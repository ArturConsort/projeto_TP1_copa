package src.java.modelo.classes;

import java.io.Serializable;

public class Ingresso implements Serializable {
    private static final long serialVersionUID = 1L;

    // contador estatico que garante IDs unicos na sessao.
    // o IngressoDAO e responsavel por sincroniza-lo com o arquivo ao carregar a lista.
    private static int proximoId = 1;

    private int idIngresso;
    private Partida partida;
    private CategoriaIngresso categoria;
    private boolean foiValidado;

    public Ingresso(Partida partida, CategoriaIngresso categoria) {
        this.idIngresso = proximoId++;
        this.partida = partida;
        this.categoria = categoria;
        this.foiValidado = false;
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

    // chamado pelo IngressoDAO ao carregar o arquivo, para que o contador
    // nunca gere IDs repetidos entre sessoes
    public static void sincronizarContador(int maiorIdExistente) {
        if (maiorIdExistente >= proximoId) {
            proximoId = maiorIdExistente + 1;
        }
    }

    // ------- getters e setters ------- //

    public int getIdIngresso() {
        return idIngresso;
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

    @Override
    public String toString() {
        String nomePartida = (partida != null) ? partida.toString() : "sem partida";
        String nomeCategoria = (categoria != null) ? categoria.getNome() : "sem categoria";
        String validado = foiValidado ? "validado" : "não validado";
        return "Ingresso #" + idIngresso + " | " + nomePartida + " | " + nomeCategoria + " | " + validado;
    }
}
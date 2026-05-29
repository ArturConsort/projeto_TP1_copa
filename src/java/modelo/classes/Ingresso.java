package src.java.modelo.classes;

public class Ingresso {
    private String idIngresso;
    private Partida partida;
    private boolean foiValidado;

    public Ingresso(String idIngresso, Partida partida, boolean foiValidado) {
        this.idIngresso = idIngresso;
        this.partida = partida;
        this.foiValidado = foiValidado;
    }

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

    public boolean isFoiValidado() {
        return foiValidado;
    }
    public void setFoiValidado(boolean foiValidado) {
        this.foiValidado = foiValidado;
    }
}

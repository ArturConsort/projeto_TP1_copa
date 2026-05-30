package src.java.modelo.classes;
import java.io.Serializable;

public class ResultadoPartida implements Serializable{
    private static final long serialVersionUID = 1L;
    private Selecao timeVencedor;
    private Selecao timePerdedor;
    private Partida partida;
    private String placar;
    private String placarPenaltis;
    private int cartoesAmarelos;
    private int cartoesVermelhos;

    public ResultadoPartida(Selecao timeVencedor, Selecao timePerdedor, Partida partida, String placar, String placarPenaltis, int cartoesAmarelos, int cartoesVermelhos) {
        this.timeVencedor = timeVencedor;
        this.timePerdedor = timePerdedor;
        this.partida = partida;
        this.placar = placar;
        this.placarPenaltis = placarPenaltis;
        this.cartoesAmarelos = cartoesAmarelos;
        this.cartoesVermelhos = cartoesVermelhos;
    }

    public Selecao getTimeVencedor() {
        return timeVencedor;
    }

    public void setTimeVencedor(Selecao timeVencedor) {
        this.timeVencedor = timeVencedor;
    }

    public Selecao getTimePerdedor() {
        return timePerdedor;
    }

    public void setTimePerdedor(Selecao timePerdedor) {
        this.timePerdedor = timePerdedor;
    }

    public Partida getPartida() {
        return partida;
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
    }

    public String getPlacar() {
        return placar;
    }

    public void setPlacar(String placar) {
        this.placar = placar;
    }

    public String getPlacarPenaltis() {
        return placarPenaltis;
    }

    public void setPlacarPenaltis(String placarPenaltis) {
        this.placarPenaltis = placarPenaltis;
    }

    public int getCartoesAmarelos() {
        return cartoesAmarelos;
    }

    public void setCartoesAmarelos(int cartoesAmarelos) {
        this.cartoesAmarelos = cartoesAmarelos;
    }

    public int getCartoesVermelhos() {
        return cartoesVermelhos;
    }

    public void setCartoesVermelhos(int cartoesVermelhos) {
        this.cartoesVermelhos = cartoesVermelhos;
    }
}

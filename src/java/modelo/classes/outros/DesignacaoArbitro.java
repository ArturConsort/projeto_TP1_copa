package src.java.modelo.classes.outros;
import java.util.List;

public class DesignacaoArbitro {

    private Partida partida;
    private Arbitro principalArbitro;
    private List<Arbitro> assistentes;

    public DesignacaoArbitro(Partida partida, Arbitro arbitro, List<Arbitro> assistentes) {
        this.partida = partida;
        this.principalArbitro = arbitro;
        this.assistentes = assistentes;
    }

    public boolean confirmarDesignacao() {
        List<Selecao> times = List.of(partida.getTimeCasa(), partida.getTimeVisitante());

        for (Selecao time : times) {
            if (time.getPais().equalsIgnoreCase(principalArbitro.getNacionalidade())) {
                return false;
            }
            for (Arbitro assistente : assistentes) {
                if (time.getPais().equalsIgnoreCase(assistente.getNacionalidade())) {
                    return false;
                }
            }
        }
        partida.setArbitroPrincipal(principalArbitro);
        partida.setListaAssistentes(assistentes);
        return true;
    }

    // getters e setters
    public Partida getPartida() {
        return partida;
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
    }

    public Arbitro getPrincipalArbitro() {
        return principalArbitro;
    }

    public void setPrincipalArbitro(Arbitro principalArbitro) {
        this.principalArbitro = principalArbitro;
    }

    public List<Arbitro> getAssistentes() {
        return assistentes;
    }

    public void setAssistentes(List<Arbitro> assistentes) {
        this.assistentes = assistentes;
    }

}
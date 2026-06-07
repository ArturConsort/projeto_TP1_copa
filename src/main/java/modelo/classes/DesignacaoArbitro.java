package modelo.classes;

import java.io.Serializable;
import java.util.List;
import modelo.excecoes.designacaoarbitro.NacionalidadeConflitanteException;

public class DesignacaoArbitro implements Serializable{

    private static final long serialVersionUID = 1L;

    private Partida partida;
    private Arbitro principalArbitro;
    private List<Arbitro> assistentes;

    public DesignacaoArbitro(Partida partida, Arbitro arbitro, List<Arbitro> assistentes) {
        setPartida(partida);
        setPrincipalArbitro(arbitro);
        setAssistentes(assistentes);
    }

    public void verificarConflitosNacionalidade() throws NacionalidadeConflitanteException {
        List<Selecao> times = List.of(partida.getTimeCasa(), partida.getTimeVisitante());

        for (Selecao time : times) {
            if (time.getPais().equalsIgnoreCase(principalArbitro.getNacionalidade())) {
                throw new NacionalidadeConflitanteException(principalArbitro.getNome(), time.getPais());
            }
            for (Arbitro assistente : assistentes) {
                if (time.getPais().equalsIgnoreCase(assistente.getNacionalidade())) {
                    throw new NacionalidadeConflitanteException(assistente.getNome(), time.getPais());
                }
            }
        }
    }

    public Partida getPartida() {
        return partida;
    }

    public void setPartida(Partida partida) {
        if (partida == null) {
            throw new IllegalArgumentException("A partida não pode ser nula.");
        }
        this.partida = partida;
    }

    public Arbitro getPrincipalArbitro() {
        return principalArbitro;
    }

    public void setPrincipalArbitro(Arbitro principalArbitro) {

        if (principalArbitro == null) {
            throw new IllegalArgumentException("O árbitro principal não pode ser nulo.");
        }
        this.principalArbitro = principalArbitro;
    }

    public List<Arbitro> getAssistentes() {
        return assistentes;
    }

    public void setAssistentes(List<Arbitro> assistentes) {

        if (assistentes == null) {
            throw new IllegalArgumentException("A lista de assistentes não pode ser nula.");
        }
        if (assistentes.isEmpty()) {
            throw new IllegalArgumentException("A lista de assistentes não pode estar vazia.");
        }
        if (assistentes.size() != 2) {
            throw new IllegalArgumentException("Uma designação deve possuir exatamente 2 árbitros assistentes.");
        }
        for (Arbitro assistente : assistentes) {
            if (assistente == null) {
                throw new IllegalArgumentException("Nenhum assistente pode ser nulo.");
            }
        }
        this.assistentes = assistentes;
    }

}
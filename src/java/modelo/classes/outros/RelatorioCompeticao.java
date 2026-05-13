package src.java.modelo.classes.outros;

import modelo.enumerations.DesempenhoDeSelecoes;

public class RelatorioCompeticao {

    private int numeroDePartidas;
    private int publico;
    private DesempenhoDeSelecoes desempenho;

    public RelatorioCompeticao(int numeroDePartidas, int publico, DesempenhoDeSelecoes desempenho) {
        this.numeroDePartidas = numeroDePartidas;
        this.publico = publico;
        this.desempenho = desempenho;
    }
}

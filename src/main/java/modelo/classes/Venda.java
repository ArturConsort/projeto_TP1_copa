package modelo.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Venda implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idVenda;
    private String dataVenda;
    private Usuario cliente;
    private double valorTotal;
    private String status;
    private List<Ingresso> ingressos;

    public Venda(String idVenda, String dataVenda, Usuario cliente, double valorTotal, String status) {
        this.idVenda = idVenda;
        this.dataVenda = dataVenda;
        this.cliente = cliente;
        this.valorTotal = valorTotal;
        this.status = status;
        this.ingressos = new ArrayList<>();
    }

    // ------- metodos do diagrama ------- //

    public void adicionarIngresso(Ingresso ingresso) {
        ingressos.add(ingresso);
    }

    public void calcularTotal() {
        double total = 0.0;
        for (Ingresso i : ingressos) {
            total += i.getPrecoEfetivo();
        }
        this.valorTotal = total;
    }

    public void finalizarVenda() {
        calcularTotal();
        this.status = "FINALIZADA";
        for (Ingresso i : ingressos) {
            if (i.getCategoria() != null) {
                i.getCategoria().reduzirEstoque(1);
            }
        }
    }

    // ------- getters e setters ------- //

    public String getIdVenda() {
        return idVenda;
    }
    public void setIdVenda(String idVenda) {
        this.idVenda = idVenda;
    }

    public String getDataVenda() {
        return dataVenda;
    }
    public void setDataVenda(String dataVenda) {
        this.dataVenda = dataVenda;
    }

    public Usuario getCliente() {
        return cliente;
    }
    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }

    public double getValorTotal() {
        return valorTotal;
    }
    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public List<Ingresso> getIngressos() {
        return ingressos;
    }
    public void setIngressos(List<Ingresso> ingressos) {
        this.ingressos = ingressos;
    }
}
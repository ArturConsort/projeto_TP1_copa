package persistencia;

import modelo.classes.Venda;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VendaDAO {

    // define que a classe VendaDAO sempre vai escrever no arquivo "vendas.dat" (static).
    // o arquivo no qual vao ser guardadas as vendas nao pode ser trocado (final)
    private static final String ARQUIVO = "vendas.dat";

    // metodo que recebe uma lista de vendas e salva elas em "vendas.dat".
    // o metodo eh private pois so eh usado pela propria classe.
    // os metodos: salvar, atualizar, remover fazem modificacoes na lista e depois chamam ele
    private void salvarLista(List<Venda> listaVendas) {
        try {
            ObjectOutputStream escrita = new ObjectOutputStream(new FileOutputStream(ARQUIVO));
            escrita.writeObject(listaVendas);
            escrita.close();
        }
        catch (IOException e) {
            System.err.println("Erro ao salvar vendas: " + e.getMessage());
        }
    }

    // metodo que carrega toda a lista de vendas
    // usada pra passar a lista para outros metodos
    public List<Venda> carregaLista() {

        File arquivo = new File(ARQUIVO);                       // carrega "vendas.dat" pra variavel "arquivo"
        if (!arquivo.exists()) return new ArrayList<>();        // se o arquivo nao existe (no caso de primeira execucao), retorna uma nova lista em branco

        try {
            ObjectInputStream leitura = new ObjectInputStream(new FileInputStream(ARQUIVO));
            List<Venda> lista = (List<Venda>) leitura.readObject();
            leitura.close();
            return lista;                                       // faz o casting dos dados em leitura para uma lista de vendas
        }
        catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar vendas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void salvar(Venda venda) {
        List<Venda> listaVendas = carregaLista();
        listaVendas.add(venda);
        salvarLista(listaVendas);
    }

    public void remover(String idVenda) {
        List<Venda> listaVendas = carregaLista();
        listaVendas.removeIf(v -> v.getIdVenda().equals(idVenda));
        salvarLista(listaVendas);
    }

    public Venda buscarPorId(String idVenda) {
        for (Venda v : carregaLista()) {
            if (v.getIdVenda().equals(idVenda)) return v;
        }
        return null;
    }

    public void atualizar(Venda vendaAtualizada) {
        List<Venda> listaVendas = carregaLista();
        for (int i = 0; i < listaVendas.size(); i++) {
            if (listaVendas.get(i).getIdVenda().equals(vendaAtualizada.getIdVenda())) {
                listaVendas.set(i, vendaAtualizada);
                break;
            }
        }
        salvarLista(listaVendas);
    }
}
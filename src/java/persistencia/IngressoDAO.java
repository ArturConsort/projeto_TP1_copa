package src.java.persistencia;

import src.java.modelo.classes.Ingresso;
import src.java.modelo.classes.Venda;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class IngressoDAO {

    // define que a classe IngressoDAO sempre vai escrever no arquivo "ingressos.dat" (static).
    // o arquivo no qual vao ser guardados os ingressos nao pode ser trocado (final)
    private static final String ARQUIVO = "ingressos.dat";



    // metodo que recebe uma lista de ingressos e salva eles em "ingressos.dat".
    // o metodo eh private pois so eh usado pela propria classe.
    // os metodos: salvar, atualizar, remover fazem modificacoes na lista e depois chamam ele
    private void salvarLista(List<Ingresso> listaIngressos) {
        try {
            ObjectOutputStream escrita = new ObjectOutputStream(new FileOutputStream(ARQUIVO));
            escrita.writeObject(listaIngressos);
            escrita.close();
        }
        catch (IOException e) {
            System.err.println("Erro ao salvar ingressos: " + e.getMessage());
        }
    }

    // metodo que carrega toda a lista de ingressos.
    // tambem sincroniza o contador estatico de Ingresso para que novos IDs
    // nunca repitam os que ja existem no arquivo.
    public List<Ingresso> carregaLista() {

        File arquivo = new File(ARQUIVO);                       // carrega "ingressos.dat" pra variavel "arquivo"
        if (!arquivo.exists()) return new ArrayList<>();        // se "ingressos.dat" nao existe (primeira execucao), retorna lista em branco

        try {
            ObjectInputStream leitura = new ObjectInputStream(new FileInputStream(ARQUIVO));
            List<Ingresso> lista = (List<Ingresso>) leitura.readObject();
            leitura.close();

            // sincroniza o contador com o maior ID ja salvo no arquivo
            int maiorId = lista.stream()
                    .mapToInt(Ingresso::getIdIngresso)
                    .max()
                    .orElse(0);
            Ingresso.sincronizarContador(maiorId);

            return lista;
        }
        catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar ingressos: " + e.getMessage());
            return new ArrayList<>();
        }
    }



    public void salvar(Ingresso ingresso) {
        List<Ingresso> listaIngressos = carregaLista();
        listaIngressos.add(ingresso);
        salvarLista(listaIngressos);
    }

    public void remover(int idIngresso) {
        List<Ingresso> listaIngressos = carregaLista();
        listaIngressos.removeIf(i -> i.getIdIngresso() == idIngresso);
        salvarLista(listaIngressos);
    }

    public Ingresso buscarPorId(int idIngresso) {
        for (Ingresso i : carregaLista()) {
            if (i.getIdIngresso() == idIngresso) return i;
        }
        return null;
    }

    public void atualizar(Ingresso ingressoAtualizado) {
        List<Ingresso> listaIngressos = carregaLista();
        for (int i = 0; i < listaIngressos.size(); i++) {
            if (listaIngressos.get(i).getIdIngresso() == ingressoAtualizado.getIdIngresso()) {
                listaIngressos.set(i, ingressoAtualizado);
                break;
            }
        }
        salvarLista(listaIngressos);
    }

    // retorna todos os ingressos que pertencem a vendas do usuario informado.
    // percorre o arquivo de vendas para descobrir quais ingressos sao do cliente.
    public List<Ingresso> buscarPorUsuario(String loginUsuario) {
        List<Ingresso> resultado = new ArrayList<>();

        VendaDAO vendaDAO = new VendaDAO();
        for (Venda venda : vendaDAO.carregaLista()) {
            if (venda.getCliente() != null
                    && venda.getCliente().getLogin().equals(loginUsuario)) {
                resultado.addAll(venda.getIngressos());
            }
        }

        return resultado;
    }
}
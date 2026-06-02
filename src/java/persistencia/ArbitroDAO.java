package src.java.persistencia;

import src.java.modelo.classes.Arbitro;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ArbitroDAO {

    // define que a classe ArbitroDAO sempre vai escrever no arquivo "arbitros.dat" (static).
    // o arquivo no qual vao ser guardados os arbitros nao pode ser trocado (final)
    private static final String ARQUIVO = "arbitros.dat";



    // metodo que recebe uma lista de arbitros e salva eles em "arbitros.dat".
    // o metodo eh private pois so eh usado pela propria classe.
    // os metodos: salvar, atualizar, remover fazem modificacoes na lista e depois chamam ele
    private void salvarLista(List<Arbitro> listaArbitros) {
        try {
            ObjectOutputStream escrita = new ObjectOutputStream(new FileOutputStream(ARQUIVO));
            escrita.writeObject(listaArbitros);
            escrita.close();
        }
        catch (IOException e) {
            System.err.println("Erro ao salvar arbitros: " + e.getMessage());
        }
    }



    // metodo que carrega toda a lista de arbitros
    // usada pra passar a lista para outros metodos
    public List<Arbitro> carregaLista() {

        File arquivo = new File(ARQUIVO);                       // carrega "arbitros.dat" pra variavel "arquivo"
        if (!arquivo.exists()) return new ArrayList<>();        // se o arquivo nao existe (no caso de primeira execucao), retorna uma nova lista em branco

        try {
            ObjectInputStream leitura = new ObjectInputStream(new FileInputStream(ARQUIVO));
            List<Arbitro> lista = (List<Arbitro>) leitura.readObject();
            leitura.close();
            return lista;                                       // faz o casting dos dados em leitura para uma lista de arbitros
        }
        catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar arbitros: " + e.getMessage());
            return new ArrayList<>();
        }
    }



    public void salvar(Arbitro arbitro) {
        List<Arbitro> listaArbitros = carregaLista();
        listaArbitros.add(arbitro);
        salvarLista(listaArbitros);
    }

    public void remover(String nome) {
        List<Arbitro> listaArbitros = carregaLista();
        listaArbitros.removeIf(a -> a.getNome().equals(nome));
        salvarLista(listaArbitros);
    }

    public Arbitro buscarPorNome(String nome) {
        for (Arbitro a : carregaLista()) {
            if (a.getNome().equals(nome)) return a;
        }
        return null;
    }

    public void atualizar(Arbitro arbitroAtualizado) {
        List<Arbitro> listaArbitros = carregaLista();
        for (int i = 0; i < listaArbitros.size(); i++) {
            if (listaArbitros.get(i).getNome().equals(arbitroAtualizado.getNome())) {
                listaArbitros.set(i, arbitroAtualizado);
                break;
            }
        }
        salvarLista(listaArbitros);
    }

}
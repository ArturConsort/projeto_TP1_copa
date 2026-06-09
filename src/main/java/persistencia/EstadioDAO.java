package persistencia;



import modelo.classes.Estadio;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EstadioDAO {

    // define que a classe EstadioDAO sempre vai escrever no arquivo "estadios.dat" (static).
    // o arquivo no qual vao ser guardados os estadios nao pode ser trocado (final)
    private static final String ARQUIVO = "estadios.dat";



    // metodo que recebe uma lista de estadios e salva eles em "estadios.dat".
    // o metodo eh private pois so eh usado pela propria classe.
    // os metodos: salvar, atualizar, remover fazem modificacoes na lista e depois chamam ele
    private void salvarLista(List<Estadio> listaEstadios) {
        try {
            ObjectOutputStream escrita = new ObjectOutputStream(new FileOutputStream(ARQUIVO));
            escrita.writeObject(listaEstadios);
            escrita.close();
        }
        catch (IOException e) {
            System.err.println("Erro ao salvar estadios: " + e.getMessage());
        }
    }



    // metodo que carrega toda a lista de estadios
    // usada pra passar a lista para outros metodos
    public List<Estadio> carregaLista() {

        File arquivo = new File(ARQUIVO);                       // carrega "estadios.dat" pra variavel "arquivo"
        if (!arquivo.exists()) return new ArrayList<>();        // se o arquivo nao existe (no caso de primeira execucao), retorna uma nova lista em branco

        try {
            ObjectInputStream leitura = new ObjectInputStream(new FileInputStream(ARQUIVO));
            List<Estadio> lista = (List<Estadio>) leitura.readObject();
            leitura.close();
            return lista;                                       // faz o casting dos dados em leitura para uma lista de estadios
        }
        catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar estadios: " + e.getMessage());
            return new ArrayList<>();
        }
    }



    public void salvar(Estadio estadio) {
        List<Estadio> listaEstadios = carregaLista();
        listaEstadios.add(estadio);
        salvarLista(listaEstadios);
    }

    public void remover(String nome) {
        List<Estadio> listaEstadios = carregaLista();
        listaEstadios.removeIf(e -> e.getNome().equals(nome));
        salvarLista(listaEstadios);
    }

    public Estadio buscarPorNome(String nome) {
        for (Estadio e : carregaLista()) {
            if (e.getNome().equals(nome)) return e;
        }
        return null;
    }

    public void atualizar(Estadio estadioAtualizado) {
        List<Estadio> listaEstadios = carregaLista();
        for (int i = 0; i < listaEstadios.size(); i++) {
            if (listaEstadios.get(i).getNome().equals(estadioAtualizado.getNome())) {
                listaEstadios.set(i, estadioAtualizado);
                break;
            }
        }
        salvarLista(listaEstadios);
    }
}
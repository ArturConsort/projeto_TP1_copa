package src.java.persistencia;

import src.java.modelo.classes.CategoriaIngresso;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaIngressoDAO {

    // define que a classe CategoriaIngressoDAO sempre vai escrever no arquivo "categorias_ingresso.dat" (static).
    // o arquivo no qual vao ser guardadas as categorias nao pode ser trocado (final)
    private static final String ARQUIVO = "categorias_ingresso.dat";



    // metodo que recebe uma lista de categorias e salva elas em "categorias_ingresso.dat".
    // o metodo eh private pois so eh usado pela propria classe.
    // os metodos: salvar, atualizar, remover fazem modificacoes na lista e depois chamam ele
    private void salvarLista(List<CategoriaIngresso> listaCategorias) {
        try {
            ObjectOutputStream escrita = new ObjectOutputStream(new FileOutputStream(ARQUIVO));
            escrita.writeObject(listaCategorias);
            escrita.close();
        }
        catch (IOException e) {
            System.err.println("Erro ao salvar categorias de ingresso: " + e.getMessage());
        }
    }



    // metodo que carrega toda a lista de categorias
    // usada pra passar a lista para outros metodos
    public List<CategoriaIngresso> carregaLista() {

        File arquivo = new File(ARQUIVO);                       // carrega "categorias_ingresso.dat" pra variavel "arquivo"
        if (!arquivo.exists()) return new ArrayList<>();        // se o arquivo nao existe (no caso de primeira execucao), retorna uma nova lista em branco

        try {
            ObjectInputStream leitura = new ObjectInputStream(new FileInputStream(ARQUIVO));
            List<CategoriaIngresso> lista = (List<CategoriaIngresso>) leitura.readObject();
            leitura.close();
            return lista;                                       // faz o casting dos dados em leitura para uma lista de categorias
        }
        catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar categorias de ingresso: " + e.getMessage());
            return new ArrayList<>();
        }
    }



    public void salvar(CategoriaIngresso categoria) {
        List<CategoriaIngresso> listaCategorias = carregaLista();
        listaCategorias.add(categoria);
        salvarLista(listaCategorias);
    }

    public void remover(String nome) {
        List<CategoriaIngresso> listaCategorias = carregaLista();
        listaCategorias.removeIf(c -> c.getNome().equals(nome));
        salvarLista(listaCategorias);
    }

    public CategoriaIngresso buscarPorNome(String nome) {
        for (CategoriaIngresso c : carregaLista()) {
            if (c.getNome().equals(nome)) return c;
        }
        return null;
    }

    public void atualizar(CategoriaIngresso categoriaAtualizada) {
        List<CategoriaIngresso> listaCategorias = carregaLista();
        for (int i = 0; i < listaCategorias.size(); i++) {
            if (listaCategorias.get(i).getNome().equals(categoriaAtualizada.getNome())) {
                listaCategorias.set(i, categoriaAtualizada);
                break;
            }
        }
        salvarLista(listaCategorias);
    }
}
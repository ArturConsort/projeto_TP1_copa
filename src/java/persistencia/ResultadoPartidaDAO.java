package src.java.persistencia;

import src.java.modelo.classes.ResultadoPartida;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ResultadoPartidaDAO {

    private static final String ARQUIVO = "resultados.dat";

    private void salvarLista(List<ResultadoPartida> lista) {
        try {
            ObjectOutputStream escrita = new ObjectOutputStream(new FileOutputStream(ARQUIVO));
            escrita.writeObject(lista);
            escrita.close();
        } catch (IOException e) {
            System.err.println("Erro ao salvar resultados: " + e.getMessage());
        }
    }

    public List<ResultadoPartida> carregaLista() {
        File arquivo = new File(ARQUIVO);
        if (!arquivo.exists()) return new ArrayList<>();

        try {
            ObjectInputStream leitura = new ObjectInputStream(new FileInputStream(ARQUIVO));
            List<ResultadoPartida> lista = (List<ResultadoPartida>) leitura.readObject();
            leitura.close();
            return lista;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar resultados: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void salvar(ResultadoPartida resultado) {
        List<ResultadoPartida> lista = carregaLista();
        lista.add(resultado);
        salvarLista(lista);
    }

    public void remover(int numeroPartida) {
        List<ResultadoPartida> lista = carregaLista();
        lista.removeIf(r -> r.getPartida().getNumeroPartidas() == numeroPartida);
        salvarLista(lista);
    }

    public ResultadoPartida buscarPorNumeroPartida(int numeroPartida) {
        for (ResultadoPartida r : carregaLista()) {
            if (r.getPartida().getNumeroPartidas() == numeroPartida) return r;
        }
        return null;
    }
}
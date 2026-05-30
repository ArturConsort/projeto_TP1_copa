package src.java.persistencia;

import src.java.modelo.classes.Partida;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PartidaDAO {

    private static final String ARQUIVO = "dados/partidas.dat";

    // ---------------------------------------------------------------
    // PRIVADO: salva a lista inteira de partidas no arquivo
    // ---------------------------------------------------------------
    private void salvarLista(List<Partida> lista) {
        // Cria a pasta "dados/" automaticamente se não existir
        new File("dados").mkdirs();

        try {
            ObjectOutputStream escrita = new ObjectOutputStream(
                    new FileOutputStream(ARQUIVO)
            );
            escrita.writeObject(lista);
            escrita.close();
        } catch (IOException e) {
            System.err.println("Erro ao salvar partidas: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // PÚBLICO: carrega e retorna todas as partidas do arquivo
    // ---------------------------------------------------------------
    public List<Partida> carregaLista() {
        File arquivo = new File(ARQUIVO);
        if (!arquivo.exists()) return new ArrayList<>();

        try {
            ObjectInputStream leitura = new ObjectInputStream(
                    new FileInputStream(ARQUIVO)
            );
            List<Partida> lista = (List<Partida>) leitura.readObject();
            leitura.close();
            return lista;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar partidas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ---------------------------------------------------------------
    // Salva uma nova partida no arquivo
    // ---------------------------------------------------------------
    public void salvar(Partida partida) {
        List<Partida> lista = carregaLista();
        lista.add(partida);
        salvarLista(lista);
    }

    // ---------------------------------------------------------------
    // Busca uma partida pelo número dela
    // ---------------------------------------------------------------
    public Partida buscarPorNumero(int numero) {
        for (Partida p : carregaLista()) {
            if (p.getNumeroPartidas() == numero) return p;
        }
        return null;
    }

    // ---------------------------------------------------------------
    // Remove uma partida pelo número
    // ---------------------------------------------------------------
    public void remover(int numero) {
        List<Partida> lista = carregaLista();
        lista.removeIf(p -> p.getNumeroPartidas() == numero);
        salvarLista(lista);
    }

    // ---------------------------------------------------------------
    // Atualiza uma partida existente (substitui pelo número)
    // ---------------------------------------------------------------
    public void atualizar(Partida partidaAtualizada) {
        List<Partida> lista = carregaLista();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getNumeroPartidas() == partidaAtualizada.getNumeroPartidas()) {
                lista.set(i, partidaAtualizada);
                break;
            }
        }
        salvarLista(lista);
    }
}
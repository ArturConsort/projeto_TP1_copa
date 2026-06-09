package persistencia;

import modelo.classes.Partida;
import modelo.classes.Selecao;
import modelo.enumerations.FasePartida;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PartidaDAO {

    private static final String ARQUIVO = "partidas.dat";

    // Privado — só chamado internamente pelos métodos salvar/remover/atualizar
    private void salvarLista(List<Partida> listaPartidas) {
        try {
            ObjectOutputStream escrita = new ObjectOutputStream(new FileOutputStream(ARQUIVO));
            escrita.writeObject(listaPartidas);
            escrita.close();
        } catch (IOException e) {
            System.err.println("Erro ao salvar partidas: " + e.getMessage());
        }
    }

    // Carrega todas as partidas do arquivo
    public List<Partida> carregaLista() {
        File arquivo = new File(ARQUIVO);
        if (!arquivo.exists()) return new ArrayList<>(); // primeira execução

        try {
            ObjectInputStream leitura = new ObjectInputStream(new FileInputStream(ARQUIVO));
            List<Partida> lista = (List<Partida>) leitura.readObject(); // lê primeiro
            leitura.close();                                             // fecha depois
            return lista;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar partidas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Adiciona uma nova partida no arquivo
    public void salvar(Partida partida) {
        List<Partida> listaPartidas = carregaLista();
        listaPartidas.add(partida);
        salvarLista(listaPartidas);
    }

    // Busca uma partida pelo número dela
    public Partida buscarPorNumero(int numero) {
        List<Partida> listaPartidas = carregaLista();
        for (Partida p : listaPartidas) {
            if (p.getNumeroPartidas() == numero) return p;
        }
        return null;
    }

    // Remove uma partida pelo número
    public void remover(int numero) {
        List<Partida> listaPartidas = carregaLista();
        listaPartidas.removeIf(p -> p.getNumeroPartidas() == numero);
        salvarLista(listaPartidas);
    }

    // Substitui uma partida existente pelo número
    public void atualizar(Partida partidaAtualizada) {
        List<Partida> listaPartidas = carregaLista();
        for (int i = 0; i < listaPartidas.size(); i++) {
            if (listaPartidas.get(i).getNumeroPartidas() == partidaAtualizada.getNumeroPartidas()) {
                listaPartidas.set(i, partidaAtualizada);
                break;
            }
        }
        salvarLista(listaPartidas);
    }

    public List<Partida> buscarPorFase(FasePartida fase) {
        List<Partida> resultado = new ArrayList<>();
        for (Partida p : carregaLista()) {
            if (p.getFase() == fase) resultado.add(p);
        }
        return resultado;
    }

    // Consulta por data — ex: todas as partidas do dia 12/07/2026
    public List<Partida> buscarPorData(String data) {
        List<Partida> resultado = new ArrayList<>();
        for (Partida p : carregaLista()) {
            if (p.getData().equals(data)) resultado.add(p);
        }
        return resultado;
    }

    // Consulta por seleção — ex: todas as partidas do Brasil
    public List<Partida> buscarPorSelecao(Selecao selecao) {
        List<Partida> resultado = new ArrayList<>();
        for (Partida p : carregaLista()) {
            if (p.getTimeCasa().equals(selecao) || p.getTimeVisitante().equals(selecao)) {
                resultado.add(p);
            }
        }
        return resultado;
    }
}
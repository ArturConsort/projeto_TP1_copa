package persistencia;



import modelo.classes.DesignacaoArbitro;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DesignacaoArbitroDAO {

    private static final String ARQUIVO = "designacoes_arbitro.dat";

    private void salvarLista(List<DesignacaoArbitro> lista) throws IOException {
        try (ObjectOutputStream escrita = new ObjectOutputStream(
                new FileOutputStream(ARQUIVO))) {
            escrita.writeObject(lista);
        }
    }

    public List<DesignacaoArbitro> carregaLista() throws IOException {
        File arquivo = new File(ARQUIVO);
        if (!arquivo.exists()) return new ArrayList<>();

        try (ObjectInputStream leitura = new ObjectInputStream(
                new FileInputStream(ARQUIVO))) {
            return (List<DesignacaoArbitro>) leitura.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Formato de arquivo inválido.", e);
        }
    }

    public void salvar(DesignacaoArbitro designacao) throws IOException {
        List<DesignacaoArbitro> lista = carregaLista();
        lista.add(designacao);
        salvarLista(lista);
    }

    public void remover(int numeroPartida) throws IOException {
        List<DesignacaoArbitro> lista = carregaLista();
        lista.removeIf(d -> d.getPartida().getNumeroPartidas() == numeroPartida);
        salvarLista(lista);
    }

    public Optional<DesignacaoArbitro> buscarPorPartida(int numeroPartida) throws IOException {
        return carregaLista().stream()
                .filter(d -> d.getPartida().getNumeroPartidas() == numeroPartida)
                .findFirst();
    }

    public void atualizar(DesignacaoArbitro designacaoAtualizada) throws IOException {
        List<DesignacaoArbitro> lista = carregaLista();

        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getPartida().getNumeroPartidas() ==
                    designacaoAtualizada.getPartida().getNumeroPartidas()) {
                lista.set(i, designacaoAtualizada);
                break;
            }
        }

        salvarLista(lista);
    }
}
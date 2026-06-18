package persistencia;

import modelo.classes.Jogador;
import modelo.classes.Selecao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SelecaoDAO {

    private static final String ARQUIVO = "selecoes.dat";

    private void salvarLista(List<Selecao> listaSelecoes) {
        try {
            ObjectOutputStream escrita = new ObjectOutputStream(new FileOutputStream(ARQUIVO));
            escrita.writeObject(listaSelecoes);
            escrita.close();
        } catch (IOException e) {
            System.err.println("Erro ao salvar selecao: " + e.getMessage());
        }
    }

    public List<Selecao> carregaLista() {
        File arquivo = new File(ARQUIVO);
        if (!arquivo.exists()) return new ArrayList<>();

        try {
            ObjectInputStream leitura = new ObjectInputStream(new FileInputStream(ARQUIVO));
            List<Selecao> lista = (List<Selecao>) leitura.readObject();
            leitura.close();
            return lista;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar seleções: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void salvar(Selecao selecao) {
        List<Selecao> lista = carregaLista();
        lista.add(selecao);
        salvarLista(lista);
    }

    public void remover(String nome) {
        List<Selecao> lista = carregaLista();
        lista.removeIf(u -> u.getPais().equals(nome));
        salvarLista(lista);
    }

    public Selecao buscarPorPais(String pais) {
        for (Selecao u : carregaLista()) {
            if (u.getPais().equals(pais)) return u;
        }
        return null;
    }

    public List<Selecao> buscarPorGrupo(String grupo) {
        List<Selecao> lista = new ArrayList<>();
        for (Selecao u : carregaLista()) {
            // ← CORRIGIDO: era == (compara referência), agora usa .equals()
            if (u.getGrupo().equals(grupo)) lista.add(u);
        }
        return lista;
    }

    public void adicionarJog(Jogador jog) {
        List<Selecao> lista = carregaLista();
        for (Selecao s : lista) {
            if (s.getPais().equals(jog.getSelecao().getPais())) {
                s.addJogadores(jog);
                atualizaSelecao(s);
                return;
            }
        }
    }

    public void atualizaSelecao(Selecao selecao_atualizada) {
        List<Selecao> lista = carregaLista();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getPais().equals(selecao_atualizada.getPais())) {
                lista.set(i, selecao_atualizada);
                break;
            }
        }
        salvarLista(lista);
    }
}
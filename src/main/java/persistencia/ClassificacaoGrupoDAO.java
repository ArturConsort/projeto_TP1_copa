package persistencia;

import modelo.classes.ClassificacaoGrupo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ClassificacaoGrupoDAO {

    private static final String ARQUIVO = "classificacao_grupos.dat";

    private void salvarLista(List<ClassificacaoGrupo> lista) {
        try {
            ObjectOutputStream escrita = new ObjectOutputStream(new FileOutputStream(ARQUIVO));
            escrita.writeObject(lista);
            escrita.close();
        } catch (IOException e) {
            System.err.println("Erro ao salvar classificação: " + e.getMessage());
        }
    }

    public List<ClassificacaoGrupo> carregaLista() {
        File arquivo = new File(ARQUIVO);
        if (!arquivo.exists()) return new ArrayList<>();

        try {
            ObjectInputStream leitura = new ObjectInputStream(new FileInputStream(ARQUIVO));
            List<ClassificacaoGrupo> lista = (List<ClassificacaoGrupo>) leitura.readObject();
            leitura.close();
            return lista;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar classificação: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Salva ou atualiza o grupo
    public void salvarGrupo(ClassificacaoGrupo grupo) {
        List<ClassificacaoGrupo> lista = carregaLista();
        boolean encontrado = false;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getGrupo().equals(grupo.getGrupo())) {
                lista.set(i, grupo); // atualiza
                encontrado = true;
                break;
            }
        }
        if (!encontrado) lista.add(grupo); // novo grupo
        salvarLista(lista);
    }

    public ClassificacaoGrupo buscarPorGrupo(String grupo) {
        for (ClassificacaoGrupo g : carregaLista()) {
            if (g.getGrupo().equals(grupo)) return g; // era ==
        }
        return null;
    }

}
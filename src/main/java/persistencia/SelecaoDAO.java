package persistencia;
import modelo.classes.Jogador;
import modelo.classes.Selecao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;



public class SelecaoDAO {
    // essa classe efaz um intermediario entre arquivos e aplicacao, facilitando o uso de arquivos
    // define que a classe UsuarioDAO  sempre vai escrever no arquivo "usuarios.dat" (static).
    // o arquivo no qual vao ser guardados os usuarios nao pode ser trocado (final)
    private static final String ARQUIVO = "selecoes.dat";



    // metodo que recebe uma lista de usuarios e salva eles em "usuarios.dat".
    // o metodo eh private pois so eh usado pela propria classe.
    // os metodos: salvar, atualizar, remover fazem modificacoes na lista e depois chamam ele
    private void salvarLista(List<Selecao> listaSelecoes){
        try{
            ObjectOutputStream escrita = new ObjectOutputStream(new FileOutputStream("selecoes.dat"));
            escrita.writeObject(listaSelecoes);
            escrita.close();
        }
        catch (IOException e){
            System.err.println("Erro ao salvar selecao: " + e.getMessage());   //tipo um System.out.println, mas para mensagem de erro
        }
    }


    // metodo que carrega toda a lista de usuarios
    // usada pra passar a lista para outros metodos
    public List<Selecao> carregaLista(){

        File arquivo = new File(ARQUIVO);                   // carrega "usuarios.dat" pra variavel "arquivo"
        if(!arquivo.exists()) return new ArrayList<>();       // se "usuarios.dat" nao existe (no caso de primeira execucao), retorna uma nova lista em branco

        try{
            ObjectInputStream leitura = new ObjectInputStream(new FileInputStream("selecoes.dat"));     //leitura recebe os dados dos arquivos, ainda em formato serializado que nao tem sentido proprio
            List<Selecao> lista = (List<Selecao>) leitura.readObject();
            leitura.close();
            return lista;        // faz o casting dos dados em leituras para uma lista de usuarios, dando sentido aos dados lidos
        }
        catch(IOException | ClassNotFoundException e){
            System.err.println("Erro ao carregar seleções" + e.getMessage());
            return  new ArrayList<>();
        }

    }




    public void salvar(Selecao selecao){
        List<Selecao> listaSelecoes = carregaLista();
        listaSelecoes.add(selecao);
        salvarLista(listaSelecoes);
    }

    public void remover(String nome){
        List<Selecao> listaSelecoes = carregaLista();
        listaSelecoes.removeIf(u -> u.getPais().equals(nome));
        salvarLista(listaSelecoes);
    }

    public Selecao buscarPorPais(String pais){
        List<Selecao> listaSelecoes = carregaLista();
        for(Selecao u : carregaLista()){
            if(u.getPais().equals(pais)) return u;
        }
        return null;
    }

    public List<Selecao> buscarPorGrupo(String grupo){
        List<Selecao> listaSelecoes = new ArrayList<>();
        for(Selecao u : carregaLista()){
            if(u.getGrupo() == grupo) listaSelecoes.add(u);
        }
        return listaSelecoes;
    }

    public void adicionarJog(Jogador jog) {
        List<Selecao> lista = carregaLista();
        for (Selecao s : lista) {
            if (s.getPais().equals(jog.getSelecao().getPais())) {
                s.addJogadores(jog);
                atualizaSelecao(s); // salva o 's' que acabou de ser modificado
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

        salvarLista(lista); // ← faltava essa linha
    }

}


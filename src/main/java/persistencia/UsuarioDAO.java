// essa classe efaz um intermediario entre arquivos e aplicacao, facilitando o uso de arquivos

package persistencia;

import modelo.classes.Usuario;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // define que a classe UsuarioDAO  sempre vai escrever no arquivo "usuarios.dat" (static).
    // o arquivo no qual vao ser guardados os usuarios nao pode ser trocado (final)
    private static final String ARQUIVO = "usuarios.dat";




    // metodo que recebe uma lista de usuarios e salva eles em "usuarios.dat".
    // o metodo eh private pois so eh usado pela propria classe.
    // os metodos: salvar, atualizar, remover fazem modificacoes na lista e depois chamam ele
    private void salvarLista(List<Usuario> listaUsuarios){
        try{
            ObjectOutputStream escrita = new ObjectOutputStream(new FileOutputStream("usuarios.dat"));
            escrita.writeObject(listaUsuarios);
            escrita.close();
        }
        catch (IOException e){
            System.err.println("Erro ao salvar usuários: " + e.getMessage());   //tipo um System.out.println, mas para mensagem de erro
        }
    }



    // metodo que carrega toda a lista de usuarios
    // usada pra passar a lista para outros metodos
    public List<Usuario> carregaLista(){

        File arquivo = new File(ARQUIVO);                   // carrega "usuarios.dat" pra variavel "arquivo"
        if(!arquivo.exists()) return new ArrayList<>();       // se "usuarios.dat" nao existe (no caso de primeira execucao), retorna uma nova lista em branco

        try{
            ObjectInputStream leitura = new ObjectInputStream(new FileInputStream("usuarios.dat"));     //leitura recebe os dados dos arquivos, ainda em formato serializado que nao tem sentido proprio
            List<Usuario> lista = (List<Usuario>) leitura.readObject();
            leitura.close();
            return lista;        // faz o casting dos dados em leituras para uma lista de usuarios, dando sentido aos dados lidos
        }
        catch(IOException | ClassNotFoundException e){
            System.err.println("Erro ao carregar usuários" + e.getMessage());
            return  new ArrayList<>();
        }

    }




    public void salvar(Usuario usuario){
        List<Usuario> listaUsuarios = carregaLista();
        listaUsuarios.add(usuario);
        salvarLista(listaUsuarios);
    }

    public void remover(String login){
        List<Usuario> listaUsuarios = carregaLista();
        listaUsuarios.removeIf(u -> u.getLogin().equals(login));
        salvarLista(listaUsuarios);
    }

    public Usuario buscarPorLogin(String login){
        List<Usuario> listaUsuarios = carregaLista();
        for(Usuario u : carregaLista()){
            if(u.getLogin().equals(login)) return u;
        }
        return null;
    }

    public void atualizaUsuario(Usuario usuarioAtualizado){
        List<Usuario> listaUsuarios = carregaLista();
        for(int i=0; i<listaUsuarios.size(); i++){
            if(listaUsuarios.get(i).getLogin().equals(usuarioAtualizado.getLogin())){
                listaUsuarios.set(i, usuarioAtualizado);
                break;
            }
        }
        salvarLista(listaUsuarios);
    }




}

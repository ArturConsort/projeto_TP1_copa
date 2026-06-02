public class ArbitroService extends ServiceBase {

    private final ArbitroDAO arbitroDAO;

    public ArbitroService(ArbitroDAO arbitroDAO) {
        this.arbitroDAO = arbitroDAO;
    }

    public void cadastrarArbitro(Usuario solicitante, String nome, int idade,
                                 CategoriaArbitro categoria, int experiencia,
                                 String nacionalidade)
            throws AcessoNegadoException, ArbitroJaCadastradoException, IOException {

        verificarPermissao(solicitante, TipoPerfil.ADMINISTRADOR, TipoPerfil.ORGANIZADOR);

        Optional<Arbitro> existente = arbitroDAO.buscarPorNome(nome);
        if (existente.isPresent()) {
            throw new ArbitroJaCadastradoException(nome);
        }

        Arbitro arbitro = new Arbitro(nome, idade, categoria, experiencia, nacionalidade);
        arbitroDAO.salvar(arbitro);
    }

    public void removerArbitro(Usuario solicitante, String nome)
            throws AcessoNegadoException, ArbitroNaoEncontradoException, IOException {

        verificarPermissao(solicitante, TipoPerfil.ADMINISTRADOR, TipoPerfil.ORGANIZADOR);

        Optional<Arbitro> existente = arbitroDAO.buscarPorNome(nome);
        if (existente.isEmpty()) {
            throw new ArbitroNaoEncontradoException(nome);
        }

        arbitroDAO.remover(nome);
    }

    public Arbitro buscarPorNome(String nome)
            throws ArbitroNaoEncontradoException, IOException {

        return arbitroDAO.buscarPorNome(nome)
                .orElseThrow(() -> new ArbitroNaoEncontradoException(nome));
    }

    public List<Arbitro> listarArbitros() throws IOException {
        return arbitroDAO.carregaLista();
    }
}
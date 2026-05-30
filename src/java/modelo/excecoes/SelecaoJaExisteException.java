package src.java.modelo.excecoes;

public class SelecaoJaExisteException extends RuntimeException {
  public SelecaoJaExisteException(String message) {
    super(message);
  }
}

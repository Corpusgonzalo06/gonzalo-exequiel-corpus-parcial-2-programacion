package exceptions;

public class CodigoDuplicadoException extends Exception {
    public CodigoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
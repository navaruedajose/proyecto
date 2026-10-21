package mx.unam.aragon.tsp.vmartinez.pizzas.exceptions;

/**
 * Se lanza cuando un recurso no se puede eliminar porque otros registros dependen de él.
 * Se traduce en un HTTP 409.
 */
public class RecursoEnUsoException extends RuntimeException {

    public RecursoEnUsoException(String mensaje) {
        super(mensaje);
    }
}

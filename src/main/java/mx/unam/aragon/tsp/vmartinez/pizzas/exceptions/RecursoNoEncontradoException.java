package mx.unam.aragon.tsp.vmartinez.pizzas.exceptions;

/**
 * Se lanza cuando se busca un recurso que no existe. Se traduce en un HTTP 404.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}

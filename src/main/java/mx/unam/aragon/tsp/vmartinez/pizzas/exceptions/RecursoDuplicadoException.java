package mx.unam.aragon.tsp.vmartinez.pizzas.exceptions;

/**
 * Se lanza cuando se intenta registrar un valor que ya existe (nombre o correo repetido).
 * Se traduce en un HTTP 409.
 */
public class RecursoDuplicadoException extends RuntimeException {

    public RecursoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}

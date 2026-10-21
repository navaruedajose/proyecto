package mx.unam.aragon.tsp.vmartinez.pizzas.dtos;

/**
 * Respuesta simple con el id afectado y un mensaje, usada en las operaciones DELETE.
 */
public record IdDto(Long id, String mensaje) {
}

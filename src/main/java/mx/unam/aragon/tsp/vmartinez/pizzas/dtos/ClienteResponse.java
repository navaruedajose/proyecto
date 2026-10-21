package mx.unam.aragon.tsp.vmartinez.pizzas.dtos;

import mx.unam.aragon.tsp.vmartinez.pizzas.entities.Cliente;

/**
 * Datos que devuelve la API para un cliente.
 */
public record ClienteResponse(
        Long id,
        String nombre,
        String apellidos,
        String email,
        String telefono,
        String direccion
) {

    public static ClienteResponse from(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellidos(),
                cliente.getEmail(),
                cliente.getTelefono(),
                cliente.getDireccion()
        );
    }
}

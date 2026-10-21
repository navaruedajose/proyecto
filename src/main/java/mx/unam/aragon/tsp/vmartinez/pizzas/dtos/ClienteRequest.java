package mx.unam.aragon.tsp.vmartinez.pizzas.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos para crear o actualizar un cliente.
 */
public record ClienteRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80, message = "El nombre no puede tener mas de 80 caracteres")
        String nombre,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 80, message = "Los apellidos no pueden tener mas de 80 caracteres")
        String apellidos,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato valido")
        @Size(max = 120, message = "El correo no puede tener mas de 120 caracteres")
        String email,

        @Size(max = 20, message = "El telefono no puede tener mas de 20 caracteres")
        String telefono,

        @Size(max = 200, message = "La direccion no puede tener mas de 200 caracteres")
        String direccion
) {
}

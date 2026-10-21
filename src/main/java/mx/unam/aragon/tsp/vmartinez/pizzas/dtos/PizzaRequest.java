package mx.unam.aragon.tsp.vmartinez.pizzas.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Datos para crear o actualizar una pizza.
 * {@code tamanoId} es opcional y debe existir en el catalogo de tamaños.
 */
public record PizzaRequest(

        @NotBlank(message = "El nombre de la pizza es obligatorio")
        @Size(max = 80, message = "El nombre no puede tener mas de 80 caracteres")
        String nombre,

        @Size(max = 255, message = "La descripcion no puede tener mas de 255 caracteres")
        String descripcion,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
        @Digits(integer = 8, fraction = 2, message = "El precio admite maximo 8 enteros y 2 decimales")
        BigDecimal precio,

        Long tamanoId,

        Boolean disponible
) {
}

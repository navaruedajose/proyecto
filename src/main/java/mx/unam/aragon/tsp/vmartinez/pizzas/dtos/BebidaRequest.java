package mx.unam.aragon.tsp.vmartinez.pizzas.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mx.unam.aragon.tsp.vmartinez.pizzas.entities.TipoBebida;

import java.math.BigDecimal;

/**
 * Datos para crear o actualizar una bebida.
 */
public record BebidaRequest(

        @NotBlank(message = "El nombre de la bebida es obligatorio")
        @Size(max = 80, message = "El nombre no puede tener mas de 80 caracteres")
        String nombre,

        @NotNull(message = "El tipo de bebida es obligatorio")
        TipoBebida tipo,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
        @Digits(integer = 8, fraction = 2, message = "El precio admite maximo 8 enteros y 2 decimales")
        BigDecimal precio,

        @NotNull(message = "El volumen en mililitros es obligatorio")
        @Min(value = 100, message = "El volumen minimo es de 100 ml")
        @Max(value = 5000, message = "El volumen maximo es de 5000 ml")
        Integer volumenMl,

        Boolean disponible
) {
}

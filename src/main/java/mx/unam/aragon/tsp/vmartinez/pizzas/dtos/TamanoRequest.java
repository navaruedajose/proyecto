package mx.unam.aragon.tsp.vmartinez.pizzas.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mx.unam.aragon.tsp.vmartinez.pizzas.entities.Tamano;

import java.math.BigDecimal;

/**
 * Datos que llegan en el cuerpo de la peticion para crear o actualizar un tamaño.
 */
public record TamanoRequest(

        @NotBlank(message = "El nombre del tamaño es obligatorio")
        @Size(max = 40, message = "El nombre no puede tener mas de 40 caracteres")
        String nombre,

        @NotNull(message = "El diametro en centimetros es obligatorio")
        @Min(value = 10, message = "El diametro minimo es de 10 cm")
        @Max(value = 100, message = "El diametro maximo es de 100 cm")
        Integer diametroCm,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
        @Digits(integer = 8, fraction = 2, message = "El precio admite maximo 8 enteros y 2 decimales")
        BigDecimal precio
) {

    public Tamano toEntity() {
        return new Tamano(nombre.trim(), diametroCm, precio);
    }
}

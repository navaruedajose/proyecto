package mx.unam.aragon.tsp.vmartinez.pizzas.dtos;

import mx.unam.aragon.tsp.vmartinez.pizzas.entities.Tamano;

import java.math.BigDecimal;

/**
 * Datos que devuelve la API para un tamaño.
 */
public record TamanoResponse(
        Long id,
        String nombre,
        Integer diametroCm,
        BigDecimal precio
) {

    public static TamanoResponse from(Tamano tamano) {
        if (tamano == null) {
            return null;
        }
        return new TamanoResponse(
                tamano.getId(),
                tamano.getNombre(),
                tamano.getDiametroCm(),
                tamano.getPrecio()
        );
    }
}

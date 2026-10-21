package mx.unam.aragon.tsp.vmartinez.pizzas.dtos;

import mx.unam.aragon.tsp.vmartinez.pizzas.entities.Bebida;
import mx.unam.aragon.tsp.vmartinez.pizzas.entities.TipoBebida;

import java.math.BigDecimal;

/**
 * Datos que devuelve la API para una bebida.
 */
public record BebidaResponse(
        Long id,
        String nombre,
        TipoBebida tipo,
        BigDecimal precio,
        Integer volumenMl,
        boolean disponible
) {

    public static BebidaResponse from(Bebida bebida) {
        return new BebidaResponse(
                bebida.getId(),
                bebida.getNombre(),
                bebida.getTipo(),
                bebida.getPrecio(),
                bebida.getVolumenMl(),
                bebida.isDisponible()
        );
    }
}

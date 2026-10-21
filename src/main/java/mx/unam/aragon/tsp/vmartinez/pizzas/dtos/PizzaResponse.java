package mx.unam.aragon.tsp.vmartinez.pizzas.dtos;

import mx.unam.aragon.tsp.vmartinez.pizzas.entities.Pizza;

import java.math.BigDecimal;

/**
 * Datos que devuelve la API para una pizza.
 */
public record PizzaResponse(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        TamanoResponse tamano,
        boolean disponible
) {

    public static PizzaResponse from(Pizza pizza) {
        return new PizzaResponse(
                pizza.getId(),
                pizza.getNombre(),
                pizza.getDescripcion(),
                pizza.getPrecio(),
                TamanoResponse.from(pizza.getTamano()),
                pizza.isDisponible()
        );
    }
}

package mx.unam.aragon.tsp.vmartinez.pizzas.config;

import mx.unam.aragon.tsp.vmartinez.pizzas.entities.Bebida;
import mx.unam.aragon.tsp.vmartinez.pizzas.entities.Cliente;
import mx.unam.aragon.tsp.vmartinez.pizzas.entities.Pizza;
import mx.unam.aragon.tsp.vmartinez.pizzas.entities.Tamano;
import mx.unam.aragon.tsp.vmartinez.pizzas.entities.TipoBebida;
import mx.unam.aragon.tsp.vmartinez.pizzas.repositories.BebidaRepository;
import mx.unam.aragon.tsp.vmartinez.pizzas.repositories.ClienteRepository;
import mx.unam.aragon.tsp.vmartinez.pizzas.repositories.PizzaRepository;
import mx.unam.aragon.tsp.vmartinez.pizzas.repositories.TamanoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Carga datos de ejemplo la primera vez que arranca la aplicacion,
 * para poder probar los endpoints sin capturar todo a mano.
 */
@Component
public class DatosIniciales implements CommandLineRunner {

    private final TamanoRepository tamanoRepository;
    private final PizzaRepository pizzaRepository;
    private final BebidaRepository bebidaRepository;
    private final ClienteRepository clienteRepository;

    public DatosIniciales(
            TamanoRepository tamanoRepository,
            PizzaRepository pizzaRepository,
            BebidaRepository bebidaRepository,
            ClienteRepository clienteRepository
    ) {
        this.tamanoRepository = tamanoRepository;
        this.pizzaRepository = pizzaRepository;
        this.bebidaRepository = bebidaRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public void run(String... args) {
        if (tamanoRepository.count() > 0) {
            return;
        }

        Tamano chica = tamanoRepository.save(new Tamano("Chica", 25, new BigDecimal("129.00")));
        Tamano mediana = tamanoRepository.save(new Tamano("Mediana", 30, new BigDecimal("179.00")));
        Tamano grande = tamanoRepository.save(new Tamano("Grande", 35, new BigDecimal("229.00")));
        Tamano familiar = tamanoRepository.save(new Tamano("Familiar", 40, new BigDecimal("279.00")));

        pizzaRepository.saveAll(List.of(
                new Pizza("Pepperoni", "Pepperoni, queso mozzarella y salsa de tomate",
                        new BigDecimal("179.00"), mediana, true),
                new Pizza("Hawaiana", "Jamón, piña y queso mozzarella",
                        new BigDecimal("169.00"), mediana, true),
                new Pizza("Mexicana", "Chorizo, jalapeño, cebolla y queso",
                        new BigDecimal("189.00"), grande, true),
                new Pizza("Cuatro Quesos", "Mozzarella, parmesano, manchego y gouda",
                        new BigDecimal("199.00"), grande, true),
                new Pizza("Vegetariana", "Champiñones, pimiento, aceituna y cebolla",
                        new BigDecimal("169.00"), chica, true),
                new Pizza("Suprema", "Todos los ingredientes de la casa",
                        new BigDecimal("299.00"), familiar, false)
        ));

        bebidaRepository.saveAll(List.of(
                new Bebida("Coca-Cola", TipoBebida.REFRESCO, new BigDecimal("35.00"), 600, true),
                new Bebida("Sprite", TipoBebida.REFRESCO, new BigDecimal("35.00"), 600, true),
                new Bebida("Agua natural", TipoBebida.AGUA, new BigDecimal("25.00"), 500, true),
                new Bebida("Jugo de naranja", TipoBebida.JUGO, new BigDecimal("45.00"), 400, true),
                new Bebida("Cerveza artesanal", TipoBebida.CERVEZA, new BigDecimal("65.00"), 355, false)
        ));

        clienteRepository.saveAll(List.of(
                new Cliente("Ana", "García López", "ana.garcia@example.com", "5512345678",
                        "Av. Juárez 123, Aragon"),
                new Cliente("Luis", "Martínez Ruiz", "luis.martinez@example.com", "5587654321",
                        "Calle 5 de Mayo 45, Nezahualcóyotl"),
                new Cliente("María", "Hernández Cruz", "maria.hernandez@example.com", "5511223344",
                        "Av. Central 890, Ecatepec")
        ));
    }
}

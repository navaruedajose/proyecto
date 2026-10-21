package mx.unam.aragon.tsp.vmartinez.pizzas.repositories;

import mx.unam.aragon.tsp.vmartinez.pizzas.entities.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PizzaRepository extends JpaRepository<Pizza, Long> {

    Optional<Pizza> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    List<Pizza> findByDisponibleTrue();

    List<Pizza> findByTamanoId(Long tamanoId);
}

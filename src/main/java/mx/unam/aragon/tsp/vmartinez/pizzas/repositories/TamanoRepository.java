package mx.unam.aragon.tsp.vmartinez.pizzas.repositories;

import mx.unam.aragon.tsp.vmartinez.pizzas.entities.Tamano;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TamanoRepository extends JpaRepository<Tamano, Long> {

    Optional<Tamano> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
}

package mx.unam.aragon.tsp.vmartinez.pizzas.repositories;

import mx.unam.aragon.tsp.vmartinez.pizzas.entities.Bebida;
import mx.unam.aragon.tsp.vmartinez.pizzas.entities.TipoBebida;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BebidaRepository extends JpaRepository<Bebida, Long> {

    Optional<Bebida> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    List<Bebida> findByTipo(TipoBebida tipo);

    List<Bebida> findByDisponibleTrue();
}

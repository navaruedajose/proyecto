package mx.unam.aragon.tsp.vmartinez.pizzas.repositories;

import mx.unam.aragon.tsp.vmartinez.pizzas.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}

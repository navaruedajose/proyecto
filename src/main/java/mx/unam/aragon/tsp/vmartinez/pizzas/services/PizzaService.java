package mx.unam.aragon.tsp.vmartinez.pizzas.services;

import mx.unam.aragon.tsp.vmartinez.pizzas.dtos.IdDto;
import mx.unam.aragon.tsp.vmartinez.pizzas.dtos.PizzaRequest;
import mx.unam.aragon.tsp.vmartinez.pizzas.dtos.PizzaResponse;
import mx.unam.aragon.tsp.vmartinez.pizzas.entities.Pizza;
import mx.unam.aragon.tsp.vmartinez.pizzas.entities.Tamano;
import mx.unam.aragon.tsp.vmartinez.pizzas.exceptions.RecursoDuplicadoException;
import mx.unam.aragon.tsp.vmartinez.pizzas.exceptions.RecursoNoEncontradoException;
import mx.unam.aragon.tsp.vmartinez.pizzas.repositories.PizzaRepository;
import mx.unam.aragon.tsp.vmartinez.pizzas.repositories.TamanoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Reglas de negocio del catalogo de pizzas.
 */
@Service
@Transactional(readOnly = true)
public class PizzaService {

    private final PizzaRepository pizzaRepository;
    private final TamanoRepository tamanoRepository;

    public PizzaService(PizzaRepository pizzaRepository, TamanoRepository tamanoRepository) {
        this.pizzaRepository = pizzaRepository;
        this.tamanoRepository = tamanoRepository;
    }

    public List<PizzaResponse> listar(Boolean disponible) {
        List<Pizza> pizzas = (disponible == null)
                ? pizzaRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                : pizzaRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                        .stream()
                        .filter(pizza -> pizza.isDisponible() == disponible)
                        .toList();

        return pizzas.stream().map(PizzaResponse::from).toList();
    }

    public PizzaResponse obtenerPorId(Long id) {
        return PizzaResponse.from(buscarEntidad(id));
    }

    @Transactional
    public PizzaResponse crear(PizzaRequest peticion) {
        validarNombreDisponible(peticion.nombre(), null);

        Pizza pizza = new Pizza();
        aplicarDatos(pizza, peticion);

        return PizzaResponse.from(pizzaRepository.save(pizza));
    }

    @Transactional
    public PizzaResponse actualizar(Long id, PizzaRequest peticion) {
        Pizza pizza = buscarEntidad(id);
        validarNombreDisponible(peticion.nombre(), id);

        aplicarDatos(pizza, peticion);

        return PizzaResponse.from(pizzaRepository.save(pizza));
    }

    @Transactional
    public IdDto eliminar(Long id) {
        Pizza pizza = buscarEntidad(id);
        pizzaRepository.delete(pizza);
        return new IdDto(id, "Pizza eliminada correctamente");
    }

    private void aplicarDatos(Pizza pizza, PizzaRequest peticion) {
        pizza.setNombre(peticion.nombre().trim());
        pizza.setDescripcion(peticion.descripcion());
        pizza.setPrecio(peticion.precio());
        pizza.setDisponible(peticion.disponible() == null || peticion.disponible());
        pizza.setTamano(resolverTamano(peticion.tamanoId()));
    }

    private Tamano resolverTamano(Long tamanoId) {
        if (tamanoId == null) {
            return null;
        }
        return tamanoRepository.findById(tamanoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un tamaño con el id " + tamanoId
                ));
    }

    private Pizza buscarEntidad(Long id) {
        return pizzaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una pizza con el id " + id
                ));
    }

    private void validarNombreDisponible(String nombre, Long idActual) {
        pizzaRepository.findByNombreIgnoreCase(nombre.trim())
                .filter(encontrada -> !encontrada.getId().equals(idActual))
                .ifPresent(encontrada -> {
                    throw new RecursoDuplicadoException(
                            "Ya existe una pizza con el nombre " + nombre
                    );
                });
    }
}

package mx.unam.aragon.tsp.vmartinez.pizzas.services;

import mx.unam.aragon.tsp.vmartinez.pizzas.dtos.IdDto;
import mx.unam.aragon.tsp.vmartinez.pizzas.dtos.TamanoRequest;
import mx.unam.aragon.tsp.vmartinez.pizzas.dtos.TamanoResponse;
import mx.unam.aragon.tsp.vmartinez.pizzas.entities.Tamano;
import mx.unam.aragon.tsp.vmartinez.pizzas.exceptions.RecursoDuplicadoException;
import mx.unam.aragon.tsp.vmartinez.pizzas.exceptions.RecursoEnUsoException;
import mx.unam.aragon.tsp.vmartinez.pizzas.exceptions.RecursoNoEncontradoException;
import mx.unam.aragon.tsp.vmartinez.pizzas.repositories.PizzaRepository;
import mx.unam.aragon.tsp.vmartinez.pizzas.repositories.TamanoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Reglas de negocio del catalogo de tamaños.
 */
@Service
@Transactional(readOnly = true)
public class TamanoService {

    private final TamanoRepository tamanoRepository;
    private final PizzaRepository pizzaRepository;

    public TamanoService(TamanoRepository tamanoRepository, PizzaRepository pizzaRepository) {
        this.tamanoRepository = tamanoRepository;
        this.pizzaRepository = pizzaRepository;
    }

    public List<TamanoResponse> listar() {
        return tamanoRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(TamanoResponse::from)
                .toList();
    }

    public TamanoResponse obtenerPorId(Long id) {
        return TamanoResponse.from(buscarEntidad(id));
    }

    @Transactional
    public TamanoResponse crear(TamanoRequest peticion) {
        validarNombreDisponible(peticion.nombre(), null);

        Tamano tamano = peticion.toEntity();
        return TamanoResponse.from(tamanoRepository.save(tamano));
    }

    @Transactional
    public TamanoResponse actualizar(Long id, TamanoRequest peticion) {
        Tamano tamano = buscarEntidad(id);
        validarNombreDisponible(peticion.nombre(), id);

        tamano.setNombre(peticion.nombre().trim());
        tamano.setDiametroCm(peticion.diametroCm());
        tamano.setPrecio(peticion.precio());

        return TamanoResponse.from(tamanoRepository.save(tamano));
    }

    @Transactional
    public IdDto eliminar(Long id) {
        Tamano tamano = buscarEntidad(id);

        if (!pizzaRepository.findByTamanoId(id).isEmpty()) {
            throw new RecursoEnUsoException(
                    "No se puede eliminar el tamaño " + tamano.getNombre()
                            + " porque hay pizzas que lo usan"
            );
        }

        tamanoRepository.delete(tamano);
        return new IdDto(id, "Tamaño eliminado correctamente");
    }

    private Tamano buscarEntidad(Long id) {
        return tamanoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un tamaño con el id " + id
                ));
    }

    private void validarNombreDisponible(String nombre, Long idActual) {
        tamanoRepository.findByNombreIgnoreCase(nombre.trim())
                .filter(encontrado -> !encontrado.getId().equals(idActual))
                .ifPresent(encontrado -> {
                    throw new RecursoDuplicadoException(
                            "Ya existe un tamaño con el nombre " + nombre
                    );
                });
    }
}

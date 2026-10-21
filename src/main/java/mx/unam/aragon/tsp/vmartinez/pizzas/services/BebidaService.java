package mx.unam.aragon.tsp.vmartinez.pizzas.services;

import mx.unam.aragon.tsp.vmartinez.pizzas.dtos.BebidaRequest;
import mx.unam.aragon.tsp.vmartinez.pizzas.dtos.BebidaResponse;
import mx.unam.aragon.tsp.vmartinez.pizzas.dtos.IdDto;
import mx.unam.aragon.tsp.vmartinez.pizzas.entities.Bebida;
import mx.unam.aragon.tsp.vmartinez.pizzas.entities.TipoBebida;
import mx.unam.aragon.tsp.vmartinez.pizzas.exceptions.RecursoDuplicadoException;
import mx.unam.aragon.tsp.vmartinez.pizzas.exceptions.RecursoNoEncontradoException;
import mx.unam.aragon.tsp.vmartinez.pizzas.repositories.BebidaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Reglas de negocio del catalogo de bebidas.
 */
@Service
@Transactional(readOnly = true)
public class BebidaService {

    private final BebidaRepository bebidaRepository;

    public BebidaService(BebidaRepository bebidaRepository) {
        this.bebidaRepository = bebidaRepository;
    }

    public List<BebidaResponse> listar(TipoBebida tipo, Boolean disponible) {
        return bebidaRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .filter(bebida -> tipo == null || bebida.getTipo() == tipo)
                .filter(bebida -> disponible == null || bebida.isDisponible() == disponible)
                .map(BebidaResponse::from)
                .toList();
    }

    public BebidaResponse obtenerPorId(Long id) {
        return BebidaResponse.from(buscarEntidad(id));
    }

    @Transactional
    public BebidaResponse crear(BebidaRequest peticion) {
        validarNombreDisponible(peticion.nombre(), null);

        Bebida bebida = new Bebida();
        aplicarDatos(bebida, peticion);

        return BebidaResponse.from(bebidaRepository.save(bebida));
    }

    @Transactional
    public BebidaResponse actualizar(Long id, BebidaRequest peticion) {
        Bebida bebida = buscarEntidad(id);
        validarNombreDisponible(peticion.nombre(), id);

        aplicarDatos(bebida, peticion);

        return BebidaResponse.from(bebidaRepository.save(bebida));
    }

    @Transactional
    public IdDto eliminar(Long id) {
        Bebida bebida = buscarEntidad(id);
        bebidaRepository.delete(bebida);
        return new IdDto(id, "Bebida eliminada correctamente");
    }

    private void aplicarDatos(Bebida bebida, BebidaRequest peticion) {
        bebida.setNombre(peticion.nombre().trim());
        bebida.setTipo(peticion.tipo());
        bebida.setPrecio(peticion.precio());
        bebida.setVolumenMl(peticion.volumenMl());
        bebida.setDisponible(peticion.disponible() == null || peticion.disponible());
    }

    private Bebida buscarEntidad(Long id) {
        return bebidaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una bebida con el id " + id
                ));
    }

    private void validarNombreDisponible(String nombre, Long idActual) {
        bebidaRepository.findByNombreIgnoreCase(nombre.trim())
                .filter(encontrada -> !encontrada.getId().equals(idActual))
                .ifPresent(encontrada -> {
                    throw new RecursoDuplicadoException(
                            "Ya existe una bebida con el nombre " + nombre
                    );
                });
    }
}

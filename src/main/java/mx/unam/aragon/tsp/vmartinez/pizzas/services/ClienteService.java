package mx.unam.aragon.tsp.vmartinez.pizzas.services;

import mx.unam.aragon.tsp.vmartinez.pizzas.dtos.ClienteRequest;
import mx.unam.aragon.tsp.vmartinez.pizzas.dtos.ClienteResponse;
import mx.unam.aragon.tsp.vmartinez.pizzas.dtos.IdDto;
import mx.unam.aragon.tsp.vmartinez.pizzas.entities.Cliente;
import mx.unam.aragon.tsp.vmartinez.pizzas.exceptions.RecursoDuplicadoException;
import mx.unam.aragon.tsp.vmartinez.pizzas.exceptions.RecursoNoEncontradoException;
import mx.unam.aragon.tsp.vmartinez.pizzas.repositories.ClienteRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Reglas de negocio del catalogo de clientes.
 */
@Service
@Transactional(readOnly = true)
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<ClienteResponse> listar() {
        return clienteRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(ClienteResponse::from)
                .toList();
    }

    public ClienteResponse obtenerPorId(Long id) {
        return ClienteResponse.from(buscarEntidad(id));
    }

    @Transactional
    public ClienteResponse crear(ClienteRequest peticion) {
        validarEmailDisponible(peticion.email(), null);

        Cliente cliente = new Cliente();
        aplicarDatos(cliente, peticion);

        return ClienteResponse.from(clienteRepository.save(cliente));
    }

    @Transactional
    public ClienteResponse actualizar(Long id, ClienteRequest peticion) {
        Cliente cliente = buscarEntidad(id);
        validarEmailDisponible(peticion.email(), id);

        aplicarDatos(cliente, peticion);

        return ClienteResponse.from(clienteRepository.save(cliente));
    }

    @Transactional
    public IdDto eliminar(Long id) {
        Cliente cliente = buscarEntidad(id);
        clienteRepository.delete(cliente);
        return new IdDto(id, "Cliente eliminado correctamente");
    }

    private void aplicarDatos(Cliente cliente, ClienteRequest peticion) {
        cliente.setNombre(peticion.nombre().trim());
        cliente.setApellidos(peticion.apellidos().trim());
        cliente.setEmail(peticion.email().trim().toLowerCase());
        cliente.setTelefono(peticion.telefono());
        cliente.setDireccion(peticion.direccion());
    }

    private Cliente buscarEntidad(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un cliente con el id " + id
                ));
    }

    private void validarEmailDisponible(String email, Long idActual) {
        clienteRepository.findByEmailIgnoreCase(email.trim())
                .filter(encontrado -> !encontrado.getId().equals(idActual))
                .ifPresent(encontrado -> {
                    throw new RecursoDuplicadoException(
                            "Ya existe un cliente con el correo " + email
                    );
                });
    }
}

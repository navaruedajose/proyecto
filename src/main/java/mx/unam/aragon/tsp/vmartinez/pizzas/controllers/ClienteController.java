package mx.unam.aragon.tsp.vmartinez.pizzas.controllers;

import jakarta.validation.Valid;
import mx.unam.aragon.tsp.vmartinez.pizzas.dtos.ClienteRequest;
import mx.unam.aragon.tsp.vmartinez.pizzas.dtos.ClienteResponse;
import mx.unam.aragon.tsp.vmartinez.pizzas.dtos.IdDto;
import mx.unam.aragon.tsp.vmartinez.pizzas.services.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * CRUD de clientes.
 *
 * GET    /api/v1/clientes
 * GET    /api/v1/clientes/{id}
 * POST   /api/v1/clientes
 * PUT    /api/v1/clientes/{id}
 * DELETE /api/v1/clientes/{id}
 */
@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public List<ClienteResponse> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    public ClienteResponse obtenerPorId(@PathVariable Long id) {
        return clienteService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> crear(@Valid @RequestBody ClienteRequest peticion) {
        ClienteResponse creado = clienteService.crear(peticion);
        return ResponseEntity
                .created(URI.create("/api/v1/clientes/" + creado.id()))
                .body(creado);
    }

    @PutMapping("/{id}")
    public ClienteResponse actualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequest peticion) {
        return clienteService.actualizar(id, peticion);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public IdDto eliminar(@PathVariable Long id) {
        return clienteService.eliminar(id);
    }
}

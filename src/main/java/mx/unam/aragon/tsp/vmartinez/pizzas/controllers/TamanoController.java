package mx.unam.aragon.tsp.vmartinez.pizzas.controllers;

import jakarta.validation.Valid;
import mx.unam.aragon.tsp.vmartinez.pizzas.dtos.IdDto;
import mx.unam.aragon.tsp.vmartinez.pizzas.dtos.TamanoRequest;
import mx.unam.aragon.tsp.vmartinez.pizzas.dtos.TamanoResponse;
import mx.unam.aragon.tsp.vmartinez.pizzas.services.TamanoService;
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
 * CRUD de tamaños.
 *
 * GET    /api/v1/tamanos
 * GET    /api/v1/tamanos/{id}
 * POST   /api/v1/tamanos
 * PUT    /api/v1/tamanos/{id}
 * DELETE /api/v1/tamanos/{id}
 */
@RestController
@RequestMapping("/api/v1/tamanos")
public class TamanoController {

    private final TamanoService tamanoService;

    public TamanoController(TamanoService tamanoService) {
        this.tamanoService = tamanoService;
    }

    @GetMapping
    public List<TamanoResponse> listar() {
        return tamanoService.listar();
    }

    @GetMapping("/{id}")
    public TamanoResponse obtenerPorId(@PathVariable Long id) {
        return tamanoService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<TamanoResponse> crear(@Valid @RequestBody TamanoRequest peticion) {
        TamanoResponse creado = tamanoService.crear(peticion);
        return ResponseEntity
                .created(URI.create("/api/v1/tamanos/" + creado.id()))
                .body(creado);
    }

    @PutMapping("/{id}")
    public TamanoResponse actualizar(@PathVariable Long id, @Valid @RequestBody TamanoRequest peticion) {
        return tamanoService.actualizar(id, peticion);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public IdDto eliminar(@PathVariable Long id) {
        return tamanoService.eliminar(id);
    }
}

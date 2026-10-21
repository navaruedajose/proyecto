package mx.unam.aragon.tsp.vmartinez.pizzas.controllers;

import jakarta.validation.Valid;
import mx.unam.aragon.tsp.vmartinez.pizzas.dtos.BebidaRequest;
import mx.unam.aragon.tsp.vmartinez.pizzas.dtos.BebidaResponse;
import mx.unam.aragon.tsp.vmartinez.pizzas.dtos.IdDto;
import mx.unam.aragon.tsp.vmartinez.pizzas.entities.TipoBebida;
import mx.unam.aragon.tsp.vmartinez.pizzas.services.BebidaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * CRUD de bebidas.
 *
 * GET    /api/v1/bebidas
 * GET    /api/v1/bebidas/{id}
 * POST   /api/v1/bebidas
 * PUT    /api/v1/bebidas/{id}
 * DELETE /api/v1/bebidas/{id}
 */
@RestController
@RequestMapping("/api/v1/bebidas")
public class BebidaController {

    private final BebidaService bebidaService;

    public BebidaController(BebidaService bebidaService) {
        this.bebidaService = bebidaService;
    }

    @GetMapping
    public List<BebidaResponse> listar(
            @RequestParam(required = false) TipoBebida tipo,
            @RequestParam(required = false) Boolean disponible
    ) {
        return bebidaService.listar(tipo, disponible);
    }

    @GetMapping("/{id}")
    public BebidaResponse obtenerPorId(@PathVariable Long id) {
        return bebidaService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<BebidaResponse> crear(@Valid @RequestBody BebidaRequest peticion) {
        BebidaResponse creada = bebidaService.crear(peticion);
        return ResponseEntity
                .created(URI.create("/api/v1/bebidas/" + creada.id()))
                .body(creada);
    }

    @PutMapping("/{id}")
    public BebidaResponse actualizar(@PathVariable Long id, @Valid @RequestBody BebidaRequest peticion) {
        return bebidaService.actualizar(id, peticion);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public IdDto eliminar(@PathVariable Long id) {
        return bebidaService.eliminar(id);
    }
}

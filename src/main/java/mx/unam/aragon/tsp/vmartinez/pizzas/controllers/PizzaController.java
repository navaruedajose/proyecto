package mx.unam.aragon.tsp.vmartinez.pizzas.controllers;

import jakarta.validation.Valid;
import mx.unam.aragon.tsp.vmartinez.pizzas.dtos.IdDto;
import mx.unam.aragon.tsp.vmartinez.pizzas.dtos.PizzaRequest;
import mx.unam.aragon.tsp.vmartinez.pizzas.dtos.PizzaResponse;
import mx.unam.aragon.tsp.vmartinez.pizzas.services.PizzaService;
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
 * CRUD de pizzas.
 *
 * GET    /api/v1/pizzas
 * GET    /api/v1/pizzas/{id}
 * POST   /api/v1/pizzas
 * PUT    /api/v1/pizzas/{id}
 * DELETE /api/v1/pizzas/{id}
 */
@RestController
@RequestMapping("/api/v1/pizzas")
public class PizzaController {

    private final PizzaService pizzaService;

    public PizzaController(PizzaService pizzaService) {
        this.pizzaService = pizzaService;
    }

    @GetMapping
    public List<PizzaResponse> listar(@RequestParam(required = false) Boolean disponible) {
        return pizzaService.listar(disponible);
    }

    @GetMapping("/{id}")
    public PizzaResponse obtenerPorId(@PathVariable Long id) {
        return pizzaService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<PizzaResponse> crear(@Valid @RequestBody PizzaRequest peticion) {
        PizzaResponse creada = pizzaService.crear(peticion);
        return ResponseEntity
                .created(URI.create("/api/v1/pizzas/" + creada.id()))
                .body(creada);
    }

    @PutMapping("/{id}")
    public PizzaResponse actualizar(@PathVariable Long id, @Valid @RequestBody PizzaRequest peticion) {
        return pizzaService.actualizar(id, peticion);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public IdDto eliminar(@PathVariable Long id) {
        return pizzaService.eliminar(id);
    }
}

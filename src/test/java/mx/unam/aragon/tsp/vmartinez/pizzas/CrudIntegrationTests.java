package mx.unam.aragon.tsp.vmartinez.pizzas;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integracion del CRUD de las cuatro entidades.
 * Cada prueba se ejecuta dentro de una transaccion que se revierte al final,
 * por lo que los datos iniciales quedan intactos.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CrudIntegrationTests {

    private static final String JSON = MediaType.APPLICATION_JSON_VALUE;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Pizzas: ciclo completo listar, crear, consultar, actualizar y eliminar")
    void pizzasCrudCompleto() throws Exception {
        mockMvc.perform(get("/api/v1/pizzas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nombre").exists());

        mockMvc.perform(post("/api/v1/pizzas")
                        .contentType(JSON)
                        .content("""
                                {"nombre":"Pizza de prueba","descripcion":"Creada en la prueba",
                                 "precio":199.50,"tamanoId":1,"disponible":true}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre").value("Pizza de prueba"))
                .andExpect(jsonPath("$.tamano.nombre").value("Chica"));

        mockMvc.perform(get("/api/v1/pizzas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Pepperoni"));

        mockMvc.perform(put("/api/v1/pizzas/1")
                        .contentType(JSON)
                        .content("""
                                {"nombre":"Pepperoni especial","descripcion":"Con doble pepperoni",
                                 "precio":209.00,"tamanoId":2,"disponible":false}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Pepperoni especial"))
                .andExpect(jsonPath("$.precio").value(209.00))
                .andExpect(jsonPath("$.disponible").value(false));

        mockMvc.perform(delete("/api/v1/pizzas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.mensaje").exists());

        mockMvc.perform(get("/api/v1/pizzas/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Pizzas: rechaza datos invalidos con 400 y detalle por campo")
    void pizzasValidaDatos() throws Exception {
        mockMvc.perform(post("/api/v1/pizzas")
                        .contentType(JSON)
                        .content("""
                                {"nombre":"","precio":-10}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nombre").exists())
                .andExpect(jsonPath("$.errores.precio").exists());
    }

    @Test
    @DisplayName("Pizzas: rechaza nombres repetidos con 409")
    void pizzasNombreDuplicado() throws Exception {
        mockMvc.perform(post("/api/v1/pizzas")
                        .contentType(JSON)
                        .content("""
                                {"nombre":"Pepperoni","precio":150.00}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Tamaños: ciclo completo de CRUD")
    void tamanosCrudCompleto() throws Exception {
        mockMvc.perform(get("/api/v1/tamanos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));

        mockMvc.perform(post("/api/v1/tamanos")
                        .contentType(JSON)
                        .content("""
                                {"nombre":"Jumbo","diametroCm":45,"precio":329.00}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Jumbo"));

        mockMvc.perform(put("/api/v1/tamanos/5")
                        .contentType(JSON)
                        .content("""
                                {"nombre":"Jumbo","diametroCm":46,"precio":339.00}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diametroCm").value(46));

        mockMvc.perform(delete("/api/v1/tamanos/5"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/tamanos/5"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Tamaños: no se puede eliminar un tamaño usado por pizzas")
    void tamanoEnUsoNoSeElimina() throws Exception {
        mockMvc.perform(delete("/api/v1/tamanos/2"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Bebidas: ciclo completo de CRUD y filtro por tipo")
    void bebidasCrudCompleto() throws Exception {
        mockMvc.perform(post("/api/v1/bebidas")
                        .contentType(JSON)
                        .content("""
                                {"nombre":"Limonada","tipo":"JUGO","precio":42.00,
                                 "volumenMl":500,"disponible":true}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("JUGO"));

        mockMvc.perform(get("/api/v1/bebidas").param("tipo", "JUGO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.nombre == 'Limonada')]").exists());

        mockMvc.perform(put("/api/v1/bebidas/1")
                        .contentType(JSON)
                        .content("""
                                {"nombre":"Coca-Cola","tipo":"REFRESCO","precio":38.00,
                                 "volumenMl":600,"disponible":false}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precio").value(38.00));

        mockMvc.perform(delete("/api/v1/bebidas/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/bebidas/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Bebidas: rechaza un tipo que no existe en el enum")
    void bebidaTipoInvalido() throws Exception {
        mockMvc.perform(post("/api/v1/bebidas")
                        .contentType(JSON)
                        .content("""
                                {"nombre":"Refresco raro","tipo":"NO_EXISTE","precio":10.00,
                                 "volumenMl":600}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Clientes: ciclo completo de CRUD")
    void clientesCrudCompleto() throws Exception {
        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(JSON)
                        .content("""
                                {"nombre":"Pedro","apellidos":"Ramírez","email":"pedro@example.com",
                                 "telefono":"5512345678","direccion":"Calle 1"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("pedro@example.com"));

        mockMvc.perform(get("/api/v1/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana"));

        mockMvc.perform(put("/api/v1/clientes/1")
                        .contentType(JSON)
                        .content("""
                                {"nombre":"Ana","apellidos":"García López","email":"ana.garcia@example.com",
                                 "telefono":"5500000000","direccion":"Nueva dirección 456"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.telefono").value("5500000000"));

        mockMvc.perform(delete("/api/v1/clientes/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/clientes/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Clientes: rechaza correos repetidos y con formato invalido")
    void clientesValidaEmail() throws Exception {
        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(JSON)
                        .content("""
                                {"nombre":"Otro","apellidos":"Cliente",
                                 "email":"ana.garcia@example.com"}
                                """))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(JSON)
                        .content("""
                                {"nombre":"Otro","apellidos":"Cliente","email":"no-es-correo"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.email").exists());
    }
}

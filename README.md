# Pizzas - CRUD con Spring Boot

API REST con el CRUD completo de **pizzas**, **tamaños**, **bebidas** y **clientes**,
construida sobre la plantilla del profesor
([javaSpring_pizza](https://github.com/vmartinez1984/javaSpring_pizza)):
mismo paquete base, mismo `build.gradle` y misma versión de Spring Boot.

## Tecnologías

| Componente | Versión |
|---|---|
| Java | 21 |
| Spring Boot | 4.1.1 |
| Gradle | 9.7.1 (wrapper incluido) |
| Spring Data JPA + Hibernate | incluido en el starter |
| Base de datos | H2 en memoria |
| Validación | Jakarta Bean Validation |
| Pruebas | JUnit 5 + MockMvc |

## Estructura

```
src/main/java/mx/unam/aragon/tsp/vmartinez/pizzas/
├── PizzasApplication.java
├── config/
│   └── DatosIniciales.java        # datos de ejemplo al arrancar
├── controllers/
│   ├── PizzaController.java
│   ├── TamanoController.java
│   ├── BebidaController.java
│   └── ClienteController.java
├── dtos/                          # Request / Response + IdDto
├── entities/
│   ├── Pizza.java                 # ManyToOne -> Tamano
│   ├── Tamano.java
│   ├── Bebida.java                # usa el enum TipoBebida
│   ├── TipoBebida.java
│   └── Cliente.java
├── exceptions/                    # excepciones de negocio + @RestControllerAdvice
├── repositories/                  # interfaces JpaRepository
└── services/                      # reglas de negocio y validaciones
```

Cada entidad sigue el mismo camino:
`Controller -> Service -> Repository -> Entity`, con DTOs de entrada y salida.

## Cómo ejecutar

**Opción 1 - IntelliJ IDEA**

1. `File > Open` y selecciona la carpeta `pizzas`.
2. Espera a que Gradle sincronice (usa Java 21 como SDK del proyecto).
3. Ejecuta la clase `PizzasApplication`.

**Opción 2 - Terminal**

```bash
./gradlew bootRun
```

La API queda disponible en `http://localhost:8080`.

- Panel web de pruebas: `http://localhost:8080/`
- Consola de la base de datos: `http://localhost:8080/h2-console`
  (JDBC URL `jdbc:h2:mem:pizzas`, usuario `sa`, sin contraseña)

## Endpoints

Todas las rutas están bajo `/api/v1`.

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| GET | `/pizzas` | Lista pizzas (filtro opcional `?disponible=true`) | 200 |
| GET | `/pizzas/{id}` | Busca una pizza | 200 / 404 |
| POST | `/pizzas` | Crea una pizza | 201 + `Location` |
| PUT | `/pizzas/{id}` | Actualiza una pizza | 200 / 404 |
| DELETE | `/pizzas/{id}` | Elimina una pizza | 200 / 404 |

Se repite igual para `/tamanos`, `/bebidas` y `/clientes`.
Las bebidas aceptan además los filtros `?tipo=JUGO` y `?disponible=true`.

## Ejemplos

```bash
# Listar
curl http://localhost:8080/api/v1/pizzas

# Crear
curl -X POST http://localhost:8080/api/v1/pizzas \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Pizza Postman","descripcion":"Prueba","precio":199.50,"tamanoId":1,"disponible":true}'

# Actualizar
curl -X PUT http://localhost:8080/api/v1/pizzas/1 \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Pepperoni especial","descripcion":"Con doble pepperoni","precio":209.00,"tamanoId":2,"disponible":false}'

# Eliminar
curl -X DELETE http://localhost:8080/api/v1/pizzas/1
```

Respuesta de un `DELETE`:

```json
{ "id": 1, "mensaje": "Pizza eliminada correctamente" }
```

## Cuerpos JSON

```json
// POST /api/v1/tamanos
{ "nombre": "Mediana", "diametroCm": 30, "precio": 179.00 }

// POST /api/v1/pizzas
{ "nombre": "Pepperoni", "descripcion": "Pepperoni y queso",
  "precio": 179.00, "tamanoId": 2, "disponible": true }

// POST /api/v1/bebidas
{ "nombre": "Coca-Cola", "tipo": "REFRESCO", "precio": 35.00,
  "volumenMl": 600, "disponible": true }

// POST /api/v1/clientes
{ "nombre": "Ana", "apellidos": "García López", "email": "ana@example.com",
  "telefono": "5512345678", "direccion": "Av. Juárez 123" }
```

`tipo` acepta: `REFRESCO`, `AGUA`, `JUGO`, `CERVEZA`, `CAFE`, `OTRA`.

## Validaciones y errores

Los errores se devuelven con el formato `ProblemDetail`:

```json
{
  "type": "about:blank",
  "title": "Error de validacion",
  "status": 400,
  "detail": "La peticion tiene datos invalidos",
  "instance": "/api/v1/pizzas",
  "errores": {
    "nombre": "El nombre de la pizza es obligatorio",
    "precio": "El precio debe ser mayor a 0"
  }
}
```

| Código | Cuándo ocurre |
|---|---|
| 400 | Datos inválidos, JSON mal formado o un valor de enum que no existe |
| 404 | El id solicitado no existe (o el `tamanoId` enviado no existe) |
| 409 | Nombre o correo repetido; o intentar borrar un tamaño que usan pizzas |
| 500 | Error inesperado del servidor |

## Datos de ejemplo

Al arrancar se cargan 4 tamaños, 6 pizzas, 5 bebidas y 3 clientes
(ver `config/DatosIniciales.java`). Como H2 está en memoria, la base se
reinicia en cada arranque.

## Pruebas

```bash
./gradlew test          # 10 pruebas de integración con MockMvc
bash smoke_test.sh      # 36 verificaciones HTTP contra la app corriendo
```

La colección de Postman está en
`postman/Pizzas_CRUD.postman_collection.json` (28 peticiones, con pruebas
automáticas de códigos de estado). Importarla en Postman y ejecutar la
colección con la app encendida.

## Notas

- El proyecto conserva el paquete `mx.unam.aragon.tsp.vmartinez.pizzas` y el
  nombre de artefacto `pizzas` de la plantilla original, para que sea
  compatible con lo que ya revisa el profesor.
- Para cambiar a MySQL solo hay que sustituir el driver en `build.gradle` y la
  URL en `application.properties`; las entidades y repositorios no cambian.

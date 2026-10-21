# Pizzas - CRUD con Spring Boot

Entrega de la tarea de CRUD: **pizzas, tamaños, bebidas y clientes**.


---

## 1. Cómo arrancarlo


Queda en `http://localhost:8080`:

- Panel para probar el CRUD desde el navegador: `http://localhost:8080/`
- Consola de la base: `http://localhost:8080/h2-console`
  (URL `jdbc:h2:mem:pizzas`, usuario `sa`, sin contraseña)

La base es H2 en memoria, así que cada vez que arranca vuelve a tener los datos
de ejemplo que cargué en `DatosIniciales.java`.

---

## 2. Cómo está organizado el proyecto

Separé el código en carpetas por responsabilidad. La idea es que si algo falla,
sepa más o menos en qué capa buscarlo sin leer todo:

| Carpeta | Qué tiene | Para qué sirve |
|---|---|---|
| `entities` | `Pizza`, `Tamano`, `Bebida`, `Cliente`, `TipoBebida` | Las tablas de la base, con sus campos y relaciones |
| `repositories` | 4 interfaces | Las consultas. Solo extienden `JpaRepository`, no escribí SQL |
| `services` | 4 clases | La lógica: validar que no se repita un nombre, que exista el tamaño, etc. |
| `dtos` | `XxxRequest` / `XxxResponse` + `IdDto` | Lo que entra y lo que sale por la API |
| `controllers` | 4 clases | Las rutas HTTP y los códigos de respuesta |
| `exceptions` | 3 excepciones + `GlobalExceptionHandler` | Convertir errores en respuestas JSON claras |
| `config` | `DatosIniciales` | Carga datos de ejemplo al arrancar |

El recorrido de una petición siempre es el mismo:
**Controller → Service → Repository → Entity**.

### Las entidades

`Pizza` es la única que se relaciona con otra tabla: tiene un `tamano`
(`@ManyToOne`) porque cada pizza del menú se ofrece en un tamaño, y así no
repito el diámetro y el precio del tamaño en cada pizza.

`Bebida` usa un enum `TipoBebida` (REFRESCO, AGUA, JUGO, CERVEZA, CAFE, OTRA)
en vez de un texto libre, para que no se pueda guardar "refresco" y "REFRESCO"
como si fueran tipos distintos.

El `id` de las cuatro entidades lo genera la base (`GenerationType.IDENTITY`),
no lo mando yo desde el cliente.

### Los repositorios

No escribí consultas: con extender `JpaRepository<Pizza, Long>` ya tengo
`findAll`, `findById`, `save` y `deleteById`.

Lo único que agregué son métodos con nombre, que Spring resuelve solo:

```java
Optional<Pizza> findByNombreIgnoreCase(String nombre);
List<Bebida> findByTipo(TipoBebida tipo);
```

Los usé para dos cosas: buscar sin importar mayúsculas (para que "Pepperoni" y
"pepperoni" cuenten como el mismo nombre) y para filtrar por tipo en bebidas.

### Los servicios

Aquí puse las reglas que no son solo "guardar en la base":

- Que no se repita el nombre de una pizza, un tamaño o una bebida.
- Que no se repita el correo de un cliente.
- Que el `tamanoId` que llega exista de verdad.
- Que no se pueda borrar un tamaño que ya están usando las pizzas.

Si el CRUD fuera nada más guardar y listar, esto podría estar en el controller.
Lo separé porque son varias validaciones por entidad y el controller se llenaba
de `if`, y también para no repetir la misma validación en `POST` y en `PUT`.

### Los DTOs

No devuelvo la entidad tal cual. Uso `PizzaRequest` para lo que llega y
`PizzaResponse` para lo que sale. Lo hice por tres razones concretas:

1. **Validar sin ensuciar la entidad.** Las anotaciones (`@NotBlank`,
   `@DecimalMin`) quedan en el DTO, no en la clase que es la tabla.
2. **No exponer de más.** En la respuesta de una pizza solo mando el nombre y el
   precio del tamaño, no la entidad `Tamano` completa con todos sus campos.
3. **Poder cambiar la base sin cambiar la API.** Si mañana cambio una columna,
   la respuesta al cliente sigue igual.

El `IdDto` sí lo tomé de la plantilla del profesor: es el `{ "id": ..., "mensaje": ... }`
que regresa el `DELETE`.

### Los controladores

Solo reciben, llaman al servicio y regresan el código HTTP que corresponde.
No tienen lógica de negocio.

### El manejo de errores

`GlobalExceptionHandler` atrapa las excepciones y las convierte en un JSON
parecido a este, para que quien consuma la API sepa qué pasó:

```json
{
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

Sin eso, Spring regresaba un error 500 genérico o una página de error en HTML,
y desde Postman no se entendía qué campo estaba mal.

---

## 3. Los endpoints

Todo está bajo `/api/v1`. Las cuatro entidades tienen exactamente las mismas
cinco rutas, cambiando solo el nombre del recurso:

| Método | Ruta | Qué hace | Regresa |
|---|---|---|---|
| GET | `/pizzas` | Lista todas | 200 |
| GET | `/pizzas/{id}` | Busca una | 200 o 404 |
| POST | `/pizzas` | Crea | 201 y la cabecera `Location` |
| PUT | `/pizzas/{id}` | Actualiza | 200 o 404 |
| DELETE | `/pizzas/{id}` | Elimina | 200 o 404 |

Igual para `/tamanos`, `/bebidas` y `/clientes`.

Filtros que agregué:

- `GET /api/v1/pizzas?disponible=true`
- `GET /api/v1/bebidas?tipo=JUGO`
- `GET /api/v1/bebidas?disponible=true`

Ejemplos:

```bash
# Listar
curl http://localhost:8080/api/v1/pizzas

# Crear
curl -X POST http://localhost:8080/api/v1/pizzas \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Pizza BBQ","descripcion":"Pollo y tocino","precio":215.00,"tamanoId":3,"disponible":true}'

# Actualizar
curl -X PUT http://localhost:8080/api/v1/pizzas/1 \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Pepperoni especial","descripcion":"Doble pepperoni","precio":209.00,"tamanoId":2,"disponible":false}'

# Eliminar
curl -X DELETE http://localhost:8080/api/v1/pizzas/1
```

El `DELETE` responde así:

```json
{ "id": 1, "mensaje": "Pizza eliminada correctamente" }
```

Cuerpos que espera cada `POST`:

```json
// /tamanos
{ "nombre": "Mediana", "diametroCm": 30, "precio": 179.00 }

// /pizzas
{ "nombre": "Pepperoni", "descripcion": "Pepperoni y queso",
  "precio": 179.00, "tamanoId": 2, "disponible": true }

// /bebidas
{ "nombre": "Coca-Cola", "tipo": "REFRESCO", "precio": 35.00,
  "volumenMl": 600, "disponible": true }

// /clientes
{ "nombre": "Ana", "apellidos": "García López", "email": "ana@example.com",
  "telefono": "5512345678", "direccion": "Av. Juárez 123" }
```

---

## 4. Reglas y códigos de error

| Código | Cuándo lo regresa |
|---|---|
| 400 | Falta un campo, el precio es negativo, el correo está mal escrito, o mandé un `tipo` que no existe |
| 404 | El id no existe, o el `tamanoId` que mandé en la pizza no existe |
| 409 | Nombre o correo repetido, o intenté borrar un tamaño que ya usan las pizzas |
| 500 | Error inesperado del servidor |

Validaciones que puse en cada entidad:

- **Tamaño:** nombre obligatorio (máx. 40), diámetro entre 10 y 100 cm, precio mayor a 0.
- **Pizza:** nombre obligatorio y único (máx. 80), precio mayor a 0, descripción opcional.
- **Bebida:** nombre obligatorio y único, tipo obligatorio del enum, volumen entre 100 y 5000 ml.
- **Cliente:** nombre y apellidos obligatorios, correo obligatorio con formato válido y único.

---

## 5. Cómo comprobé que funciona

No lo probé solo a mano, porque con 4 entidades es fácil que algo se rompa sin
darte cuenta:

```bash
./gradlew test          # 10 pruebas de integración con MockMvc
bash smoke_test.sh      # 36 verificaciones HTTP contra la app corriendo
```

- Las **pruebas de MockMvc** hacen el ciclo completo por cada entidad
  (crear, consultar, actualizar, borrar y comprobar el 404) y también los casos
  de error (400 por datos inválidos, 409 por duplicado). Cada prueba corre en
  una transacción que se revierte, así no se ensucian los datos entre pruebas.
- El **`smoke_test.sh`** pega contra la app ya levantada y revisa 36 respuestas:
  es lo que uso para comprobar rápido después de cualquier cambio.

Además dejé:

- `postman/Pizzas_CRUD.postman_collection.json` — 28 peticiones con pruebas
  automáticas de código de estado, organizadas en una carpeta por entidad.
- `peticiones.http` — las mismas peticiones para probarlas desde IntelliJ sin
  abrir Postman.

---

## 6. Decisiones que tomé y por qué

**¿Por qué H2 y no MySQL?**
Porque la tarea es el CRUD, no la instalación de un motor. Con H2 la app arranca
con `bootRun` y ya funciona, sin configurar usuario ni crear la base a mano, y
así quien la revise no tiene que instalar nada. Para pasar a MySQL solo cambio
el driver en `build.gradle` y la URL en `application.properties`; las entidades
y los repositorios no se tocan.

**¿Por qué el `DELETE` regresa 200 con mensaje y no 204?**
Porque 204 no trae cuerpo y quería confirmar qué se borró. Devolver
`{ "id": 1, "mensaje": "..." }` hace que la prueba sea más clara cuando la
corro desde Postman. Es la misma idea del `IdDto` que ya venía en la plantilla.

**¿Por qué una pizza tiene un solo tamaño?**
Porque así lo planteé para que el CRUD de tamaños tuviera sentido: el tamaño es
un catálogo y la pizza apunta a uno. Si fuera "la misma pizza en los 4 tamaños",
tendría que ser una tabla intermedia con precio por combinación, y eso ya se
sale de lo que pedía la tarea. Lo dejé anotado abajo como siguiente paso.

**¿Por qué el panel web si la tarea es la API?**
Porque para mostrar el CRUD en clase es más rápido abrir el navegador que
ejecutar 28 peticiones de Postman. El panel es un `index.html` con JavaScript
que consume la misma API; no tiene lógica aparte ni toca la base directamente.

**¿Por qué los datos de ejemplo?**
Para que al arrancar no esté vacío y se pueda probar el `GET`, el `PUT` y el
`DELETE` sin tener que capturar primero un tamaño. El `DatosIniciales` revisa si
ya hay tamaños y, si los hay, no vuelve a insertar.

---

## 7. Lo que me costó

- Entender que si devolvía la entidad `Pizza` directo, la respuesta traía el
  objeto `Tamano` completo dentro, y eso hacía la respuesta más pesada de lo
  necesario. Ahí fue donde decidí usar DTOs de respuesta.
- El error de borrar un tamaño en uso: al principio salía un 500 de la base por
  la llave foránea. Lo cambié para revisar primero si hay pizzas con ese tamaño
  y regresar un 409 con un mensaje entendible.
- Los acentos en los JSON: tuve que revisar que la respuesta saliera bien en
  UTF-8, sobre todo en nombres como "García" o "Piña".

## 8. Qué le falta

- **Pedidos:** relacionar cliente + pizza + bebida con una tabla de detalle y
  calcular el total. Es lo que sigue naturalmente después de estos 4 catálogos.
- **Pizza en varios tamaños:** tabla intermedia con precio por tamaño.
- **Paginación** en los `GET`, para cuando haya muchos registros.
- **MySQL** en lugar de H2, si se quiere que los datos no se pierdan al cerrar.

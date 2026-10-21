import json
import os

BASE = "http://localhost:8080"

def req(name, method, path, body=None, tests=None, pre=None):
    item = {
        "name": name,
        "event": [],
        "request": {
            "method": method,
            "header": [],
            "url": {
                "raw": "{{baseUrl}}" + path,
                "host": ["{{baseUrl}}"],
                "path": [p for p in path.strip("/").split("/")],
            },
        },
        "response": [],
    }
    if body is not None:
        item["request"]["header"].append(
            {"key": "Content-Type", "value": "application/json", "type": "text"}
        )
        item["request"]["body"] = {
            "mode": "raw",
            "raw": json.dumps(body, ensure_ascii=False, indent=4),
            "options": {"raw": {"language": "json"}},
        }
    if pre:
        item["event"].append(
            {"listen": "prerequest", "script": {"type": "text/javascript", "exec": pre}}
        )
    if tests:
        item["event"].append(
            {"listen": "test", "script": {"type": "text/javascript", "exec": tests}}
        )
    return item


def t_list(nombre):
    return [
        f'pm.test("Devuelve 200", () => pm.response.to.have.status(200));',
        f'pm.test("Devuelve una lista de {nombre}", () => {{',
        "    pm.expect(pm.response.json()).to.be.an('array');",
        "});",
    ]


def t_created(var, campo):
    return [
        'pm.test("Devuelve 201", () => pm.response.to.have.status(201));',
        'pm.test("Devuelve el recurso creado con id", () => {',
        "    const creado = pm.response.json();",
        "    pm.expect(creado).to.have.property('id');",
        f"    pm.expect(creado).to.have.property('{campo}');",
        f'    pm.collectionVariables.set("{var}", creado.id);',
        "});",
        'pm.test("Regresa la cabecera Location", () => {',
        "    pm.expect(pm.response.headers.has('Location')).to.be.true;",
        "});",
    ]


def t_ok(campo, valor):
    return [
        'pm.test("Devuelve 200", () => pm.response.to.have.status(200));',
        'pm.test("Refleja los datos actualizados", () => {',
        "    const cuerpo = pm.response.json();",
        f"    pm.expect(cuerpo.{campo}).to.eql({json.dumps(valor, ensure_ascii=False)});",
        "});",
    ]


def t_deleted():
    return [
        'pm.test("Devuelve 200", () => pm.response.to.have.status(200));',
        'pm.test("Confirma la eliminacion", () => {',
        "    pm.expect(pm.response.json()).to.have.property('mensaje');",
        "});",
    ]


def t_not_found():
    return ['pm.test("Devuelve 404 despues de eliminar", () => pm.response.to.have.status(404));']


def t_bad_request():
    return [
        'pm.test("Devuelve 400 por datos invalidos", () => pm.response.to.have.status(400));',
        'pm.test("Explica que campos fallaron", () => {',
        "    pm.expect(pm.response.json()).to.have.property('errores');",
        "});",
    ]


def folder(nombre, ruta, var, campo, crear, actualizar, campo_actualizado, valor_actualizado, invalid=None):
    p = f"/api/v1/{ruta}"
    items = [
        req(f"Listar {nombre}", "GET", p, tests=t_list(nombre)),
        req(f"Crear {nombre}", "POST", p, body=crear, tests=t_created(var, campo)),
        req(f"Buscar {nombre} por id", "GET", p + f"/{{{{{var}}}}}", tests=[
            'pm.test("Devuelve 200", () => pm.response.to.have.status(200));',
        ]),
        req(f"Actualizar {nombre}", "PUT", p + f"/{{{{{var}}}}}", body=actualizar,
            tests=t_ok(campo_actualizado, valor_actualizado)),
        req(f"Eliminar {nombre}", "DELETE", p + f"/{{{{{var}}}}}", tests=t_deleted()),
        req("Comprobar eliminación", "GET", p + f"/{{{{{var}}}}}", tests=t_not_found()),
    ]
    if invalid:
        items.append(req(f"Validación: {nombre} con datos inválidos", "POST", p,
                         body=invalid, tests=t_bad_request()))
    return {"name": nombre, "item": items}


collection = {
    "info": {
        "name": "Pizzas CRUD - Spring Boot",
        "description": (
            "Colección de pruebas para el CRUD de pizzas, tamaños, bebidas y clientes. "
            "Ejecuta las carpetas en orden: Tamaños -> Pizzas -> Bebidas -> Clientes."
        ),
        "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json",
    },
    "item": [
        folder("Tamaños", "tamanos", "tamanoId", "nombre",
               {"nombre": "Mediana Postman", "diametroCm": 30, "precio": 179.00},
               {"nombre": "Mediana Postman actualizada", "diametroCm": 32, "precio": 189.50},
               "nombre", "Mediana Postman actualizada",
               invalid={"nombre": "", "diametroCm": 5, "precio": -1}),
        folder("Pizzas", "pizzas", "pizzaId", "nombre",
               {"nombre": "Pizza Postman", "descripcion": "Pizza creada desde Postman",
                "precio": 199.00, "tamanoId": 1, "disponible": True},
               {"nombre": "Pizza Postman actualizada", "descripcion": "Pizza modificada desde Postman",
                "precio": 210.50, "tamanoId": 2, "disponible": False},
               "precio", 210.50,
               invalid={"nombre": "", "precio": 0, "tamanoId": 99999}),
        folder("Bebidas", "bebidas", "bebidaId", "nombre",
               {"nombre": "Bebida Postman", "tipo": "REFRESCO", "precio": 40.00,
                "volumenMl": 600, "disponible": True},
               {"nombre": "Bebida Postman actualizada", "tipo": "JUGO", "precio": 45.00,
                "volumenMl": 500, "disponible": False},
               "tipo", "JUGO",
               invalid={"nombre": "", "tipo": "NO_EXISTE", "precio": -5, "volumenMl": 10}),
        folder("Clientes", "clientes", "clienteId", "email",
               {"nombre": "Cliente", "apellidos": "Postman",
                "email": "cliente.postman@example.com", "telefono": "5512345678",
                "direccion": "Calle Postman 123"},
               {"nombre": "Cliente", "apellidos": "Postman actualizado",
                "email": "cliente.postman@example.com", "telefono": "5598765432",
                "direccion": "Calle Postman 456"},
               "apellidos", "Postman actualizado",
               invalid={"nombre": "", "apellidos": "", "email": "no-es-un-correo"}),
    ],
    "variable": [
        {"key": "baseUrl", "value": BASE},
        {"key": "tamanoId", "value": ""},
        {"key": "pizzaId", "value": ""},
        {"key": "bebidaId", "value": ""},
        {"key": "clienteId", "value": ""},
    ],
}

salida = os.path.join(os.path.dirname(os.path.abspath(__file__)),
                      "postman", "Pizzas_CRUD.postman_collection.json")
os.makedirs(os.path.dirname(salida), exist_ok=True)
with open(salida, "w", encoding="utf-8") as f:
    json.dump(collection, f, ensure_ascii=False, indent=2)

print("OK ->", salida)
print("Carpetas:", [c["name"] for c in collection["item"]])
print("Peticiones totales:", sum(len(c["item"]) for c in collection["item"]))

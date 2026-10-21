#!/usr/bin/env bash
# Prueba de humo del CRUD completo contra una instancia local.
# Uso: ./smoke_test.sh [puerto]
set -u
B="http://localhost:${1:-8080}/api/v1"

ok=0
fail=0

# esperado, descripcion, metodo, ruta, cuerpo
check() {
  local esperado="$1" desc="$2" metodo="$3" ruta="$4" cuerpo="${5:-}"
  local salida codigo
  if [ -n "$cuerpo" ]; then
    salida=$(curl -s -X "$metodo" "$B$ruta" -H 'Content-Type: application/json' -d "$cuerpo" -w '\n%{http_code}')
  else
    salida=$(curl -s -X "$metodo" "$B$ruta" -w '\n%{http_code}')
  fi
  codigo=$(printf '%s' "$salida" | tail -n1)
  if [ "$codigo" = "$esperado" ]; then
    ok=$((ok+1))
    printf '  OK   %-52s %s\n' "$desc" "$codigo"
  else
    fail=$((fail+1))
    printf '  FALLO %-51s esperado %s, obtenido %s\n' "$desc" "$esperado" "$codigo"
    printf '        %s\n' "$(printf '%s' "$salida" | head -c 300)"
  fi
}

echo "== TAMANOS =="
check 200 "Listar tamanos" GET /tamanos
check 201 "Crear tamano" POST /tamanos '{"nombre":"Jumbo Test","diametroCm":45,"precio":329.00}'
check 200 "Buscar tamano 5" GET /tamanos/5
check 200 "Actualizar tamano 5" PUT /tamanos/5 '{"nombre":"Jumbo Test","diametroCm":46,"precio":339.00}'
check 400 "Validacion: diametro fuera de rango" POST /tamanos '{"nombre":"","diametroCm":5,"precio":-1}'
check 409 "Duplicado: nombre repetido" POST /tamanos '{"nombre":"Jumbo Test","diametroCm":45,"precio":329.00}'
check 404 "Tamano inexistente" GET /tamanos/9999

echo "== PIZZAS =="
check 200 "Listar pizzas" GET /pizzas
check 201 "Crear pizza" POST /pizzas '{"nombre":"Pizza Test","descripcion":"Prueba","precio":199.50,"tamanoId":1,"disponible":true}'
check 200 "Buscar pizza 7" GET /pizzas/7
check 200 "Filtrar pizzas no disponibles" GET "/pizzas?disponible=false"
check 200 "Actualizar pizza 7" PUT /pizzas/7 '{"nombre":"Pizza Test","descripcion":"Actualizada","precio":210.00,"tamanoId":2,"disponible":false}'
check 400 "Validacion: precio 0" POST /pizzas '{"nombre":"","precio":0}'
check 404 "Pizza con tamano inexistente" POST /pizzas '{"nombre":"Otra","precio":100,"tamanoId":9999}'
check 409 "Duplicado: pizza repetida" POST /pizzas '{"nombre":"Pizza Test","precio":100}'
check 200 "Eliminar pizza 7" DELETE /pizzas/7
check 404 "Pizza eliminada" GET /pizzas/7

echo "== BEBIDAS =="
check 200 "Listar bebidas" GET /bebidas
check 201 "Crear bebida" POST /bebidas '{"nombre":"Bebida Test","tipo":"REFRESCO","precio":40.00,"volumenMl":600,"disponible":true}'
check 200 "Buscar bebida 6" GET /bebidas/6
check 200 "Filtrar por tipo JUGO" GET "/bebidas?tipo=JUGO"
check 200 "Actualizar bebida 6" PUT /bebidas/6 '{"nombre":"Bebida Test","tipo":"JUGO","precio":45.00,"volumenMl":500,"disponible":false}'
check 400 "Validacion: tipo inexistente" POST /bebidas '{"nombre":"X","tipo":"NO_EXISTE","precio":10,"volumenMl":500}'
check 400 "Validacion: volumen invalido" POST /bebidas '{"nombre":"","tipo":"AGUA","precio":10,"volumenMl":10}'
check 200 "Eliminar bebida 6" DELETE /bebidas/6
check 404 "Bebida eliminada" GET /bebidas/6

echo "== CLIENTES =="
check 200 "Listar clientes" GET /clientes
check 201 "Crear cliente" POST /clientes '{"nombre":"Cliente","apellidos":"Test","email":"test@example.com","telefono":"5512345678","direccion":"Calle 1"}'
check 200 "Buscar cliente 4" GET /clientes/4
check 200 "Actualizar cliente 4" PUT /clientes/4 '{"nombre":"Cliente","apellidos":"Actualizado","email":"test@example.com","telefono":"5598765432","direccion":"Calle 2"}'
check 400 "Validacion: correo invalido" POST /clientes '{"nombre":"","apellidos":"","email":"no-es-correo"}'
check 409 "Duplicado: correo repetido" POST /clientes '{"nombre":"Otro","apellidos":"Cliente","email":"test@example.com"}'
check 200 "Eliminar cliente 4" DELETE /clientes/4
check 404 "Cliente eliminado" GET /clientes/4

echo "== INTEGRIDAD =="
check 409 "No eliminar tamano en uso (id 1)" DELETE /tamanos/1
check 200 "Eliminar tamano sin uso (id 5)" DELETE /tamanos/5

echo
echo "Resultado: $ok correctas, $fail fallidas"
[ "$fail" -eq 0 ]

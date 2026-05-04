# Manual de uso — Café de Juegos

El sistema cuenta con tres consolas independientes, cada una pensada para un tipo de usuario distinto. También incluye pruebas automatizadas con JUnit para validar la lógica principal de la aplicación.

---

## 1. Ejecución de consolas

Para ejecutar cualquiera de las consolas en Eclipse:

1. Abrir el proyecto en Eclipse.
2. En el Package Explorer, expandir `src/consola/`.
3. Hacer clic derecho sobre el archivo deseado.
4. Seleccionar Run As → Java Application.

Consolas disponibles:

- `ConsolaAdministrador.java` → Administrador del sistema.
- `ConsolaCliente.java` → Cliente del café.
- `ConsolaEmpleado.java` → Mesero o Cocinero.

Al cerrar una consola usando la opción Salir, los cambios se guardan automáticamente en `datos_prueba/`.

Si se cierra la ventana directamente sin usar Salir, los cambios pueden no guardarse correctamente.

---

## 2. Credenciales de acceso

### Administrador

- Login: `admin`
- Contraseña: `admin123`

### Empleados

- Login: `ana`
  - Contraseña: `pass`
  - Rol: Mesera

- Login: `luis`
  - Contraseña: `pass`
  - Rol: Mesero

- Login: `chef`
  - Contraseña: `pass`
  - Rol: Cocinero

### Clientes

- Login: `maria`
  - Contraseña: `1234`

- Login: `pedro`
  - Contraseña: `5678`

Los clientes también pueden registrarse directamente desde la pantalla inicial de `ConsolaCliente`, opción:

`2. Registrarse`

---

## 3. ConsolaAdministrador

Gestiona los aspectos operativos principales del café.

### Usuarios

1. Agregar usuario  
   Registra un Cliente, Mesero o Cocinero. El ID se genera automáticamente con formato `U7`, `U8`, etc. El sistema rechaza logins y códigos de descuento ya existentes.

2. Listar usuarios  
   Muestra todos los usuarios registrados con su tipo y login.

### Inventario de juegos

3. Agregar juego al inventario  
   Ingresa los datos del juego y permite agregarlo a préstamo, venta o ambos. El sistema rechaza IDs duplicados.

4. Gestionar inventario de juegos  
   Permite ver copias, mover unidades de venta a préstamo, reparar copias, marcarlas como robadas o reabastecer stock.

### Cafetería

5. Agregar item al menú  
   Agrega una Bebida o Pastelería con su precio. El sistema rechaza IDs duplicados.

### Turnos de empleados

6. Construir / modificar turno de empleado  
   Permite ver turnos, crear turnos o eliminarlos.

7. Ver solicitudes de cambio de turno  
   Lista las solicitudes pendientes enviadas por empleados.

8. Aprobar / Rechazar solicitud de turno  
   Resuelve una solicitud pendiente.

### Sugerencias de menú

9. Ver sugerencias de menú  
   Lista todas las sugerencias enviadas por empleados.

10. Aprobar / Rechazar sugerencia  
    Al aprobar, ofrece la opción de agregar el ítem sugerido al menú de inmediato.

### Torneos

11. Crear torneo  
    Crea un torneo Amistoso, con bono premio, o Competitivo, con tarifa de entrada. El sistema rechaza IDs duplicados.

12. Ver torneos  
    Lista todos los torneos con su tipo, juego, día, cupos y condiciones.

13. Registrar ganador de torneo  
    Selecciona el ganador entre los usuarios inscritos en un torneo.

### Reportes

14. Ver reporte de ventas  
    Genera reportes por tipo y rango de fechas, por semana o por mes.

### Salida

15. Salir  
    Cierra la consola y guarda los datos.

---

## 4. ConsolaCliente

Al iniciar se muestra una pantalla con dos opciones: iniciar sesión con una cuenta existente o registrarse como nuevo cliente.

### Pantalla inicial

1. Iniciar sesión  
   Permite entrar con un cliente ya registrado.

2. Registrarse  
   Crea una nueva cuenta de cliente. El ID se genera automáticamente y el sistema rechaza logins duplicados.

### Mesas

1. Registrar mesa  
   Indica cuántas personas, niños y jóvenes hay en el grupo. Se asigna una mesa disponible.

2. Liberar mesa  
   Libera la mesa actual al terminar la visita.

### Juegos

3. Ver catálogo de juegos  
   Muestra todos los juegos con disponibilidad de préstamo y precio de venta.

4. Pedir préstamo de juego  
   Solicita una copia de un juego. Requiere tener mesa asignada.

5. Devolver juego prestado  
   Devuelve un juego actualmente en préstamo.

### Cafetería

6. Realizar pedido en cafetería  
   Selecciona ítems del menú, indica propina, puede aplicar código de descuento y puede usar puntos de fidelidad.

### Tienda

7. Comprar juego en tienda  
   Compra unidades del inventario de venta. Permite aplicar código de descuento y usar puntos de fidelidad.

### Fidelización y torneos

8. Ver mis puntos de fidelidad  
   Muestra los puntos acumulados y bonos de torneos pendientes.

9. Ver / agregar juegos favoritos  
   Permite ver, agregar o quitar juegos favoritos. Esta lista influye en los cupos reservados de torneos.

10. Ver torneos disponibles  
    Lista los torneos existentes con cupos y condiciones.

11. Inscribirse a torneo  
    Elige torneo e indica número de participantes. Máximo 3 por usuario.

12. Desinscribirse de torneo  
    Cancela la inscripción en un torneo.

### Salida

13. Salir  
    Cierra la consola y guarda los datos.

---

## 5. ConsolaEmpleado

Permite que Meseros y Cocineros consulten sus turnos, soliciten cambios, realicen operaciones de juego/cafetería y participen en torneos.

### Turnos

1. Ver mi turno actual  
   Muestra el turno asignado y su estado.

2. Solicitar cambio de turno  
   Envía una solicitud de cambio general o intercambio con otro empleado. La solicitud queda pendiente hasta que el administrador la apruebe o rechace.

### Juegos y cafetería

3. Pedir préstamo de juego  
   Permite pedir juegos prestados si el empleado no está bloqueado por estar en turno con clientes.

4. Devolver juego prestado  
   Devuelve un juego prestado por el empleado.

5. Realizar pedido en cafetería  
   Permite realizar pedidos para una mesa ocupada. El descuento de empleado del 20% se aplica automáticamente, salvo que exista un bono de torneo aplicable.

6. Comprar juego en tienda  
   Permite comprar juegos del inventario de venta. El descuento de empleado del 20% se aplica automáticamente, salvo que exista un bono de torneo aplicable.

### Sugerencias

7. Sugerir item al menú  
   Propone un nuevo ítem con nombre y categoría. La sugerencia queda pendiente de aprobación del administrador.

### Fidelización y torneos

8. Ver mis puntos de fidelidad  
   Muestra los puntos acumulados y bonos de torneos pendientes.

9. Ver / agregar juegos favoritos  
   Permite ver, agregar o quitar juegos favoritos.

10. Inscribirse a torneo  
    Permite inscribirse a un torneo si el empleado no está cubriendo turno ese día.

11. Desinscribirse de torneo  
    Cancela la inscripción del empleado en un torneo.

En torneos competitivos, la participación del empleado es gratuita, pero no puede recibir premio metálico.

### Salida

12. Salir  
    Cierra la consola y guarda los datos.

---

## 6. Persistencia de datos

Todas las consolas cargan y guardan información en la carpeta:

`datos_prueba/`

La carga de datos ocurre al iniciar la consola.

El guardado ocurre al salir correctamente usando la opción Salir.

Si se cierra la consola desde la ventana de Eclipse sin usar Salir, los datos modificados durante esa ejecución pueden no guardarse.

---

## 7. Ejecución de pruebas automatizadas con JUnit

El proyecto incluye pruebas automatizadas con JUnit 5 para validar la lógica principal del sistema.

Estas pruebas cubren funcionalidades como:

- clientes y puntos de fidelidad;
- mesas;
- juegos de mesa;
- inventario;
- préstamos;
- ventas;
- cafetería;
- turnos;
- reportes;
- sistema general;
- torneos.

### Ejecutar todas las pruebas

Para correr todas las pruebas en Eclipse:

1. En el Package Explorer, ubicar la carpeta donde están los tests.
2. Hacer clic derecho sobre la carpeta de pruebas o sobre el proyecto.
3. Seleccionar Run As → JUnit Test.

Eclipse ejecutará todas las pruebas y mostrará el resultado en la vista de JUnit.

Si todo está correcto, la barra debe aparecer en verde.

### Ejecutar una prueba específica

Para correr una clase de prueba específica:

1. Abrir o seleccionar el archivo de test, por ejemplo `ServicioVentasTest.java`, `ServicioTorneoTest.java` o `SistemaCafeTest.java`.
2. Hacer clic derecho sobre el archivo.
3. Seleccionar Run As → JUnit Test.

---

## 8. Ejecutar pruebas con cobertura

Para revisar la cobertura de las pruebas:

1. Hacer clic derecho sobre el proyecto, paquete o clase de prueba.
2. Seleccionar Coverage As → JUnit Test.

Eclipse mostrará qué líneas fueron cubiertas por las pruebas:

- Verde → línea ejecutada por las pruebas.
- Rojo → línea no ejecutada.
- Amarillo → línea parcialmente cubierta.

La cobertura global puede verse afectada por paquetes como `consola` o `persistencia`, que no necesariamente requieren pruebas unitarias. Lo importante es que las clases con lógica de negocio principal estén cubiertas por pruebas relevantes.

---

## 9. Notas importantes

- Para que los datos se guarden correctamente, siempre se debe salir desde la opción Salir dentro de la consola.
- Los clientes pueden registrarse directamente desde `ConsolaCliente`.
- Los empleados deben ser registrados por el administrador.
- El administrador puede crear torneos y registrar ganadores.
- Los empleados pueden inscribirse a torneos si no están cubriendo turno el día del torneo.
- En torneos competitivos, los empleados participan gratis pero no reciben premio metálico.
- En torneos amistosos, el ganador recibe un bono de descuento no acumulable con otros descuentos.
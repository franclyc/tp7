package ar.edu.unju.escmi.main;

// Tipos usados para mostrar colecciones y leer datos desde la consola.
import java.util.List;
import java.util.Scanner;

// Acceso a la fábrica JPA compartida para cerrar recursos al terminar.
import ar.edu.unju.escmi.config.EmfSingleton;
// Contratos DAO utilizados por la lógica de la aplicación.
import ar.edu.unju.escmi.dao.IClienteDao;
import ar.edu.unju.escmi.dao.IFacturaDao;
import ar.edu.unju.escmi.dao.IProductoDao;
// Implementaciones concretas de los DAO utilizados por el menú.
import ar.edu.unju.escmi.dao.imp.ClienteDaoImp;
import ar.edu.unju.escmi.dao.imp.FacturaDaoImp;
import ar.edu.unju.escmi.dao.imp.ProductoDaoImp;
// Entidades que representan clientes, productos, facturas y sus detalles.
import ar.edu.unju.escmi.entities.Cliente;
import ar.edu.unju.escmi.entities.DetalleFactura;
import ar.edu.unju.escmi.entities.Factura;
import ar.edu.unju.escmi.entities.Producto;

// Punto de entrada y menú interactivo para administrar ventas y datos.
public class Main {

	// Lector de datos por teclado, único para toda la aplicación
	private static final Scanner sc = new Scanner(System.in);

	// Acceso a datos a través del patrón DAO (se programa contra la interfaz, no la implementación)
	// DAO para registrar, consultar, modificar y dar de baja clientes.
	private static final IClienteDao clienteDao = new ClienteDaoImp();
	// DAO para registrar, consultar, modificar y dar de baja productos.
	private static final IProductoDao productoDao = new ProductoDaoImp();
	// DAO para registrar, consultar y dar de baja facturas.
	private static final IFacturaDao facturaDao = new FacturaDaoImp();

	// Monto fijo pedido por la consigna para la opción 12
	private static final double TOTAL_MINIMO_FACTURAS = 900000;

	// Inicia el ciclo principal de interacción con el usuario.
	public static void main(String[] args) {
		// Guarda la opción ingresada para decidir cuándo salir del ciclo.
		int opcion;
		// Repite el menú al menos una vez y continúa hasta que se elija salir.
		do {
			// Presenta las acciones que el usuario puede seleccionar.
			mostrarMenu();
			// Lee y valida una opción entera ingresada por teclado.
			opcion = leerEntero("Seleccione una opción: ");
			// Cada opción se ejecuta dentro de un try/catch para que un error no corte el programa
			try {
				// Ejecuta la acción correspondiente al número elegido.
				switch (opcion) {
					// Registra un cliente nuevo.
					case 1 -> altaCliente();
					// Registra un producto nuevo.
					case 2 -> altaProducto();
					// Crea una factura con los productos de una venta.
					case 3 -> realizarVenta();
					// Busca y muestra una factura por su identificador.
					case 4 -> buscarFactura();
					// Desactiva una factura sin borrar físicamente sus datos.
					case 5 -> eliminarFactura();
					// Desactiva un producto sin borrar físicamente sus datos.
					case 6 -> eliminarProducto();
					// Permite editar los datos de un cliente existente.
					case 7 -> modificarCliente();
					// Permite cambiar el precio de un producto.
					case 8 -> modificarPrecioProducto();
					// Desactiva un cliente sin borrar físicamente sus datos.
					case 9 -> eliminarCliente();
					// Muestra las facturas activas.
					case 10 -> mostrarFacturas();
					// Muestra los clientes activos.
					case 11 -> mostrarClientes();
					// Muestra las facturas cuyo total supera el importe definido.
					case 12 -> mostrarFacturasMayoresA(TOTAL_MINIMO_FACTURAS);
					// Informa que la aplicación está terminando.
					case 0 -> System.out.println("\nSaliendo del sistema...");
					// Informa que no existe una acción asociada a la opción.
					default -> System.out.println("\nOpción inexistente. Intente nuevamente.");
				}
			} catch (Exception e) {
				// Mensaje claro para el usuario, sin que se interrumpa el flujo del menú
				// Muestra la causa más específica disponible de la operación fallida.
				System.out.println("\n[!] No se pudo completar la operación: " + obtenerMensajeDeError(e));
			}
		} while (opcion != 0);

		// Cierra la fábrica JPA para liberar los recursos de la aplicación.
		cerrarRecursos();
	}

	// Imprime todas las opciones disponibles en el menú principal.
	private static void mostrarMenu() {
		System.out.println("\n===================== MENÚ PRINCIPAL =====================");
		System.out.println(" 1  - Alta de cliente");
		System.out.println(" 2  - Alta de producto");
		System.out.println(" 3  - Realizar la venta de productos (nueva factura)");
		System.out.println(" 4  - Buscar una factura por su número");
		System.out.println(" 5  - Eliminar una factura (baja lógica)");
		System.out.println(" 6  - Eliminar un producto (baja lógica)");
		System.out.println(" 7  - Modificar datos de un cliente");
		System.out.println(" 8  - Modificar el precio de un producto");
		System.out.println(" 9  - Eliminar un cliente (baja lógica)");
		System.out.println(" 10 - Mostrar todas las facturas");
		System.out.println(" 11 - Mostrar todos los clientes");
		System.out.println(" 12 - Mostrar las facturas que superen los $" + (long) TOTAL_MINIMO_FACTURAS);
		System.out.println(" 0  - Salir");
		System.out.println("==========================================================");
	}

	// ---------------------------------------------------------------------
	// Opción 1 - Alta de cliente
	// ---------------------------------------------------------------------
	// Solicita los datos personales y registra un cliente nuevo.
	private static void altaCliente() {
		System.out.println("\n----- ALTA DE CLIENTE -----");
		// Lee el nombre obligatorio del cliente.
		String nombre = leerTexto("Nombre: ");
		// Lee el apellido obligatorio del cliente.
		String apellido = leerTexto("Apellido: ");
		// Lee el domicilio obligatorio solicitado por esta pantalla.
		String domicilio = leerTexto("Domicilio: ");
		// Lee el DNI como un número entero.
		int dni = leerEntero("DNI: ");

		// Construye la entidad con los datos ingresados.
		Cliente cliente = new Cliente(nombre, apellido, domicilio, dni);
		// Persiste el cliente mediante su DAO.
		clienteDao.guardar(cliente);
		// Confirma al usuario el resultado y muestra los datos guardados.
		System.out.println("Cliente registrado correctamente: " + cliente);
	}

	// ---------------------------------------------------------------------
	// Opción 2 - Alta de producto
	// ---------------------------------------------------------------------
	// Solicita los datos del producto y lo registra.
	private static void altaProducto() {
		System.out.println("\n----- ALTA DE PRODUCTO -----");
		// Lee la descripción obligatoria del producto.
		String descripcion = leerTexto("Descripción: ");
		// Lee un precio numérico estrictamente positivo.
		double precio = leerDoublePositivo("Precio unitario: ");

		// Construye la entidad del producto con los datos recibidos.
		Producto producto = new Producto(descripcion, precio);
		// Persiste el producto mediante su DAO.
		productoDao.guardar(producto);
		// Informa el resultado y muestra el producto registrado.
		System.out.println("Producto registrado correctamente: " + producto);
	}

	// ---------------------------------------------------------------------
	// Opción 3 - Realizar la venta (alta de una nueva factura)
	// ---------------------------------------------------------------------
	// Selecciona un cliente y productos para crear y guardar una factura.
	private static void realizarVenta() {
		System.out.println("\n----- NUEVA VENTA (FACTURA) -----");

		// 1) Elegir el cliente
		// Obtiene los clientes que están activos.
		List<Cliente> clientes = clienteDao.listar();
		// Impide crear una venta si todavía no hay clientes disponibles.
		if (clientes.isEmpty()) {
			System.out.println("No hay clientes registrados. Primero debe dar de alta un cliente (opción 1).");
			return;
		}
		// Muestra los clientes disponibles para que el usuario elija uno.
		mostrarListaClientes(clientes);
		// Lee el identificador elegido.
		Long idCliente = leerLong("Ingrese el id del cliente: ");
		// Busca el cliente correspondiente a ese identificador.
		Cliente cliente = clienteDao.buscarPorId(idCliente);
		// Evita asociar la factura a un cliente inexistente o inactivo.
		if (cliente == null || !cliente.isEstado()) {
			System.out.println("No existe un cliente activo con el id " + idCliente + ".");
			return;
		}

		// 2) Cargar los productos de la factura
		// Obtiene los productos que están activos.
		List<Producto> productos = productoDao.listar();
		// Impide iniciar una venta si no hay productos para vender.
		if (productos.isEmpty()) {
			System.out.println("No hay productos registrados. Primero debe dar de alta un producto (opción 2).");
			return;
		}

		// Crea la factura asociada al cliente seleccionado.
		Factura factura = new Factura(cliente);
		// Controla si el usuario desea seguir agregando productos.
		boolean seguirCargando = true;
		// Permite agregar varios detalles hasta que el usuario termine.
		while (seguirCargando) {
			// Muestra los productos activos disponibles.
			mostrarListaProductos(productos);
			// Lee el identificador del producto que se desea vender.
			Long idProducto = leerLong("Ingrese el id del producto a vender: ");
			// Busca el producto seleccionado.
			Producto producto = productoDao.buscarPorId(idProducto);
			// Informa si el producto no existe o no está activo.
			if (producto == null || !producto.isEstado()) {
				System.out.println("No existe un producto activo con el id " + idProducto + ".");
			} else {
				// Solicita una cantidad de unidades mayor que cero.
				int cantidad = leerEnteroPositivo("Cantidad: ");
				// Crea el detalle y calcula su subtotal inicial.
				DetalleFactura detalle = new DetalleFactura(producto, cantidad);
				// Agrega el detalle a la factura y actualiza el total de la misma.
				factura.agregarDetalle(detalle);
				// Muestra la línea agregada a la venta.
				System.out.println("  Agregado: " + detalle);
			}
			// Pregunta si se deben cargar más productos.
			seguirCargando = leerConfirmacion("¿Desea agregar otro producto? (s/n): ");
		}

		// Cancela el proceso si no se agregó ningún producto a la factura.
		if (factura.getDetalles().isEmpty()) {
			System.out.println("La factura no tiene productos cargados. Venta cancelada.");
			return;
		}

		// 3) Guardar la factura (sus detalles se guardan en cascada)
		// Persiste la factura y sus detalles asociados.
		facturaDao.guardar(factura);
		// Informa el resultado y muestra el resumen de la venta.
		System.out.println("\nVenta registrada correctamente.");
		mostrarFacturaCompleta(factura);
	}

	// ---------------------------------------------------------------------
	// Opción 4 - Buscar una factura por su número y mostrar todos sus datos
	// ---------------------------------------------------------------------
	// Busca una factura por identificador y muestra sus datos si existe.
	private static void buscarFactura() {
		System.out.println("\n----- BUSCAR FACTURA -----");
		// Lee el identificador de factura buscado.
		Long id = leerLong("Ingrese el número (id) de la factura: ");
		// Recupera la factura desde la base de datos.
		Factura factura = facturaDao.buscarPorId(id);
		// Informa que no se encontró ningún registro con ese identificador.
		if (factura == null) {
			System.out.println("No existe una factura con el número " + id + ".");
			return;
		}
		// Muestra los datos principales y todos los detalles de la factura.
		mostrarFacturaCompleta(factura);
	}

	// ---------------------------------------------------------------------
	// Opción 5 - Eliminar una factura (baja lógica)
	// ---------------------------------------------------------------------
	// Solicita una factura y la marca como inactiva.
	private static void eliminarFactura() {
		System.out.println("\n----- ELIMINAR FACTURA -----");
		// Lee el identificador de la factura que se va a desactivar.
		Long id = leerLong("Ingrese el número (id) de la factura a eliminar: ");
		// Ejecuta la baja lógica mediante el DAO.
		facturaDao.eliminarLogico(id);
		// Informa que se solicitó la baja de la factura.
		System.out.println("Factura " + id + " eliminada (baja lógica) correctamente.");
	}

	// ---------------------------------------------------------------------
	// Opción 6 - Eliminar un producto (baja lógica)
	// ---------------------------------------------------------------------
	// Muestra los productos activos y desactiva el elegido por el usuario.
	private static void eliminarProducto() {
		System.out.println("\n----- ELIMINAR PRODUCTO -----");
		// Obtiene los productos activos disponibles.
		List<Producto> productos = productoDao.listar();
		// Termina si no hay productos que puedan darse de baja.
		if (productos.isEmpty()) {
			System.out.println("No hay productos activos para eliminar.");
			return;
		}
		// Permite identificar qué producto se desea desactivar.
		mostrarListaProductos(productos);
		// Lee el identificador del producto seleccionado.
		Long id = leerLong("Ingrese el id del producto a eliminar: ");
		// Ejecuta la baja lógica mediante el DAO.
		productoDao.eliminarLogico(id);
		// Informa que se solicitó la baja del producto.
		System.out.println("Producto " + id + " eliminado (baja lógica) correctamente.");
	}

	// ---------------------------------------------------------------------
	// Opción 7 - Modificar datos de un cliente
	// ---------------------------------------------------------------------
	// Permite modificar nombre, apellido y domicilio de un cliente activo.
	private static void modificarCliente() {
		System.out.println("\n----- MODIFICAR CLIENTE -----");
		// Obtiene los clientes activos que se pueden modificar.
		List<Cliente> clientes = clienteDao.listar();
		// Termina si no hay clientes disponibles.
		if (clientes.isEmpty()) {
			System.out.println("No hay clientes activos para modificar.");
			return;
		}
		// Muestra los clientes entre los que se puede elegir.
		mostrarListaClientes(clientes);
		// Lee el identificador del cliente seleccionado.
		Long id = leerLong("Ingrese el id del cliente a modificar: ");
		// Busca el cliente elegido.
		Cliente cliente = clienteDao.buscarPorId(id);
		// Evita modificar un cliente inexistente o que fue desactivado.
		if (cliente == null || !cliente.isEstado()) {
			System.out.println("No existe un cliente activo con el id " + id + ".");
			return;
		}

		// Explica que un campo vacío conserva el dato que ya tiene el cliente.
		System.out.println("Deje el campo vacío y presione ENTER para conservar el valor actual.");
		// Lee el nombre nuevo o conserva el nombre actual si no se escribió nada.
		cliente.setNombre(leerTextoConValorActual("Nombre", cliente.getNombre()));
		// Lee el apellido nuevo o conserva el apellido actual si no se escribió nada.
		cliente.setApellido(leerTextoConValorActual("Apellido", cliente.getApellido()));
		// Lee el domicilio nuevo o conserva el domicilio actual si no se escribió nada.
		cliente.setDomicilio(leerTextoConValorActual("Domicilio", cliente.getDomicilio()));

		// Guarda los cambios del cliente.
		clienteDao.actualizar(cliente);
		// Informa el resultado y muestra los datos actualizados.
		System.out.println("Cliente modificado correctamente: " + cliente);
	}

	// ---------------------------------------------------------------------
	// Opción 8 - Modificar el precio de un producto
	// ---------------------------------------------------------------------
	// Permite establecer un nuevo precio unitario para un producto activo.
	private static void modificarPrecioProducto() {
		System.out.println("\n----- MODIFICAR PRECIO DE PRODUCTO -----");
		// Obtiene los productos activos que se pueden modificar.
		List<Producto> productos = productoDao.listar();
		// Termina si no hay productos disponibles.
		if (productos.isEmpty()) {
			System.out.println("No hay productos activos para modificar.");
			return;
		}
		// Muestra los productos entre los que se puede elegir.
		mostrarListaProductos(productos);
		// Lee el identificador del producto seleccionado.
		Long id = leerLong("Ingrese el id del producto a modificar: ");
		// Busca el producto elegido.
		Producto producto = productoDao.buscarPorId(id);
		// Evita modificar un producto inexistente o desactivado.
		if (producto == null || !producto.isEstado()) {
			System.out.println("No existe un producto activo con el id " + id + ".");
			return;
		}

		// Informa el precio actual antes de solicitar el nuevo valor.
		System.out.println("Precio actual: $" + producto.getPrecioUnitario());
		// Lee y valida que el nuevo precio sea mayor que cero.
		double nuevoPrecio = leerDoublePositivo("Nuevo precio unitario: ");
		// Actualiza el precio en la entidad.
		producto.setPrecioUnitario(nuevoPrecio);

		// Persiste el cambio de precio.
		productoDao.actualizar(producto);
		// Informa el resultado y muestra el producto actualizado.
		System.out.println("Precio modificado correctamente: " + producto);
	}

	// ---------------------------------------------------------------------
	// Opción 9 - Eliminar un cliente (baja lógica)
	// ---------------------------------------------------------------------
	// Muestra los clientes activos y desactiva el elegido.
	private static void eliminarCliente() {
		System.out.println("\n----- ELIMINAR CLIENTE -----");
		// Obtiene los clientes activos disponibles.
		List<Cliente> clientes = clienteDao.listar();
		// Termina si no hay clientes que puedan darse de baja.
		if (clientes.isEmpty()) {
			System.out.println("No hay clientes activos para eliminar.");
			return;
		}
		// Permite identificar el cliente que se desea desactivar.
		mostrarListaClientes(clientes);
		// Lee el identificador del cliente seleccionado.
		Long id = leerLong("Ingrese el id del cliente a eliminar: ");
		// Ejecuta la baja lógica mediante el DAO.
		clienteDao.eliminarLogico(id);
		// Informa que se solicitó la baja del cliente.
		System.out.println("Cliente " + id + " eliminado (baja lógica) correctamente.");
	}

	// ---------------------------------------------------------------------
	// Opción 10 - Mostrar todas las facturas (activas)
	// ---------------------------------------------------------------------
	// Consulta y muestra cada factura activa.
	private static void mostrarFacturas() {
		System.out.println("\n----- LISTADO DE FACTURAS -----");
		// Recupera las facturas activas.
		List<Factura> facturas = facturaDao.listar();
		// Informa si la consulta no encontró facturas.
		if (facturas.isEmpty()) {
			System.out.println("No hay facturas registradas.");
			return;
		}
		// Recorre las facturas encontradas y muestra sus datos completos.
		for (Factura factura : facturas) {
			mostrarFacturaCompleta(factura);
		}
	}

	// ---------------------------------------------------------------------
	// Opción 11 - Mostrar todos los clientes (activos)
	// ---------------------------------------------------------------------
	// Consulta y muestra todos los clientes activos.
	private static void mostrarClientes() {
		System.out.println("\n----- LISTADO DE CLIENTES -----");
		// Recupera los clientes activos.
		List<Cliente> clientes = clienteDao.listar();
		// Informa si la consulta no encontró clientes.
		if (clientes.isEmpty()) {
			System.out.println("No hay clientes registrados.");
			return;
		}
		// Muestra la lista de clientes obtenida.
		mostrarListaClientes(clientes);
	}

	// ---------------------------------------------------------------------
	// Opción 12 - Mostrar las facturas que superen un total determinado
	// ---------------------------------------------------------------------
	// Consulta y muestra las facturas activas cuyo total supera el importe recibido.
	private static void mostrarFacturasMayoresA(double total) {
		System.out.println("\n----- FACTURAS CON TOTAL MAYOR A $" + (long) total + " -----");
		// Recupera facturas cuyo total supera el umbral indicado.
		List<Factura> facturas = facturaDao.listarMayoresA(total);
		// Informa si ninguna factura cumple el criterio.
		if (facturas.isEmpty()) {
			System.out.println("No hay facturas que superen los $" + (long) total + ".");
			return;
		}
		// Recorre los resultados y muestra cada factura completa.
		for (Factura factura : facturas) {
			mostrarFacturaCompleta(factura);
		}
	}

	// =====================================================================
	// Métodos auxiliares para mostrar datos
	// =====================================================================
	// Imprime una colección de clientes en líneas separadas.
	private static void mostrarListaClientes(List<Cliente> clientes) {
		// Identifica el tipo de información que sigue.
		System.out.println("Clientes:");
		// Recorre la lista para imprimir cada cliente.
		for (Cliente cliente : clientes) {
			System.out.println("  " + cliente);
		}
	}

	// Imprime una colección de productos en líneas separadas.
	private static void mostrarListaProductos(List<Producto> productos) {
		// Identifica el tipo de información que sigue.
		System.out.println("Productos:");
		// Recorre la lista para imprimir cada producto.
		for (Producto producto : productos) {
			System.out.println("  " + producto);
		}
	}

	// Muestra la cabecera de la factura junto con el detalle de cada producto vendido
	// Imprime una factura y maneja el caso en que todavía no tenga detalles.
	private static void mostrarFacturaCompleta(Factura factura) {
		// Muestra los datos generales de la factura.
		System.out.println("\n" + factura);
		// Introduce la sección con las líneas de productos vendidos.
		System.out.println("  Detalles:");
		// Informa si la factura no tiene detalles cargados.
		if (factura.getDetalles() == null || factura.getDetalles().isEmpty()) {
			System.out.println("    (sin detalles)");
		} else {
			// Recorre y muestra cada producto incluido en la factura.
			for (DetalleFactura detalle : factura.getDetalles()) {
				System.out.println("    " + detalle);
			}
		}
	}

	// =====================================================================
	// Métodos auxiliares para leer y validar datos por teclado
	// =====================================================================

	// Lee un número entero; si el valor no es válido vuelve a pedirlo
	// Repite la lectura hasta recibir texto que pueda convertirse a entero.
	private static int leerEntero(String mensaje) {
		// Mantiene la solicitud activa mientras la entrada no sea válida.
		while (true) {
			// Muestra el mensaje que indica qué dato se debe ingresar.
			System.out.print(mensaje);
			// Lee una línea y elimina espacios al comienzo y al final.
			String linea = sc.nextLine().trim();
			try {
				// Convierte la línea ingresada a un entero y lo devuelve.
				return Integer.parseInt(linea);
			} catch (NumberFormatException e) {
				// Informa el formato esperado y permite que el usuario intente otra vez.
				System.out.println("  Valor inválido. Ingrese un número entero.");
			}
		}
	}

	// Lee enteros hasta recibir un valor positivo.
	private static int leerEnteroPositivo(String mensaje) {
		// Repite la lectura mientras el entero ingresado no sea mayor que cero.
		while (true) {
			// Utiliza el lector de enteros para validar el formato.
			int valor = leerEntero(mensaje);
			// Devuelve el valor solamente si es positivo.
			if (valor > 0) {
				return valor;
			}
			// Informa que cero y los valores negativos no están permitidos.
			System.out.println("  El valor debe ser mayor a cero.");
		}
	}

	// Lee un número entero de tipo Long, por ejemplo un identificador.
	private static Long leerLong(String mensaje) {
		// Repite la lectura hasta que la entrada sea un entero válido.
		while (true) {
			// Solicita el valor al usuario.
			System.out.print(mensaje);
			// Lee la línea y elimina espacios externos.
			String linea = sc.nextLine().trim();
			try {
				// Convierte el texto a Long y lo devuelve.
				return Long.parseLong(linea);
			} catch (NumberFormatException e) {
				// Informa el formato esperado y permite volver a intentarlo.
				System.out.println("  Valor inválido. Ingrese un número entero.");
			}
		}
	}

	// Lee un número decimal positivo y admite coma o punto como separador decimal.
	private static double leerDoublePositivo(String mensaje) {
		// Repite la lectura hasta recibir un número válido mayor que cero.
		while (true) {
			// Solicita el precio u otro importe al usuario.
			System.out.print(mensaje);
			// Lee el texto, quita espacios y normaliza la coma decimal a punto.
			String linea = sc.nextLine().trim().replace(",", ".");
			try {
				// Convierte el texto ingresado a un número decimal.
				double valor = Double.parseDouble(linea);
				// Devuelve el importe solo cuando es estrictamente positivo.
				if (valor > 0) {
					return valor;
				}
				// Informa que cero y los valores negativos no están permitidos.
				System.out.println("  El valor debe ser mayor a cero.");
			} catch (NumberFormatException e) {
				// Informa un ejemplo de formato válido para el número decimal.
				System.out.println("  Valor inválido. Ingrese un número (por ejemplo 1500.50).");
			}
		}
	}

	// Lee un texto obligatorio: no permite dejarlo vacío
	// Repite la solicitud hasta recibir una línea con contenido.
	private static String leerTexto(String mensaje) {
		// Mantiene la solicitud activa mientras el texto esté vacío.
		while (true) {
			// Solicita el texto al usuario.
			System.out.print(mensaje);
			// Lee la línea y elimina espacios externos.
			String linea = sc.nextLine().trim();
			// Devuelve el texto si contiene al menos un carácter.
			if (!linea.isEmpty()) {
				return linea;
			}
			// Informa que el campo es obligatorio.
			System.out.println("  El dato no puede quedar vacío.");
		}
	}

	// Lee un texto en una modificación: si se deja vacío conserva el valor actual
	// Devuelve el texto nuevo o el valor existente cuando se deja la entrada vacía.
	private static String leerTextoConValorActual(String etiqueta, String valorActual) {
		// Muestra la etiqueta y el contenido actual para ayudar al usuario.
		System.out.print(etiqueta + " [" + valorActual + "]: ");
		// Lee la nueva entrada y elimina espacios externos.
		String linea = sc.nextLine().trim();
		// Conserva el valor actual si no se escribió uno nuevo.
		return linea.isEmpty() ? valorActual : linea;
	}

	// Lee una respuesta de tipo sí/no
	// Acepta varias formas de escribir afirmación o negación y sigue preguntando ante otras entradas.
	private static boolean leerConfirmacion(String mensaje) {
		// Mantiene la solicitud activa hasta reconocer una respuesta.
		while (true) {
			// Muestra la pregunta al usuario.
			System.out.print(mensaje);
			// Lee la respuesta, elimina espacios y normaliza mayúsculas.
			String linea = sc.nextLine().trim().toLowerCase();
			// Reconoce las variantes afirmativas permitidas.
			if (linea.equals("s") || linea.equals("si") || linea.equals("sí")) {
				return true;
			}
			// Reconoce las variantes negativas permitidas.
			if (linea.equals("n") || linea.equals("no")) {
				return false;
			}
			// Solicita una respuesta comprensible antes de volver a preguntar.
			System.out.println("  Responda 's' (sí) o 'n' (no).");
		}
	}

	// Devuelve el mensaje de la causa más profunda de la excepción (suele ser el más útil)
	// Recorre las causas encadenadas para encontrar el error original.
	private static String obtenerMensajeDeError(Exception e) {
		// Comienza por la excepción recibida.
		Throwable causa = e;
		// Avanza por la cadena mientras haya una causa más profunda.
		while (causa.getCause() != null) {
			causa = causa.getCause();
		}
		// Obtiene el mensaje de la causa original.
		String mensaje = causa.getMessage();
		// Si no existe mensaje, devuelve el nombre de la clase de excepción.
		return (mensaje != null) ? mensaje : causa.getClass().getSimpleName();
	}

	// Cierra la fábrica de EntityManager al salir del programa
	// Libera los recursos JPA antes de finalizar la aplicación.
	private static void cerrarRecursos() {
		try {
			// Obtiene la fábrica compartida y la cierra.
			EmfSingleton.getInstance().getEmf().close();
		} catch (Exception e) {
			// Si ya estaba cerrada o nunca se abrió, no es necesario hacer nada
			// Evita que un problema durante el cierre impida terminar el programa.
		}
	}

}

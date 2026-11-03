package ar.edu.unju.escmi.entities;

// Anotaciones JPA para definir la entidad y sus relaciones en la base de datos.
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

// Indica que la clase se persiste como entidad JPA.
@Entity
// Asocia la entidad con la tabla de detalles de factura.
@Table(name = "detalles_factura")
public class DetalleFactura {
	// Marca el identificador principal del detalle.
	@Id
	// Permite que la base de datos genere el identificador automáticamente.
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// Asocia el identificador con la columna det_id.
	@Column(name = "det_id")
	private Long id;

	// Relaciona muchos detalles con un mismo producto.
	@ManyToOne
	// Guarda la clave foránea del producto y exige que tenga un valor.
	@JoinColumn(name = "prod_id", nullable = false)
	private Producto producto;

	// Registra cuántas unidades del producto se vendieron en este detalle.
	@Column(name = "det_cantidad", nullable = false)
	private int cantidad;

	// Guarda el importe resultante de multiplicar cantidad por precio unitario.
	@Column(name = "det_subtotal")
	private double subtotal;

	// Lado propietario de la relación con la factura y su clave foránea fac_id.
	@ManyToOne
	// Cada detalle debe pertenecer obligatoriamente a una factura.
	@JoinColumn(name = "fac_id", nullable = false)
	private Factura factura;

	// Constructor vacío requerido por JPA para materializar entidades.
	public DetalleFactura() {

	}

	// Crea un detalle con producto y cantidad y calcula su subtotal inicial.
	public DetalleFactura(Producto producto, int cantidad) {
		// Asocia el producto vendido con el detalle.
		this.producto = producto;
		// Registra la cantidad vendida.
		this.cantidad = cantidad;
		// Calcula el subtotal con el precio actual del producto.
		this.subtotal = calcularSubtotal();
	}

	// Calcula el subtotal multiplicando la cantidad por el precio unitario.
	public double calcularSubtotal() {
		return cantidad * producto.getPrecioUnitario();
	}

	// Devuelve el identificador del detalle.
	public Long getId() {
		return id;
	}

	// Actualiza el identificador del detalle.
	public void setId(Long id) {
		this.id = id;
	}

	// Devuelve el producto asociado al detalle.
	public Producto getProducto() {
		return producto;
	}

	// Cambia el producto asociado al detalle.
	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	// Devuelve la cantidad de unidades vendidas.
	public int getCantidad() {
		return cantidad;
	}

	// Actualiza la cantidad de unidades vendidas.
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	// Devuelve el subtotal guardado para el detalle.
	public double getSubtotal() {
		return subtotal;
	}

	// Actualiza el subtotal del detalle.
	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}

	// Devuelve la factura a la que pertenece este detalle.
	public Factura getFactura() {
		return factura;
	}

	// Asocia el detalle con una factura.
	public void setFactura(Factura factura) {
		this.factura = factura;
	}

	// Devuelve los datos del detalle sin incluir la factura para evitar el ciclo Factura -> Detalle -> Factura.
	@Override
	public String toString() {
		return "DetalleFactura [id=" + id + ", producto=" + producto.getDescripcion() + ", precioUnitario="
				+ producto.getPrecioUnitario() + ", cantidad=" + cantidad + ", subtotal=" + subtotal + "]";
	}

}

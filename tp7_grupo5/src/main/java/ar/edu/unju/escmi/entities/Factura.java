package ar.edu.unju.escmi.entities;

// Tipos de Java usados para registrar la fecha y administrar la lista de detalles.
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Anotaciones JPA para mapear la entidad, sus columnas y sus relaciones.
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

// Indica que esta clase se persiste como entidad JPA.
@Entity
// Asocia la entidad con la tabla de facturas.
@Table(name = "facturas")

// Consulta reutilizable que obtiene facturas activas por encima del total indicado, ordenadas de mayor a menor.
@NamedQuery(name = "Factura.mayoresA", query = "SELECT f FROM Factura f WHERE f.estado = true AND f.total > :total ORDER BY f.total DESC")  

public class Factura {
	// Marca el identificador principal de la factura.
	@Id
	// Deja que la base de datos genere el identificador al insertar la factura.
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// Asocia el identificador con la columna fac_id.
	@Column(name = "fac_id")
	private Long id;

	// Guarda la fecha de emisión y exige que no sea nula.
	@Column(name = "fac_fecha", nullable = false)
	private LocalDate fecha;

	// Relaciona muchas facturas con un cliente; esta entidad contiene la clave foránea cli_id.
	@ManyToOne
	// Cada factura debe estar asociada obligatoriamente a un cliente.
	@JoinColumn(name = "cli_id", nullable = false)
	private Cliente cliente;

	// Conserva el domicilio del cliente al momento de emitir la factura.
	@Column(name = "fac_domicilio", length = 100)
	private String domicilio;

	// Guarda el importe total calculado a partir de los detalles.
	@Column(name = "fac_total")
	private double total;

	// Indica si la factura está activa o fue eliminada lógicamente.
	@Column(name = "fac_estado")
	private boolean estado;

	// Una factura agrupa varios detalles; DetalleFactura es el lado propietario de la relación.
	// CascadeType.ALL propaga las operaciones de persistencia a sus detalles.
	// FetchType.EAGER carga los detalles junto con la factura.
	@OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<DetalleFactura> detalles = new ArrayList<>();

	// Constructor vacío requerido por JPA para materializar entidades.
	public Factura() {

	}

	// Inicializa una factura nueva para el cliente y copia sus datos necesarios.
	public Factura(Cliente cliente) {
		// Registra como fecha de emisión el día actual.
		this.fecha = LocalDate.now();
		// Asocia la factura con el cliente que la realiza.
		this.cliente = cliente;
		// Conserva el domicilio del cliente al momento de crear la factura.
		this.domicilio = cliente.getDomicilio();
		// Inicializa el total antes de cargar detalles.
		this.total = 0;
		// Las facturas nuevas comienzan activas.
		this.estado = true;
	}

	// Agrega un detalle, actualiza ambos lados de la relación y recalcula el total.
	public void agregarDetalle(DetalleFactura detalle) {
		// Asocia el detalle con esta factura.
		detalle.setFactura(this);
		// Incorpora el detalle a la lista de la factura.
		this.detalles.add(detalle);
		// Actualiza el total con los subtotales de todos los detalles.
		this.total = calcularTotal();
	}

	// Suma los subtotales de todos los detalles de la factura.
	public double calcularTotal() {
		// Acumulador que comienza en cero antes de recorrer los detalles.
		double suma = 0;
		// Recorre los detalles asociados a la factura.
		for (DetalleFactura detalle : detalles) {
			// Incorpora el subtotal actual al acumulado.
			suma += detalle.getSubtotal();
		}
		// Devuelve el total resultante.
		return suma;
	}

	// Devuelve el identificador de la factura.
	public Long getId() {
		return id;
	}

	// Actualiza el identificador de la factura.
	public void setId(Long id) {
		this.id = id;
	}

	// Devuelve la fecha de emisión.
	public LocalDate getFecha() {
		return fecha;
	}

	// Actualiza la fecha de emisión.
	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	// Devuelve el cliente asociado a la factura.
	public Cliente getCliente() {
		return cliente;
	}

	// Cambia el cliente asociado a la factura.
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	// Devuelve el domicilio guardado en la factura.
	public String getDomicilio() {
		return domicilio;
	}

	// Actualiza el domicilio guardado en la factura.
	public void setDomicilio(String domicilio) {
		this.domicilio = domicilio;
	}

	// Devuelve el importe total de la factura.
	public double getTotal() {
		return total;
	}

	// Actualiza el importe total de la factura.
	public void setTotal(double total) {
		this.total = total;
	}

	// Devuelve true si la factura está activa.
	public boolean isEstado() {
		return estado;
	}

	// Cambia el estado de actividad de la factura.
	public void setEstado(boolean estado) {
		this.estado = estado;
	}

	// Devuelve la lista de detalles de la factura.
	public List<DetalleFactura> getDetalles() {
		return detalles;
	}

	// Reemplaza la lista de detalles de la factura.
	public void setDetalles(List<DetalleFactura> detalles) {
		this.detalles = detalles;
	}

	// Devuelve una representación textual de los datos principales de la factura.
	@Override
	public String toString() {
		return "Factura [id=" + id + ", fecha=" + fecha + ", cliente=" + cliente.getNombre() + " "
				+ cliente.getApellido() + ", domicilio=" + domicilio + ", total=" + total + ", estado=" + estado + "]";
	}

}

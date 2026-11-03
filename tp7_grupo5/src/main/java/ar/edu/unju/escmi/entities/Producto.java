package ar.edu.unju.escmi.entities;

// Anotaciones JPA para relacionar la clase con la tabla y sus columnas.
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

// Indica que esta clase se guarda como entidad en la base de datos.
@Entity
// Asocia la entidad con la tabla "productos".
@Table(name = "productos")
public class Producto {
	// Marca el identificador de la entidad.
	@Id
	// Deja que la base de datos genere el identificador al insertar el registro.
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// Asocia el atributo con la columna prod_id.
	@Column(name = "prod_id")
	private Long id;

	// Guarda la descripción obligatoria del producto, hasta 100 caracteres.
	@Column(name = "prod_descripcion", length = 100, nullable = false)
	private String descripcion;

	// Guarda el precio unitario obligatorio del producto.
	@Column(name = "prod_precio_unitario", nullable = false)
	private double precioUnitario;

	// Indica si el producto está activo o fue eliminado lógicamente.
	@Column(name = "prod_estado")
	private boolean estado;

	// Constructor vacío requerido por JPA para materializar entidades.
	public Producto() {

	}

	// Inicializa descripción y precio, y marca el producto como activo.
	public Producto(String descripcion, double precioUnitario) {
		// Copia la descripción recibida.
		this.descripcion = descripcion;
		// Copia el precio unitario recibido.
		this.precioUnitario = precioUnitario;
		// Los productos recién creados comienzan activos.
		this.estado = true;
	}

	// Devuelve el identificador del producto.
	public Long getId() {
		return id;
	}

	// Actualiza el identificador del producto.
	public void setId(Long id) {
		this.id = id;
	}

	// Devuelve la descripción del producto.
	public String getDescripcion() {
		return descripcion;
	}

	// Actualiza la descripción del producto.
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	// Devuelve el precio unitario del producto.
	public double getPrecioUnitario() {
		return precioUnitario;
	}

	// Actualiza el precio unitario del producto.
	public void setPrecioUnitario(double precioUnitario) {
		this.precioUnitario = precioUnitario;
	}

	// Devuelve true si el producto está activo.
	public boolean isEstado() {
		return estado;
	}

	// Cambia el estado de actividad del producto.
	public void setEstado(boolean estado) {
		this.estado = estado;
	}

	// Devuelve una representación textual útil para mostrar el producto.
	@Override
	public String toString() {
		return "Producto [id=" + id + ", descripcion=" + descripcion + ", precioUnitario=" + precioUnitario
				+ ", estado=" + estado + "]";
	}

}

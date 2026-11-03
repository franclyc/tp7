package ar.edu.unju.escmi.entities;

// Anotaciones JPA para definir la tabla, columnas e identificador de la entidad.
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

// Indica que esta clase se guarda como entidad en la base de datos.
@Entity
// Asocia la entidad con la tabla "clientes".
@Table(name = "clientes")
public class Cliente {
	// Marca el identificador de la entidad.
	@Id
	// Deja que la base de datos genere el identificador al insertar el registro.
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// Asocia el atributo con la columna cli_id.
	@Column(name = "cli_id")
	private Long id;

	// Guarda el nombre, obligatorio y limitado a 50 caracteres.
	@Column(name = "cli_nombre", length = 50, nullable = false)
	private String nombre;

	// Guarda el apellido, obligatorio y limitado a 50 caracteres.
	@Column(name = "cli_apellido", length = 50, nullable = false)
	private String apellido;

	// Guarda el domicilio, limitado a 100 caracteres y opcional.
	@Column(name = "cli_domicilio", length = 100)
	private String domicilio;

	// Guarda el DNI obligatorio, que debe ser único en la tabla.
	@Column(name = "cli_dni", nullable = false, unique = true)
	private int dni;

	// Indica si el cliente está activo o fue eliminado lógicamente.
	@Column(name = "cli_estado")
	private boolean estado;

	// Constructor vacío requerido por JPA para materializar entidades.
	public Cliente() {

	}

	// Inicializa los datos del cliente y lo marca como activo.
	public Cliente(String nombre, String apellido, String domicilio, int dni) {
		// Copia el nombre recibido al atributo de la entidad.
		this.nombre = nombre;
		// Copia el apellido recibido al atributo de la entidad.
		this.apellido = apellido;
		// Copia el domicilio recibido al atributo de la entidad.
		this.domicilio = domicilio;
		// Copia el DNI recibido al atributo de la entidad.
		this.dni = dni;
		// Los clientes recién creados comienzan activos.
		this.estado = true;
	}

	// Devuelve el identificador del cliente.
	public Long getId() {
		return id;
	}

	// Actualiza el identificador del cliente.
	public void setId(Long id) {
		this.id = id;
	}

	// Devuelve el nombre del cliente.
	public String getNombre() {
		return nombre;
	}

	// Actualiza el nombre del cliente.
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	// Devuelve el apellido del cliente.
	public String getApellido() {
		return apellido;
	}

	// Actualiza el apellido del cliente.
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	// Devuelve el domicilio del cliente.
	public String getDomicilio() {
		return domicilio;
	}

	// Actualiza el domicilio del cliente.
	public void setDomicilio(String domicilio) {
		this.domicilio = domicilio;
	}

	// Devuelve el DNI del cliente.
	public int getDni() {
		return dni;
	}

	// Actualiza el DNI del cliente.
	public void setDni(int dni) {
		this.dni = dni;
	}

	// Devuelve true si el cliente está activo.
	public boolean isEstado() {
		return estado;
	}

	// Cambia el estado de actividad del cliente.
	public void setEstado(boolean estado) {
		this.estado = estado;
	}

	// Devuelve una representación textual útil para mostrar los datos del cliente.
	@Override
	public String toString() {
		return "Cliente [id=" + id + ", nombre=" + nombre + ", apellido=" + apellido + ", domicilio=" + domicilio
				+ ", dni=" + dni + ", estado=" + estado + "]";
	}

}

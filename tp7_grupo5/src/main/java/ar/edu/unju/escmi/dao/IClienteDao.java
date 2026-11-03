package ar.edu.unju.escmi.dao;

// Tipo de colección utilizado para devolver varios clientes.
import java.util.List;

// Entidad administrada por las operaciones de esta interfaz.
import ar.edu.unju.escmi.entities.Cliente;

// Define las operaciones de persistencia disponibles para los clientes.
public interface IClienteDao {
	// Guarda un cliente nuevo en la base de datos.
	public void guardar(Cliente cliente);
	// Busca un cliente por su identificador y devuelve null si no existe.
	public Cliente buscarPorId(Long id);
	// Devuelve los clientes activos registrados.
	public List<Cliente> listar();
	// Persiste los cambios realizados sobre un cliente.
	public void actualizar(Cliente cliente);
	// Marca como inactivo al cliente identificado, sin borrar su registro.
	public void eliminarLogico(Long id);
}

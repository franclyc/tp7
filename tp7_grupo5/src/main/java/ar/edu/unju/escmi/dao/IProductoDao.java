package ar.edu.unju.escmi.dao;

// Tipo de colección utilizado para devolver varios productos.
import java.util.List;

// Entidad administrada por las operaciones de esta interfaz.
import ar.edu.unju.escmi.entities.Producto;

// Define las operaciones de persistencia disponibles para los productos.
public interface IProductoDao {
	// Guarda un producto nuevo en la base de datos.
	public void guardar(Producto producto);
	// Busca un producto por su identificador.
	public Producto buscarPorId(Long id);
	// Devuelve los productos activos registrados.
	public List<Producto> listar();
	// Persiste los cambios realizados sobre un producto.
	public void actualizar(Producto producto);
	// Marca como inactivo al producto indicado, sin borrar su registro.
	public void eliminarLogico(Long id);
}

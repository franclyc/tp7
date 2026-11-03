package ar.edu.unju.escmi.dao;

// Tipo de colección utilizado para devolver varias facturas.
import java.util.List;

// Entidad administrada por las operaciones de esta interfaz.
import ar.edu.unju.escmi.entities.Factura;

// Define las operaciones de persistencia disponibles para las facturas.
public interface IFacturaDao {
	// Guarda una factura y, según la configuración de la entidad, sus detalles.
	public void guardar(Factura factura);
	// Busca una factura por su identificador.
	public Factura buscarPorId(Long id);
	// Devuelve las facturas activas registradas.
	public List<Factura> listar();
	// Marca como inactiva la factura indicada, sin borrar su registro.
	public void eliminarLogico(Long id);
	// Busca las facturas activas cuyo total supera el importe indicado.
	public List<Factura> listarMayoresA(double total);
}

package ar.edu.unju.escmi.dao;

// Tipo de colección utilizado para devolver varios detalles.
import java.util.List;

// Entidad que representa cada producto incluido en una factura.
import ar.edu.unju.escmi.entities.DetalleFactura;

// Define las operaciones de persistencia disponibles para los detalles de factura.
public interface IDetalleFacturaDao {
	// Guarda un detalle de factura nuevo.
	public void guardar(DetalleFactura detalle);
	// Devuelve los detalles asociados a la factura indicada.
	public List<DetalleFactura> listarPorFactura(Long idFactura);
}

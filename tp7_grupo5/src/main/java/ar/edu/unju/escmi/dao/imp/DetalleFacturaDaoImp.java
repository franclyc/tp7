package ar.edu.unju.escmi.dao.imp;

// Colección usada para devolver varios detalles.
import java.util.List;

// API JPA para administrar sesiones y transacciones de base de datos.
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

// Proporciona la fábrica compartida de EntityManager.
import ar.edu.unju.escmi.config.EmfSingleton;
// Contrato de operaciones que implementa esta clase.
import ar.edu.unju.escmi.dao.IDetalleFacturaDao;
// Entidad persistida por este DAO.
import ar.edu.unju.escmi.entities.DetalleFactura;

// Implementa el acceso a datos de los detalles de factura mediante JPA.
public class DetalleFacturaDaoImp implements IDetalleFacturaDao {
	// Única fábrica de EntityManager de toda la aplicación (patrón Singleton)
	// Se obtiene del singleton para crear contextos JPA por operación.
	private EntityManagerFactory emf = EmfSingleton.getInstance().getEmf();

	// Normalmente no hace falta: los detalles se guardan junto con la factura por la cascada.
	// Sirve para agregar un detalle a una factura que ya existe (el detalle debe tener su factura asignada).
	// Inserta un detalle nuevo dentro de una transacción.
	@Override
	public void guardar(DetalleFactura detalle) {
		// Abre un contexto de persistencia para esta operación.
		EntityManager manager = emf.createEntityManager();
		try {
			// Inicia la transacción antes de guardar el detalle.
			manager.getTransaction().begin();
			// Registra el detalle en el contexto de persistencia.
			manager.persist(detalle);
			// Confirma los cambios en la base de datos.
			manager.getTransaction().commit();
		} catch (Exception e) {
			// Revierte cambios incompletos si la transacción sigue activa.
			if (manager.getTransaction().isActive()) {
				manager.getTransaction().rollback();
			}
			// Informa al llamador que no se pudo persistir el detalle.
			throw new PersistenceException("No se pudo guardar el detalle de factura", e);
		} finally {
			// Libera siempre los recursos del contexto JPA.
			manager.close();
		}
	}

	// Busca todos los detalles pertenecientes a una factura determinada.
	@Override
	public List<DetalleFactura> listarPorFactura(Long idFactura) {
		// Crea el contexto JPA usado para la consulta.
		EntityManager manager = emf.createEntityManager();
		try {
			// Prepara una consulta parametrizada para filtrar por el identificador de factura.
			TypedQuery<DetalleFactura> query = manager.createQuery(
					"SELECT d FROM DetalleFactura d WHERE d.factura.id = :idFactura ORDER BY d.id", DetalleFactura.class);
			// Asigna el identificador recibido al parámetro de la consulta.
			query.setParameter("idFactura", idFactura);
			// Ejecuta la consulta y devuelve los detalles encontrados.
			return query.getResultList();
		} finally {
			// Libera el contexto al terminar la consulta.
			manager.close();
		}
	}

}

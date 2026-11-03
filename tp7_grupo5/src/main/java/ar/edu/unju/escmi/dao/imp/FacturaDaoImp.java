package ar.edu.unju.escmi.dao.imp;

// Colección usada para devolver los resultados de las consultas.
import java.util.List;

// API JPA para administrar sesiones y transacciones de base de datos.
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

// Proporciona la fábrica compartida de EntityManager.
import ar.edu.unju.escmi.config.EmfSingleton;
// Contrato de operaciones que implementa esta clase.
import ar.edu.unju.escmi.dao.IFacturaDao;
// Entidad persistida por este DAO.
import ar.edu.unju.escmi.entities.Factura;

// Implementa el acceso a datos de facturas mediante JPA.
public class FacturaDaoImp implements IFacturaDao {
	// Única fábrica de EntityManager de toda la aplicación (patrón Singleton)
	// Se obtiene del singleton para crear contextos JPA por operación.
	private EntityManagerFactory emf = EmfSingleton.getInstance().getEmf();

	// Al persistir la factura también se guardan sus detalles (CascadeType.ALL en Factura.detalles)
	// Inserta una factura nueva dentro de una transacción.
	@Override
	public void guardar(Factura factura) {
		// Abre un contexto de persistencia para esta operación.
		EntityManager manager = emf.createEntityManager();
		try {
			// Inicia la transacción antes de guardar la factura.
			manager.getTransaction().begin();
			// Registra la factura; la cascada también persiste sus detalles asociados.
			manager.persist(factura);
			// Confirma los cambios en la base de datos.
			manager.getTransaction().commit();
		} catch (Exception e) {
			// Revierte cambios incompletos si la transacción sigue activa.
			if (manager.getTransaction().isActive()) {
				manager.getTransaction().rollback();
			}
			// Informa al llamador que no se pudo persistir la factura.
			throw new PersistenceException("No se pudo guardar la factura", e);
		} finally {
			// Libera siempre los recursos del contexto JPA.
			manager.close();
		}
	}

	// Busca una factura por su clave primaria.
	@Override
	public Factura buscarPorId(Long id) {
		// Crea el contexto JPA usado para la búsqueda.
		EntityManager manager = emf.createEntityManager();
		try {
			// Recupera la factura o null si no existe.
			return manager.find(Factura.class, id);
		} finally {
			// Cierra el contexto incluso si ocurre un error.
			manager.close();
		}
	}

	// Consulta dinámica: solo devuelve las facturas activas
	@Override
	public List<Factura> listar() {
		// Crea el contexto JPA usado para consultar facturas.
		EntityManager manager = emf.createEntityManager();
		try {
			// Consulta únicamente facturas activas y las ordena por identificador.
			TypedQuery<Factura> query = manager.createQuery(
					"SELECT f FROM Factura f WHERE f.estado = true ORDER BY f.id", Factura.class);
			// Ejecuta la consulta y devuelve todas las facturas encontradas.
			return query.getResultList();
		} finally {
			// Libera el contexto al terminar la consulta.
			manager.close();
		}
	}

	// Realiza una baja lógica cambiando el estado de la factura a inactivo.
	@Override
	public void eliminarLogico(Long id) {
		// Abre el contexto JPA para buscar y actualizar la factura.
		EntityManager manager = emf.createEntityManager();
		try {
			// Inicia la transacción que contiene la baja lógica.
			manager.getTransaction().begin();
			// Busca la entidad que se va a desactivar.
			Factura factura = manager.find(Factura.class, id);
			// Evita intentar modificar una factura inexistente.
			if (factura == null) {
				throw new IllegalArgumentException("No existe una factura con id " + id);
			}
			// La factura está administrada por el manager: el cambio se guarda al hacer commit
			// Marca la factura como inactiva sin eliminar físicamente su fila.
			factura.setEstado(false);
			// Persiste el cambio de estado.
			manager.getTransaction().commit();
		} catch (Exception e) {
			// Revierte la operación si quedó una transacción abierta.
			if (manager.getTransaction().isActive()) {
				manager.getTransaction().rollback();
			}
			// Comunica que la baja lógica no pudo completarse.
			throw new PersistenceException("No se pudo eliminar la factura con id " + id, e);
		} finally {
			// Libera el contexto de persistencia.
			manager.close();
		}
	}

	// Consulta estática: usa la @NamedQuery "Factura.mayoresA" declarada en la entidad Factura
	@Override
	public List<Factura> listarMayoresA(double total) {
		// Crea el contexto JPA usado para ejecutar la consulta nombrada.
		EntityManager manager = emf.createEntityManager();
		try {
			// Recupera la consulta "Factura.mayoresA" definida en la entidad Factura.
			TypedQuery<Factura> query = manager.createNamedQuery("Factura.mayoresA", Factura.class);
			// Asigna el importe mínimo que debe superar cada factura.
			query.setParameter("total", total);
			// Ejecuta la consulta y devuelve las facturas que cumplen el criterio.
			return query.getResultList();
		} finally {
			// Libera el contexto al terminar la consulta.
			manager.close();
		}
	}

}

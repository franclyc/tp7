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
import ar.edu.unju.escmi.dao.IClienteDao;
// Entidad persistida por este DAO.
import ar.edu.unju.escmi.entities.Cliente;

// Implementa el acceso a datos de clientes mediante JPA.
public class ClienteDaoImp implements IClienteDao {
	// Única fábrica de EntityManager de toda la aplicación (patrón Singleton)
	// Se obtiene del singleton para crear contextos JPA por operación.
	private EntityManagerFactory emf = EmfSingleton.getInstance().getEmf();

	// Inserta un cliente nuevo en una transacción.
	@Override
	public void guardar(Cliente cliente) {
		// Abre un contexto de persistencia para esta operación.
		EntityManager manager = emf.createEntityManager();
		try {
			// Inicia la transacción antes de modificar la base de datos.
			manager.getTransaction().begin();
			// Registra el cliente nuevo en el contexto de persistencia.
			manager.persist(cliente);
			// Confirma los cambios en la base de datos.
			manager.getTransaction().commit();
		} catch (Exception e) {
			// Revierte cambios incompletos si la transacción sigue activa.
			if (manager.getTransaction().isActive()) {
				manager.getTransaction().rollback();
			}
			// Informa al llamador que no se pudo persistir el cliente.
			throw new PersistenceException("No se pudo guardar el cliente", e);
		} finally {
			// Libera siempre los recursos del contexto JPA.
			manager.close();
		}
	}

	// Busca un cliente por su clave primaria.
	@Override
	public Cliente buscarPorId(Long id) {
		// Crea el contexto JPA usado para la búsqueda.
		EntityManager manager = emf.createEntityManager();
		try {
			// Recupera el cliente o null si no existe.
			return manager.find(Cliente.class, id);
		} finally {
			// Cierra el contexto incluso si ocurre un error.
			manager.close();
		}
	}

	// Solo devuelve los clientes activos (los eliminados lógicamente tienen estado = false)
	@Override
	public List<Cliente> listar() {
		// Crea el contexto JPA usado para consultar los clientes.
		EntityManager manager = emf.createEntityManager();
		try {
			// Construye una consulta que devuelve solo clientes activos ordenados por apellido y nombre.
			TypedQuery<Cliente> query = manager.createQuery(
					"SELECT c FROM Cliente c WHERE c.estado = true ORDER BY c.apellido, c.nombre", Cliente.class);
			// Ejecuta la consulta y devuelve todos sus resultados.
			return query.getResultList();
		} finally {
			// Libera el contexto una vez terminada la consulta.
			manager.close();
		}
	}

	// Actualiza los datos de un cliente existente.
	@Override
	public void actualizar(Cliente cliente) {
		// Abre un contexto de persistencia para la actualización.
		EntityManager manager = emf.createEntityManager();
		try {
			// Inicia la transacción de escritura.
			manager.getTransaction().begin();
			// Sincroniza los cambios del cliente con el contexto de persistencia.
			manager.merge(cliente);
			// Confirma la actualización en la base de datos.
			manager.getTransaction().commit();
		} catch (Exception e) {
			// Revierte la transacción si la actualización falló.
			if (manager.getTransaction().isActive()) {
				manager.getTransaction().rollback();
			}
			// Propaga un error contextualizado al llamador.
			throw new PersistenceException("No se pudo modificar el cliente", e);
		} finally {
			// Cierra el contexto aunque la operación termine con error.
			manager.close();
		}
	}

	// Realiza una baja lógica cambiando el estado del cliente a inactivo.
	@Override
	public void eliminarLogico(Long id) {
		// Abre el contexto JPA para buscar y actualizar el cliente.
		EntityManager manager = emf.createEntityManager();
		try {
			// Inicia la transacción que contiene la baja lógica.
			manager.getTransaction().begin();
			// Busca la entidad que se va a desactivar.
			Cliente cliente = manager.find(Cliente.class, id);
			// Evita intentar modificar un cliente inexistente.
			if (cliente == null) {
				throw new IllegalArgumentException("No existe un cliente con id " + id);
			}
			// El cliente está administrado por el manager: el cambio se guarda al hacer commit
			// Marca el cliente como inactivo sin eliminar físicamente su fila.
			cliente.setEstado(false);
			// Persiste el cambio de estado.
			manager.getTransaction().commit();
		} catch (Exception e) {
			// Revierte la operación si quedó una transacción abierta.
			if (manager.getTransaction().isActive()) {
				manager.getTransaction().rollback();
			}
			// Comunica que la baja lógica no pudo completarse.
			throw new PersistenceException("No se pudo eliminar el cliente con id " + id, e);
		} finally {
			// Libera el contexto de persistencia.
			manager.close();
		}
	}

}

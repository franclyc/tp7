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
import ar.edu.unju.escmi.dao.IProductoDao;
// Entidad persistida por este DAO.
import ar.edu.unju.escmi.entities.Producto;

// Implementa el acceso a datos de productos mediante JPA.
public class ProductoDaoImp implements IProductoDao {
	// Única fábrica de EntityManager de toda la aplicación (patrón Singleton)
	// Se obtiene del singleton para crear contextos JPA por operación.
	private EntityManagerFactory emf = EmfSingleton.getInstance().getEmf();

	// Inserta un producto nuevo en una transacción.
	@Override
	public void guardar(Producto producto) {
		// Abre un contexto de persistencia para esta operación.
		EntityManager manager = emf.createEntityManager();
		try {
			// Inicia la transacción antes de modificar la base de datos.
			manager.getTransaction().begin();
			// Registra el producto nuevo en el contexto de persistencia.
			manager.persist(producto);
			// Confirma los cambios en la base de datos.
			manager.getTransaction().commit();
		} catch (Exception e) {
			// Revierte cambios incompletos si la transacción sigue activa.
			if (manager.getTransaction().isActive()) {
				manager.getTransaction().rollback();
			}
			// Informa al llamador que no se pudo persistir el producto.
			throw new PersistenceException("No se pudo guardar el producto", e);
		} finally {
			// Libera siempre los recursos del contexto JPA.
			manager.close();
		}
	}

	// Busca un producto por su clave primaria.
	@Override
	public Producto buscarPorId(Long id) {
		// Crea el contexto JPA usado para la búsqueda.
		EntityManager manager = emf.createEntityManager();
		try {
			// Recupera el producto o null si no existe.
			return manager.find(Producto.class, id);
		} finally {
			// Cierra el contexto incluso si ocurre un error.
			manager.close();
		}
	}

	// Solo devuelve los productos activos (los eliminados lógicamente tienen estado = false)
	@Override
	public List<Producto> listar() {
		// Crea el contexto JPA usado para consultar los productos.
		EntityManager manager = emf.createEntityManager();
		try {
			// Construye una consulta que devuelve solo productos activos ordenados por descripción.
			TypedQuery<Producto> query = manager.createQuery(
					"SELECT p FROM Producto p WHERE p.estado = true ORDER BY p.descripcion", Producto.class);
			// Ejecuta la consulta y devuelve todos sus resultados.
			return query.getResultList();
		} finally {
			// Libera el contexto una vez terminada la consulta.
			manager.close();
		}
	}

	// Actualiza los datos de un producto existente.
	@Override
	public void actualizar(Producto producto) {
		// Abre un contexto de persistencia para la actualización.
		EntityManager manager = emf.createEntityManager();
		try {
			// Inicia la transacción de escritura.
			manager.getTransaction().begin();
			// Sincroniza los cambios del producto con el contexto de persistencia.
			manager.merge(producto);
			// Confirma la actualización en la base de datos.
			manager.getTransaction().commit();
		} catch (Exception e) {
			// Revierte la transacción si la actualización falló.
			if (manager.getTransaction().isActive()) {
				manager.getTransaction().rollback();
			}
			// Propaga un error contextualizado al llamador.
			throw new PersistenceException("No se pudo modificar el producto", e);
		} finally {
			// Cierra el contexto aunque la operación termine con error.
			manager.close();
		}
	}

	// Realiza una baja lógica cambiando el estado del producto a inactivo.
	@Override
	public void eliminarLogico(Long id) {
		// Abre el contexto JPA para buscar y actualizar el producto.
		EntityManager manager = emf.createEntityManager();
		try {
			// Inicia la transacción que contiene la baja lógica.
			manager.getTransaction().begin();
			// Busca la entidad que se va a desactivar.
			Producto producto = manager.find(Producto.class, id);
			// Evita intentar modificar un producto inexistente.
			if (producto == null) {
				throw new IllegalArgumentException("No existe un producto con id " + id);
			}
			// El producto está administrado por el manager: el cambio se guarda al hacer commit
			// Marca el producto como inactivo sin eliminar físicamente su fila.
			producto.setEstado(false);
			// Persiste el cambio de estado.
			manager.getTransaction().commit();
		} catch (Exception e) {
			// Revierte la operación si quedó una transacción abierta.
			if (manager.getTransaction().isActive()) {
				manager.getTransaction().rollback();
			}
			// Comunica que la baja lógica no pudo completarse.
			throw new PersistenceException("No se pudo eliminar el producto con id " + id, e);
		} finally {
			// Libera el contexto de persistencia.
			manager.close();
		}
	}

}

package ar.edu.unju.escmi.config;

// Permite crear y administrar los EntityManager usados para acceder a la base de datos.
import javax.persistence.EntityManagerFactory;
// Proporciona el punto de entrada para inicializar la unidad de persistencia.
import javax.persistence.Persistence;

// Centraliza la fábrica de EntityManager para que exista una sola instancia en la aplicación.
public class EmfSingleton {
	// Crea la instancia única al cargar la clase.
	private static EmfSingleton miInstancia = new EmfSingleton();
	// Nombre de la unidad de persistencia configurada en persistence.xml.
	private static final String PERSISTENCE_UNIT_NAME = "TestPersistence";
	// Guarda la fábrica JPA y evita crearla hasta que sea necesaria.
	private EntityManagerFactory emf = null;

	// Impide que otras clases creen instancias de este singleton.
	private EmfSingleton() {

	}

	// Devuelve la única instancia disponible de esta clase.
	public static EmfSingleton getInstance() {
		return miInstancia;
	}

	// Crea la fábrica JPA la primera vez y luego reutiliza la misma instancia.
	public EntityManagerFactory getEmf() {
		// Inicializa la fábrica solo cuando todavía no fue creada.
		if(this.emf == null)
			this.emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		// Entrega la fábrica para que los DAO creen EntityManager.
		return this.emf;
	}
}

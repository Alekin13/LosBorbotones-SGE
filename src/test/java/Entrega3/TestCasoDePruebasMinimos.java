package Entrega3;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Test;
import org.uqbarproject.jpa.java8.extras.PerThreadEntityManagers;

import Dispositivo.Dispositivo;
import Dispositivo.DispositivoEstado;
import Dispositivo.DispositivoFactory;
import Dispositivo.DispositivoInteligente;
import Helper.EntityManagerHelper;
import Reporte.Reporte;
import Usuario.Cliente;

public class TestCasoDePruebasMinimos {
	
	private static final String PATH_JSON_ESTADOS = "src/test/resources/Data/Estados.json";
	
	
	public void casoPrueba1(){
		
		EntityManagerHelper dbhelper = new EntityManagerHelper();
		
		EntityManager entityManager = PerThreadEntityManagers.getEntityManager();
		Query query = entityManager.createQuery("FROM Cliente WHERE apellido = :id");

		Cliente alejandro = new Cliente("Alekin", "123456", "Alejandro", "Mattioli", "Av. Rivadavia 5000", LocalDateTime.now(), "DNI", 38993333, 1551515555, "R1");
		dbhelper.persistirCliente(alejandro);
		
		System.out.println(alejandro.getLatitud() + " ; " + alejandro.getLongitud());
		
		query.setParameter("id", alejandro.getApellido());
		List list = query.getResultList();
		
		Cliente ale2 = (Cliente) list.get((new Long(0)).intValue());
		alejandro.setLatitud(20.59);
		alejandro.setLongitud(14.59);		
		dbhelper.modificar(alejandro);

		System.out.println(ale2.getLatitud() + " ; " + ale2.getLongitud());
		
	}
	
	@SuppressWarnings("unchecked")
	public void casoPrueba2() throws ParseException{
		//Recuperar un dispositivo. 
		//Mostrar por consola todos los intervalos que estuvo encendido durante el �ltimo mes. 
		//Modificar su nombre (o cualquier otro atributo editable) y grabarlo. 
		//Recuperarlo y evaluar que el nombre coincida con el esperado.
		EntityManagerHelper dbhelper = new EntityManagerHelper();
		DispositivoFactory fabricaDeDispositivos = new DispositivoFactory();
		DispositivoInteligente dispositivo = fabricaDeDispositivos.aireAcondicionado2200();
		DispositivoInteligente dispositivo2 = fabricaDeDispositivos.lamparaAlogena40w();
		
		dbhelper.cargarEstadosFromJson(PATH_JSON_ESTADOS);
		Cliente unCliente = new Cliente("jey", "123456", "Jael", "Duran", "Av. Rivadavia 6000", LocalDateTime.now(), "DNI", 98745632, 45459595, "R1");
		unCliente.addDispositivo(dispositivo);
		unCliente.addDispositivo(dispositivo2);
		dbhelper.persistirCliente(unCliente);

		System.out.println(dispositivo.getEstado().getDescripcion());
		dispositivo.setCambioEstado("A");
		System.out.println(dispositivo.getEstado().getDescripcion());
		dbhelper.modificar(dispositivo);
		
		EntityManager entityManager = PerThreadEntityManagers.getEntityManager();
		Query query = entityManager.createQuery("FROM Dispositivo WHERE disp_id = :id");
		query.setParameter("id", unCliente.getDispositivos().get(0).getId());
		List list = query.getResultList();
		
		DispositivoInteligente dispositivo1 = (DispositivoInteligente) list.get(0);
		System.out.println(dispositivo1.getNombreDispositivo());
		
		dispositivo.setCambioEstado("E");
		System.out.println(dispositivo.getEstado().getDescripcion());
		dbhelper.modificar(dispositivo);

		dispositivo2.setCambioEstado("E");
		System.out.println(dispositivo2.getEstado().getDescripcion());
		dbhelper.modificar(dispositivo2);
		
		List<DispositivoEstado> est = dbhelper.buscarTodos(DispositivoEstado.class, new ImmutablePair<>("idDispositivo",dispositivo.getId()));
		
		//Query consultaConsumos = entityManager.createQuery("from DispositivoEstado");
		//consultaConsumos.setParameter("id", dispositivo.getId());
		for(DispositivoEstado e : est){
			System.out.println(e.getConsumoEstadoPasado() + " " + e.getEstadoActual());
		}
			
		Reporte report = new Reporte();
		report.consumoHogarxPeriodo(unCliente, LocalDateTime.now().minusMinutes(10), LocalDateTime.now());
	}
	
	@Test
	public void test() throws ParseException{
		
		
		this.casoPrueba2();
		
	}
}
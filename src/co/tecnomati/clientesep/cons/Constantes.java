package co.tecnomati.clientesep.cons;

public class Constantes {

	public static final String URL="http://www.jujuyconectada.com.ar:8000/sep/";
//	public static final String URL="http://192.168.0.100:8080/sep/";
//	public static final String URL="http://joelpelaezz.ddns.net/sep/";
//	public static final String URL="http://test.jujuyconectada.com.ar/";
	public static final String URL_ENVIAR_EMERGENCIA = Constantes.URL +"service/emergencia";
	public static final String URL_LISTAR_EMERGENCIAS_ACTIVAS = Constantes.URL +"service/emergenciasActivas";
	public static final String URL_LISTAR_EMERGENCIAS_BY_USER = Constantes.URL +"service/emergenciasbyuser/usid/";
	public static final String URL_CREAR_USUARIO = Constantes.URL +"service/usuario";
	public static final String URL_ENVIAR_MONITOREO = Constantes.URL +"service/monitoreo";
	public static final String URL_RECUPERAR_MONITOREO = Constantes.URL +"service/monitoreo/usid";
	
	
	public static final int SERVICIO_EMERGENCIA_POLICIA=1;
	public static final int SERVICIO_EMERGENCIA_HOSPITAL=3;
	public static final int SERVICIO_EMERGENCIA_BOMBERO=2;
	
	public static final int SERVICIO_EMERGENCIA_TODOS=0;
		
	// recien envia
	public static final int ESTADO_EMERGENCIA_PENDIENTE=1;
	// estan auxiliando
	public static final int ESTADO_EMERGENCIA_ACTIVA=2;
	// finalizada.
	public static final int ESTADO_EMERGENCIA_RESUELTA=3;
	
	public static final String SENDER_ID= "299647685608";
	public static final String API_KEY_SERVER_GCM= "AIzaSyCJGgncqAh3-SNjw1adBIR74Qi_CsUXlnA";
	
}

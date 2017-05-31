package co.tecnomati.clientesep.util;

import java.text.DecimalFormat;

import co.tecnomati.clientesep.activity.EnviaFotoActivity;
import co.tecnomati.clientesep.activity.HomeActivity;
import co.tecnomati.clientesep.asyncTask.TareaEnviarEmergencia;
import co.tecnomati.clientesep.cons.Constantes;
import co.tecnomati.clientesep.dominio.Emergencia;
import co.tecnomati.clientesep.dominio.Usuario;





import com.google.android.gms.maps.model.LatLng;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocalizacionUtil {

	private LocationManager locManager;
	private onRecibirActualiacionesGPS locListener;
	private AlertDialog alert;
    private Location locationOld;
    private Location location;
	public Context contexto;
	private double latitud;
	private double longitud;
    
	Location localizacion;

	boolean bloqueo = false;
	Usuario miPreferencia;
	Emergencia emergencia;
    ProgressDialog pDialog;
	/**
	 *  Constructor de la Clase
	 * @param ctx contexto de la aplicacion
	 */
	public LocalizacionUtil(Context ctx,Emergencia emergencia,ProgressDialog pdialog) {
		super();
		this.contexto = ctx;
		this.emergencia=emergencia;
		this.pDialog= pdialog;
		   Log.i("conte",contexto.getClass().equals(EnviaFotoActivity.class) +"");
		   Log.i("conte",contexto.getClass().equals(HomeActivity.class) +"");
		if (isActivadoGPS()) {
			comenzarLocalizacion();	
		}else{
			AlertNoGps();
		}
		

	}

	
	
	public void comenzarLocalizacion() {
		Log.e("Entro al comenzar la localizacion", "entro alocoalizar");
		
		
		// Obtenemos la última posición conocida
		localizacion = locManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		// Nos registramos para recibir actualizaciones de la posición
		locListener = new onRecibirActualiacionesGPS();
		
		// reciba actualizaciones del gps 
		// tiempo 0 - distancia 20
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				(LocationListener)	locListener);
		
        
	}
	
	
	/**
	 * 
	 * @return true si el GPS del dispositivo esta activo.
	 */
	public boolean isActivadoGPS() {
		boolean b = false;
		// Obtenemos una referencia al LocationManager
				locManager = (LocationManager) contexto
						.getSystemService(Context.LOCATION_SERVICE);

		if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			b = true;
		}

		return b;
	}

	int numIteracion=0;	
	/**
	 * 
	 * @author joel
	 *
	 *  clase que implementa LocatinListener . para poder recibir actualizaciones
	 *  del GPS. 
	 */
	
	public class onRecibirActualiacionesGPS implements LocationListener {
          
		
		

		public onRecibirActualiacionesGPS() {
			super();
			// TODO Auto-generated constructor stub
		}

		// cuando se carga una nueva posicion de gps
		@Override
		public void onLocationChanged(Location location) {
			 miPreferencia = UsuarioUtil.getAllPreferencias(contexto);
			
		if (numIteracion<3){
			 if(contexto.getClass().equals(HomeActivity.class))
			{
				// enviar emergencia desde el boton 
				 numIteracion++;
				   Log.i("ingreso a evento onchange","numero iteracion "+numIteracion+"  "+location.toString());		 
					 if(isBetterLocation(getOldLocation(), location)) {
					       Log.v("se encontro mejor localizacion",isBetterLocation(getOldLocation(), location)+"");
					       Log.v("esta Location es mejor   ",location.toString());
					    	//abortarActualizacionesGPS=true;			        
					        
					    setOldLocation(location);}
			}else if(contexto.getClass().equals(EnviaFotoActivity.class))
			{
			    // enviar emergencia desde foto
				
				numIteracion++;
				   Log.i("ingreso a evento onchange","numero iteracion "+numIteracion+"  "+location.toString());		 
					 if(isBetterLocation(getOldLocation(), location)) {
					       Log.v("se encontro mejor localizacion",isBetterLocation(getOldLocation(), location)+"");
					       Log.v("esta Location es mejor   ",location.toString());
					    	//abortarActualizacionesGPS=true;			        
					        
					    setOldLocation(location);
					 
					 }
				
			}
					 
		}else{
			numIteracion=0;
			 locManager.removeUpdates((LocationListener)this );
		 Log.i("Se cumplio el numero de iteracion.. Enviado la emergencia...",numIteracion+" ");
		 Log.v("esta Location es la mejor  ",locationOld.toString());	
		 latitud = locationOld.getLatitude();
			longitud =locationOld.getLongitude();
		 // enviar sms de peligro a los contactos
			String coordenadaGPS = "https://www.google.com.ar/maps/@" + latitud
					+ "," + longitud+",17z";
			String Avisopeligro = "Auxilio, estoy en: ";
			
			String textoMensaje = miPreferencia.getUser() + ": " + Avisopeligro
					+ "\n " + coordenadaGPS;
             if(miPreferencia.getContacto1()!="")
			 SMSUtil.enviaSMS(contexto,miPreferencia.getContacto1(),textoMensaje);
             if(miPreferencia.getContacto2()!="")
             SMSUtil.enviaSMS(contexto,miPreferencia.getContacto2(),textoMensaje);
			
			// creo el objeto emergencia con los datos a enviar
			
			emergencia.setLatitud(String.valueOf(latitud));
			emergencia.setLongitud(String.valueOf(longitud));
			
			// creo la tarea asincronica que maneja el envio de los datos al
			// servidor
			

			 EmergenciaUtil eu = new EmergenciaUtil();
		
			TareaEnviarEmergencia enviarEmergencia = new TareaEnviarEmergencia(contexto,emergencia,pDialog);
			enviarEmergencia.execute(String.valueOf(emergencia.getUsid()),
					String.valueOf(emergencia.getSid()),
					emergencia.getLatitud(), emergencia.getLongitud(),
					String.valueOf(emergencia.getEstado()),
					emergencia.getComentarioU(), emergencia.getComentarioS(),
					emergencia.getRutaImagen());
		   	// Stop listening to location updates, also stops providers.
		}
         //  Log.i("locmanager ",locManager.toString());
         //  locListener = null;
		}

		// Para ver el estado del GPS
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		// cuando se activa GPS
		@Override
		public void onProviderEnabled(String provider) {

			// lblEstado.setText("Provider ON");
		}

		// cuando se desabilita el GPS
		@Override
		public void onProviderDisabled(String provider) {

			// lblEstado.setText("Provider OFF");
		}

	}
	

	/**
	 * Muestra el gestor de GPS si no esta activado
	 */
	private void AlertNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
		builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
				.setCancelable(false)
				.setPositiveButton("Si", new DialogInterface.OnClickListener() {
					public void onClick(
							@SuppressWarnings("unused") final DialogInterface dialog,
							@SuppressWarnings("unused") final int id) {
						contexto.startActivity(new Intent(
								android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog,
							@SuppressWarnings("unused") final int id) {
						dialog.cancel();
					}
				});
		alert = builder.create();
		alert.show();
	}

	/**
	 * 
	 * @return un objeto Localizacion de ahi podemos obtener la latitud y
	 *         longitud
	 */
	public Location getLocation() {
		return this.localizacion;
	}

	/**
	 * 
	 * @param latitud
	 *            latitud del comercio
	 * @param longitud
	 *            longitud del comercio
	 * @return devuelve el km de distancia entre un comercio y la plaza belgrano
	 */
	public double getDistanciaenKM(double latitud, double longitud) {
		double distKM;
		Location instLoc = new Location("localizac actualizada");
		LatLng pointC = new LatLng(latitud, longitud);
		instLoc.setLatitude(pointC.latitude);
		instLoc.setLongitude(pointC.longitude);
		distKM = localizacion.distanceTo(instLoc) / 1000;
		DecimalFormat df = new DecimalFormat("##.##");
		df.setMaximumFractionDigits(3);
		df.format(distKM);

		return distKM;

	}

	 public void setOldLocation(Location l)
	 {
		this.locationOld=l; 
	 }
	 public Location getOldLocation()
	 {
		 return locationOld;
	 }
	/**
	* Time difference threshold set for one minute.
	*/
	static final int TIME_DIFFERENCE_THRESHOLD = 1 * 60 * 1000;
	 
	/**
	* Decide if new location is better than older by following some basic criteria.
	* This algorithm can be as simple or complicated as your needs dictate it.
	* Try experimenting and get your best location strategy algorithm.
	* 
	* @param oldLocation Old location used for comparison.
	* @param newLocation Newly acquired location compared to old one.
	* @return If new location is more accurate and suits your criteria more than the old one.
	*/
	boolean isBetterLocation(Location oldLocation, Location newLocation) {
	    // If there is no old location, of course the new location is better.
	    if(oldLocation == null) {
	        return true;
	    }
	 
	    // Check if new location is newer in time.
	    boolean isNewer = newLocation.getTime() > oldLocation.getTime();
	 
	    // Check if new location more accurate. Accuracy is radius in meters, so less is better.
	    boolean isMoreAccurate = newLocation.getAccuracy() < oldLocation.getAccuracy();       
	    if(isMoreAccurate && isNewer) {         
	        // More accurate and newer is always better.         
	        return true;     
	    } else if(isMoreAccurate && !isNewer) {         
	        // More accurate but not newer can lead to bad fix because of user movement.         
	        // Let us set a threshold for the maximum tolerance of time difference.         
	        long timeDifference = newLocation.getTime() - oldLocation.getTime(); 
	 
	        // If time difference is not greater then allowed threshold we accept it.         
	        if(timeDifference > -TIME_DIFFERENCE_THRESHOLD) {
	            return true;
	        }
	    }
	 
	    return false;
	}
	
	/**
	 * Get provider name.
	 * @return Name of best suiting provider.
	 * */
	String getProviderName() {
	    LocationManager locationManager = (LocationManager) contexto
	            .getSystemService(Context.LOCATION_SERVICE);
	 
	    Criteria criteria = new Criteria();
	    criteria.setPowerRequirement(Criteria.POWER_LOW); // Chose your desired power consumption level.
	    criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
	    criteria.setSpeedRequired(true); // Chose if speed for first location fix is required.
	    criteria.setAltitudeRequired(false); // Choose if you use altitude.
	    criteria.setBearingRequired(false); // Choose if you use bearing.
	    criteria.setCostAllowed(true); // Choose if this provider can waste money :-)
	 
	    // Provide your criteria and flag enabledOnly that tells
	    // LocationManager only to return active providers.
	    return locationManager.getBestProvider(criteria, true);
	}
	

}

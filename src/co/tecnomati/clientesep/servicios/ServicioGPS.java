package co.tecnomati.clientesep.servicios;

import co.tecnomati.clientesep.asyncTask.TareaMonitorearUsuario;
import co.tecnomati.clientesep.dominio.Monitoreo;
import co.tecnomati.clientesep.dominio.Usuario;
import co.tecnomati.clientesep.util.UsuarioUtil;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;



public class ServicioGPS extends Service implements LocationListener{
 Context ctx;
public static String TAG_SERVICIO="My Servicio";
	double latitud;
	double longitud;
	boolean gpsActivo;
	TextView texto;
	LocationManager locationManager;
	Location location;
    static final int CANTIDAD_SEGUNDOS_UPDATE_GPS=3;
    static final int MIN_DISTANCIA_UDATE_GPS=0;
    Usuario miPreferencia;
    String eid;
    boolean bandera=false;
    public ServicioGPS()
	{
		super();
	    
     
	}

	
	public ServicioGPS(Context c)
	{
		super();
		
		this.ctx=c;
	}

//	public void setView(View v)
//	{
//		texto=(TextView) v;
//		texto.setText("Coordenadas:"+latitud+","+longitud);	
//	}
	
	public void getLocation()
	{
	  try {
		locationManager=(LocationManager) this.ctx.getSystemService(LOCATION_SERVICE);
		gpsActivo= locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);		
		  
	} catch (Exception e) {
		// TODO: handle exception
	}	
	 if(gpsActivo)
	 {
		 Log.i(TAG_SERVICIO, "GPS_Activo");
		 locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, CANTIDAD_SEGUNDOS_UPDATE_GPS*1000, MIN_DISTANCIA_UDATE_GPS, this);
		 location= locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
		 latitud= location.getLatitude();
		 longitud= location.getLongitude();
		 		 
	 } 
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG_SERVICIO, "onBind");
		
		return null;
	}


	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.i(TAG_SERVICIO, "onCreate");
	


		super.onCreate();
//		ctx= getApplicationContext();
//		Log.i(TAG_SERVICIO, "Contexto:"+ ctx.getClass().toString());
//		
//		SharedPreferences prefs = ctx.getSharedPreferences("Pref_Emergencia",Context.MODE_PRIVATE);
//	//	eid=preferencia.getString("eid", "");
//		eid=prefs.getString("eid", "0");
//		Log.i(TAG_SERVICIO, "ID_Emergencia:"+ eid);
//		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		ctx= getApplication().getApplicationContext();
		Log.i(TAG_SERVICIO, "Contexto:"+ ctx.getClass().toString());
	   
		
	
//		SharedPreferences prefs = getSharedPreferences("Pref_Emergencia",Context.MODE_PRIVATE);		
//		eid=prefs.getString("eid", "0");
		
		
		try{
			eid = intent.getStringExtra("eidd");
			Log.i(TAG_SERVICIO, "onStartCommand_eid:"+ eid);
		}catch(Exception es){
			
			Log.i(TAG_SERVICIO, "onStartCommand_eidFalled:"+ eid);
		}
		
 		//return super.onStartCommand(intent, flags, startId);
//		try{
//Log.i(TAG_SERVICIO, "bandera"+ bandera);
//			Bundle bundle =  intent.getExtras();
//			eid= bundle.getString("eid");			
			
			
//		}catch(Exception ex){
//			Log.i(TAG_SERVICIO, "ERROR_STARTCOMMAND"+ eid);
//		}
			
		
	
		// aqui deberia enviar continuamente las coordenadas GPS al server
		Log.i(TAG_SERVICIO, "onStartCommmand: "+startId);
		//obtengo la posicion 
		getLocation();
		// envio al servidor
		
		
		return START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy() {
		// aqui  deberia parar el envio de datos al server.
		Log.i(TAG_SERVICIO, "onDestroy");

		super.onDestroy();
		
	}


	
	
	// LocationListener
	@Override
	public void onLocationChanged(Location location) {
		// enviar posicion al servidor 
		Log.i(TAG_SERVICIO, "onLocationChanged");
		// obtengo desde preferencia el usuario
		miPreferencia = UsuarioUtil.getAllPreferencias(getApplicationContext());
		// OBTENGO LA LONGITUD LATITUD PRECISION
		double latitud = location.getLatitude();
		double longitud= location.getLongitude();
		float precision= location.getAccuracy();
		Monitoreo monitoreo = new Monitoreo();
		monitoreo.setUsid(Integer.valueOf(miPreferencia.getIdUser()));
		monitoreo.setEid(Integer.valueOf(eid));
		monitoreo.setLatitud(String.valueOf(latitud));
		monitoreo.setLongitud(String.valueOf(longitud));
		monitoreo.setPrecision(String.valueOf(precision));
		
		// enviar registro de monitoreo
        TareaMonitorearUsuario tm = new TareaMonitorearUsuario(ctx, monitoreo);	
		tm.execute(String.valueOf(monitoreo.getUsid()),String.valueOf(monitoreo.getEid()),monitoreo.getLatitud(),monitoreo.getLongitud(),monitoreo.getPrecision());
		
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

}

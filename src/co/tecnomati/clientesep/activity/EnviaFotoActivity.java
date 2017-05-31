package co.tecnomati.clientesep.activity;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import co.tecnomati.clientesep.R;
import co.tecnomati.clientesep.cons.Constantes;
import co.tecnomati.clientesep.dominio.Emergencia;
import co.tecnomati.clientesep.dominio.Usuario;
import co.tecnomati.clientesep.util.EmergenciaUtil;
import co.tecnomati.clientesep.util.LocalizacionUtil;
import co.tecnomati.clientesep.util.UsuarioUtil;

public class EnviaFotoActivity extends ActionBarActivity implements
		android.view.View.OnClickListener, OnItemSelectedListener,
		OnCheckedChangeListener {

	// para la foto
	public static final int FOTO_GALERIA = 2;
	public static final int FOTO_CAMARA = 3;
	public static final int FOTO_CAPTURADA = 1;

	private Uri selectedImage = null;

	EditText txtDescripcion;
	Spinner spnServicio;
	Button btnEnviarEmergencia, btnDesdeGaleria, btnDesdeCamara;
	CheckBox chkFacebook, chkSMS;

	ImageView imgFotoEmergencia;
	int itemServicio=0;
	private ArrayAdapter<CharSequence> adapter;

	boolean publicoFoto = false;
	private ProgressDialog pDialog;

	private String latitud;
	private String longitud;

	LocationManager miLocManager;
	Location locationOld;
	Location location;
	int numIteracion=0;
	Usuario miPreferencia ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enviafoto);
        // relaciono los elementos de layout con la actividad
		txtDescripcion = (EditText) findViewById(R.id.txtDescripcionEmergencia);
		spnServicio = (Spinner) findViewById(R.id.spnServicio);
		btnEnviarEmergencia = (Button) findViewById(R.id.btnAceptarNuser);
		chkFacebook = (CheckBox) findViewById(R.id.chkFacebook);
		chkSMS = (CheckBox) findViewById(R.id.chkSms);
		imgFotoEmergencia = (ImageView) findViewById(R.id.imgFotoCapturada);
		imgFotoEmergencia.setOnClickListener(this);

		btnDesdeCamara = (Button) findViewById(R.id.btnDesdeCamara);
		btnDesdeGaleria = (Button) findViewById(R.id.btnDesdeArchivo);
		btnDesdeCamara.setOnClickListener(this);
		btnDesdeGaleria.setOnClickListener(this);

		// setear un adaptador al spiner Servicio
		adapter = ArrayAdapter.createFromResource(this,
				R.array.ArrayStringServicios,
				android.R.layout.simple_spinner_item);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnServicio.setAdapter(adapter);
		spnServicio.setOnItemSelectedListener(this);

		// evento boton subir emergencia
		btnEnviarEmergencia.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemSelected(AdapterView<?> adap, View view, int position,
			long id) {
		itemServicio = adap.getSelectedItemPosition();
		Log.i("item Servicio", itemServicio + "");
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View boton) {
		if (boton.equals(btnEnviarEmergencia)) {
          
			// progreso
			ProgressDialog pDialog = new ProgressDialog(this);
			pDialog.setMessage("Enviando Emergencia...");
			pDialog.setCancelable(true);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.show();
			// enviar la emergencia.
            
			
			// capturar los datos de usuario desde preferencias
		 miPreferencia = UsuarioUtil.getAllPreferencias(this);
			Log.i("mis preferenncias", miPreferencia.toString());
			Log.i("mandar emergencia con foto", "entro a evento envio de foto");
		
			// vibrara segun la preferencia del usuario
			if (miPreferencia.isVibrar()) {

				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(2000);
			}

			btnEnviarEmergencia.setEnabled(false);

			
			Emergencia emergencia = new Emergencia();
			emergencia.setUsid(Integer.valueOf(miPreferencia.getIdUser()));
			
			if (itemServicio==0) {
				emergencia.setSid(Constantes.SERVICIO_EMERGENCIA_POLICIA);	
			}else{
				emergencia.setSid(itemServicio);
			}
			
			//emergencia.setLatitud(String.valueOf(latitud));
			//emergencia.setLongitud(String.valueOf(longitud));
			emergencia.setEstado(Constantes.ESTADO_EMERGENCIA_PENDIENTE);

			 emergencia.setComentarioU(txtDescripcion.getText().toString());

			
            LocalizacionUtil lcu = new LocalizacionUtil(this,emergencia,pDialog);
			//lcu.comenzarLocalizacion();
			// capturar los datos de geolocalizacion

			// obtengo el servicio
//			miLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			// instaceo la clase privada que es la que implementara los eventos
			// producidos por el gps
//			LocationListener miLocListetener = new MiLocationListener();

			// a traves de este metodo se guardan la latitud y longitud en las
			// variables globales
//			obtenerUltimaposRegistrada(miLocManager, miLocListetener);
             
               	// Stop listening to location updates, also stops providers.
            //	miLocManager.removeUpdates(miLocListetener);
		           	
            
			
//			// enviar sms de peligro a los contactos
//			String coordenadaGPS = "https://www.google.com.ar/maps/@" + latitud
//					+ "," + longitud+",17z";
//			String Avisopeligro = "Auxilio, estoy en: ";
//			String textoMensaje = miPreferencia.getUser() + ": " + Avisopeligro
//					+ "\n " + coordenadaGPS;
//             if(miPreferencia.getContacto1()!="")
//			 enviaSMS(miPreferencia.getContacto1(),textoMensaje);
//             if(miPreferencia.getContacto1()!="")
//             enviaSMS(miPreferencia.getContacto2(),textoMensaje);
//			
//			// creo el objeto emergencia con los datos a enviar
//			
//			
//			Emergencia emergencia = new Emergencia();
//			emergencia.setUsid(Integer.valueOf(miPreferencia.getIdUser()));
//			
//			if (itemServicio==0) {
//				emergencia.setSid(Constantes.SERVICIO_EMERGENCIA_POLICIA);	
//			}else{
//				emergencia.setSid(itemServicio);
//			}
//			
//			emergencia.setLatitud(latitud);
//			emergencia.setLongitud(longitud);
//			emergencia.setEstado(Constantes.ESTADO_EMERGENCIA_PENDIENTE);
//
//			 emergencia.setComentarioU(txtDescripcion.getText().toString());
//			// emergencia.setComentarioS("hola");
//			// emergencia.setRutaImagen("holas");
//
//			// creo la tarea asincronica que maneja el envio de los datos al
//			// servidor
//			TareaEnviarEmergencia enviarEmergencia = new TareaEnviarEmergencia();
//			enviarEmergencia.execute(String.valueOf(emergencia.getUsid()),
//					String.valueOf(emergencia.getSid()),
//					emergencia.getLatitud(), emergencia.getLongitud(),
//					String.valueOf(emergencia.getEstado()),
//					emergencia.getComentarioU(), emergencia.getComentarioS(),
//					emergencia.getRutaImagen());
           
			
		} else if (boton.equals(btnDesdeGaleria)) {
			// capturar la foto desde la galeria
			Intent intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
			startActivityForResult(intent, FOTO_GALERIA);

		} else if (boton.equals(btnDesdeCamara)) {
			// captura foto desde la camara
			Intent intentSacarFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intentSacarFoto, FOTO_CAMARA);

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		// if (requestCode==FOTO_GALERIA){

		if (data != null) {
			publicoFoto = true;
			selectedImage = data.getData(); // la uri donde se encuentra foto

			InputStream is;
			try {
				// BitmapFactory.decodeStream(bis, outPadding, opts)
				is = getContentResolver().openInputStream(selectedImage);
				BufferedInputStream bis = new BufferedInputStream(is);
				Bitmap bitmap = BitmapFactory.decodeStream(bis);
				// hago visible el espacio para la imagen
				// imgCmt.setVisibility(View.VISIBLE);
				imgFotoEmergencia.setImageBitmap(bitmap);
				Log.v("la foto emergencia se encuentra en la ruta:",
						selectedImage.getPath());
			} catch (FileNotFoundException e) {
			}

		}
		// }

	}

	/**
	 * Obtiene la ultima posicion registrada por los servidores de localizacion
	 * , en caso de que no tenga registrada , activara el gps para buscar una
	 * posicion
	 * 
	 * @param miLocManager
	 * @param miLocListetener
	 */
//	private void obtenerUltimaposRegistrada(LocationManager miLocManager,
//			LocationListener miLocListetener) {
//		android.location.Location location = miLocManager
//				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//		if (location == null) {
//			location = miLocManager
//					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//		}
//		if (location != null) {
//			latitud = String.valueOf(location.getLatitude());
//			longitud = String.valueOf(location.getLongitude());
//		} else {
//			long minTime = 2 * 1000; // Minimum time interval for update in seconds, i.e. 5 seconds.
//			long minDistance = 10; // Minimum distance change for update in meters, i.e. 10 meters
//			miLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//					minTime, minDistance, miLocListetener);
//		}
//
//	}

	
//	private void obtenerUltimaposRegistrada(LocationManager miLocManager,
//			LocationListener miLocListetener) {
//		
//		 location = miLocManager
//				.getLastKnownLocation(getProviderName());
//		if (location == null) {
//			location = miLocManager
//					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//		}
//		if (location != null) {
//			latitud = String.valueOf(location.getLatitude());
//			longitud = String.valueOf(location.getLongitude());
//		} 
//	//	else {
//			long minTime = 3 * 1000; // Minimum time interval for update in seconds, i.e. 5 seconds.
//			long minDistance = 5; // Minimum distance change for update in meters, i.e. 10 meters
////			miLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//			miLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//			minTime, minDistance, miLocListetener);
//	//	}
//
//	}

	/**
	 * Get provider name.
	 * @return Name of best suiting provider.
	 * */
//	String getProviderName() {
//	    LocationManager locationManager = (LocationManager) this
//	            .getSystemService(Context.LOCATION_SERVICE);
//	 
//	    Criteria criteria = new Criteria();
//	    criteria.setPowerRequirement(Criteria.POWER_LOW); // Chose your desired power consumption level.
//	    criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
//	    criteria.setSpeedRequired(true); // Chose if speed for first location fix is required.
//	    criteria.setAltitudeRequired(false); // Choose if you use altitude.
//	    criteria.setBearingRequired(false); // Choose if you use bearing.
//	    criteria.setCostAllowed(false); // Choose if this provider can waste money :-)
//	 
//	    // Provide your criteria and flag enabledOnly that tells
//	    // LocationManager only to return active providers.
//	    return locationManager.getBestProvider(criteria, true);
//	}
//	
	/**
	* Make use of location after deciding if it is better than previous one.
	*
	* @param location Newly acquired location.
	*/
	//void doWorkWithNewLocation(Location location) {
	//    if(isBetterLocation(getOldLocation(), location)) {
	//    	setOldLocation(location);
	        	
	    	// If location is better, do some user preview.
	      //  Toast.makeText(MainActivity.this,
	       //                 "Better location found: " + provider, Toast.LENGTH_SHORT)
	       //                 .show();
	  //  }
	 
	    
//	}
//	 public void setOldLocation(Location l)
//	 {
//		this.locationOld=l; 
//	 }
//	 public Location getOldLocation()
//	 {
//		 return locationOld;
//	 }
//	/**
//	* Time difference threshold set for one minute.
//	*/
//	static final int TIME_DIFFERENCE_THRESHOLD = 1 * 60 * 1000;
//	 
//	/**
//	* Decide if new location is better than older by following some basic criteria.
//	* This algorithm can be as simple or complicated as your needs dictate it.
//	* Try experimenting and get your best location strategy algorithm.
//	* 
//	* @param oldLocation Old location used for comparison.
//	* @param newLocation Newly acquired location compared to old one.
//	* @return If new location is more accurate and suits your criteria more than the old one.
//	*/
//	boolean isBetterLocation(Location oldLocation, Location newLocation) {
//	    // If there is no old location, of course the new location is better.
//	    if(oldLocation == null) {
//	        return true;
//	    }
//	 
//	    // Check if new location is newer in time.
//	    boolean isNewer = newLocation.getTime() > oldLocation.getTime();
//	 
//	    // Check if new location more accurate. Accuracy is radius in meters, so less is better.
//	    boolean isMoreAccurate = newLocation.getAccuracy() < oldLocation.getAccuracy();       
//	    if(isMoreAccurate && isNewer) {         
//	        // More accurate and newer is always better.         
//	        return true;     
//	    } else if(isMoreAccurate && !isNewer) {         
//	        // More accurate but not newer can lead to bad fix because of user movement.         
//	        // Let us set a threshold for the maximum tolerance of time difference.         
//	        long timeDifference = newLocation.getTime() - oldLocation.getTime(); 
//	 
//	        // If time difference is not greater then allowed threshold we accept it.         
//	        if(timeDifference > -TIME_DIFFERENCE_THRESHOLD) {
//	            return true;
//	        }
//	    }
//	 
//	    return false;
//	}
	
	
	// metodo que implementa el gps
//	private class MiLocationListener implements LocationListener {
//
//		@Override
//		public void onLocationChanged(Location loc) {
//			//7latitud = String.valueOf(loc.getLatitude());
//			//longitud = String.valueOf(loc.getLongitude());
//
//			//Toast.makeText(getApplicationContext(), latitud + " " + longitud,
//			//		Toast.LENGTH_SHORT).show();
//			numIteracion++;
//		   Log.i("ingreso a evento onchange",loc.toString());		 
//			 if(isBetterLocation(getOldLocation(), location)) {
//			         
//			    	//abortarActualizacionesGPS=true;			        
//			        
//			    setOldLocation(location);
//			 
//			 }
//			 if (numIteracion==3){
//				 Log.i("Se cumplio el numero de iteracion.. Enviado la emergencia...",numIteracion+" ");
//					latitud = String.valueOf(location.getLatitude());
//					longitud = String.valueOf(location.getLongitude());
//				 // enviar sms de peligro a los contactos
//					String coordenadaGPS = "https://www.google.com.ar/maps/@" + latitud
//							+ "," + longitud+",17z";
//					String Avisopeligro = "Auxilio, estoy en: ";
//					String textoMensaje = miPreferencia.getUser() + ": " + Avisopeligro
//							+ "\n " + coordenadaGPS;
//		             if(miPreferencia.getContacto1()!="")
//					 enviaSMS(miPreferencia.getContacto1(),textoMensaje);
//		             if(miPreferencia.getContacto1()!="")
//		             enviaSMS(miPreferencia.getContacto2(),textoMensaje);
//					
//					// creo el objeto emergencia con los datos a enviar
//					
//					
//					Emergencia emergencia = new Emergencia();
//					emergencia.setUsid(Integer.valueOf(miPreferencia.getIdUser()));
//					
//					if (itemServicio==0) {
//						emergencia.setSid(Constantes.SERVICIO_EMERGENCIA_POLICIA);	
//					}else{
//						emergencia.setSid(itemServicio);
//					}
//					
//					emergencia.setLatitud(latitud);
//					emergencia.setLongitud(longitud);
//					emergencia.setEstado(Constantes.ESTADO_EMERGENCIA_PENDIENTE);
//
//					 emergencia.setComentarioU(txtDescripcion.getText().toString());
//					// emergencia.setComentarioS("hola");
//					// emergencia.setRutaImagen("holas");
//
//					// creo la tarea asincronica que maneja el envio de los datos al
//					// servidor
//					TareaEnviarEmergencia enviarEmergencia = new TareaEnviarEmergencia();
//					enviarEmergencia.execute(String.valueOf(emergencia.getUsid()),
//							String.valueOf(emergencia.getSid()),
//							emergencia.getLatitud(), emergencia.getLongitud(),
//							String.valueOf(emergencia.getEstado()),
//							emergencia.getComentarioU(), emergencia.getComentarioS(),
//							emergencia.getRutaImagen());
//
//
//				   	// Stop listening to location updates, also stops providers.
//	               miLocManager.removeUpdates(this);
//			    
//			 }
//			 
//		}
//
//		@Override
//		public void onProviderDisabled(String arg0) {
//			// TODO Auto-generated method stub
//
//		}
//
//		@Override
//		public void onProviderEnabled(String arg0) {
//			// TODO Auto-generated method stub
//
//		}
//
//		@Override
//		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
//			// TODO Auto-generated method stub
//     
//		}
//
//	}

//	// Tarea Asíncrona para llamar al WS de inserción en segundo plano
//	private class TareaEnviarEmergencia extends
//			AsyncTask<String, Integer, Boolean> {
//
//		// private int idCli;
//		String idCli;
//
//		protected Boolean doInBackground(String... params) {
//			Log.e("llego hasta aqui", "entro");
//			boolean resul = true;
//
//			HttpClient httpClient = new DefaultHttpClient();
//
//			HttpPost post = new HttpPost(Constantes.URL_ENVIAR_EMERGENCIA);
//			post.setHeader("content-type", "application/json");
//
//			try {
//				// Construimos el objeto emergencia en formato JSON
//				JSONObject dato = new JSONObject();
//
//				dato.put("usid", params[0]);
//				dato.put("sid", params[1]);
//				dato.put("latitud", params[2]);
//				dato.put("longitud", params[3]);
//				dato.put("estado", params[4]);
//				dato.put("comentariou", params[5]);
//				dato.put("comentarios", params[6]);
//				dato.put("imagen", params[7]);
//
//				StringEntity entity = new StringEntity(dato.toString());
//				post.setEntity(entity);
//				HttpResponse resp = httpClient.execute(post);
//
//				String respStr = EntityUtils.toString(resp.getEntity());
//				// JSONObject respJSON = new JSONObject(respStr);
//				Log.e("respuSTr", respStr + "");
//				idCli = respStr.toString();
//				Log.e("id de la emergencia", idCli + "");
//				if (respStr.equals("0"))
//					resul = false;
//
//			} catch (Exception ex) {
//				Log.e("ServicioRest", "Error!", ex);
//				resul = false;
//			}
//
//			return resul;
//		}
//
//		protected void onPostExecute(Boolean result) {
//
//			if (result) {
//				// idUser.setText("Insertado OK.  : " + idCli);
//				pDialog.dismiss();
//				Toast.makeText(getApplicationContext(), "Resultado ok  ",
//						Toast.LENGTH_LONG).show();
//
//			}
//		}
//	}
//
//
//	/**
//	 * envia SMS a los contactos registrados en preferencias de ususario
//	 */
//public void enviaSMS(String tel,String mje){
//		
//		// incio rastrear y confiramar msj de entrega
//				String SENT_SMS_ACTION = "SENT_SMS_ACTION";
//				String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
//
//				// Create the sentIntent parameter
//				Intent sentIntent = new Intent(SENT_SMS_ACTION);
//				PendingIntent sentPI = PendingIntent.getBroadcast(
//						getApplicationContext(), 0, sentIntent, 0);
//
//				// Create the deliveryIntent parameter
//				Intent deliveryIntent = new Intent(DELIVERED_SMS_ACTION);
//				PendingIntent deliverPI = PendingIntent.getBroadcast(
//						getApplicationContext(), 0, deliveryIntent, 0);
//
//				// Register the Broadcast Receivers
//				registerReceiver(new BroadcastReceiver() {
//					@Override
//					public void onReceive(Context _context, Intent _intent) {
//						switch (getResultCode()) {
//						case Activity.RESULT_OK:
//							// [… send success actions … ]; break;
//							Toast.makeText(getApplicationContext(),
//									"Mensaje Enviado Satifactoriamente a ",
//									Toast.LENGTH_SHORT).show();
//							break;
//						case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//							// [… generic failure actions … ]; break;
//							Toast.makeText(
//									getApplicationContext(),
//									"Problemas al Enviar el mje- no se pudo enviar el mje",
//									Toast.LENGTH_LONG).show();
//							break;
//
//						case SmsManager.RESULT_ERROR_RADIO_OFF:
//							// [… radio off failure actions … ]; break;
//							Toast.makeText(getApplicationContext(),
//									"No hay Red - no se pudo entregar el mje",
//									Toast.LENGTH_LONG).show();
//							break;
//
//						case SmsManager.RESULT_ERROR_NULL_PDU:
//							// [… null PDU failure actions … ]; break;
//							Toast.makeText(getApplicationContext(),
//									"Error PDU- no se pude entregar el mje",
//									Toast.LENGTH_LONG).show();
//							break;
//
//						}
//					}
//				}, new IntentFilter(SENT_SMS_ACTION));
//				registerReceiver(new BroadcastReceiver() {
//					@Override
//					public void onReceive(Context _context, Intent _intent) {
//						// [… SMS delivered actions … ]
//						Log.i("esta accion", "se ejecuta una vez entregado el mje ");
//
//					}
//				}, new IntentFilter(DELIVERED_SMS_ACTION));
//
//				// --------- fin confirmar mje entrega-----------
//
//		   SmsManager sms = SmsManager.getDefault();
//		   sms.sendTextMessage(tel.toString(), null, mje , sentPI, deliverPI);
//		
//	}

}
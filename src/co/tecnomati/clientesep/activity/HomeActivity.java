package co.tecnomati.clientesep.activity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import co.tecnomati.clientesep.R;
import co.tecnomati.clientesep.asyncTask.TareaEnviarEmergencia;
import co.tecnomati.clientesep.cons.Constantes;
import co.tecnomati.clientesep.dominio.Emergencia;
import co.tecnomati.clientesep.dominio.Usuario;
import co.tecnomati.clientesep.servicios.ServicioGPS;
import co.tecnomati.clientesep.util.EmergenciaUtil;
import co.tecnomati.clientesep.util.InternetUtil;
import co.tecnomati.clientesep.util.LocalizacionUtil;
import co.tecnomati.clientesep.util.UsuarioUtil;

public class HomeActivity extends Activity implements OnClickListener {

	ImageButton btnImgEnviarFoto, btnImgExplorarZona, btnImgConfiguracion;
	Button btnPanico;
	String latitud, longitud;
	ProgressDialog pDialog;
	Usuario usuario;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// vincular la activity con el layout correspondiente
		setContentView(R.layout.activity_home);

		// vincular con los botones de la vista
		btnImgConfiguracion = (ImageButton) findViewById(R.id.btnImgConfigurar);
		btnImgEnviarFoto = (ImageButton) findViewById(R.id.btnImgFoto);
		btnImgExplorarZona = (ImageButton) findViewById(R.id.btnImgExplorar);

		btnPanico = (Button) findViewById(R.id.btnPeligro);

		// escuchadores de evento onclick
		btnImgExplorarZona.setOnClickListener(this);
		btnImgConfiguracion.setOnClickListener(this);
		btnImgEnviarFoto.setOnClickListener(this);
		btnPanico.setOnClickListener(this);

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

	// CUANDO SE HACE CLIK SOBRE UN BOTON
	@Override
	public void onClick(View boton) {
		if (boton.equals(btnImgConfiguracion)) {

			Intent intentActivityConfigurar = new Intent(this,
					ListaConfiguracionActivity.class);
			// debo pasasr el usuario
			// intentHome.putExtra("usuario", usuario.getText().toString());
			// getDatosdePreferencias();

			startActivity(intentActivityConfigurar);

		} else if (boton.equals(btnImgEnviarFoto)) {

			Intent intentActivityEnviarFoto = new Intent(this,
					EnviaFotoActivity.class);
			startActivity(intentActivityEnviarFoto);

		} else if (boton.equals(btnImgExplorarZona)) {

			if (InternetUtil.isConnectingToInternet(this)) {
				Intent intentActivityEplorarZona = new Intent(this,
						ExplorarActivity.class);
				intentActivityEplorarZona.putExtra("miEmergencia", false);
				startActivity(intentActivityEplorarZona);

			} else {
				Toast.makeText(this, "Debes estar conectado a Internet",
						Toast.LENGTH_LONG).show();
			}

		} else if (boton.equals(btnPanico)) {

			// process boton panico
			// capturar los datos de usuario desde preferencias
			usuario = UsuarioUtil.getAllPreferencias(this);

			Log.i("ento a btn panico", "entro a evento del boton panico");
			btnPanico.setBackgroundResource(R.drawable.ic_peligroactivado);
			// vibrara segun la preferencia del usuario
			if (usuario.isVibrar()) {
				vibrarTelefono(2000);
			}

			btnPanico.setEnabled(false);

			// progreso
			pDialog = new ProgressDialog(this);
			pDialog.setMessage("Enviando Emergencia...");
			pDialog.setCancelable(true);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.show();

			// capturar los datos de geolocalizacion
			   
			// obtengo el servicio
//			LocationManager miLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//			// instaceo la clase privada que es la que implementara los eventos
//			// producidos por el gps
//			LocationListener miLocListetener = new MiLocationListener();
//
//			// a traves de este metodo se guardan la latitud y longitud en las
//			// variables globales
//			obtenerUltimaposRegistrada(miLocManager, miLocListetener);

			// enviar sms de peligro a los contactos
//			String coordenadaGPS = "https://www.google.com.ar/maps/@" + latitud
//					+ "," + longitud+",17z";
//			String Avisopeligro = "Auxilio, estoy en: ";
//			String textoMensaje = usuario.getUser() + ": " + Avisopeligro
//					+ "\n " + coordenadaGPS;
//             if(usuario.getContacto1().toString()!=""){
//			 enviaSMS2(usuario.getContacto1().toString(),textoMensaje);}
//             if(usuario.getContacto2().toString()!=""){
//			 enviaSMS2(usuario.getContacto2().toString(),textoMensaje);}

			// enviar señal de peligro al servidor

			// creo el objeto emergencia con los datos a enviar
			Emergencia emergencia = new Emergencia();
			emergencia.setUsid(Integer.valueOf(usuario.getIdUser()));
			emergencia.setSid(Constantes.SERVICIO_EMERGENCIA_POLICIA);
//			emergencia.setLatitud(latitud);
//			emergencia.setLongitud(longitud);
			emergencia.setEstado(Constantes.ESTADO_EMERGENCIA_PENDIENTE);
			emergencia.setPrecision(1000);
			emergencia.setPrecision_b(1000);
			emergencia.setComentarioU("Aviso Emergente");
			// emergencia.setComentarioS("hola");
			// emergencia.setRutaImagen("holas");

			// creo la tarea asincronica que maneja el envio de los datos al
			// servidor
//			TareaEnviarEmergencia enviarEmergencia = new TareaEnviarEmergencia();
//			enviarEmergencia.execute(String.valueOf(emergencia.getUsid()),
//					String.valueOf(emergencia.getSid()),
//					emergencia.getLatitud(), emergencia.getLongitud(),
//					String.valueOf(emergencia.getEstado()),
//					emergencia.getComentarioU(), emergencia.getComentarioS(),
//					emergencia.getRutaImagen());
		//  LocalizacionUtil lcu = new LocalizacionUtil(this,emergencia,pDialog);
		    
		  //EmergenciaUtil eu = new EmergenciaUtil();			
			TareaEnviarEmergencia enviarEmergencia = new TareaEnviarEmergencia(this,emergencia,pDialog);
		
			enviarEmergencia.execute(String.valueOf(emergencia.getUsid()),
					String.valueOf(emergencia.getSid()),
					emergencia.getLatitud(), emergencia.getLongitud(),
					String.valueOf(emergencia.getEstado()),
					emergencia.getComentarioU(), emergencia.getComentarioS(),
					emergencia.getRutaImagen(),String.valueOf(emergencia.getPrecision()),String.valueOf(emergencia.getPrecision_b()));
			
			// Ejecuta el servicio que envia la localizacion del usuario constantemente.
		//	Intent i = new Intent(this,ServicioGPS.class);
		//    startService(i);
			   
			  
		}

	}

	/**
	 * hace vibrar el telefono la cantidad de tiempo indicado en el argunmento
	 * 
	 * @param i
	 *            indica la cantidad de milisegundo q vibrara el telefono
	 */
	private void vibrarTelefono(int i) {
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(i);

	}

		
	public void enviaSMS2(String tel,String mje){
		
		// incio rastrear y confiramar msj de entrega
				String SENT_SMS_ACTION = "SENT_SMS_ACTION";
				String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";

				// Create the sentIntent parameter
				Intent sentIntent = new Intent(SENT_SMS_ACTION);
				PendingIntent sentPI = PendingIntent.getBroadcast(
						getApplicationContext(), 0, sentIntent, 0);

				// Create the deliveryIntent parameter
				Intent deliveryIntent = new Intent(DELIVERED_SMS_ACTION);
				PendingIntent deliverPI = PendingIntent.getBroadcast(
						getApplicationContext(), 0, deliveryIntent, 0);

				// Register the Broadcast Receivers
				registerReceiver(new BroadcastReceiver() {
					@Override
					public void onReceive(Context _context, Intent _intent) {
						switch (getResultCode()) {
						case Activity.RESULT_OK:
							// [… send success actions … ]; break;
							Toast.makeText(getApplicationContext(),
									"Mensaje Enviado Satifactoriamente a ",
									Toast.LENGTH_SHORT).show();
							break;
						case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
							// [… generic failure actions … ]; break;
							Toast.makeText(
									getApplicationContext(),
									"Problemas al Enviar el mje- no se pudo enviar el mje",
									Toast.LENGTH_LONG).show();
							break;

						case SmsManager.RESULT_ERROR_RADIO_OFF:
							// [… radio off failure actions … ]; break;
							Toast.makeText(getApplicationContext(),
									"No hay Red - no se pudo entregar el mje",
									Toast.LENGTH_LONG).show();
							break;

						case SmsManager.RESULT_ERROR_NULL_PDU:
							// [… null PDU failure actions … ]; break;
							Toast.makeText(getApplicationContext(),
									"Error PDU- no se pude entregar el mje",
									Toast.LENGTH_LONG).show();
							break;

						}
					}
				}, new IntentFilter(SENT_SMS_ACTION));
				registerReceiver(new BroadcastReceiver() {
					@Override
					public void onReceive(Context _context, Intent _intent) {
						// [… SMS delivered actions … ]
						Log.i("esta accion", "se ejecuta una vez entregado el mje ");

					}
				}, new IntentFilter(DELIVERED_SMS_ACTION));

				// --------- fin confirmar mje entrega-----------

		   SmsManager sms = SmsManager.getDefault();
		   sms.sendTextMessage(tel.toString(), null, mje , sentPI, deliverPI);
		
	}
	

	/**
	 * envia SMS a los contactos registrados en preferencias de ususario
	 */
	private void enviarSMS(String contacto1) {
		// String latitud = "-24.2581915";
		// String longitud = "-65.2134156,16z";
		String coordenadaGPS = "https://www.google.com.ar/maps/@" + latitud
				+ "," + longitud;
		String Avisopeligro = "Estoy en peligro ayudame , aquí me encuentro";
		String textoMensaje = usuario.getUser() + ": " + Avisopeligro
				+ "\n " + coordenadaGPS;
		Log.i("contacto 1", contacto1);
		Log.i("contacto 1", textoMensaje);

		// incio rastrear y confiramar msj de entrega
		String SENT_SMS_ACTION = "SENT_SMS_ACTION";
		String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";

		// Create the sentIntent parameter
		Intent sentIntent = new Intent(SENT_SMS_ACTION);
		PendingIntent sentPI = PendingIntent.getBroadcast(
				getApplicationContext(), 0, sentIntent, 0);

		// Create the deliveryIntent parameter
		Intent deliveryIntent = new Intent(DELIVERED_SMS_ACTION);
		PendingIntent deliverPI = PendingIntent.getBroadcast(
				getApplicationContext(), 0, deliveryIntent, 0);

		// Register the Broadcast Receivers
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context _context, Intent _intent) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					// [… send success actions … ]; break;
					Toast.makeText(getApplicationContext(),
							"Mensaje Enviado Satifactoriamente a ",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					// [… generic failure actions … ]; break;
					Toast.makeText(
							getApplicationContext(),
							"Problemas al Enviar el mje- no se pudo enviar el mje",
							Toast.LENGTH_LONG).show();
					break;

				case SmsManager.RESULT_ERROR_RADIO_OFF:
					// [… radio off failure actions … ]; break;
					Toast.makeText(getApplicationContext(),
							"No hay Red - no se pudo entregar el mje",
							Toast.LENGTH_LONG).show();
					break;

				case SmsManager.RESULT_ERROR_NULL_PDU:
					// [… null PDU failure actions … ]; break;
					Toast.makeText(getApplicationContext(),
							"Error PDU- no se pude entregar el mje",
							Toast.LENGTH_LONG).show();
					break;

				}
			}
		}, new IntentFilter(SENT_SMS_ACTION));
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context _context, Intent _intent) {
				// [… SMS delivered actions … ]
				Log.i("esta accion", "se ejecuta una vez entregado el mje ");

			}
		}, new IntentFilter(DELIVERED_SMS_ACTION));

		// --------- fin confirmar mje entrega-----------

		// se envia mje con los eventos senpi deliverpi para rastrear y
		// confirmar el mje de entrega
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(contacto1, null, textoMensaje, sentPI, deliverPI);

	}

	/**
	 * 
	 * @return el numero de telefono del celular Importante : si el dispositivo
	 *         no cuenta registrado el numero en el SIM este metodoo retorna
	 *         null. Para saber si esta o no registrado en el SIM. ir: ajuste ->
	 *         acerca del telefono -> estado -> mi numero de telefono.
	 * 
	 */
	private String getNumeroTelefono() {
		TelephonyManager mTelephonyManager;
		mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		return mTelephonyManager.getLine1Number();
	}

	/**
	 * obtiene las preferencias configuradas por el usuario en la opcion
	 * preferencias. las misma se guardan en variables de la activity
	 */
	// private void getDatosdePreferencias() {
	// SharedPreferences preferencia =
	// getSharedPreferences("co.tecnomati.clientesep", MODE_PRIVATE);
	// SharedPreferences preferencia = PreferenceManager
	// .getDefaultSharedPreferences(this);
	// idUser= preferencia.getString("id", "");
	// user = preferencia.getString("usuario", "");
	// telefono = preferencia.getString("telefono", "");
	// contacto1 = preferencia.getString("contacto1", "");
	// contacto2 = preferencia.getString("contacto2", "");
	// vibrar = preferencia.getBoolean("vibrar", true);
	// Log.i("Datos de Preferencias", "Datos de usuario: " + user + " "
	// + telefono + " " + contacto1 + " " + contacto2);
	// }

	// Tarea Asíncrona para llamar al WS de inserción en segundo plano
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
//				dato.put("sid",  params[1]);
//				dato.put("latitud", params[2]);
//				dato.put("longitud", params[3]);
//				dato.put("estado", params[4]);
//				dato.put("comentariou", params[5]);
//				dato.put("comentarios", params[6]);
//				dato.put("imagen",params[7]);
//				
//			//	dato.put("usid", (String)params[0]);
//			//	dato.put("sid", (String) params[1]);
//			//	dato.put("latitud",(String) params[2]);
//			//	dato.put("longitud",(String) params[3]);
//			//	dato.put("estado",(String) params[4]);
//			//	dato.put("comentariou", (String)params[5]);
//			//	dato.put("comentarios",(String) params[6]);
//			//	dato.put("imagen",(String) params[7]);
//			//	kdato.put("fecha",(Date) params[8]);
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

	// ------------ metodo que implementa el
	// gps-----------------------------------------------------------
	private class MiLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {
			latitud = String.valueOf(loc.getLatitude());
			longitud = String.valueOf(loc.getLongitude());

			Toast.makeText(getApplicationContext(), latitud + " " + longitud,
					Toast.LENGTH_SHORT).show();

		}

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * Obtiene la ultima posicion registrada por los servidores de localizacion
	 * , en caso de que no tenga registrada , activara el gps para buscar una
	 * posicion
	 * 
	 * @param miLocManager
	 * @param miLocListetener
	 */
	private void obtenerUltimaposRegistrada(LocationManager miLocManager,
			LocationListener miLocListetener) {
		android.location.Location location = miLocManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location == null) {
			location = miLocManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		if (location != null) {
			latitud = String.valueOf(location.getLatitude());
			longitud = String.valueOf(location.getLongitude());
		} else {
			miLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					2000, 0, miLocListetener);
		}

	}

}

package co.tecnomati.clientesep.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import co.tecnomati.clientesep.R;
import co.tecnomati.clientesep.cons.Constantes;
import co.tecnomati.clientesep.dominio.Emergencia;
import co.tecnomati.clientesep.util.EmergenciaUtil;
import co.tecnomati.clientesep.util.InternetUtil;
import co.tecnomati.clientesep.util.ItemListEmergenciaAdapter;
import co.tecnomati.clientesep.util.Lista_itemConfiguracion;
import co.tecnomati.clientesep.util.UsuarioUtil;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class ExplorarActivity extends android.support.v4.app.FragmentActivity
		implements OnClickListener {

	private GoogleMap mapa;
	private int vista = 0;
	double latitudPalpala = -24.264804;
	double longitudPalpala = -65.211849;
	LatLng palpala;

	boolean cargadoListaEmergencia = false;

	int op_Busqueda = 0; // 0-todos -- los otros con las constantes que tiene

	private ListView lista;

	private List<Emergencia> listaEmergencia = new ArrayList<Emergencia>();

	ArrayList<Lista_itemConfiguracion> datos = new ArrayList<Lista_itemConfiguracion>();

	private ItemListEmergenciaAdapter adaptador;

	private CameraUpdate mCamera; // CameraUpdate nos permite el movimiento de
									// la camara.

	ProgressDialog pDialog;

	boolean miEmergencia = false;// determina si cargara mis emergencias o de
									// todos

	ImageButton btnPolice, btnBombero, btnHospital, btnTodos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_explorar);

		// obtengo los botones del include este layout
		btnPolice = (ImageButton) findViewById(R.id.imgBtnPoliceSearch);
		btnPolice.setOnClickListener(this);
		btnBombero = (ImageButton) findViewById(R.id.imgBtnBomberoSearch);
		btnBombero.setOnClickListener(this);
		btnHospital = (ImageButton) findViewById(R.id.imgBtnHospitalSearch);
		btnHospital.setOnClickListener(this);
		btnTodos = (ImageButton) findViewById(R.id.imgBtnUpdateSearch);
		btnTodos.setOnClickListener(this);

		// determina si carga mis emergencias solamente
		miEmergencia = getIntent().getExtras().getBoolean("miEmergencia");

		Resources res = getResources();

		TabHost tabs = (TabHost) findViewById(android.R.id.tabhost);
		tabs.setup();

		TabHost.TabSpec spec = tabs.newTabSpec("tab_mapa");
		spec.setContent(R.id.tab1);
		spec.setIndicator("MAPA",
				res.getDrawable(android.R.drawable.ic_dialog_map));
		tabs.addTab(spec);

		spec = tabs.newTabSpec("tab_lista");
		spec.setContent(R.id.tab2);
		spec.setIndicator("LISTA",
				res.getDrawable(android.R.drawable.ic_menu_sort_alphabetically));
		tabs.addTab(spec);

		// indico q tab aparecera por defecto 0 , 1...
		tabs.setCurrentTab(0);

		pDialog = new ProgressDialog(this);
		pDialog.setMessage("Obteniendo Informacion...");
		pDialog.setCancelable(false);
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.show();
		if (InternetUtil.isConnectingToInternet(this)) {
			TareaListarEmergencia tlistarEmergencia = new TareaListarEmergencia();
			tlistarEmergencia.execute();
			Log.i("carga emergencia del servidor","1 ra vez- connectado a internet");
			Log.i("tamaño de la lista ", listaEmergencia.size()+" primera vez");
		} else {
			Toast.makeText(this, "Debes conectarte a una red",
					Toast.LENGTH_SHORT).show();
			pDialog.dismiss();
		}

		tabs.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				if (tabId == "tab_lista") {
					Log.i("ttab", "tab lista");

					if (!cargadoListaEmergencia) {
						Log.i("carga emergencias", "el tamño de la lista es 0");
						TareaListarEmergencia tlistarEmergencia = new TareaListarEmergencia();
						tlistarEmergencia.execute();
						

						// si se obtuvo los dato del servidor mostrarlos
						if (cargadoListaEmergencia) {
							if (op_Busqueda == Constantes.SERVICIO_EMERGENCIA_TODOS) {
								// busca todos
								adaptador = new ItemListEmergenciaAdapter(
										ExplorarActivity.this, listaEmergencia);
								lista.setAdapter(adaptador);
							} else {

								adaptador = new ItemListEmergenciaAdapter(

								ExplorarActivity.this, EmergenciaUtil
										.listEmergencias_by_Servicio(
												listaEmergencia, op_Busqueda));
								lista.setAdapter(adaptador);
							}

						}

					} else {

						if (op_Busqueda == Constantes.SERVICIO_EMERGENCIA_TODOS) {
							// busca todos
							adaptador = new ItemListEmergenciaAdapter(
									ExplorarActivity.this, listaEmergencia);
							lista.setAdapter(adaptador);
						} else {

							adaptador = new ItemListEmergenciaAdapter(

							ExplorarActivity.this, EmergenciaUtil
									.listEmergencias_by_Servicio(
											listaEmergencia, op_Busqueda));
							lista.setAdapter(adaptador);
						}
					}

				} else if (tabId == "tab_mapa") {
					Log.i("ttab", "tab mpaa");
					if (op_Busqueda == Constantes.SERVICIO_EMERGENCIA_TODOS) {
						mostrarMapa(listaEmergencia);
					} else {
						mostrarMapa(EmergenciaUtil.listEmergencias_by_Servicio(
								listaEmergencia, op_Busqueda));
					}

				}
			}

		});

		lista = (ListView) findViewById(R.id.listViewMapa);

	}

	/**
	 * se encarga de mostrar el mapa en el fragment de tab 0
	 */
	private void mostrarMapa(List<Emergencia> listaEmergencia) {
		// creo el mapa
		mapa = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		// muestra mi localizacion en el mapa
		mapa.setMyLocationEnabled(true);

		// mueve el mapa hacia la localizacion enviada por parametro
		mCamera = CameraUpdateFactory.newLatLngZoom(new LatLng(latitudPalpala,
				longitudPalpala), 16);
		mapa.animateCamera(mCamera);

		// palpala = new LatLng(latitudPalpala, longitudPalpala);
		// CameraUpdate camUpd1 = CameraUpdateFactory.newLatLng(palpala);

		// mapa.moveCamera(camUpd1);
		Log.i("longitud de la lista cuando entra a mostrar mapa",
				listaEmergencia.size() + "");

		if (!cargadoListaEmergencia) {
			Log.i("entro a cargar de nuevo", "ya cargo anteriormente q culeao");
			TareaListarEmergencia tareaCargarListaEmergencia = new TareaListarEmergencia();
			tareaCargarListaEmergencia.execute();
		}

		// mostrar las emergencias en el mapa.

		if (cargadoListaEmergencia) {

			for (Emergencia emergencia : listaEmergencia) {
				int resIcono = getIcono(emergencia.getSid());
				double latitud = Double.parseDouble(emergencia.getLatitud());
				double longitud = Double.parseDouble(emergencia.getLongitud());
				Log.e("latitud en mapa", latitud + "");
				Log.e("longitud en mapa", longitud + "");
				mostrarMarcador(latitud, longitud, emergencia.getUsid() + "",
						resIcono);

			}

		}

	}

	private int getIcono(int ico) {
		int intIcon = 0;
		switch (ico) {
		case Constantes.SERVICIO_EMERGENCIA_BOMBERO:
			intIcon = R.drawable.ic_bombero;
			break;

		case Constantes.SERVICIO_EMERGENCIA_POLICIA:
			intIcon = R.drawable.ic_policia;
			break;

		case Constantes.SERVICIO_EMERGENCIA_HOSPITAL:
			intIcon = R.drawable.ic_hospital;
			break;

		default:
			break;
		}

		return intIcon;
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

	// Tarea Asíncrona para llamar al WS de listado en segundo plano
	private class TareaListarEmergencia extends
			AsyncTask<String, Integer, Boolean> {

		private String[] clientes;

		protected Boolean doInBackground(String... params) {

			boolean resul = true;

			HttpClient httpClient = new DefaultHttpClient();

			HttpGet del;

			// personalizar para obtenga de todos mis emergencias.
			if (miEmergencia) {
				String usid = UsuarioUtil.getUSID(ExplorarActivity.this);
				del = new HttpGet(Constantes.URL_LISTAR_EMERGENCIAS_BY_USER
						+ usid);
			} else {
				del = new HttpGet(Constantes.URL_LISTAR_EMERGENCIAS_ACTIVAS);
			}

			del.setHeader("content-type", "application/json");

			try {
				HttpResponse resp = httpClient.execute(del);
				String respStr = EntityUtils.toString(resp.getEntity());

				JSONArray respJSON = new JSONArray(respStr);

				clientes = new String[respJSON.length()];

				for (int i = 0; i < respJSON.length(); i++) {
					JSONObject obj = respJSON.getJSONObject(i);
					// GUARDAR LOS DATOS DE LA EMERGENCIA EN UN OBJETO
					// EMERGENCIA

					Emergencia emergencia = new Emergencia();
					emergencia.setUsid(Integer.valueOf(obj.getString("USID")));
					;
					emergencia.setSid(Integer.valueOf(obj.getString("SID")));
					;
					emergencia.setLongitud(obj.getString("LONGITUD"));
					emergencia.setLatitud(obj.getString("LATITUD"));
					emergencia.setEstado(Integer.valueOf(obj
							.getString("ESTADO")));
					emergencia.setComentarioU(obj.getString("COMENTARIOU"));
					emergencia.setComentarioS(obj.getString("COMENTARIOS"));
					emergencia.setFecha(obj.getString("FECHA"));
					emergencia.setHora(obj.getString("HORA"));
					String direccion = get_Calle_Ciudad_Provincia(
							Double.parseDouble(emergencia.getLatitud()),
							Double.parseDouble(emergencia.getLongitud()));
					emergencia.setDireccion(direccion);
					
					listaEmergencia.add(emergencia);
					Log.i("emergencia", emergencia.toString());
				}
			} catch (Exception ex) {
				Log.e("ServicioRest", "Error!", ex);
				resul = false;
			}

			return resul;
		}

		protected void onPostExecute(Boolean result) {

			if (result) {
				// Log.e("LISTA ", "ENTRO LA LITA");
				// adaptador = new ItemListEmergenciaAdapter(
				// ExplorarActivity.this, listaEmergencia);
				// lista.setAdapter(adaptador);
				cargadoListaEmergencia = true;
				mostrarMapa(listaEmergencia);
				pDialog.dismiss();
				
			}
		}

	}

	/**
	 * Muestra un marcador con el icono de un globo en el punto indicado con lat
	 * y lng
	 * 
	 * @param lat
	 * @param lng
	 */
	private void mostrarMarcador(double lat, double lng, String title,
			int intIconResource) {
		mapa.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
				.title(title)
				.icon(BitmapDescriptorFactory.fromResource(intIconResource)));

	}

	private void mostrarLineas() {
		// Dibujo con Lineas

		PolylineOptions lineas = new PolylineOptions()
				.add(new LatLng(45.0, -12.0)).add(new LatLng(45.0, 5.0))
				.add(new LatLng(34.5, 5.0)).add(new LatLng(34.5, -12.0))
				.add(new LatLng(45.0, -12.0));

		lineas.width(8);
		lineas.color(Color.RED);

		mapa.addPolyline(lineas);

		// Dibujo con polígonos

		// PolygonOptions rectangulo = new PolygonOptions()
		// .add(new LatLng(45.0, -12.0),
		// new LatLng(45.0, 5.0),
		// new LatLng(34.5, 5.0),
		// new LatLng(34.5, -12.0),
		// new LatLng(45.0, -12.0));
		//
		// rectangulo.strokeWidth(8);
		// rectangulo.strokeColor(Color.RED);
		//
		// mapa.addPolygon(rectangulo);
	}

	@Override
	public void onClick(View boton) {
		mapa.clear();
		List<Emergencia> listaAux = new ArrayList<Emergencia>();

		if (boton.equals(btnPolice)) {
			// BUSQUEDA POR POLICIA
			op_Busqueda = Constantes.SERVICIO_EMERGENCIA_POLICIA;
			Toast.makeText(this, "policia", Toast.LENGTH_LONG).show();
			listaAux = EmergenciaUtil.listEmergencias_by_Servicio(
					listaEmergencia, Constantes.SERVICIO_EMERGENCIA_POLICIA);
			mostrarMapa(listaAux);
		} else if (boton.equals(btnBombero)) {
			// BUSQUEDA POR BOMBERO
			op_Busqueda = Constantes.SERVICIO_EMERGENCIA_BOMBERO;
			Toast.makeText(this, "bombero", Toast.LENGTH_LONG).show();
			listaAux = EmergenciaUtil.listEmergencias_by_Servicio(
					listaEmergencia, Constantes.SERVICIO_EMERGENCIA_BOMBERO);
			mostrarMapa(listaAux);
		} else if (boton.equals(btnHospital)) {
			// BUSQUEDA POR HOSPITAL
			op_Busqueda = Constantes.SERVICIO_EMERGENCIA_HOSPITAL;
			Toast.makeText(this, "hospitla", Toast.LENGTH_LONG).show();
			listaAux = EmergenciaUtil.listEmergencias_by_Servicio(
					listaEmergencia, Constantes.SERVICIO_EMERGENCIA_HOSPITAL);
			mostrarMapa(listaAux);

		} else if (boton.equals(btnTodos)) {
			// BUSQUEDA POR TODOS
			op_Busqueda = 0;
			Toast.makeText(this, "hospitla", Toast.LENGTH_LONG).show();
			mostrarMapa(listaEmergencia);
		}

	}
	
	
	
	/**
	 * 
	 * @param latitud de geolocalizacion
	 * @param longitud de geolocalizacion
	 * @return convierte una localizacion en Direccion Real en formato CALLE-CIUDAD-PROVINCIA
	 */
	public String get_Calle_Ciudad_Provincia(double latitud, double longitud) {

		List<Address> addresses = null;
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		try {
			addresses = geocoder.getFromLocation(latitud, longitud, 1);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Address address = addresses.get(0);
	//	Log.i("Localidad", address.getLocality()+"");
	//	Log.i("adres line", address.getAddressLine(0)+"");
	//	Log.i("countri name", address.getCountryName()+"");
	//	Log.i("feature name", address.getFeatureName()+"");
	//	Log.i("premises", address.getPremises()+"");
	//	Log.i("subAminArea", address.getSubAdminArea()+"");
	//	Log.i("subthourghfare", address.getSubThoroughfare()+"");
	//	Log.i("thoroughfare", address.getThoroughfare()+"");
		ArrayList<String> addressFragments = new ArrayList<String>();

		// Fetch the address lines using getAddressLine,
		// join them, and send them to the thread.
		for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
			addressFragments.add(address.getAddressLine(i));
		}

		String direccion = TextUtils.join(System.getProperty("line.separator"),
				addressFragments);

		return direccion;
	}


}

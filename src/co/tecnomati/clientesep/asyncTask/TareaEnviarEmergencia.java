package co.tecnomati.clientesep.asyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import co.tecnomati.clientesep.activity.EnviaFotoActivity;
import co.tecnomati.clientesep.activity.HomeActivity;
import co.tecnomati.clientesep.cons.Constantes;
import co.tecnomati.clientesep.dominio.Emergencia;
import co.tecnomati.clientesep.servicios.ServicioGPS;

// Tarea Asíncrona para llamar al WS de inserción en segundo plano
public class TareaEnviarEmergencia extends
		AsyncTask<String, Integer, Boolean> {
	public static String TAG_ASYNCTASK_EMERGENCIA="Tarea Enviar Emergencia";
	// private int idCli;
	String idCli;
	Context contexto;
	Emergencia emergencia;
	private ProgressDialog pDialog;
	public TareaEnviarEmergencia(Context ctx,Emergencia emergencia,ProgressDialog pDialog){
		super();
		contexto = ctx;
		this.emergencia= emergencia;
	    this.pDialog=pDialog;
	}

	protected Boolean doInBackground(String... params) {
		Log.i(TAG_ASYNCTASK_EMERGENCIA, "doInBackground");
		// progreso
//				pDialog = new ProgressDialog(contexto);
//				pDialog.setMessage("Enviando Emergencia...");
//				pDialog.setCancelable(true);
//				pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//				pDialog.show();

		
		boolean resul = true;

		HttpClient httpClient = new DefaultHttpClient();

		HttpPost post = new HttpPost(Constantes.URL_ENVIAR_EMERGENCIA);
		post.setHeader("content-type", "application/json");

		try {
			// Construimos el objeto emergencia en formato JSON
			JSONObject dato = new JSONObject();

			dato.put("usid", params[0]);
			dato.put("sid", params[1]);
			dato.put("latitud", params[2]);
			dato.put("longitud", params[3]);
			dato.put("estado", params[4]);
			dato.put("comentariou", params[5]);
			dato.put("comentarios", params[6]);
			dato.put("imagen", params[7]);
			dato.put("precision", params[8]);
			dato.put("precision_b", params[9]);
			StringEntity entity = new StringEntity(dato.toString());
			post.setEntity(entity);
			HttpResponse resp = httpClient.execute(post);

			String respStr = EntityUtils.toString(resp.getEntity());
			// JSONObject respJSON = new JSONObject(respStr);
			Log.e("respuSTr", respStr + "");
			idCli = respStr.toString();
			Log.i(TAG_ASYNCTASK_EMERGENCIA, " idEmergencia:" +idCli );
			if (respStr.equals("0"))
				resul = false;

		} catch (Exception ex) {
			Log.e("ServicioRest", "Error!", ex);
			resul = false;
		}

		return resul;
	}

	protected void onPostExecute(Boolean result) {
		
		Log.i(TAG_ASYNCTASK_EMERGENCIA, "onPostExecute");
		if (result) {
			Log.i(TAG_ASYNCTASK_EMERGENCIA, "contexto: "+contexto.getApplicationContext().getClass());
			// guardo el id de la emergencia en una preferencia
			
//			SharedPreferences prefs = contexto.getApplicationContext().getSharedPreferences("Pref_Emergencia",Context.MODE_PRIVATE);
//			SharedPreferences.Editor editor = prefs.edit();
//			editor.putString("eid",idCli );
			Log.i(TAG_ASYNCTASK_EMERGENCIA, "idcli: "+idCli);
			// idUser.setText("Insertado OK.  : " + idCli);
			// Ejecuta el servicio que envia la localizacion del usuario constantemente.
			Intent i = new Intent(contexto.getApplicationContext(),ServicioGPS.class);
			i.putExtra("eidd", idCli.toString());
//			if(contexto.getClass().equals(HomeActivity.class))
//			{
//				// envia continuamente  y lo asigno un emergencia 
//                i.putExtra("eid", idCli); 
//                Log.i(TAG_ASYNCTASK_EMERGENCIA, "HomeActivity_eid"+idCli);
//			}
//			else if(contexto.getClass().equals(EnviaFotoActivity.class))
//			{
//				 Log.i(TAG_ASYNCTASK_EMERGENCIA, "HomeActivity_eid"+idCli);
//				// paro la el servicio
//				 i.putExtra("eid", "0");
//			}
			
			contexto.startService(i);
			pDialog.dismiss();
			Toast.makeText(contexto, "Resultado ok  ",
					Toast.LENGTH_LONG).show();

		}
	}
}

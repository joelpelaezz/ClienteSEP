package co.tecnomati.clientesep.asyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import co.tecnomati.clientesep.cons.Constantes;
import co.tecnomati.clientesep.dominio.Monitoreo;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class TareaMonitorearUsuario extends AsyncTask<String, Integer, Boolean> {
	public static String TAG_SERVICIO="TareaMonitorearUsuario";
	// private int idCli;
	
	Context contexto;
	Monitoreo monitoreo;
	private ProgressDialog pDialog;

	public TareaMonitorearUsuario(Context ctx, Monitoreo monitoreo) {
		super();
		contexto = ctx;
		this.monitoreo = monitoreo;
		this.pDialog = pDialog;
		
	}

	protected Boolean doInBackground(String... params) {
		 Log.i(TAG_SERVICIO, "doInBackground");
		
		boolean resul = true;

		HttpClient httpClient = new DefaultHttpClient();

		HttpPost post = new HttpPost(Constantes.URL_ENVIAR_MONITOREO);
		post.setHeader("content-type", "application/json");

		try {
			// Construimos el objeto emergencia en formato JSON
			JSONObject dato = new JSONObject();

			dato.put("usid", params[0]);
			dato.put("eid", params[1]);
			dato.put("latitud", params[2]);
			dato.put("longitud", params[3]);
			dato.put("precision", params[4]);
			StringEntity entity = new StringEntity(dato.toString());
			post.setEntity(entity);
			HttpResponse resp = httpClient.execute(post);

			String respStr = EntityUtils.toString(resp.getEntity());
			Log.i(TAG_SERVICIO, "respStr: "+respStr);
			if (respStr.equals("0"))
				resul = false;

		} catch (Exception ex) {
			Log.e("ServicioRest", "Error!", ex);
			resul = false;
		}
     
		return resul;
	}

	protected void onPostExecute(Boolean result) {

		if (result) {
			// idUser.setText("Insertado OK.  : " + idCli);
//			pDialog.dismiss();
//			Toast.makeText(contexto, "Resultado ok  ", Toast.LENGTH_LONG)
//					.show();

		}
	}
}

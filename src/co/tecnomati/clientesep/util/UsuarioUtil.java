package co.tecnomati.clientesep.util;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import co.tecnomati.clientesep.activity.HomeActivity;
import co.tecnomati.clientesep.cons.Constantes;
import co.tecnomati.clientesep.dominio.Usuario;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class UsuarioUtil {

	
	public UsuarioUtil() {
		super();
		// TODO Auto-generated constructor stub
	}
//	public UsuarioUtil(Context ctx ,Usuario u) {
//		super();
//		UsuarioUtil.setPreferencias(ctx, u);
//		TareaCrearUsuario tCrearUsuario = new TareaCrearUsuario(ctx);
//		tCrearUsuario.execute(u.getUser(),u.getTelefono(),u.getDomicilio(),u.getComentario(),u.getIdGCM());
//		
//		// TODO Auto-generated constructor stub
//	}
	/**
	 * 
	 * @param ctx contexto de la actividdad
	 * 
	 * @return retorna el id del usuario de la aplicacion
	 */
	public static String getUSID(Context ctx){
		SharedPreferences preferencia = PreferenceManager.getDefaultSharedPreferences(ctx);
		String idUser = preferencia.getString("id","");
		return idUser;
	}
	
	/**
	 * obtiene las preferencias configuradas por el usuario en la opcion
	 * preferencias. las misma se guardan en variables de la activity
	 */
	public static Usuario getAllPreferencias(Context ctx) {
		// SharedPreferences preferencia =
		// getSharedPreferences("co.tecnomati.clientesep", MODE_PRIVATE);
		SharedPreferences preferencia = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		Usuario usuario= new Usuario() ;
		usuario.setIdUser(preferencia.getString("id", ""));
		usuario.setUser(preferencia.getString("usuario", ""));
		usuario.setTelefono(preferencia.getString("telefono", ""));
		usuario.setContacto1(preferencia.getString("contacto1", ""));
		usuario.setContacto2(preferencia.getString("contacto2", ""));
		usuario.setVibrar(preferencia.getBoolean("vibrar", true));
		usuario.setDomicilio(preferencia.getString("domicilio", ""));	
		usuario.setIdGCM(preferencia.getString("idgcm", ""));
		usuario.setComentario(preferencia.getString("comentario", ""));
		usuario.setMonitoreo(preferencia.getBoolean("monitoreo", false));	
		
		Log.i("Datos de Preferencias",usuario.toString());
		return usuario;
	}
	
	
	public static void setPreferencias(Context ctx,Usuario u){
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		SharedPreferences.Editor editor = pref.edit();

		editor.putString("id",u.getIdUser() );
		editor.putString("usuario",u.getUser());
		editor.putString("telefono", u.getTelefono());
		editor.putString("domicilio", u.getDomicilio());
		editor.putString("comentario",u.getComentario());
		editor.putString("idgcm", u.getIdGCM());
		editor.putString("contacto1", u.getContacto1());
		editor.putString("contacto2", u.getContacto2());
		editor.putBoolean("vibrar", u.isVibrar());
		editor.putBoolean("monitoreo", u.isMonitoreo());
		
		editor.commit();
		
	}
	

//	// Tarea Asíncrona para llamar al WS de inserción en segundo plano
//	private class TareaCrearUsuario extends AsyncTask<String, Integer, Boolean> {
//
//		// private int idCli;
//		String idUser;
//		public ProgressDialog pDialog;
//		Context ctx;
//		
//		public TareaCrearUsuario(Context ctx) {
//			super();
//			this.ctx= ctx;
//			this.pDialog= pDialog;
//			// TODO Auto-generated constructor stub
//		}
//
//		@Override
//	    protected void onPreExecute() {
//	        // TODO Auto-generated method stub
//	        super.onPreExecute();
//	         
//	      //  pDialog = new ProgressDialog(ctx);
//	       // pDialog.setMessage("Cargando Datos...");
//	       // pDialog.setCancelable(true);
//	       // pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//	       // pDialog.show();
//	         
//	    }
//
//		protected Boolean doInBackground(String... params) {
//			Log.e(" tarea asincronica user", "en backgroud");
//			boolean resul = true;
//
//			HttpClient httpClient = new DefaultHttpClient();
//
//			HttpPost post = new HttpPost(Constantes.URL_CREAR_USUARIO);
//			post.setHeader("content-type", "application/json");
//			
//			
//	        
//			try {
//				
//				
//				// Construimos el objeto emergencia en formato JSON
//				JSONObject dato = new JSONObject();
//				
//				Log.i("parametro 4 ",params[4] );
//				dato.put("usuario", params[0]);
//				dato.put("telefono", params[1]);
//				dato.put("domicilio", params[2]);
//				dato.put("comentario", params[3]);
//				dato.put("idgcm", params[4]);
//				StringEntity entity = new StringEntity(dato.toString());
//				post.setEntity(entity);
//				HttpResponse resp = httpClient.execute(post);
//
//				String respStr = EntityUtils.toString(resp.getEntity());
//				
//				Log.e("respuSTr", respStr + "");
//				idUser = respStr.toString();
//				Log.e("id de la emergencia", idUser + "");
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
//				Usuario u = UsuarioUtil.getAllPreferencias(ctx);
//				u.setIdUser(idUser);
//				UsuarioUtil.setPreferencias(ctx, u);
//				//SharedPreferences preferencia = PreferenceManager.getDefaultSharedPreferences(ctx);
//				//SharedPreferences.Editor editor = preferencia.edit();
//
//				//editor.putString("id",idUser );
//				//editor.commit();
//				Log.i("resultado ", "guardado con exxito");	
//				Log.i("resultado ", UsuarioUtil.getAllPreferencias(ctx).toString());	
//				//contacto1 = preferencia.getString("contacto1", "3884729680");
//				//contacto2 = preferencia.getString("contacto2", "3884618569");
//				
//				
//									
//		//		Toast.makeText(getApplicationContext(), "Bienvenido.. ",
//		//				Toast.LENGTH_SHORT).show();
//				
//				Intent intentActivityHome = new Intent(ctx,HomeActivity.class);
//				ctx.startActivity(intentActivityHome);
//				//pDialog.dismiss();
//
//			}else{
//				Toast.makeText(ctx, "Problemas con el Servicio .. ",
//						Toast.LENGTH_LONG).show();
//			}
//		}
//	}

}	
	
	
	


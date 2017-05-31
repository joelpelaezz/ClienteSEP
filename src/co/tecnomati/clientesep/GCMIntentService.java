package co.tecnomati.clientesep;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import co.tecnomati.clientesep.activity.HomeActivity;
import co.tecnomati.clientesep.cons.Constantes;
import co.tecnomati.clientesep.dominio.Usuario;


import co.tecnomati.clientesep.util.UsuarioUtil;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {
	Context ctx;
	private static final int NOTIF_ALERTA_ID = 1;
    

	public GCMIntentService() {
		super(Constantes.SENDER_ID);
	}
 
		
	@Override
	protected void onError(Context context, String errorId) {
		this.ctx=context;
		Log.d("GCMTest", "REGISTRATION: Error -> " + errorId);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		this.ctx=context;
		String msg = intent.getExtras().getString("message");
		Log.d("GCMTest", "Mensaje: " + msg);
		mostrarNotificacion3(context, msg);
	}

	@Override
	protected void onRegistered(Context context, String regId) {
		this.ctx=context;
    	Log.d("GCMTest", "REGISTRATION: Registrado OK."+regId);
    	// obtengo todoos los datos 
		Usuario usuario = UsuarioUtil.getAllPreferencias(context);
    	usuario.setIdGCM(String.valueOf(regId));
		
    	registroServidor(context,usuario,regId);
    	
    	
	}

	@Override
	protected void onUnregistered(Context context, String regId) {
		Log.d("GCMTest", "REGISTRATION: Desregistrado OK.");
	}

	private void registroServidor(Context ctx,Usuario u,String regid)
	{
		Log.i("usuario", u.toString());
//		UsuarioUtil uu = new UsuarioUtil(ctx, u);
		//registrar el usuario en el servidor
		UsuarioUtil.setPreferencias(ctx, u);
		TareaCrearUsuario tCrearUsuario = new TareaCrearUsuario(this.ctx);
		tCrearUsuario.execute(u.getUser(),u.getTelefono(),u.getDomicilio(),u.getComentario(),regid);
//		
		
	}
	
	private void mostrarNotificacion(Context context, String msg)
	{
		//Obtenemos una referencia al servicio de notificaciones
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager notManager =
		    (NotificationManager) context.getSystemService(ns);
		
		//Configuramos la notificación
		int icono = android.R.drawable.stat_sys_warning;
		CharSequence textoEstado = "Notifiacion  SEP!";
		long hora = System.currentTimeMillis();
		 
		Notification notif =
		    new Notification(icono, textoEstado, hora);
		
		//Configuramos el Intent
		Context contexto = context.getApplicationContext();
		CharSequence titulo = "Nuevo Mensaje";
		CharSequence descripcion = msg;
		 
		Intent notIntent = new Intent(contexto,
		    GCMIntentService.class);
		 
		PendingIntent contIntent = PendingIntent.getActivity(
		    contexto, 0, notIntent, 0);
		 
		notif.setLatestEventInfo(
		    contexto, titulo, descripcion, contIntent);
		
		//AutoCancel: cuando se pulsa la notificaión ésta desaparece
		notif.flags |= Notification.FLAG_AUTO_CANCEL;
		
		//Enviar notificación
		notManager.notify(1, notif);
	}
	
	
	private void mostrarNotificacion2(Context context, String msg)
	{	
	
	// indico a que activity se irá cuando se haga click en la notificacion	
	Intent intent = new Intent(this, NotificationReceiverActivity.class);
	PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
		
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(android.R.drawable.stat_sys_warning)
		        .setLargeIcon((((BitmapDrawable)getResources()
		        		.getDrawable(R.drawable.ic_launcher)).getBitmap()))
		        .setContentTitle("Mensaje de Alerta")
		        .setContentText(msg)
		        .setContentInfo("4")
		        .addAction(R.drawable.ic_launcher, "Mostrar mas..", pIntent)
		        .setTicker(msg);
				
		Intent notIntent = 
				new Intent(context, GCMIntentService.class);
		
		PendingIntent contIntent = PendingIntent.getActivity(
				context, 0, notIntent, 0);
		
		mBuilder.setContentIntent(contIntent);
		
		NotificationManager mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		mNotificationManager.notify(NOTIF_ALERTA_ID, mBuilder.build());
	
		
		
		
	}
	
	
	 public void mostrarNotificacion3(Context context, String msg) {
		    // Prepare intent which is triggered if the
		    // notification is selected
		    Intent intent = new Intent(context, NotificationReceiverActivity.class);
		    intent.putExtra("msg", msg);
		    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

		    // Build notification
		    // Actions are just fake
		    long[] vibrate = new long[] { 1000, 1000, 1000, 1000, 1000 };
		    Notification noti = new Notification.Builder(this)
		  //  .setStyle(new Notification.BigTextStyle().bigText("Mi Estilo"))
		    .setVibrate(vibrate)
		    .setContentTitle("New mail from " + "test@gmail.com")
		    .setContentText(msg).setSmallIcon(R.drawable.ic_launcher)
		    .setContentIntent(pIntent)
			.addAction(R.drawable.ic_facebook, "Call", pIntent)
		    .addAction(R.drawable.ic_launcher, "More", pIntent)
		        
		        .addAction(R.drawable.ic_launcher, "And more", pIntent).build();
		    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		    
		    // hide the notification after its selected
		    noti.flags |= Notification.FLAG_AUTO_CANCEL;

		    notificationManager.notify(0, noti);

		  }
	
	
	// Tarea Asíncrona para llamar al WS de inserción en segundo plano
	public class TareaCrearUsuario extends AsyncTask<String, Integer, Boolean> {
		Context ctx;
		// private int idCli;
		String idUser;
		public ProgressDialog pDialog;
		
		
		
		public TareaCrearUsuario(Context ctx) {
			super();
			this.ctx = ctx;
		}

		@Override
	    protected void onPreExecute() {
	        // TODO Auto-generated method stub
	        super.onPreExecute();
	         
//	        pDialog = new ProgressDialog(ctx);
//	        pDialog.setMessage("Cargando Datos...");
//	        pDialog.setCancelable(true);
//	        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//	        pDialog.show();
	         
	    }

		protected Boolean doInBackground(String... params) {
			Log.e(" tarea asincronica user", "en backgroud");
			boolean resul = true;

			HttpClient httpClient = new DefaultHttpClient();

			HttpPost post = new HttpPost(Constantes.URL_CREAR_USUARIO);
			post.setHeader("content-type", "application/json");
			
			
	        
			try {
				
				
				// Construimos el objeto emergencia en formato JSON
				JSONObject dato = new JSONObject();
				
				Log.i("parametro 4 ",params[4] );
				dato.put("usuario", params[0]);
				dato.put("telefono", params[1]);
				dato.put("domicilio", params[2]);
				dato.put("comentario", params[3]);
				dato.put("idgcm", params[4]);
				StringEntity entity = new StringEntity(dato.toString());
				post.setEntity(entity);
				HttpResponse resp = httpClient.execute(post);

				String respStr = EntityUtils.toString(resp.getEntity());
				
				Log.e("respuSTr", respStr + "");
				idUser = respStr.toString();
				Log.e("id de la emergencia", idUser + "");
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
				Usuario u = UsuarioUtil.getAllPreferencias(ctx);
				u.setIdUser(idUser);
				UsuarioUtil.setPreferencias(ctx, u);
				//SharedPreferences preferencia = PreferenceManager.getDefaultSharedPreferences(ctx);
				//SharedPreferences.Editor editor = preferencia.edit();

				//editor.putString("id",idUser );
				//editor.commit();
				Log.i("resultado ", "guardado con exxito");	
				Log.i("resultado ", UsuarioUtil.getAllPreferencias(ctx).toString());	
				//contacto1 = preferencia.getString("contacto1", "3884729680");
				//contacto2 = preferencia.getString("contacto2", "3884618569");
				
				
									
			Toast.makeText(getApplicationContext(), "Bienvenido.. ",
						Toast.LENGTH_SHORT).show();
				
				Intent intentActivityHome = new Intent(ctx.getApplicationContext(),HomeActivity.class);
				intentActivityHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ctx.startActivity(intentActivityHome);
		//		pDialog.dismiss();
				
			}else{
				Toast.makeText(this.ctx, "Problemas con el Servicio .. ",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	
	
	
}
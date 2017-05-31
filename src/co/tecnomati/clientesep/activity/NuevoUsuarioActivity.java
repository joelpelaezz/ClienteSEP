package co.tecnomati.clientesep.activity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import co.tecnomati.clientesep.R;
import co.tecnomati.clientesep.cons.Constantes;
import co.tecnomati.clientesep.dominio.Usuario;
import co.tecnomati.clientesep.util.UsuarioUtil;


import com.google.android.gcm.GCMRegistrar;

public class NuevoUsuarioActivity extends Activity implements
		OnClickListener {

	EditText txtUsuario, telefono, comentario, domicilio;
	Button btnAceptar, btnCancelar;
	Usuario u;
	ProgressDialog pDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences preferencia = PreferenceManager.getDefaultSharedPreferences(this);
		String idUser = preferencia.getString("id","");
		if (idUser.trim().length()==0) {
			setContentView(R.layout.activity_nuevo_usuario);

			txtUsuario = (EditText) findViewById(R.id.txtNameUser);
			telefono = (EditText) findViewById(R.id.txtTelefUser);
			comentario = (EditText) findViewById(R.id.txtComentarUser);
     		domicilio = (EditText) findViewById(R.id.txtDomicilioUser);
			btnAceptar = (Button) findViewById(R.id.btnAceptarNuser);
			btnCancelar = (Button) findViewById(R.id.btnCancelarNuser);

			btnAceptar.setOnClickListener(this);
			btnCancelar.setOnClickListener(this);

		}else{
			finish();
			Intent intentActivityHome = new Intent(getApplicationContext(),HomeActivity.class);
			startActivity(intentActivityHome);
		}
		
		
			}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nuevo_usuario, menu);
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
	public void onClick(View boton) {
		// presiono boton aceptar
		if (boton.equals(btnAceptar)) {
			// progreso
				 pDialog = new ProgressDialog(this);
				pDialog.setMessage("Enviando Emergencia...");
				pDialog.setCancelable(true);
				pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pDialog.show();   
				         
			Log.i("entro a aceptar", "aceptar true");
									
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = pref.edit();

			//editor.putString("id",idUser );
			editor.putString("usuario",txtUsuario.getText().toString());
			editor.putString("telefono", telefono.getText().toString());
			editor.putString("domicilio", domicilio.getText().toString());
			editor.putString("comentario",comentario.getText().toString());
			editor.commit();
	
			Log.i("datos creados en preferencias", UsuarioUtil.getAllPreferencias(this).toString());			
			
			// registrar el idGCM
			//Si no estamos registrados --> Nos registramos en GCM
			
	        final String regId = GCMRegistrar.getRegistrationId(this);
	        if (regId.equals("")) {
	        	GCMRegistrar.register(this, Constantes.SENDER_ID); //Sender ID
	           
	        } else {
	        	Log.v("GCMTest", "Ya registrado");
	        }
			pDialog.dismiss();
			// 1- guardar los datos del usuario en el sevidor 
		
	     // TareaCrearUsuario tCrearUsuario = new TareaCrearUsuario();
	    //	tCrearUsuario.execute(u.getUsuario(),u.getTelefono(),u.getDomicilio(),u.getComentario());
			
			// 2- crear una preferencia idUser con el id del usuario creado ene l paso anterior
			
			//3- ingresar a la pantalla home.
			
		} else if (boton.equals(btnCancelar)) {
			// presiona boton cancelar
			
			finish();
		}
	}

	
	

		

	
	
}

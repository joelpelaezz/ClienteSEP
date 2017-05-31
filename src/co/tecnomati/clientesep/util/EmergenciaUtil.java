package co.tecnomati.clientesep.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import co.tecnomati.clientesep.cons.Constantes;
import co.tecnomati.clientesep.dominio.Emergencia;

public  class EmergenciaUtil {
	Context  contexto;
	private ProgressDialog pDialog;

	public static List<Emergencia> listEmergencias_by_Servicio(List<Emergencia>listaEmergencias, int sid){
		List<Emergencia> lista = new ArrayList<Emergencia>();
		
		for (Emergencia emergencia : listaEmergencias) {
			if (emergencia.getSid()== sid) {
				lista.add(emergencia);
		
			}
		}
		
		return lista;
		
	}
	


	
	
}

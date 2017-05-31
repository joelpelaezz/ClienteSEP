package co.tecnomati.clientesep.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import co.tecnomati.clientesep.R;
import co.tecnomati.clientesep.dominio.Emergencia;

public class ItemListEmergencia extends LinearLayout implements OnClickListener {

	// creo tantas variables como componentes tenga la lista
	ImageView imgEmergencia;
	TextView lblLugar, lblFechaHora,lblComentarioServ;
	Emergencia emergencia;
    
	private Context context;

	public ItemListEmergencia(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ItemListEmergencia(Context _context, Emergencia emerg) {

		super(_context);
		this.context = _context;
		this.emergencia = emerg;
		inicializar();

	}

	private void inicializar() {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(
				infService);
		li.inflate(co.tecnomati.clientesep.R.layout.itm_lista_emergencias,
				this, true);

		lblLugar = (TextView) findViewById(R.id.lblLuga);
		lblFechaHora = (TextView) findViewById(R.id.lblFechaHora);
		lblComentarioServ = (TextView) findViewById(R.id.txtComentarioServ);
		imgEmergencia = (ImageView) findViewById(R.id.imgFotoEmerg);

		// transformar las coordenadas en direcciones reales

		//

		
		// lblLugar.setText(emergencia.getLatitud());
		lblLugar.setText(emergencia.getDireccion());
		lblFechaHora.setText(emergencia.getFecha()+" - Hs: "+emergencia.getHora());
		lblComentarioServ.setText(emergencia.getComentarioU());
		// imgEmergencia.setBackgroundResource(co.tecnomati.clientesep.R.drawable.ic_peligroactivado);
		// downloadFile("http://200.43.187.123:8000/sep/assets/img/sep.png");
	}

	 void downloadFile(String imageHttpAddress) {
        URL imageUrl = null;
	        try {
	            imageUrl = new URL(imageHttpAddress);
	            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
	            conn.connect();
	           Bitmap loadedImage = BitmapFactory.decodeStream(conn.getInputStream());
	           imgEmergencia.setImageBitmap(loadedImage);
	        } catch (IOException e) {
	          //  Toast.makeText(getApplicationContext(), "Error cargando la imagen: "+e.getMessage(), Toast.LENGTH_LONG).show();
	            e.printStackTrace();
	        }
	    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	

}

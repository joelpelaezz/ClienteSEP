package co.tecnomati.clientesep.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import co.tecnomati.clientesep.R;
import co.tecnomati.clientesep.util.Lista_itemConfiguracion;
import co.tecnomati.clientesep.util.Lista_itmAdaptadorConfig;

//Array of options --> Array Adapter --> ListView

//List Views {views:simple_list_items_1.xml }

public class ListaConfiguracionActivity extends Activity {

	public static final String OP_PREFERENCIA_USUARIO = "MIS PREFERENCIAS";
	public static final String OP_CONTACTO = "CONTACTOS";
	public static final String OP_MIS_EMERGENCIA = "MIS EMERGENCIAS";
	public static final String OP_MAPA_EMERGENCIA = "MAPA DE EMERGENCIAS";
	public static final String OP_AYUDA = "AYUDA";
	public static final String OP_VOLVER = "VOLVER";

	private ListView lista;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lista_configuracion);

		// creo un array de item
		ArrayList<Lista_itemConfiguracion> datos = new ArrayList<Lista_itemConfiguracion>();

		// agrego al array los item
		datos.add(new Lista_itemConfiguracion(android.R.drawable.ic_menu_edit,
				OP_PREFERENCIA_USUARIO, ">"));
		// datos.add(new Lista_itemConfiguracion(
		// android.R.drawable.ic_menu_agenda, OP_CONTACTO, ">"));

		datos.add(new Lista_itemConfiguracion(
				android.R.drawable.ic_menu_myplaces, OP_MIS_EMERGENCIA, ">"));
		datos.add(new Lista_itemConfiguracion(android.R.drawable.ic_dialog_map,
				OP_MAPA_EMERGENCIA, ">"));
		datos.add(new Lista_itemConfiguracion(android.R.drawable.ic_menu_help,
				OP_AYUDA, ">"));
		datos.add(new Lista_itemConfiguracion(
				android.R.drawable.ic_menu_revert, OP_VOLVER, ">"));

		// vinculo la lista
		lista = (ListView) findViewById(R.id.listViewConfiguracion);

		// le paso un adaptador
		lista.setAdapter(new Lista_itmAdaptadorConfig(this,
				R.layout.activity_itmlista_config, datos) {
			@Override
			public void onEntrada(Object entrada, View view) {
				if (entrada != null) {
					TextView texto_superior_entrada = (TextView) view
							.findViewById(R.id.lblOpcionConfig);
					if (texto_superior_entrada != null)
						texto_superior_entrada
								.setText(((Lista_itemConfiguracion) entrada)
										.get_textoEncima());

					TextView texto_inferior_entrada = (TextView) view
							.findViewById(R.id.lblsignotiburon);
					if (texto_inferior_entrada != null)
						texto_inferior_entrada
								.setText(((Lista_itemConfiguracion) entrada)
										.get_textoDebajo());

					ImageView imagen_entrada = (ImageView) view
							.findViewById(R.id.imgFotoConfig);
					if (imagen_entrada != null)
						imagen_entrada
								.setImageResource(((Lista_itemConfiguracion) entrada)
										.get_idImagen());
				}
			}
		});

		lista.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> pariente, View view,
					int posicion, long id) {
				Lista_itemConfiguracion elegido = (Lista_itemConfiguracion) pariente
						.getItemAtPosition(posicion);
				String menu;
				menu = elegido.get_textoEncima();
				if (OP_PREFERENCIA_USUARIO == menu) {
					Intent i = new Intent(ListaConfiguracionActivity.this,
							PreferenciaUsuarioActivity.class);
					startActivity(i);
				} else if (OP_CONTACTO == menu) {
					Intent intentActivityEplorarZona = new Intent(ListaConfiguracionActivity.this,ExplorarActivity.class);
					startActivity(intentActivityEplorarZona);
				} else if (OP_MIS_EMERGENCIA == menu) {
					Intent intentActivityEplorarZona = new Intent(ListaConfiguracionActivity.this,ExplorarActivity.class);
					intentActivityEplorarZona.putExtra("miEmergencia", true);
					startActivity(intentActivityEplorarZona);
				} else if (OP_MAPA_EMERGENCIA == menu) {
					Intent intentActivityEplorarZona = new Intent(ListaConfiguracionActivity.this,ExplorarActivity.class);
					intentActivityEplorarZona.putExtra("miEmergencia", false);
					startActivity(intentActivityEplorarZona);
				} else if (OP_AYUDA == menu) {
					// Intent i=new Intent(ListadoMenu.this,
					// ListViewAdapter.class);
					// startActivity(i);
				} else if (OP_VOLVER == menu){
					
					Intent intentActivityHome = new Intent(getApplicationContext(),HomeActivity.class);
					startActivity(intentActivityHome);
					
					//CharSequence texto = "Seleccionado: "
					//		+ elegido.get_textoEncima();
					//Toast toast = Toast.makeText(
					//		ListaConfiguracionActivity.this, texto,
					//		Toast.LENGTH_LONG);
					//toast.show();
				}
			}
		});

	} /* fin de oncreate */
	
	
	
	

}/* fin de activity */
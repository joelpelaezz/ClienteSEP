package co.tecnomati.clientesep.util;

import java.util.List;

import co.tecnomati.clientesep.dominio.Emergencia;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ItemListEmergenciaAdapter extends BaseAdapter {

	public static final int ADAPTADOR_COMENTARIO_HOME = 1;
	public static final int ADAPTADOR_COMENTARIO_HIJO = 2;
	private Activity activity;
	private List<Emergencia> listaEmergencia;


	
	public ItemListEmergenciaAdapter(Activity activity,
			List<Emergencia> listaEmergencia) {
		super();
		this.activity = activity;
		this.listaEmergencia = listaEmergencia;

	}

	@Override
	public int getCount() {

		return listaEmergencia.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listaEmergencia.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		ItemListEmergencia itemList = new ItemListEmergencia(activity,
				listaEmergencia.get(position));

		return itemList;
	}

}

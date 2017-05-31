package co.tecnomati.clientesep.activity;


import java.util.Calendar;
import java.util.GregorianCalendar;



import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import co.tecnomati.clientesep.R;


	public class Widget extends AppWidgetProvider {
		
		Context context;
	    @Override
	    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
	        // Acciones que debe realizar nuestro widget cada vez que pase el periodo de actualización.
	    	this.context= context;
	    	final int N = appWidgetIds.length;
	    	 
	        for (int i=0; i<N; i++) {
	            int appWidgetId = appWidgetIds[i];
	            
	            // intent para indicar a donde queremos ir cuando presionemos el boton del widwet.
	            Intent intent = new Intent(context, WidgetActivity.class);
	            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
	            // remoteview sirve para poder acceder a los componetes del widwet en este caso accedemos al onclick del boton panico.
	            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
	            
	            views.setOnClickPendingIntent(R.id.btnWidget, pendingIntent);
	            
	            appWidgetManager.updateAppWidget(appWidgetId, views);
	      
	            //Actualizamos el widget actual
		    //    actualizarWidget(context, appWidgetManager, appWidgetId);
	        
	        }
	   
	    }
	    

		public static void actualizarWidget(Context context,
	            AppWidgetManager appWidgetManager, int widgetId)
		{
			
			//Obtenemos la lista de controles del widget actual
			RemoteViews view =
			    new RemoteViews(context.getPackageName(), R.layout.widget);
			
			//Asociamos los 'eventos' al widget
			Intent intent = new Intent("net.sgoliver.android.widgets.ACTUALIZAR_WIDGET");
			intent.putExtra(
				     AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
			PendingIntent pendingIntent = 
				PendingIntent.getBroadcast(context, widgetId, 
						intent, PendingIntent.FLAG_UPDATE_CURRENT);
			
			view.setOnClickPendingIntent(R.id.btnWidget, pendingIntent);
			
			
			//Notificamos al manager de la actualización del widget actual
			appWidgetManager.updateAppWidget(widgetId, view);
		}
	    
	    
		@Override
		public void onReceive(Context context, Intent intent) {
			 Toast.makeText(context, "entro a actualizar", Toast.LENGTH_LONG).show();  	
		    if (intent.getAction().equals("com.example.android_widget.ACTUALIZAR_WIDGET")) {
		    	 Toast.makeText(context, "entro a actualizar", Toast.LENGTH_LONG).show();  	
		        //Obtenemos el ID del widget a actualizar
		        int widgetId = intent.getIntExtra(
		            AppWidgetManager.EXTRA_APPWIDGET_ID,
		            AppWidgetManager.INVALID_APPWIDGET_ID);
		 
		        //Obtenemos el widget manager de nuestro contexto
		        AppWidgetManager widgetManager =
		            AppWidgetManager.getInstance(context);
		 
		        //Actualizamos el widget
		        if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
		            actualizarWidget(context, widgetManager, widgetId);
		            Toast.makeText(context, "entro a actualizar", Toast.LENGTH_LONG).show();  	
		        }
		    }
		}

		    
	    
	    
	}
	


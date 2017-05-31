package co.tecnomati.clientesep.util;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class SMSUtil {

	/**
	 * envia SMS a los contactos registrados en preferencias de ususario
	 */
public static void enviaSMS(final Context ctx,String tel,String mje){
		
		// incio rastrear y confiramar msj de entrega
				String SENT_SMS_ACTION = "SENT_SMS_ACTION";
				String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";

				// Create the sentIntent parameter
				Intent sentIntent = new Intent(SENT_SMS_ACTION);
				PendingIntent sentPI = PendingIntent.getBroadcast(
						ctx.getApplicationContext(), 0, sentIntent, 0);

				// Create the deliveryIntent parameter
				Intent deliveryIntent = new Intent(DELIVERED_SMS_ACTION);
				PendingIntent deliverPI = PendingIntent.getBroadcast(
						ctx.getApplicationContext(), 0, deliveryIntent, 0);

				// Register the Broadcast Receivers
				ctx.registerReceiver(new BroadcastReceiver() {
					@Override
					public void onReceive(Context _context, Intent _intent) {
						switch (getResultCode()) {
						case Activity.RESULT_OK:
							// [… send success actions … ]; break;
							Toast.makeText(ctx.getApplicationContext(),
									"Mensaje Enviado Satifactoriamente a ",
									Toast.LENGTH_SHORT).show();
							break;
						case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
							// [… generic failure actions … ]; break;
							Toast.makeText(
									ctx.getApplicationContext(),
									"Problemas al Enviar el mje- no se pudo enviar el mje",
									Toast.LENGTH_LONG).show();
							break;

						case SmsManager.RESULT_ERROR_RADIO_OFF:
							// [… radio off failure actions … ]; break;
							Toast.makeText(ctx,
									"No hay Red - no se pudo entregar el mje",
									Toast.LENGTH_LONG).show();
							break;

						case SmsManager.RESULT_ERROR_NULL_PDU:
							// [… null PDU failure actions … ]; break;
							Toast.makeText(ctx.getApplicationContext(),
									"Error PDU- no se pude entregar el mje",
									Toast.LENGTH_LONG).show();
							break;

						}
					}
				}, new IntentFilter(SENT_SMS_ACTION));
				ctx.registerReceiver(new BroadcastReceiver() {
					@Override
					public void onReceive(Context _context, Intent _intent) {
						// [… SMS delivered actions … ]
						Log.i("esta accion", "se ejecuta una vez entregado el mje ");

					}
				}, new IntentFilter(DELIVERED_SMS_ACTION));

				// --------- fin confirmar mje entrega-----------

		   SmsManager sms = SmsManager.getDefault();
		   sms.sendTextMessage(tel.toString(), null, mje , sentPI, deliverPI);
		
	}

	
}

package co.tecnomati.clientesep.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetUtil {

	
	 // Checking for all possible internet providers
    public static boolean isConnectingToInternet(Context ctx){
         
        ConnectivityManager connectivity = 
                             (ConnectivityManager) ctx.getSystemService(
                              ctx.CONNECTIVITY_SERVICE);
          if (connectivity != null)
          {
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null)
                  for (int i = 0; i < info.length; i++)
                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
                      {
                          return true;
                      }
  
          }
          return false;
    }
}

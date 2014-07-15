package com.emgeesons.crime_stoppers.vehicle_security;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Connection_Detector {
	AlertDialog pDialog;
	private Context _context;

	public Connection_Detector(Context context) {
		this._context = context;
	}

	public boolean isConnectingToInternet() {
		ConnectivityManager connectivity = (ConnectivityManager) _context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						if (info[i].isConnected()) {
							return true;
						}
					}

		}
		return false;
	}

	public void showNoInternetPopup() {
		pDialog = new AlertDialog.Builder(_context).create();
		 pDialog.setTitle("Location Access Required");
		pDialog.setMessage("Please allow My Wheels access to your location to correctly identify your location. Turn it on from Location Services");
		pDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						pDialog.dismiss();
					}
				});
		pDialog.setCancelable(true);
		pDialog.show();
	}
}

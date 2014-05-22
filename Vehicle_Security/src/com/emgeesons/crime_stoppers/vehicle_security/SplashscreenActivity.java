package com.emgeesons.crime_stoppers.vehicle_security;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Window;

import com.facebook.Session;

public class SplashscreenActivity extends Activity {
	SharedPreferences atPrefs;
	static String agree = "agree";
	static String checkllogin = "checkllogin";
	static boolean fblogin = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen_activity);
		atPrefs = PreferenceManager
				.getDefaultSharedPreferences(SplashscreenActivity.this);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {

				if (atPrefs.getBoolean(agree, true)) {
					Intent next = new Intent(getApplicationContext(),
							DisclaimerActivity.class);
					startActivity(next);
					finish();

				} else {
					// fb login check
					Session session = Session.getActiveSession();
					// if (session != null && session.isOpened()) {
					// System.out.println("yyy");
					// } else {
					// System.out.println("asd");
					// }
					if (atPrefs.getBoolean(checkllogin, true)) {
						Intent next = new Intent(getApplicationContext(),
								LoginActivity.class);
						startActivity(next);
						finish();
					} else {
						//user login from fb
						if (fblogin == false) {
							if (session != null && session.isOpened()) {
								Intent next = new Intent(
										getApplicationContext(),
										HomescreenActivity.class);
								startActivity(next);
								finish();
							} else {
								Intent next = new Intent(
										getApplicationContext(),
										LoginActivity.class);
								startActivity(next);
								finish();
							}

						} else {
							Intent next = new Intent(getApplicationContext(),
									HomescreenActivity.class);
							startActivity(next);
							finish();
						}

					}
				}

			}
		}, 1500);

	}

}

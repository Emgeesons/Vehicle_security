package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.Session;
import com.urbanairship.push.PushManager;

public class MainActivity extends SherlockFragmentActivity implements
		OnItemClickListener {

	private DrawerLayout drawlayout = null;
	private ListView listview = null;
	private ActionBarDrawerToggle actbardrawertoggle = null;

	private String[] itemname = null;
	private int[] photo = null;
	int selectedPosition = -1;
	SharedPreferences atPrefs;
	Data info;
	DatabaseHandler db;
	SQLiteDatabase dbb;
	// boolean check = true;
	static String check = "check";
	String callcheck = "callcheck";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		db = new DatabaseHandler(getApplicationContext());
		try {

			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dbb = db.openDataBase();
		dbb = db.getReadableDatabase();
		itemname = new String[] { "Home", "Emergency Numbers", "", "Feedback",
				"Share App", "Rate Us", "", "Logout" };
		info = new Data();
		photo = new int[] { R.drawable.ic_nav_home,
				R.drawable.ic_nav_emergency_numbers, R.drawable.ic_nav_home,
				R.drawable.ic_nav_feedback, R.drawable.ic_nav_share_app,
				R.drawable.ic_nav_rate_us, R.drawable.ic_nav_home,
				R.drawable.ic_nav_logout, };

		drawlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		listview = (ListView) findViewById(R.id.listview_drawer);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setIcon(R.drawable.app_icon);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#060606")));
		drawlayout.setDrawerShadow(R.drawable.box_gry, GravityCompat.START);
		drawlayout.setBackgroundColor(Color.WHITE);

		MenuListAdapter menuadapter = new MenuListAdapter(
				getApplicationContext(), itemname, photo);
		listview.setAdapter(menuadapter);
		atPrefs = PreferenceManager
				.getDefaultSharedPreferences(MainActivity.this);
		actbardrawertoggle = new ActionBarDrawerToggle(this, drawlayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);

			}

		};
		drawlayout.setDrawerListener(actbardrawertoggle);

		listview.setOnItemClickListener(this);

		if (savedInstanceState == null) {
			selectItem(0);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		actbardrawertoggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		actbardrawertoggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		selectItem(position);

	}

	private void selectItem(int position) {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		// Locate Position

		switch (position) {
		case 0:
			if (selectedPosition == 0) {
				listview.setItemChecked(position, true);
				selectedPosition = position;
				drawlayout.closeDrawer(listview);
				getSupportActionBar().setBackgroundDrawable(
						new ColorDrawable(Color.parseColor("#060606")));
				return;
			}
			HomescreenActivity fragment1 = new HomescreenActivity();
			ft.replace(R.id.content_frame, fragment1);
			getSupportActionBar().setBackgroundDrawable(
					new ColorDrawable(Color.parseColor("#060606")));
			getSupportActionBar().setTitle(
					Html.fromHtml("<font color='#ffffff'></font>"));

			break;
		case 1:
			EmergencyNumbers fragment2 = new EmergencyNumbers();
			ft.replace(R.id.content_frame, fragment2);
			getSupportActionBar().setBackgroundDrawable(
					new ColorDrawable(Color.parseColor("#060606")));
			getSupportActionBar()
					.setTitle(
							Html.fromHtml("<font color='#ffffff'>Emergency Numbers</font>"));
			// hide dropdown from action bar
			getSupportActionBar()
					.setNavigationMode(
							com.actionbarsherlock.app.ActionBar.NAVIGATION_MODE_STANDARD);

			getSupportActionBar().setDisplayShowCustomEnabled(false);

			break;

		case 3:
			// hide dropdown from action bar
			getSupportActionBar()
					.setNavigationMode(
							com.actionbarsherlock.app.ActionBar.NAVIGATION_MODE_STANDARD);

			if (!atPrefs.getBoolean(info.checkllogin, true)) {
				Feedback feedback = new Feedback();
				ft.replace(R.id.content_frame, feedback);
				getSupportActionBar().setBackgroundDrawable(
						new ColorDrawable(Color.parseColor("#060606")));
				getSupportActionBar().setTitle(
						Html.fromHtml("<font color='#ffffff'>Feedback</font>"));

			} else {
				Intent next = new Intent(getApplicationContext(),
						LoginActivity.class);
				startActivity(next);
			}

			break;
		case 4:

			String message;
			message = "Check out My Wheels by Crime Stoppers, South Australia. Download it from - https://play.google.com/store/apps/details?id=com.emgeesons.crime_stoppers.vehicle_security";
			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("text/plain");
			share.putExtra(Intent.EXTRA_TEXT, message);
			startActivity(Intent.createChooser(share, "Share My Wheels"));
			break;
		case 5:
			startActivity(new Intent(
					Intent.ACTION_VIEW,
					Uri.parse("https://play.google.com/store/apps/details?id=com.emgeesons.crime_stoppers.vehicle_security")));
			// selectItem(0);
			break;

		case 7:
			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this);
			builder.setMessage("Are you sure you want to Logout?")
					.setCancelable(false)
					.setMessage(
							"Logging out of the App will erase all User data.")
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

								}
							})
					.setPositiveButton("Continue",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									PushManager.disablePush();
									SplashscreenActivity.fblogin = true;
									atPrefs.edit()
											.putBoolean(info.checkllogin, true)
											.commit();
									atPrefs.edit()
											.putString(info.glatitude,
													String.valueOf("not"))
											.commit();
									atPrefs.edit()
											.putString(info.glongitude,
													String.valueOf("not"))

											.commit();
									atPrefs.edit()
											.putString(
													SplashscreenActivity.profile_pic,
													"").commit();
									Session session = Session
											.getActiveSession();
									if (session != null) {

										if (!session.isClosed()) {
											session.closeAndClearTokenInformation();
											// clear your preferences if saved
										}
									} else {

										session = new Session(MainActivity.this);
										Session.setActiveSession(session);

										session.closeAndClearTokenInformation();
										// clear your preferences if saved

									}
									dbb.execSQL("delete from Vehicle_info");
									dbb.execSQL("delete from Vehicle_park");
									dbb.execSQL("UPDATE profile SET user_id ='',fName='',lName='',email='',mobileNumber='',dob='',gender='',licenseNo='',street='',address='',postcode='',dtModified='',fbId='',fbToken='',contact_name='',contact_number='',pin='',squs='',sans='',spoints=''");

									Intent nextscreen = new Intent(
											MainActivity.this,
											LoginActivity.class);
									startActivity(nextscreen);
									finish();
								}

							});
			builder.show();

			break;

		}
		// ft.addToBackStack(null);
		ft.commit();
		listview.setItemChecked(position, true);
		selectedPosition = position;
		drawlayout.closeDrawer(listview);
	}

	@Override
	public void onBackPressed() {
		// to go back to 1st frg
		if (selectedPosition != 0) {
			selectItem(0);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		try {
			boolean foregroud = new ForegroundCheckTask().execute(
					getApplicationContext()).get();
			atPrefs.edit().putString(check, String.valueOf(foregroud)).commit();
			Log.i("onStop", String.valueOf(foregroud));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onStop();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			boolean foregroud = new ForegroundCheckTask().execute(
					getApplicationContext()).get();
			Log.i("onResume", String.valueOf(foregroud));
			String ch = atPrefs.getString(check, "true");
			String chh = atPrefs.getString(callcheck, "true");
			Log.i("con", ch);
			Log.i("conn", chh);
			if (ch.equalsIgnoreCase("false")) {
				if (chh.equalsIgnoreCase("true")) {
					// when we open maps,pic dont show pin
					atPrefs.edit().putString(callcheck, "false").commit();
					return;
				} else {
					if (!atPrefs.getBoolean(info.checkllogin, true)) {
//						Intent ne = new Intent(getApplicationContext(),
//								PinLock.class);
//						startActivity(ne);
					}

				}

			
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		try {
			boolean foregroud = new ForegroundCheckTask().execute(
					getApplicationContext()).get();
			Log.i("onPause", String.valueOf(foregroud));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// @Override
	// protected void onStart() {
	// // TODO Auto-generated method stub
	// atPrefs.edit().putBoolean(info.lockcheck, true).commit();
	//
	// //
	// Log.i("main", "onStart");
	// super.onStart();
	// }
	//
	// @Override
	// protected void onResume() {
	// Log.i("main", "onResume");
	//
	// if (!atPrefs.getBoolean(info.lock, true)
	// && !atPrefs.getBoolean(info.checkllogin, true)
	// && atPrefs.getBoolean(info.lockcheck, true)) {
	// Log.i("main", "on");
	// Intent n = new Intent(getApplicationContext(), PinLock.class);
	// startActivity(n);
	//
	// }
	// super.onResume();
	//
	// };
	//
	// @Override
	// protected void onPause() {
	// if (!atPrefs.getBoolean(info.checkllogin, true)) {
	// atPrefs.edit().putBoolean(info.lock, false).commit();
	// Log.i("main", "onPause");
	// atPrefs.edit().putBoolean(info.lockcheck, false).commit();
	// super.onPause();
	// }
	// };
	//
	// @Override
	// protected void onStop() {
	// // TODO Auto-generated method stub
	// if (!atPrefs.getBoolean(info.checkllogin, true)) {
	// Log.i("main", "onStop");
	// atPrefs.edit().putBoolean(info.lock, false).commit();
	// // atPrefs.edit().putBoolean(info.lockcheck, false).commit();
	// super.onStop();
	// }
	// }
	//
	// @Override
	// protected void onDestroy() {
	// if (!atPrefs.getBoolean(info.checkllogin, true)) {
	// atPrefs.edit().putBoolean(info.lock, false).commit();
	// // atPrefs.edit().putBoolean(info.lockcheck, false).commit();
	// super.onPause();
	// }
	//
	// };

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {
			if (drawlayout.isDrawerOpen(listview)) {
				drawlayout.closeDrawer(listview);
			} else {
				drawlayout.openDrawer(listview);
			}
		}
		return super.onOptionsItemSelected(item);
	}

}

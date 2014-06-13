package com.emgeesons.crime_stoppers.vehicle_security;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
	boolean check = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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
		getSupportActionBar().setIcon(R.drawable.ic_app);
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
			break;

		case 3:
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
			message = "Check Out My Wheels :-\n https://play.google.com/store/apps/details?id=com.emgeesons.crime_stoppers.vehicle_security";
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
			SplashscreenActivity.fblogin = true;
			atPrefs.edit().putBoolean(info.checkllogin, true).commit();
			Session session = Session.getActiveSession();
			if (session != null) {

				if (!session.isClosed()) {
					session.closeAndClearTokenInformation();
					// clear your preferences if saved
				}
			} else {

				session = new Session(this);
				Session.setActiveSession(session);

				session.closeAndClearTokenInformation();
				// clear your preferences if saved

			}
			Intent nextscreen = new Intent(MainActivity.this,
					LoginActivity.class);
			startActivity(nextscreen);
			finish();
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

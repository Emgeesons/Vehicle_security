package com.emgeesons.crime_stoppers.vehicle_security;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class Updates extends SherlockFragmentActivity implements
		ActionBar.TabListener {

	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	// Tab titles
	private String[] tabs = { "Others", "My Updates" };
	Connection_Detector cd = new Connection_Detector(Updates.this);
	boolean IsInternetPresent;
	String myUpdates_url = Data.url + "myUpdates.php";
	ProgressDialog pDialog;
	Data info;
	GPSTracker gps;
	SharedPreferences atPrefs;
	static List<String> mStrings = new ArrayList<String>();
	static List<String> OStrings = new ArrayList<String>();
	static List<String> imageList = new ArrayList<String>();
	static List<String> imageLists = new ArrayList<String>();
	protected static ImageLoader imageLoader;
	boolean check;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.updates);

		// Initilization
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
		info = new Data();
		atPrefs = PreferenceManager.getDefaultSharedPreferences(Updates.this);
		atPrefs.edit().putInt("updates", 0).commit();
		HomescreenActivity.checkupdate(getApplicationContext());
		viewPager.setAdapter(mAdapter);
		// actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				Updates.this).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		ImageLoader.getInstance().init(config);
		if (!atPrefs.getBoolean(info.checkllogin, true)) {
			atPrefs.edit()
					.putString("time",
							String.valueOf(System.currentTimeMillis()))
					.commit();
		}
		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// if (arg0 == 1) {
				// if (atPrefs.getBoolean(Data.checkllogin, true)) {
				//
				// AlertDialog.Builder builder = new AlertDialog.Builder(
				// Updates.this);
				// builder.setMessage("Please log in to use this feature")
				// .setCancelable(false)
				// // .setMessage("Are you sure?")
				// .setNegativeButton("Cancel",
				// new DialogInterface.OnClickListener() {
				//
				// @Override
				// public void onClick(DialogInterface dialog,
				// int which) {
				// dialog.dismiss();
				// }
				// })
				// .setPositiveButton("Sign in",
				// new DialogInterface.OnClickListener() {
				// public void onClick(DialogInterface dialog,
				// int id) {
				// Intent next = new Intent(Updates.this,
				// LoginActivity.class);
				// startActivity(next);
				// finish();
				//
				// }
				//
				// });
				// builder.show();
				//
				// }
				// }
			}
		});
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// only login user
		if (tab.getPosition() == 1) {
			if (atPrefs.getBoolean(Data.checkllogin, true)) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						Updates.this);
				builder.setMessage("Please log in to use this feature")
						.setCancelable(false)
						// .setMessage("Are you sure?")
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										actionBar.setSelectedNavigationItem(0);
										dialog.dismiss();
									}
								})
						.setPositiveButton("Sign in",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										Intent next = new Intent(Updates.this,
												LoginActivity.class);
										next.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
												| Intent.FLAG_ACTIVITY_CLEAR_TASK);
										startActivity(next);
										finish();

									}

								});
				builder.show();

			}
		}

		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:

			onBackPressed();

			break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		OStrings.clear();
		mStrings.clear();
		imageLoader.clearMemoryCache();
		imageLoader.clearDiskCache();
		// Intent next = new Intent(getApplicationContext(),
		// MainActivity.class);
		// startActivity(next);
		// finish();

		super.onBackPressed();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		try {

			boolean foregroud = new ForegroundCheckTask().execute(Updates.this)
					.get();
			atPrefs.edit().putString("check", String.valueOf(foregroud))
					.commit();
			// Log.i("onStop", String.valueOf(foregroud));
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
		// pin check
		// time @15min
		String time = atPrefs.getString("time", "");
		// String times = String.valueOf(System.currentTimeMillis());
		long t = Long.valueOf(time);
		long tw = System.currentTimeMillis();
		String ch = atPrefs.getString("check", "true");
		String chh = atPrefs.getString("callcheck", "true");
		// Log.i("con", ch);
		// Log.i("cons", chh);
		// Log.i("call form", getClass().getName());
		if (ch.equalsIgnoreCase("false")) {
			if (chh.equalsIgnoreCase("true")) {
				// when we open maps,pic dont show pin
				// Toast.makeText(getApplicationContext(), "PinLock false",
				// Toast.LENGTH_LONG).show();
				atPrefs.edit().putString("callcheck", "false").commit();
				return;
			} else {
				// Toast.makeText(getApplicationContext(), "PinLock",
				// Toast.LENGTH_LONG).show();
				if (!atPrefs.getBoolean(info.checkllogin, true)) {
					boolean s = tw > t;
					// Toast.makeText(getApplicationContext(),
					// "PinLock" + " " + s, Toast.LENGTH_LONG).show();
					long d = Math.abs(tw - t);
					Log.i("math", String.valueOf(d));
					if (Math.abs(tw - t) > 900000) {
						// Toast.makeText(getApplicationContext(),
						// "PinLock enter", Toast.LENGTH_LONG).show();
						Intent ne = new Intent(getApplicationContext(),
								PinLock.class);
						startActivity(ne);
					} else {
						// Toast.makeText(getApplicationContext(),
						// "PinLock else",
						// Toast.LENGTH_LONG).show();
					}

				}

			}

		}

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		try {
			boolean foregroud = new ForegroundCheckTask().execute(Updates.this)
					.get();
			// Log.i("onPause", String.valueOf(foregroud));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

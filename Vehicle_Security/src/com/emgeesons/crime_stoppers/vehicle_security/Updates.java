package com.emgeesons.crime_stoppers.vehicle_security;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;

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
		viewPager.setAdapter(mAdapter);
		// actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				Updates.this).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		ImageLoader.getInstance().init(config);
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
				if (arg0 == 1) {
					if (atPrefs.getBoolean(Data.checkllogin, true)) {
						Intent next = new Intent(getApplicationContext(),
								LoginActivity.class);
						startActivity(next);
						finish();
					}
				}
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
				Intent next = new Intent(getApplicationContext(),
						LoginActivity.class);
				startActivity(next);
				finish();
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
		mStrings.clear();
		imageLoader.clearMemoryCache();
		imageLoader.clearDiskCache();
		Intent next = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(next);
		finish();

		super.onBackPressed();
	}
}

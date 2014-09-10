package com.emgeesons.crime_stoppers.vehicle_security;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {
		Fragment f = null;
		switch (index) {
		case 0:

			return new Otherupdates();
		case 1:

			f = Myupdates.newInstance();
			// return new Myupdates();

		}

		return f;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 2;
	}

}

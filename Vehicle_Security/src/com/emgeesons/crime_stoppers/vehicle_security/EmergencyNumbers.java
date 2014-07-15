package com.emgeesons.crime_stoppers.vehicle_security;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;

public class EmergencyNumbers extends SherlockFragment {
	View view;
	RelativeLayout police, raa, tow, sub;
	int police_num = 131444;
	int raa_num = 131111;
	int tow_num = 82315555;
	int sub_num = 131008;
	String callcheck = "callcheck";
	SharedPreferences atPrefs;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.emergency_numbers, container, false);
		atPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		police = (RelativeLayout) view.findViewById(R.id.police);
		raa = (RelativeLayout) view.findViewById(R.id.raa);
		tow = (RelativeLayout) view.findViewById(R.id.tow);
		sub = (RelativeLayout) view.findViewById(R.id.sub);
		police.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent call = new Intent(Intent.ACTION_CALL);
				call.setData(Uri.parse("tel:" + police_num));
				atPrefs.edit().putString(callcheck, "True").commit();
				startActivity(call);
			}
		});
		raa.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent call = new Intent(Intent.ACTION_CALL);
				call.setData(Uri.parse("tel:" + raa_num));
				atPrefs.edit().putString(callcheck, "True").commit();
				startActivity(call);
			}
		});
		tow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent call = new Intent(Intent.ACTION_CALL);
				call.setData(Uri.parse("tel:" + tow_num));
				atPrefs.edit().putString(callcheck, "True").commit();
				startActivity(call);
			}
		});
		sub.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent call = new Intent(Intent.ACTION_CALL);
				call.setData(Uri.parse("tel:" + sub_num));
				atPrefs.edit().putString(callcheck, "True").commit();
				startActivity(call);
			}
		});

		return view;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon @ action bar clicked; go home
			Intent back = new Intent(getActivity(), MainActivity.class);
			startActivity(back);
			getActivity().finish();
			break;
		}
		return true;
	}

}

package com.emgeesons.crime_stoppers.vehicle_security;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class Otherupdates extends Fragment {
	int novehicles;
	RelativeLayout addetails, main;
	Button go;
	List<ParkingData> vehicles;
	DatabaseHandler db;
	SQLiteDatabase dbb;
	ListView data;
	ArrayAdapter<String> mainmenuarrayAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater
				.inflate(R.layout.otherupdate, container, false);

		return rootView;
	}

}

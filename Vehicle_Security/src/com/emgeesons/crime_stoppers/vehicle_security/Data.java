package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

public class Data {
	DatabaseHandler db;
	SQLiteDatabase dbb;
	String manufacturer, model, Version;

	String user_id, fName, lName, email, mobileNumber, dob, gender, licenseNo,
			street, suburb, postcode, dtModified, fbId, fbToken, contact_name,
			contact_number;

	public void device() {
		manufacturer = Build.MANUFACTURER;
		model = Build.MODEL;
		Version = Build.VERSION.RELEASE;

	}

	public void showInfo(Context _context) {

		manufacturer = Build.MANUFACTURER;
		model = Build.MODEL;
		Version = Build.VERSION.RELEASE;

		db = new DatabaseHandler(_context);
		try {

			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}

		dbb = db.openDataBase();
		dbb = db.getReadableDatabase();

		String selectQuery = "SELECT * FROM profile";
		SQLiteDatabase dbbb = db.getReadableDatabase();
		//

		Cursor cursor = dbbb.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				user_id = cursor.getString(cursor.getColumnIndex("user_id"));
				fName = cursor.getString(cursor.getColumnIndex("fName"));
				lName = cursor.getString(cursor.getColumnIndex("lName"));
				email = cursor.getString(cursor.getColumnIndex("email"));
				mobileNumber = cursor.getString(cursor
						.getColumnIndex("mobileNumber"));
				dob = cursor.getString(cursor.getColumnIndex("dob"));
				// String[] datespilt = dob.split("\\s+");
				// dob = String.valueOf(datespilt[0]);
				//
				gender = cursor.getString(cursor.getColumnIndex("gender"));

				licenseNo = cursor
						.getString(cursor.getColumnIndex("licenseNo"));
				street = cursor.getString(cursor.getColumnIndex("street"));
				suburb = cursor.getString(cursor.getColumnIndex("suburb"));
				postcode = cursor.getString(cursor.getColumnIndex("postcode"));
				dtModified = cursor.getString(cursor
						.getColumnIndex("dtModified"));
				fbId = cursor.getString(cursor.getColumnIndex("fbId"));
				fbToken = cursor.getString(cursor.getColumnIndex("fbToken"));
				contact_name = cursor.getString(cursor
						.getColumnIndex("contact_name"));
				contact_number = cursor.getString(cursor
						.getColumnIndex("contact_number"));

			} while (cursor.moveToNext());
		}
	}
}

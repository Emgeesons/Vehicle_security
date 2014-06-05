package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

public class Data {
	DatabaseHandler db;
	SQLiteDatabase dbb;
	String manufacturer, model, Version;
	static String url = "http://emgeesonsdevelopment.in/crimestoppers/mobile1.0/";

	String user_id, fName, lName, email, mobileNumber, dob, gender, licenseNo,
			street, address, postcode, dtModified, fbId, fbToken, contact_name,
			contact_number, pin, sans;
	int squs;

	int year, month, date;

	public void device() {
		manufacturer = Build.MANUFACTURER;
		model = Build.MODEL;
		Version = Build.VERSION.RELEASE;

	}

	public int getAge(int years, int months, int day) {

		GregorianCalendar cal = new GregorianCalendar();
		int y, m, d, a;

		y = cal.get(Calendar.YEAR);
		m = cal.get(Calendar.MONTH);
		d = cal.get(Calendar.DAY_OF_MONTH);
		cal.set(years, months, day);
		a = y - cal.get(Calendar.YEAR);
		if ((m < cal.get(Calendar.MONTH) + 1)
				|| ((m == cal.get(Calendar.MONTH) + 1) && (d < cal
						.get(Calendar.DAY_OF_MONTH)))) {
			--a;
		}
		if (a == -1) {
			a = 0;
		}
		return a;
	}

	public int qusvalues(String qus) {
		if (qus.equalsIgnoreCase("What’s your Passport Number ?")) {
			return 0;
		} else if (qus.equalsIgnoreCase("What’s your License Number ?")) {
			return 1;
		} else if (qus.equalsIgnoreCase("What’s your Mothers Maiden Name ?")) {
			return 2;
		} else if (qus.equalsIgnoreCase("What’s your First Pets Name ?")) {
			return 3;
		} else if (qus
				.equalsIgnoreCase("Who was your First Childhood Friend ?")) {
			return 4;
		} else if (qus
				.equalsIgnoreCase("What Primary School did you First Attend ?")) {
			return 5;
		} else if (qus
				.equalsIgnoreCase("What was the Colour of your First Car ?")) {
			return 6;
		} else if (qus
				.equalsIgnoreCase("What is your All Time Favourite Movie ?")) {
			return 7;
		} else if (qus.equalsIgnoreCase("What was your First Paid Job ?")) {
			return 8;
		} else {
			return 9;
		}

	}

	public void showInfo(Context _context) {

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
				try {
					String[] datespilt = dob.split("\\-");
					year = Integer.valueOf(datespilt[0]);
					month = Integer.valueOf(datespilt[1]);
					date = Integer.valueOf(datespilt[2]);
				} catch (Exception e) {
					// TODO: handle exception
				}

				gender = cursor.getString(cursor.getColumnIndex("gender"));

				licenseNo = cursor
						.getString(cursor.getColumnIndex("licenseNo"));
				street = cursor.getString(cursor.getColumnIndex("street"));
				address = cursor.getString(cursor.getColumnIndex("address"));
				postcode = cursor.getString(cursor.getColumnIndex("postcode"));
				dtModified = cursor.getString(cursor
						.getColumnIndex("dtModified"));
				fbId = cursor.getString(cursor.getColumnIndex("fbId"));
				fbToken = cursor.getString(cursor.getColumnIndex("fbToken"));
				contact_name = cursor.getString(cursor
						.getColumnIndex("contact_name"));
				contact_number = cursor.getString(cursor
						.getColumnIndex("contact_number"));
				pin = cursor.getString(cursor.getColumnIndex("pin"));
				squs = cursor.getInt(cursor.getColumnIndex("squs"));
				sans = cursor.getString(cursor.getColumnIndex("sans"));
			} while (cursor.moveToNext());
		}
	}
}

package com.emgeesons.crime_stoppers.vehicle_security;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "DATABASE";

	static final String TABLE_profile = "profile";
	static final String TABLE_Vehicle = "Vehicle_info";
	static final String TABLE_parking = "Vehicle_park";

	//
	private static String DB_PATH = "/data/data/com.emgeesons.crime_stoppers.vehicle_security/databases/";

	private SQLiteDatabase myDataBase;
	private static Context myContext;

	// // Columns names for profile
	private static final String profile_ID = "id";
	private static final String profile_user_id = "user_id";
	private static final String profile_fName = "fName";
	private static final String profile_lName = "lName";
	private static final String profile_email = "email";
	private static final String profile_mobileNumber = "mobileNumber";
	private static final String profile_dob = "dob";
	private static final String profile_gender = "gender";
	private static final String profile_licenseNo = "licenseNo";
	private static final String profile_street = "street";
	private static final String profile_address = "address";
	private static final String profile_postcode = "postcode";
	private static final String profile_dtModified = "dtModified";
	private static final String profile_fbId = "fbId";
	private static final String profile_fbToken = "fbToken";
	private static final String profile_cname = "contact_name";
	private static final String profile_cnumber = "contact_number";
	private static final String profile_pin = "pin";
	private static final String profile_squs = "squs";
	private static final String profile_ans = "sans";
	private static final String profile_spoints = "spoints";

	// // Columns names for vehicle info
	private static final String vehicle_tableID = "id";
	private static final String vehicle_id = "vehicle_id";
	private static final String vehicle_type = "vehicle_type";
	private static final String vehicle_make = "vehicle_make";
	private static final String vehicle_model = "vehicle_model";
	private static final String vehicle_body = "vehicle_body";
	private static final String vehicle_eng = "vehicle_eng";
	private static final String vehicle_ch = "vehicle_ch";
	private static final String vehicle_colour = "vehicle_colour";
	private static final String vehicle_acc = "vehicle_acc";
	private static final String vehicle_reg = "vehicle_reg";
	private static final String vehicle_insname = "vehicle_insname";
	private static final String vehicle_insno = "vehicle_insno";
	private static final String vehicle_insexp = "vehicle_insexp";
	private static final String vehicle_status = "vehicle_status";
	private static final String vehicle_expmil = "vehicle_expmil";
	private static final String vehicle_insnum = "vehicle_insnum";
	private static final String vehicle_state = "vehicle_state";

	// // Vehicle_park
	private static final String vehicle_ID = "id";
	private static final String vehicle_vid = "vid";
	private static final String vehicle_vmodel = "model";
	private static final String vehicle_lat = "lat";
	private static final String vehicle_lon = "lon";
	private static final String vehicle_comm = "comm";
	private static final String vehicle_check = "mark";
	private static final String vehicle_vtype = "type";

	/**
	 * /** Constructor Takes and keeps a reference of the passed context in
	 * order to access to the application assets and resources.
	 * 
	 * @param context
	 */
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.myContext = context;
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * 
	 * @return
	 * */
	public SQLiteDatabase createDataBase() throws IOException {

		boolean dbExist = checkDataBase();

		if (dbExist) {
			// do nothing - database already exist
		} else {

			// By calling this method and empty database will be created into
			// the default system path
			// of your application so we are gonna be able to overwrite that
			// database with our database.
			this.getReadableDatabase();

			try {

				copyDataBase();

			} catch (IOException e) {

				throw new RuntimeException(e);
			}
		}
		return myDataBase;

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {

		SQLiteDatabase checkDB = null;

		try {
			String myPath = DB_PATH + DATABASE_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException e) {

			// database does't exist yet.

		}

		if (checkDB != null) {

			checkDB.close();

		}

		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException {

		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DATABASE_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DATABASE_NAME;

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	public SQLiteDatabase openDataBase() throws SQLException {

		// Open the database
		String myPath = DB_PATH + DATABASE_NAME;
		return myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);

	}

	@Override
	public synchronized void close() {

		if (myDataBase != null)
			myDataBase.close();

		super.close();

	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed

	}

	public void updateprofileData(PersonalData data) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(profile_user_id, data.getuserid());
		values.put(profile_fName, data.getfName());
		values.put(profile_lName, data.getlName());
		values.put(profile_email, data.getemail());
		values.put(profile_mobileNumber, data.getmobileNumber());
		values.put(profile_dob, data.getdob());
		values.put(profile_gender, data.getgender());
		values.put(profile_licenseNo, data.getlicenseNo());
		values.put(profile_street, data.getstreet());
		values.put(profile_address, data.getaddress());
		values.put(profile_postcode, data.getpostcode());
		values.put(profile_dtModified, data.getdtModified());
		values.put(profile_fbId, data.getfbId());
		values.put(profile_fbToken, data.getfbToken());
		values.put(profile_cname, data.getcname());
		values.put(profile_cnumber, data.getcnumber());
		values.put(profile_pin, data.getpin());
		values.put(profile_squs, data.getsqus());
		values.put(profile_ans, data.getsans());
		values.put(profile_spoints, data.getspoints());

		// Inserting Row
		// db.insert(TABLE_profile, null, values);
		db.update(TABLE_profile, values, profile_ID + " = ?",
				new String[] { String.valueOf(1) });
		db.close(); // Closing database connection
	}

	public List<PersonalData> getConditionData() {
		List<PersonalData> con = new ArrayList<PersonalData>();

		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_profile;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				PersonalData data = new PersonalData(cursor.getString(1),
						cursor.getString(2), cursor.getString(3),
						cursor.getString(4), cursor.getString(5),
						cursor.getString(6), cursor.getString(7),
						cursor.getString(8), cursor.getString(9),
						cursor.getString(10), cursor.getString(11),
						cursor.getString(12), cursor.getString(13),
						cursor.getString(14), cursor.getString(15),
						cursor.getString(16), cursor.getString(17),
						cursor.getString(18), cursor.getString(19),
						cursor.getString(20));
				con.add(data);
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return con;
	}

	// vehicle info
	public void insertvehicleData(VehicleData data) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(vehicle_id, data.getvehicle_id());
		values.put(vehicle_type, data.getvehicle_type());
		values.put(vehicle_make, data.getvehicle_make());
		values.put(vehicle_model, data.getvehicle_model());
		values.put(vehicle_body, data.getvehicle_body());
		values.put(vehicle_eng, data.getvehicle_eng());
		values.put(vehicle_ch, data.getvehicle_ch());
		values.put(vehicle_colour, data.getvehicle_colour());
		values.put(vehicle_acc, data.getvehicle_acc());
		values.put(vehicle_reg, data.getvehicle_reg());
		values.put(vehicle_insname, data.getvehicle_insname());
		values.put(vehicle_insno, data.getvehicle_insno());
		values.put(vehicle_insexp, data.getvehicle_insexp());
		values.put(vehicle_status, data.getstatus());
		values.put(vehicle_expmil, data.getexpmil());
		values.put(vehicle_insnum, data.getinumber());
		values.put(vehicle_state, data.getstate());
		// Inserting Row
		db.insert(TABLE_Vehicle, null, values);
		db.close(); // Closing database connection
	}

	public void updatevehicleData(VehicleData data, String id) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(vehicle_id, data.getvehicle_id());
		values.put(vehicle_type, data.getvehicle_type());
		values.put(vehicle_make, data.getvehicle_make());
		values.put(vehicle_model, data.getvehicle_model());
		values.put(vehicle_body, data.getvehicle_body());
		values.put(vehicle_eng, data.getvehicle_eng());
		values.put(vehicle_ch, data.getvehicle_ch());
		values.put(vehicle_colour, data.getvehicle_colour());
		values.put(vehicle_acc, data.getvehicle_acc());
		values.put(vehicle_reg, data.getvehicle_reg());
		values.put(vehicle_insname, data.getvehicle_insname());
		values.put(vehicle_insno, data.getvehicle_insno());
		values.put(vehicle_insexp, data.getvehicle_insexp());
		values.put(vehicle_status, data.getstatus());
		values.put(vehicle_expmil, data.getexpmil());

		// Inserting Row
		db.update(TABLE_Vehicle, values, vehicle_id + " = ?",
				new String[] { String.valueOf(id) });

		db.close(); // Closing database connection
	}

	public List<VehicleData> getVehicleData() {
		List<VehicleData> con = new ArrayList<VehicleData>();

		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_Vehicle;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				VehicleData data = new VehicleData(cursor.getInt(1),
						cursor.getString(2), cursor.getString(3),
						cursor.getString(4), cursor.getString(5),
						cursor.getString(6), cursor.getString(7),
						cursor.getString(8), cursor.getString(9),
						cursor.getString(10), cursor.getString(11),
						cursor.getString(12), cursor.getString(13),
						cursor.getString(14), cursor.getString(15),
						cursor.getString(16), cursor.getString(17));
				con.add(data);
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return con;
	}

	// parking (park my vehicle)
	public void inserparkData(ParkingData data) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(vehicle_vid, data.getvehicle_id());
		values.put(vehicle_vmodel, data.getvehicle_model());
		values.put(vehicle_lat, data.getlat());
		values.put(vehicle_lon, data.getlon());
		values.put(vehicle_comm, data.getcomm());
		values.put(vehicle_check, data.getcheck());
		values.put(vehicle_vtype, data.gettype());
		db.insert(TABLE_parking, null, values);
		// long success = db.insert(TABLE_parking, null, values);
		// Log.i("sad", String.valueOf(success));
		db.close(); // Closing database connection
	}

	// parking
	public List<ParkingData> getparkingData() {
		List<ParkingData> con = new ArrayList<ParkingData>();

		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_parking;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				ParkingData data = new ParkingData(cursor.getString(1),
						cursor.getString(2), cursor.getString(3),
						cursor.getString(4), cursor.getString(5),
						cursor.getString(6), cursor.getString(7));
				con.add(data);
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return con;
	}

}
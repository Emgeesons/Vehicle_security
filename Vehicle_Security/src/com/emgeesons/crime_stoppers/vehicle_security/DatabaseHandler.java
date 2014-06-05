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

public class DatabaseHandler extends SQLiteOpenHelper {
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "DATABASE";

	static final String TABLE_profile = "profile";

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

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
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
						cursor.getString(18), cursor.getString(19));
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
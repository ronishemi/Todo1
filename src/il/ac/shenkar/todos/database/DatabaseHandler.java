package il.ac.shenkar.todos.database;

import il.ac.shenkar.todos.ItemDetails;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 5;

	// Database Name
	private static final String DATABASE_NAME = "todoManager";

	// Todos table name
	private static final String TABLE_TODOS = "todos";

	// Contacts Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_ITEMDESCRIPTION = "itemDescription";
	private static final String KEY_DELETED = "deleted";
	private static final String KEY_TIMESTAMP = "timeStamp";
	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_LONGITUDE = "longitude";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {

		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_TODOS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
				+ KEY_ITEMDESCRIPTION + " TEXT," + KEY_DELETED + " TEXT,"
				+ KEY_TIMESTAMP + " TEXT," + KEY_LATITUDE + " TEXT,"
				+ KEY_LONGITUDE + ")";
		db.execSQL(CREATE_CONTACTS_TABLE);

	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODOS);

		// Create tables again
		onCreate(db);

	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new itemDetails
	public void addItemDetails(ItemDetails itemDetails) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put(KEY_NAME, itemDetails.getName()); // item Name
			values.put(KEY_ITEMDESCRIPTION, itemDetails.getItemDescription()); // item
																				// details
			values.put(KEY_DELETED, itemDetails.getDeleted()); // item deleted
			values.put(KEY_TIMESTAMP, itemDetails.getTimeStamp()); // item
																	// timestamp
			values.put(KEY_LATITUDE, itemDetails.getLatitude()); // item
																	// Latitude
			values.put(KEY_LONGITUDE, itemDetails.getLongitude()); // item
																	// Longitude
			// Inserting Row
			db.insert(TABLE_TODOS, null, values);
			db.close(); // Closing database connection
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Getting single contact
	@SuppressWarnings("finally")
	public ItemDetails getItemDetails(Integer id) {
		SQLiteDatabase db = this.getReadableDatabase();
		ItemDetails itemDetails = null;
		try {
			Cursor cursor = db
					.query(TABLE_TODOS, new String[] { KEY_ID, KEY_NAME,
							KEY_ITEMDESCRIPTION, KEY_DELETED, KEY_TIMESTAMP,
							KEY_LATITUDE, KEY_LONGITUDE }, KEY_ID + "=?",
							new String[] { String.valueOf(id) }, null, null,
							null, null);
			if (cursor != null){
				cursor.moveToFirst();
			itemDetails = new ItemDetails(
					Integer.parseInt(cursor.getString(0)), cursor.getString(1),
					cursor.getString(2), cursor.getString(3),
					cursor.getLong(4), cursor.getDouble(5), cursor.getDouble(6));
			// return contact
			}	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(); // Closing database connection
			return itemDetails;
		}
	}

	// Getting All Contacts
	public List<ItemDetails> getAllItemDetails() {
		List<ItemDetails> itemDetailsList = new ArrayList<ItemDetails>();
		
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_TODOS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// Select All Query

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				ItemDetails itemDetails = new ItemDetails();
				itemDetails.set_id(Integer.parseInt(cursor.getString(0)));
				itemDetails.setName(cursor.getString(1));
				itemDetails.setItemDescription(cursor.getString(2));
				itemDetails.setDeleted(cursor.getString(3));
				itemDetails.setTimeStamp(cursor.getLong(4));
				itemDetails.setLatitude(cursor.getDouble(5));
				itemDetails.setLongitude(cursor.getDouble(6));

				// Adding contact to list
				itemDetailsList.add(itemDetails);
			} while (cursor.moveToNext());
		}
		// return contact list
		cursor.close();
		db.close(); // Closing database connection
		return itemDetailsList;
	}

	// Updating single contact
	public int updateItemDetails(ItemDetails itemDetails) {
		SQLiteDatabase db = this.getWritableDatabase();
		int result;
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, itemDetails.getName());
		values.put(KEY_ITEMDESCRIPTION, itemDetails.getItemDescription());
		values.put(KEY_DELETED, itemDetails.getDeleted());
		values.put(KEY_TIMESTAMP, itemDetails.getTimeStamp());
		values.put(KEY_LATITUDE, itemDetails.getLatitude());
		values.put(KEY_LONGITUDE, itemDetails.getLongitude());
		// updating row
		result = db.update(TABLE_TODOS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(itemDetails.get_id()) });
		db.close(); // Closing database connection
		return result;
	}

	// Deleting single contact
	public void deleteItemDetails(ItemDetails itemDetails) {
		if(itemDetails == null)
			return;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			db.delete(TABLE_TODOS, KEY_ID + " = ?",
					new String[] { String.valueOf(itemDetails.get_id()) });
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Getting contacts Count
	public int getItemDetailsCount() {
		int count = 0;
		
		String countQuery = "SELECT  * FROM " + TABLE_TODOS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		count = cursor.getCount();
		cursor.close();
		db.close(); // Closing database connection
		return count;
	}
}

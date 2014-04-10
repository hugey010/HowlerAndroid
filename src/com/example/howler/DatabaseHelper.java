package com.example.howler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

import com.example.howler.WebRequest.Friend;
import com.example.howler.WebRequest.User;

/**
 * Use this class to perform database actions based on web requests.
 * Whatever the user does really shouldn't be stored locally unless the server confirms it.
 * 
 * @author Tyler Hugenberg
 *
 */
public class DatabaseHelper {

	public static List<String> selectedFriends = new ArrayList<String>();

	private static final String TAG = "DatabaseHelper";

	private Context context;
	private SQLiteDatabase db;

	public DatabaseHelper(Context context) {
		this.context = context;
		HowlerDatabaseHelper openHelper = new HowlerDatabaseHelper(this.context);
		this.db = openHelper.getWritableDatabase();
	}

	public String authToken() {
		Cursor c = db.rawQuery("SELECT authtoken FROM users", null);
		c.moveToFirst();
		try {
			Log.d(TAG, "stored auth token = " + c.getString(0));
			String authtoken = c.getString(0);
			return authtoken;

		} catch (Exception e) {
			return null;
		}
	}

	public void setPersistentUser(User user) {
		clearPesistentUser();
		ContentValues contentValues = new ContentValues();
		contentValues.put("identifier", user.getIdentifier());
		contentValues.put("username", user.getUsername());
		contentValues.put("authtoken", user.getAuthtoken());
		this.db.insert("users", null, contentValues);
	}

	public void clearPesistentUser() {
		this.db.execSQL("DELETE FROM users");
	}

	public void insertFriend(Friend friend) {
		ContentValues content = new ContentValues();
		content.put("identifier", friend.getIdentifier());
		content.put("username", friend.getUsername());
		content.put("pending", friend.isPending());

		try {
			this.db.insertOrThrow("friends", null, content);
			Log.d(TAG, "Friend "+friend.getUsername()+" added");
		} catch (SQLException e) {
			Log.e(TAG, "Error replacing friend "+friend.getUsername()+" in database");
			this.db.update("friends", content, "identifier="+friend.getIdentifier(), null);
			Log.d(TAG, "Updated friend info for "+friend.getUsername());
		}

	}

	public void deleteAllFriends() {
		this.db.delete("friends", null, null);
	}

	public List<Friend> getAllFriends() {
		List<Friend> friends = new ArrayList<Friend>();
		Cursor c = db.rawQuery("SELECT * FROM friends", null);
		c.moveToFirst();
		while (c.isAfterLast() == false) {
			Friend f = new Friend();
			f.setIdentifier(c.getString(c.getColumnIndex("identifier")));
			f.setUsername(c.getString(c.getColumnIndex("username")));
			f.setPending(c.getInt(c.getColumnIndex("pending")) == 1);
			friends.add(f);
			c.moveToNext();
		}
		return friends;
	}

	/*
   public long insert(String name, String password) {
      this.insertStmt.bindString(1, name);
      this.insertStmt.bindString(2, password);
      return this.insertStmt.executeInsert();
   }
   public void deleteAll() {
      this.db.delete(TABLE_NAME, null, null);
   }

   public List<String> selectAll(String username, String password) {
      List<String> list = new ArrayList<String>();
      Cursor cursor = this.db.query(TABLE_NAME, new String[] { "name", "password" }, "name = '"+ username +"' AND password= '"+ password+"'", null, null, null, "name desc");
      if (cursor.moveToFirst()) {
        do {
        	 list.add(cursor.getString(0));
        	 list.add(cursor.getString(1));
         } while (cursor.moveToNext()); 
      }
      if (cursor != null && !cursor.isClosed()) {
         cursor.close();
      }
      return list;
   }
	 */

	private static class HowlerDatabaseHelper extends SQLiteOpenHelper {

		public static final int DATABASE_VERSION = 1;
		public static final String DATABASE_NAME = "HowlerDatabase.db";

		HowlerDatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// first database creation
			// create all tables
			db.execSQL("CREATE TABLE users (identifier INTEGER PRIMARY KEY, username TEXT, authtoken TEXT, email TEXT)");
			db.execSQL("CREATE TABLE messages (identifier INTEGER PRIMARY KEY, user_id INTEGER, timestamp TEXT, data BLOB, title TEXT, volume REAL)");
			db.execSQL("CREATE TABLE friends (identifier INTEGER PRIMARY KEY, username TEXT, pending INTEGER)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// NO REASON TO UPGRADE DATABASE FROM INITIAL VERSION
		}
	}
}

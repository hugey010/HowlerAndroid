package com.example.howler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

import com.example.howler.WebRequest.Friend;
import com.example.howler.WebRequest.Message;
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

	public void insertMessageWithoutData(Message message) {
		ContentValues content = new ContentValues();
		content.put("identifier", message.getMessage_id());
		content.put("username", message.getUsername());
		content.put("timestamp", message.getTimestamp());
		content.put("title", message.getTitle());
		content.put("volume", message.getVolume());
		content.put("read", message.getRead());
		try {
			this.db.insertOrThrow("messages", null, content);
			Log.d(TAG, "Message "+message.getTitle()+" added");
		} catch (SQLException e) {
			Log.e(TAG, "Error adding message "+message.getTitle()+" in database: "+e.getMessage());
			this.db.update("messages", content, "identifier="+message.getMessage_id(), null);
			Log.d(TAG, "Updated message info for "+message.getTitle());
		}
	}

	public void addMessageData(byte[] data, String message_id) {
		ContentValues content = new ContentValues();
		content.put("data", data);
		try {
			Log.d(TAG, "Added data for message: "+message_id);
			this.db.update("messages", content, "identifier="+message_id, null);
		} catch (SQLException e) {
			Log.e(TAG, "Error adding data to message: "+e.getMessage());
		}
	}
	
	public List<Message> getAllMessages() {
		List<Message> messages = new ArrayList<Message>();
		Cursor c = db.rawQuery("SELECT * FROM messages", null);
		c.moveToFirst();
		while (c.isAfterLast() == false) {
			Message m = new Message();
			m.setMessage_id(c.getString(c.getColumnIndex("identifier")));
			m.setUsername(c.getString(c.getColumnIndex("username")));
			m.setTitle(c.getString(c.getColumnIndex("title")));
			m.setTimestamp(c.getString(c.getColumnIndex("timestamp")));
			m.setData(c.getBlob(c.getColumnIndex("data")));
			m.setRead(c.getInt(c.getColumnIndex("read")) == 1);
			m.setVolume(c.getDouble(c.getColumnIndex("volume")));
			messages.add(m);
			c.moveToNext();
		}
		return messages;
	}

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
			db.execSQL("CREATE TABLE messages (identifier INTEGER PRIMARY KEY, username TEXT, timestamp TEXT, data BLOB, title TEXT, volume REAL, read INTEGER)");
			db.execSQL("CREATE TABLE friends (identifier INTEGER PRIMARY KEY, username TEXT, pending INTEGER)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// NO REASON TO UPGRADE DATABASE FROM INITIAL VERSION
		}
	}
}

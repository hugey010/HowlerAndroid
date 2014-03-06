package com.example.howler;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/**
 * Use this class to perform database actions based on web requests.
 * Whatever the user does really shouldn't be stored locally unless the server confirms it.
 * 
 * @author Tyler Hugenberg
 *
 */
public class DatabaseHelper {
	private static final String TAG = "DatabaseHelper";
	
   private Context context;
   private SQLiteDatabase db;
   
   public DatabaseHelper(Context context) {
      this.context = context;
      HowlerDatabaseHelper openHelper = new HowlerDatabaseHelper(this.context);
      this.db = openHelper.getWritableDatabase();
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
    	  //db.execSQL("CREATE TABLE messages (identifier INTEGER PRIMARY KEY, user_id INTEGER, timestamp TEXT, data BLOB, title TEXT, volume REAL)");
      }

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	 // NO REASON TO UPGRADE DATABASE FROM INITIAL VERSION
      }
   }
}

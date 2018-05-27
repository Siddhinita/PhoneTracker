package com.example.sid.phonetracker;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by sid on 5/2/18.
 */

public class Person {
    public static final String TABLE_NAME = "Person";
    // Naming the id column with an underscore is good to be consistent
    // with other Android things. This is ALWAYS needed
    public static final String COL_ID = "_id";
    // These fields can be anything you want.
    public static final String COL_FIRSTNAME = "firstname";
    public static final String COL_NO="number";
    public static final String[] FIELDS = { COL_ID, COL_FIRSTNAME, COL_NO};

    /*
     * The SQL code that creates a Table for storing Persons in.
     * Note that the last row does NOT end in a comma like the others.
     * This is a common source of error.
     */
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COL_ID + " INTEGER PRIMARY KEY,"
                    + COL_FIRSTNAME + " TEXT NOT NULL DEFAULT '',"
                    + COL_NO + " INTEGER NOT NULL DEFAULT ''"
                    + ")";

    // Fields corresponding to database columns
    public long id = -1;
    public String firstname = "";
    public long number= -1;

    /**
     * No need to do anything, fields are already set to default values above
     */
    public Person() {
    }

    /**
     * Convert information from the database into a Person object.
     */
    public Person(final Cursor cursor) {
        // Indices expected to match order in FIELDS!
        this.id = cursor.getLong(0);
        this.firstname = cursor.getString(1);
        this.number = cursor.getLong(2);

    }

    /**
     * Return the fields in a ContentValues object, suitable for insertion
     * into the database.
     */
    public static void onCreate(SQLiteDatabase db){
    Log.w("Person_db",CREATE_TABLE);
    db.execSQL(CREATE_TABLE);

    }
    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("Upgrade", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


}

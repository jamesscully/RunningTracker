package com.scullyapps.mdprunningtracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {


    public DBHelper(Context context) {
        super(context, "TripDB", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Trip (" +
                "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "notes TEXT" +
                ")");

        db.execSQL("CREATE TABLE Movement (" +
                "tID INTEGER," +
                "seq INTEGER NOT NULL," +
                "lat REAL NOT NULL," +
                "lng REAL NOT NULL," +
                "FOREIGN KEY(tID) REFERENCES Trip(_id)" +
                ")");

        addTestData(db);
    }

    public void addTestData(SQLiteDatabase db) {
        db.execSQL("INSERT INTO Trip     (name, notes)        VALUES ('21st Jan', 'jsonhere')");
        db.execSQL("INSERT INTO Movement (tID, seq, lat, lng) VALUES (1, 0, -50, 50)");
        db.execSQL("INSERT INTO Movement (tID, seq, lat, lng) VALUES (1, 1, -50, 51)");
        db.execSQL("INSERT INTO Movement (tID, seq, lat, lng) VALUES (1, 2, -50, 52)");
        db.execSQL("INSERT INTO Movement (tID, seq, lat, lng) VALUES (1, 3, -53, 50)");
        db.execSQL("INSERT INTO Movement (tID, seq, lat, lng) VALUES (1, 4, -54, 50)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // remove and reinstall if our tables have updated
        if(oldVersion > newVersion) {
            db.execSQL("DROP TABLE Trip");
            db.execSQL("DROP TABLE Movement");

            onCreate(db);
        }
    }
}

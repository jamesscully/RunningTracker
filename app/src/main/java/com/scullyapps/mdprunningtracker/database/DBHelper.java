package com.scullyapps.mdprunningtracker.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {


    public DBHelper(Context context) {
        super(context, "TripDB", null, 3);
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
                "elev REAL NOT NULL," +
                "time INTEGER," +
                "FOREIGN KEY(tID) REFERENCES Trip(_id)" +
                ")");



        addTestData(db);
    }

    public void addTestData(SQLiteDatabase db) {
        db.execSQL("INSERT INTO Trip     (name, notes)        VALUES ('Indian Ocean Journey', '{\"rating\":3,\"comments\":[\"Met Dave the Sea Dragon\",null,\"Found Half Life 3\"]}')");
        db.execSQL("INSERT INTO Movement (tID, seq, lat, lng, elev, time) VALUES (1, 0, -50, 50, 50, 1577628545)");
        db.execSQL("INSERT INTO Movement (tID, seq, lat, lng, elev, time) VALUES (1, 1, -50, 51, 51, 1577628555)");
        db.execSQL("INSERT INTO Movement (tID, seq, lat, lng, elev, time) VALUES (1, 2, -50, 52, 52, 1577628565)");
        db.execSQL("INSERT INTO Movement (tID, seq, lat, lng, elev, time) VALUES (1, 3, -53, 50, 51, 1577628575)");
        db.execSQL("INSERT INTO Movement (tID, seq, lat, lng, elev, time) VALUES (1, 4, -54, 50, 50, 1577628585)");
    }

    // cannot put this into the content resolver, as we don't want to cast the ContentProvider
    public int getNextTripId() {
        SQLiteDatabase db = getWritableDatabase();

        // note the table name; we're not getting a Trackpoint seq
        Cursor c = db.rawQuery("SELECT seq FROM sqlite_sequence WHERE name='Trip'", null);

        c.moveToFirst();
            int max = c.getInt(0);
        c.close();

        // returns the highest id used + 1 for next Trip
        return (max + 1);
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

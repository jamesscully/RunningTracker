package com.scullyapps.mdprunningtracker.database;

import android.net.Uri;

public class Contract {

    public static final String AUTHORITY = "com.scullyapps.mdprunningtracker.database.RunContentProvider";

    public static final Uri    URI = Uri.parse("content://" + AUTHORITY + "databases/TripDB/");

    public static final Uri ALL_TRIPS = Uri.parse("content://" + AUTHORITY + "/Trip");
    public static final Uri ALL_MOVEMENT = Uri.parse("content://" + AUTHORITY + "/Movement");


    public static Uri ID_URI(Uri uri, int id) {
        return uri.buildUpon().appendPath(id + "").build();
    }

    public static class TRIP {
        public static final String _ID = "_id";
        public static final String NAME = "name";
        public static final String NOTES = "notes";
    }

    public static class MOVEMENT {
        public static final String T_ID = "tID";
        public static final String SEQ  = "seq";
        public static final String LAT  = "lat";
        public static final String LNG  = "lng";
        public static final String ELEV = "elev";
        public static final String TIME = "time";
    }

}

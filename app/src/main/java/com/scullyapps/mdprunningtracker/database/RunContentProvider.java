package com.scullyapps.mdprunningtracker.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RunContentProvider extends ContentProvider {

    DBHelper helper;
    SQLiteDatabase db;

    public static final UriMatcher uriMatcher;

    public static final int CODE_TRIP = 1;
    public static final int CODE_TRIP_ID = 11;

    public static final int CODE_MOVEMENT = 2;
    public static final int CODE_MOVEMENT_ID = 22;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(Contract.AUTHORITY, "Trip/", CODE_TRIP);
        uriMatcher.addURI(Contract.AUTHORITY, "Trip/#", CODE_TRIP_ID);

        uriMatcher.addURI(Contract.AUTHORITY, "Movement/", CODE_MOVEMENT);
        uriMatcher.addURI(Contract.AUTHORITY, "Movement/#", CODE_MOVEMENT_ID);
    }

    @Override
    public boolean onCreate() {

        helper = new DBHelper(this.getContext());
        db = helper.getWritableDatabase();

        return db != null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        String last = uri.getLastPathSegment();

        switch (uriMatcher.match(uri)) {
            case CODE_MOVEMENT:
                return db.query("Movement", projection, selection, selectionArgs, null, null, sortOrder);
            case CODE_TRIP:
                return db.query("Trip", projection, selection, selectionArgs, null, null, sortOrder);

            case CODE_TRIP_ID:
                return db.query("Trip", projection, "_id=" + last, null, null, null, sortOrder);
            case CODE_MOVEMENT_ID:
                return db.query("Movement", projection, "tID=" + last, null, null, null, sortOrder);
        }

        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        if(uri.getLastPathSegment() == null)
            return "vnd.android.cursor.dir/RunContentProvider.data.text";
        else
            return "vnd.android.cursor.item/RunContentProvider.data.text";
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}

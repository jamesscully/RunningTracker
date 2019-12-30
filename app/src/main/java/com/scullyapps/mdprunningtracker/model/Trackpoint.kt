package com.scullyapps.mdprunningtracker.model

import android.database.Cursor
import com.google.android.gms.maps.model.LatLng

// this class functions like a GPX trkpt
data class Trackpoint(val tID  : Int,
                      val seq  : Int,
                      var lat  : Double,
                      var lng  : Double,
                      var elev : Double = -1.0,
                      var time : Int = 0) {

    val latLng = LatLng(lat, lng)


    companion object {
        fun fromCursor(cursor : Cursor) : Trackpoint {
            return Trackpoint(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getDouble(2),
                cursor.getDouble(3),
                cursor.getDouble(4),
                cursor.getInt(5)
            )
        }
    }

}
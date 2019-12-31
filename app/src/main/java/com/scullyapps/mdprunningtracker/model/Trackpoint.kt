package com.scullyapps.mdprunningtracker.model

import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng

// this class functions like a GPX trkpt
data class Trackpoint(val tID  : Int,
                      val seq  : Int,
                      var lat  : Double,
                      var lng  : Double,
                      var elev : Double = -1.0,
                      var time : Long = 0) : Parcelable {

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(tID)
        dest.writeInt(seq)
        dest.writeDouble(lat)
        dest.writeDouble(lng)
        dest.writeDouble(elev)
        dest.writeLong(time)
    }

    override fun describeContents(): Int {
        return 0
    }

    val latLng = LatLng(lat, lng)

    companion object CREATOR : Parcelable.Creator<Trackpoint> {
        override fun createFromParcel(source: Parcel): Trackpoint {
            return Trackpoint(
                source.readInt(),
                source.readInt(),
                source.readDouble(),
                source.readDouble(),
                source.readDouble(),
                source.readLong()
            )
        }

        override fun newArray(size: Int): Array<Trackpoint?> {
            return arrayOfNulls(size)
        }

        fun fromCursor(cursor : Cursor) : Trackpoint {
            return Trackpoint(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getDouble(2),
                cursor.getDouble(3),
                cursor.getDouble(4),
                cursor.getLong(5)
            )
        }
    }





}

package com.scullyapps.mdprunningtracker.model

import android.graphics.Color
import android.location.Location
import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.SphericalUtil

// this class should contain all LatLng values of the Trip; we can add functions for max distance here.

class Movement(tkpts : ArrayList<Trackpoint>) : Parcelable {

    var trackpoints = tkpts

    fun addTrackPoint(trkpt : Trackpoint) {
        trackpoints.add(trkpt)
    }

    fun getTotalDistance() : Double {
        var totalDistance : Double = 0.0
        var start = trackpoints.get(0).latLng

        for(i in 1 until trackpoints.size) {
            val current = trackpoints.get(i).latLng

            // SphericalUtil is a class provided by Google Maps for Android;
            // it calculates the distance between LatLngs in metres.
            totalDistance += SphericalUtil.computeDistanceBetween(start, current)
            start = current
        }
        return totalDistance
    }

    // this function only returns the path to be placed on the map;
    // we define Color, Width, etc elsewhere.
    fun getPlotLine() : PolylineOptions {
        val poly = PolylineOptions()

        for(x in 0 until trackpoints.size) {
            val current = trackpoints[x].latLng
            poly.add(current)
        }

        return poly
    }

    fun getTotalSeconds() : Long {
        val begin = trackpoints[0].time
        val end   = trackpoints[trackpoints.size - 1].time

        return end - begin
    }



    /*\
    ///  Parcelable Code
    \*/

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeTypedList(trackpoints)
    }

    companion object CREATOR : Parcelable.Creator<Movement> {

        override fun createFromParcel(source: Parcel): Movement {
            val trk = ArrayList<Trackpoint>()
            source.readTypedList(trk, Trackpoint.CREATOR)
            return Movement(trk)
        }
        override fun newArray(size: Int): Array<Movement?> {
            return arrayOfNulls(size)
        }

    }

    override fun describeContents(): Int = 0


}
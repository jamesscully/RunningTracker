package com.scullyapps.mdprunningtracker.model

import android.graphics.Color
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.SphericalUtil

// this class should contain all LatLng values of the Trip; we can add functions for max distance here.

class Movement(tkpts : ArrayList<Trackpoint>) {

    var trackpoints = tkpts

    fun addTrackPoint(trkpt : Trackpoint) {
        trackpoints.add(trkpt)
    }

    fun getTotalDistance() : Double {

        var totalDistance : Double = 0.0

        var start = trackpoints.get(0).latLng

        for(i in 1 until trackpoints.size) {
            val current = trackpoints.get(i).latLng
            totalDistance += SphericalUtil.computeDistanceBetween(start, current)

            start = current

        }

        return totalDistance
    }

    fun getPlotLine() : PolylineOptions {
        val poly = PolylineOptions()

        for(x in 0 until trackpoints.size) {
            val current = trackpoints[x].latLng
            poly.add(current)
            println("Adding LatLng = (${current.latitude},${current.longitude})")

        }

        return poly
    }

    fun getTotalUnixTime() : Int {
        val begin = trackpoints[0].time
        val end   = trackpoints[trackpoints.size - 1].time

        val totalUnixTime = end - begin

        return totalUnixTime
    }

}
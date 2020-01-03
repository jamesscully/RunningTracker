package com.scullyapps.mdprunningtracker.services

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.scullyapps.mdprunningtracker.model.Trackpoint


class TrackService : Service() {

    // if the service is bound, then we must be sending data to it.
    // thus, whilst we are not bound we should collect these trackpoints into one group.
    // when we rebind, we check to see if our new trackpoints > 0
    // then we send these across, and clear our copy.


    private val TAG: String = "TrackService";

    lateinit var currentLatLng : LatLng
    var locManager : LocationManager? = null
    var listener   : LocListener? = null

    var trackpoints = ArrayList<Trackpoint>()

    val binder = OurLocBinder()

    val NOTF_CHANNEL_ID = 133742

    var BOUNDED = false

    var onNewLocation: ((track : Trackpoint) -> Unit)? = null



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        startForeground(NOTF_CHANNEL_ID, makeNotification())
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.d(TAG, "Service has been binded")
        BOUNDED = true
        return binder
    }
    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "Service has been unbinded")
        BOUNDED = false
        return super.onUnbind(intent)
    }

    fun initLocManager() {
        if(locManager == null) {
            locManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
    }

    fun makeNotification() : Notification {
        val channel = NotificationChannel("TrackLocService", "Track Location Channel", NotificationManager.IMPORTANCE_HIGH)

        val def = getSystemService(NotificationManager::class.java) as NotificationManager

        def.createNotificationChannel(channel)

        val build = Notification.Builder(this, "TrackLocService")

        build.setAutoCancel(false)
             .setContentTitle("Runt - Tracking Location")
             .setContentText("Runt service is running")

        return build.build()
    }

    fun startTracking() {
        initLocManager()
        listener = LocListener(LocationManager.GPS_PROVIDER)

        // we'll need to get location updates from the GPS provider, and we must check beforehand
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0.2f, listener)
        }
    }


    inner class LocListener(provider : String) : LocationListener {

        var lastLocation : Location

        var sequence = 0

        init {
            lastLocation = Location(provider)
        }

        override fun onLocationChanged(location: Location) {
            val lat = location?.latitude
            val lng = location?.longitude
            currentLatLng  = LatLng(lat, lng)

            val newTrack = Trackpoint(99, sequence++, lat, lng, -1.0, location.time)

            // trackpoints.add()

            if(BOUNDED) {
                onNewLocation?.invoke(newTrack)
            }

            Log.d(TAG, "We've found a location: ${currentLatLng}")


        }
        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) { }
        override fun onProviderEnabled(p0: String?) { }
        override fun onProviderDisabled(p0: String?) { }
    }

    inner class OurLocBinder : Binder() {
        fun getService() : TrackService {
            return this@TrackService
        }

        fun getNewTrackpoints() : ArrayList<Trackpoint> {
            return trackpoints
        }
    }

}
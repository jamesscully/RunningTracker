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
import com.google.android.gms.maps.model.LatLng
import android.util.Log
import androidx.core.content.ContextCompat
import com.scullyapps.mdprunningtracker.R
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

    var trackpoints_buffer = ArrayList<Trackpoint>()

    val binder = OurLocBinder()


    // whether we're bounded; so we can buffer data
    var BOUNDED = false


    var PAUSED = false


    // callback for when we receive an update; this will act upon in TrackActivity
    var onNewLocation: ((track : Trackpoint) -> Unit)? = null


    // constants
    val NOTF_CHANNEL_ID = 133742
    val LOCATION_UPDATE_RATE = 1000L
    val LOCATION_RANGE       = 0.2f

    lateinit var notification        : Notification
    lateinit var notificationManager : NotificationManager


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onCreate() {
        makeNotification()
    }

    override fun onDestroy() {
        Log.d(TAG, "We're being destroyed!")
        locManager?.removeUpdates(listener)
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

    fun makeNotification() {
        val channel = NotificationChannel("TrackLocService", "Track Location Channel", NotificationManager.IMPORTANCE_HIGH)
        val def = getSystemService(NotificationManager::class.java) as NotificationManager

        def.createNotificationChannel(channel)

        val build = Notification.Builder(this, "TrackLocService")

        build.setAutoCancel(false)
             .setContentTitle("Runt - Tracking Location")
             .setContentText("Runt service is running")
             .setSmallIcon(R.drawable.ic_launcher_foreground)

        notificationManager = def
        notification = build.build()
    }

    fun startTracking() {
        initLocManager()
        listener = LocListener(LocationManager.GPS_PROVIDER)

        // we'll need to get location updates from the GPS provider, and we must check beforehand
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_RATE, LOCATION_RANGE, listener)
        }

        PAUSED = false
    }

    fun startForeground() {
        BOUNDED = false
        startForeground(NOTF_CHANNEL_ID, notification)
    }

    fun removeForeground() {
        BOUNDED = true
        stopForeground(false)
    }

    fun stop() {
        Log.d(TAG, "Stopping our service")
        notificationManager.cancelAll()

        locManager?.removeUpdates(listener)
        stopSelf()
    }

    fun pause() {
        PAUSED = true
    }


    inner class LocListener(provider : String) : LocationListener {

        val TAG = "LocListener"

        var lastLocation : Location
        var sequence = 0

        init {
            lastLocation = Location(provider)
        }

        override fun onLocationChanged(location: Location) {

            if(PAUSED)
                return

            val lat = location.latitude
            val lng = location.longitude

            currentLatLng  = LatLng(lat, lng)

            val newTrack = Trackpoint(99, sequence++, lat, lng, -1.0, location.time)

            if(BOUNDED) {
                onNewLocation?.invoke(newTrack)
            } else {
                println("Adding new track to buffer $newTrack")
                trackpoints_buffer.add(newTrack)
            }

            Log.d(TAG, "We've found a location: ${currentLatLng}")
        }
        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) { Log.d(TAG, "onStatusChanged") }
        override fun onProviderEnabled(p0: String?)  { Log.d(TAG, "onProviderEnabled") }
        override fun onProviderDisabled(p0: String?) { Log.e(TAG, "onProviderDisabled") }
    }

    inner class OurLocBinder : Binder() {
        fun getService() : TrackService {
            return this@TrackService
        }

        fun haveNewTrackpoints() : Boolean {
            return (trackpoints_buffer.size > 0)
        }

        fun getNewTrackpoints() : ArrayList<Trackpoint> {
            // create a copy of our buffer, since we're going to need to clear it
            val ret = ArrayList<Trackpoint>(trackpoints_buffer)

            trackpoints_buffer.clear()

            return ret
        }
    }

}
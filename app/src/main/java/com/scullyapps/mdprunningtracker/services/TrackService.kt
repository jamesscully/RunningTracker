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
import androidx.core.content.ContextCompat


class TrackService : Service() {


    private val TAG: String = "TrackService";

    lateinit var currentLatLng : LatLng
    var locManager : LocationManager? = null
    var listener : LocListener? = null

    val binder = OurLocBinder()


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "Service has been started", Toast.LENGTH_LONG).show()
        return START_NOT_STICKY
    }

    override fun onCreate() {
        println("SERVICE HAS BEEN CREATED")
        startForeground(69420, makeNotification())
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "Service has been destroyed", Toast.LENGTH_LONG).show()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
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

        val build = Notification.Builder(this, "TrackLocService").setAutoCancel(false)


        return build.build()
    }

    fun startTracking() {

        println("STARTING TRACKING")

        initLocManager()
        listener = LocListener(LocationManager.GPS_PROVIDER)

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0.5f, listener)
        }
    }


    inner class LocListener(provider : String) : LocationListener {

        var lastLocation : Location

        init {
            lastLocation = Location(provider)
        }

        override fun onLocationChanged(location: Location) {
            val lat = location?.latitude
            val lng = location?.longitude

            currentLatLng  = LatLng(lat, lng)
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
    }

}
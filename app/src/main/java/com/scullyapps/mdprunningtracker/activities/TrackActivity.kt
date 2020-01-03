package com.scullyapps.mdprunningtracker.activities

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.scullyapps.mdprunningtracker.R
import com.scullyapps.mdprunningtracker.model.Trackpoint
import com.scullyapps.mdprunningtracker.services.TrackService
import kotlinx.android.synthetic.main.activity_track.*

class TrackActivity : AppCompatActivity(), OnMapReadyCallback{


    lateinit var locationManager : LocationManager
    lateinit var googleMap : GoogleMap

    val TAG = "TrackActivityTAG"

    var SERVICE_RUNNING = false

    var trackService : TrackService? = null
    var binder : TrackService.OurLocBinder? = null


    var currentLatLng: LatLng = LatLng(0.0,0.0)
    var currentRotation = 0.0f

    var servConnection : TrackConnection = TrackConnection()

    lateinit var START_TRACKPOINT : Trackpoint

    override fun onMapReady(map: GoogleMap?) {
        if(map != null) {
            googleMap = map

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                val currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)

                currentLatLng = latLng

                drawRunnerDot()
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
            }
        }
    }

    inner class TrackConnection : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.d(TAG, "We've disconnected from the service")
            trackService = null
        }

        override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
            Log.d(TAG, "We've connected to the service")
            trackService = (service as TrackService.OurLocBinder).getService()
            binder = service
            trackService?.startTracking()

            // this is our callback for when we're connected; we can receive each new update from the Service.
            trackService?.onNewLocation = {track ->
                googleMap.addCircle(
                    CircleOptions().center(track.latLng).fillColor(Color.RED).radius(50.0)
                )
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track)

        Log.d(TAG, "onCreate")

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val mapFrag : SupportMapFragment = supportFragmentManager.findFragmentById(R.id.trackTripMap) as SupportMapFragment
        mapFrag.getMapAsync(this)

        track_toggle.setOnClickListener {
            trackService?.startTracking()
        }

    }

    fun startAndBind() {
        val int = Intent(this.application, TrackService::class.java)
        this.application.startService(int)
        this.application.bindService(int, servConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbind() {
        application.unbindService(servConnection)
    }

    override fun onPause() {
        super.onPause()
        unbind()
    }

    override fun onResume() {
        super.onResume()
        startAndBind()

        if(binder != null) {
            plot(binder?.getNewTrackpoints()!!)
        }
    }

    fun plot(trackpoints : ArrayList<Trackpoint>) {
        val opts = PolylineOptions().color(Color.MAGENTA)

        for(x in trackpoints) {
            opts.add(x.latLng)
        }
        googleMap.addPolyline(opts)
    }


    val runnerDot = CircleOptions()
        .fillColor(Color.BLUE)
        .radius(0.5)


    fun drawRunnerDot() {
        googleMap.clear()
        googleMap.addCircle(
            runnerDot.center(currentLatLng)
        )
    }
}


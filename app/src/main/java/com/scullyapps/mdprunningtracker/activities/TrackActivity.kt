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
import com.scullyapps.mdprunningtracker.services.TrackService
import kotlinx.android.synthetic.main.activity_track.*

class TrackActivity : AppCompatActivity(), OnMapReadyCallback{


    lateinit var locationManager : LocationManager
    lateinit var googleMap : GoogleMap

    val TAG = "TrackActivityTAG"

    var SERVICE_RUNNING = false

    lateinit var trackService : TrackService


    var currentLatLng: LatLng = LatLng(0.0,0.0)
    var currentRotation = 0.0f

    var servConnection : TrackConnection = TrackConnection()

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
        }

        override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
            Log.d(TAG, "We've connected to the service")



            trackService = (service as TrackService.OurLocBinder).getService()
            trackService.startTracking()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track)

        Log.d(TAG, "onCreate")

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val mapFrag : SupportMapFragment = supportFragmentManager.findFragmentById(R.id.trackTripMap) as SupportMapFragment
        mapFrag.getMapAsync(this)

        val int = Intent(this.application, TrackService::class.java)
        this.application.startService(int)
        this.application.bindService(int, servConnection, Context.BIND_AUTO_CREATE)


        track_toggle.setOnClickListener {
            trackService.startTracking()
        }




//        val locListener : LocationListener = object : LocationListener {
//            override fun onLocationChanged(location: Location) {
//                val lat = location?.latitude
//                val lng = location?.longitude
//
//                currentLatLng  = LatLng(lat, lng)
//
//                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 17f))
//                drawRunnerDot()
//            }
//
//            override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
//
//            }
//
//            override fun onProviderEnabled(p0: String?) {
//
//            }
//
//            override fun onProviderDisabled(p0: String?) {
//
//            }
//
//        }
//
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000L, 0.5f, locListener)
//        }



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


package com.scullyapps.mdprunningtracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.scullyapps.mdprunningtracker.model.Trip

class ViewTripActivity : AppCompatActivity(), OnMapReadyCallback {



    lateinit var googleMap : GoogleMap

    override fun onMapReady(map: GoogleMap?) {
        if(map != null) {
            googleMap = map
            map?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(51.5074, 0.1278)))
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_trip)

        val trip = intent.extras?.get("trip") as Trip

        val mapFrag : SupportMapFragment = supportFragmentManager.findFragmentById(R.id.viewTripMap) as SupportMapFragment
        mapFrag.getMapAsync(this)
    }
}

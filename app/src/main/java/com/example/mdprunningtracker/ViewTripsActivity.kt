package com.example.mdprunningtracker

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.*
import kotlinx.android.synthetic.main.activity_view_trips.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.Polyline
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




// implements OnMapReadyCallback
class ViewTripsActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var googleMap: GoogleMap

    override fun onMapReady(map: GoogleMap?) {

        System.err.println("MAP IS NOW READY")

        map?.addMarker(
            MarkerOptions()
                .position(LatLng(51.5074, 0.1278))
                .title("Marker")
        )

        map?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(51.5074, 0.1278)))

        if(map != null) {
            googleMap = map
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_trips)

        val mapFrag : SupportMapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFrag.getMapAsync(this)

        // note: use polyline to list routes
        ourbutton.setOnClickListener {
            System.err.println("CHANGINGTYPE")

            val target = LatLng(51.5074, 0.1278)

            val line = googleMap.addPolyline(
                PolylineOptions()
                    .add(LatLng(51.5, -0.1), LatLng(40.7, -74.0))
                    .width(5f)
                    .color(Color.RED)
            )



            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 128.0f))
        }
    }

}

// AIzaSyCMu0ELcICduowXc7tUsIX2mNNroKfELq8

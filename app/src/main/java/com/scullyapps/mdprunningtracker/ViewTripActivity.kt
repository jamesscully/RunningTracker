package com.scullyapps.mdprunningtracker

import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Cap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.scullyapps.mdprunningtracker.model.Trackpoint
import com.scullyapps.mdprunningtracker.model.Trip
import com.scullyapps.mdprunningtracker.views.TrackpointView
import kotlinx.android.synthetic.main.activity_view_trip.*

class ViewTripActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var googleMap : GoogleMap
    lateinit var trip : Trip

    override fun onMapReady(map: GoogleMap?) {
        if(map != null) {
            googleMap = map
            map?.moveCamera(CameraUpdateFactory.newLatLngBounds(trip.getLatLngBounds(), 100))

            drawPolyline()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_trip)

        val mapFrag : SupportMapFragment = supportFragmentManager.findFragmentById(R.id.viewTripMap) as SupportMapFragment
            mapFrag.getMapAsync(this)

        trip = intent.extras?.get("trip") as Trip
        trip.getMovement(this)

        act_trip_name.setText(trip.name)
        act_trip_distance.text = trip.getDistanceStamp()
        act_trip_time.text = trip.getTimeStamp()

        val trackpoints = trip.movement.trackpoints

        for(x in trackpoints) {
            val tv = TrackpointView(this, x)
            tv.setOnClickListener {
                highlightTrackpoint(x)
            }

            act_trip_tracklayout.addView(tv)
        }
    }

    fun highlightTrackpoint(track : Trackpoint) {
        googleMap.clear()
        drawPolyline()
        googleMap.addMarker(
            MarkerOptions().position(track.latLng).title("Test")
        )
    }

    fun drawPolyline() {
        googleMap.addPolyline(
            getPolyLine(trip.plotLineOptions)
        )
    }

    fun getPolyLine(options : PolylineOptions) : PolylineOptions {
        return options.width(15f).color(Color.BLUE)
    }
}

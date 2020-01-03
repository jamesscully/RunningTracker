package com.scullyapps.mdprunningtracker.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.scullyapps.mdprunningtracker.R
import com.scullyapps.mdprunningtracker.model.Movement
import com.scullyapps.mdprunningtracker.model.Trackpoint
import com.scullyapps.mdprunningtracker.model.Trip
import com.scullyapps.mdprunningtracker.views.TrackpointView
import kotlinx.android.synthetic.main.activity_view_trip.*

class ViewTripActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var googleMap : GoogleMap
    lateinit var trip : Trip

    val COLOR_LINE = Color.rgb(0, 32, 138)
    val COLOR_DOT  = Color.rgb(13, 70, 160)
    val COLOR_MARK = Color.rgb(84, 113, 210)

    val trackpoints get() = trip.movement.trackpoints

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

        // if we're creating, we're likely coming from importing a GPX file
        if(intent.extras?.get("creating") as Boolean) {
            trip = intent.extras?.get("trip") as Trip
            trip.movement = intent.extras?.get("movement") as Movement
        } else {
            trip = intent.extras?.get("trip") as Trip
            trip.getMovement(this)
        }

        act_trip_name.setText(trip.name)
        act_trip_distance.text = trip.getDistanceStamp()
        act_trip_time.text = trip.getTimeStamp()
        act_trip_elevgain.text = trip.getElevationGain().toString().plus("m")
        act_trip_speed.text = trip.getAverageDistance()

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

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(track.latLng, 17.5f))

        googleMap.addCircle(
            CircleOptions().center(track.latLng)
                           .radius(1.0)
                           .strokeColor(COLOR_DOT)
                           .fillColor(COLOR_DOT)
                           .zIndex(5f)
        )

        googleMap.addMarker(
            MarkerOptions().position(track.latLng).title("Test")
        )
    }


    fun drawPolyline() {
        googleMap.addPolyline(
            getPolyLine(trip.plotLineOptions).color(COLOR_LINE)
        )

        drawStartEndMarkers()
    }

    // this draws a dot on the start and finish points
    fun drawStartEndMarkers() {
        val start = trackpoints[0].latLng
        val end   = trackpoints.last().latLng

        // options apart from center
        val opts =  CircleOptions()
                    .radius(3.0)
                    .strokeColor(Color.RED)
                    .fillColor(Color.RED)
                    .zIndex(4f)

        googleMap.addCircle(
            opts.center(start)
        )

        googleMap.addCircle(
            opts.center(end)
        )
    }


    fun getPolyLine(options : PolylineOptions) : PolylineOptions {
        return options.width(10f).color(Color.BLUE)
    }
}

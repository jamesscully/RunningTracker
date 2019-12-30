package com.scullyapps.mdprunningtracker

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
import com.scullyapps.mdprunningtracker.database.Contract
import com.scullyapps.mdprunningtracker.database.DBHelper
import com.scullyapps.mdprunningtracker.model.Trip
import java.sql.Timestamp


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

        val opts : PolylineOptions

        val trips = ArrayList<Trip>()

        val projection = arrayOf(
            Contract.TRIP._ID,
            Contract.TRIP.NAME,
            Contract.TRIP.NOTES
        )

        val c = contentResolver.query(Contract.ALL_TRIPS, projection, null, null, "_id ASC")

        if(c != null) {
            c.moveToFirst()

            while(!c.isAfterLast) {
                trips.add(Trip(this, c.getInt(0), c.getString(1), c.getString(2)))
                c.moveToNext()
            }
        }


    }

}

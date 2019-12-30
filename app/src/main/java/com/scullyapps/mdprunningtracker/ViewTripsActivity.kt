package com.scullyapps.mdprunningtracker

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.maps.*
import kotlinx.android.synthetic.main.activity_view_trips.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLngBounds
import com.scullyapps.mdprunningtracker.database.Contract
import com.scullyapps.mdprunningtracker.model.Trip
import com.scullyapps.mdprunningtracker.recyclers.TripAdapter


// implements OnMapReadyCallback
class ViewTripsActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var googleMap: GoogleMap
             val trips = ArrayList<Trip>()

    private lateinit var recycler : RecyclerView
    private lateinit var mAdapter  : TripAdapter
    private lateinit var mManager  : RecyclerView.LayoutManager

    override fun onMapReady(map: GoogleMap?) {

        System.err.println("MAP IS NOW READY")

        map?.addMarker(
            MarkerOptions()
                .position(LatLng(51.5074, 0.1278))
                .title("London")
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

        val projection = arrayOf( Contract.TRIP._ID, Contract.TRIP.NAME, Contract.TRIP.NOTES )

        val c = contentResolver.query(Contract.ALL_TRIPS, projection, null, null, "_id ASC")

        if(c != null) {
            c.moveToFirst()

            while(!c.isAfterLast) {
                trips.add(Trip(this, c.getInt(0), c.getString(1), c.getString(2)))
                c.moveToNext()
            }
            c.close()
        }
        setupRecycler()
    }

    fun setupRecycler() {
        mManager = LinearLayoutManager(this)
        mAdapter = TripAdapter(trips)

        recycler = trips_recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = mManager
            adapter = mAdapter
        }

        mAdapter.onItemClick = { pos, view ->

            val trip = trips[pos]

            Toast.makeText(this, trip.name, Toast.LENGTH_LONG).show()

            googleMap.addPolyline(
                trip.plotLineOptions.width(50f).color(Color.MAGENTA)
            )

            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(trip.getLatLngBounds(), 100))

        }
    }

}

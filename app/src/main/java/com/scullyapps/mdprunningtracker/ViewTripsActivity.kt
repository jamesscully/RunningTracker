package com.scullyapps.mdprunningtracker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
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
import com.google.android.gms.maps.model.PolylineOptions
import com.scullyapps.mdprunningtracker.database.Contract
import com.scullyapps.mdprunningtracker.gpx.GPX
import com.scullyapps.mdprunningtracker.model.Trip
import com.scullyapps.mdprunningtracker.recyclers.TripAdapter
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import com.scullyapps.mdprunningtracker.database.DBHelper
import com.scullyapps.mdprunningtracker.database.RunContentProvider
import java.io.File


class ViewTripsActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var googleMap: GoogleMap
             val trips = ArrayList<Trip>()

    private lateinit var recycler : RecyclerView
    private lateinit var mAdapter  : TripAdapter
    private lateinit var mManager  : RecyclerView.LayoutManager

    override fun onMapReady(map: GoogleMap?) {
        if(map != null) {
            googleMap = map
            map?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(51.5074, 0.1278)))
        }
    }

    // todo implement permissions prompt

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

        button.setOnClickListener {
            val intent = Intent()
            intent.setType("*/*")
                  .action = Intent.ACTION_GET_CONTENT

            startActivityForResult(Intent.createChooser(intent, "Select GPX data"), 777)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 777 && resultCode == Activity.RESULT_OK) {
            val file = data?.data

            if(file != null){

                val next = DBHelper(this).nextTripId

                val gpx = GPX(this, next, file)
            }

        }
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


            googleMap.addPolyline(
                getPolyLine(trip.plotLineOptions)
            )

            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(trip.getLatLngBounds(), 100))

        }
    }

    // this standardizes our polyline process; we can
    // simply change the line effects here rather than multiple places
    fun getPolyLine(options : PolylineOptions) : PolylineOptions{
        return options.width(25f).color(Color.BLUE)
    }

}

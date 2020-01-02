package com.scullyapps.mdprunningtracker.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.*
import kotlinx.android.synthetic.main.activity_list_trips.*
import com.google.android.gms.maps.model.LatLng
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.PolylineOptions
import com.scullyapps.mdprunningtracker.R
import com.scullyapps.mdprunningtracker.database.Contract
import com.scullyapps.mdprunningtracker.gpx.GPX
import com.scullyapps.mdprunningtracker.model.Trip
import com.scullyapps.mdprunningtracker.recyclers.TripAdapter
import com.scullyapps.mdprunningtracker.database.DBHelper


class ListTripsActivity : AppCompatActivity(), OnMapReadyCallback {

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
        setContentView(R.layout.activity_list_trips)

        val mapFrag : SupportMapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFrag.getMapAsync(this)

        val projection = arrayOf( Contract.TRIP._ID, Contract.TRIP.NAME, Contract.TRIP.NOTES )

        val c = contentResolver.query(Contract.ALL_TRIPS, projection, null, null, "_id ASC")

        if(c != null) {
            c.moveToFirst()

            while(!c.isAfterLast) {

                val trip = Trip(c.getInt(0), c.getString(1), c.getString(2))
                trip.getMovement(this)

                trips.add(trip)
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

                val tIntent = Intent()
                tIntent.setClass(this, ViewTripActivity::class.java)
                tIntent.putExtra("creating", true)
                tIntent.putExtra("trip", gpx.getTrip())
                tIntent.putExtra("movement", gpx.getMovement())
                startActivity(tIntent)
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

        mAdapter.onOpenClick = { pos, view ->
            val tIntent = Intent()
            tIntent.setClass(this, ViewTripActivity::class.java)
            tIntent.putExtra("trip", trips[pos])
            tIntent.putExtra("creating", false)
            startActivity(tIntent)
        }
    }

    // this standardizes our polyline process; we can
    // simply change the line effects here rather than multiple places
    fun getPolyLine(options : PolylineOptions) : PolylineOptions{
        return options.width(25f).color(Color.BLUE)
    }

}

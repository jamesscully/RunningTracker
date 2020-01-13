package com.scullyapps.mdprunningtracker.activities

import android.content.ContentValues
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.scullyapps.mdprunningtracker.R
import com.scullyapps.mdprunningtracker.adapters.TrackAdapter
import com.scullyapps.mdprunningtracker.database.Contract
import com.scullyapps.mdprunningtracker.model.Movement
import com.scullyapps.mdprunningtracker.model.Trackpoint
import com.scullyapps.mdprunningtracker.model.Trip
import kotlinx.android.synthetic.main.activity_view_trip.*


class ViewTripActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var googleMap : GoogleMap
    lateinit var trip : Trip



    val COLOR_LINE = Color.rgb(0, 32, 138)
    val COLOR_DOT  = Color.rgb(13, 70, 160)
    val COLOR_MARK = Color.rgb(84, 113, 210)


    private lateinit var recycler : RecyclerView
    private lateinit var mAdapter  : TrackAdapter
    private lateinit var mManager  : RecyclerView.LayoutManager


    val trackpoints get() = trip.movement.trackpoints
    val commentMap get() = trip.comments

    var CREATING : Boolean = false
    var MODIFIED : Boolean = false

    override fun onMapReady(map: GoogleMap?) {
        if(map != null) {
            googleMap = map

            val width = resources.displayMetrics.widthPixels
            val height = resources.displayMetrics.heightPixels
            val padding = (width * 0.05).toInt()

            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(trip.getLatLngBounds(), width, height, padding))
            drawPolyline()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_trip)

        val mapFrag : SupportMapFragment = supportFragmentManager.findFragmentById(R.id.viewTripMap) as SupportMapFragment
            mapFrag.getMapAsync(this)

        // in either following case, the trip must be defined
        trip = intent.extras?.get("trip") as Trip

        CREATING = intent.extras?.get("creating") as Boolean

        // if we're creating, we're likely coming from importing a GPX file / tracking,
        // and thus we'll need to get movement from intent
        if(CREATING) {
            trip.movement = intent.extras?.get("movement") as Movement
        } else {
            trip.getMovement(this)
        }


        // set up our views
        act_trip_name.setText(trip.name)

        act_trip_distance.text  = trip.getDistanceStamp()
        act_trip_time.text      = trip.getTimeStamp()
        act_trip_speed.text     = trip.getAverageDistance()
        act_trip_rating.rating = trip.rating.toFloat()


        println(commentMap)

        setupRecycler()
        setupListeners()
    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putParcelable("trip", trip)

        super.onSaveInstanceState(outState)
    }

    fun setupRecycler() {
        mManager = LinearLayoutManager(this)


        // our adapter is responsible for creating views based on data,
        // thus it'll need both trackpoints and comments
        mAdapter = TrackAdapter(trackpoints, commentMap)

        recycler = act_trip_tracklayout.apply {
            setHasFixedSize(true)
            layoutManager = mManager
            adapter = mAdapter
        }

        // when the user clicks on a trackpoint, highlight it on the map.
        mAdapter.onItemClick = {pos ->
            highlightTrackpoint(trackpoints[pos])
        }

        // when the user has input a comment, we put it in the map
        mAdapter.onComment = {seq, comment ->
            commentMap.put(seq, comment)
            MODIFIED = true
        }

        // because we're using a listener for when the text _changes_, our changes via code will set this variable to true on startup.
        MODIFIED = false

    }

    fun setupListeners() {
        act_trip_rating.setOnRatingBarChangeListener { ratingBar, fl, b ->
            // we can safely change this to an Int as the ratingbar is limited to 1 star increments.
            trip.rating = fl.toInt()
            MODIFIED = true
        }

        act_trip_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                trip.name = p0.toString()
                act_trip_save.visibility = View.VISIBLE
                MODIFIED = true
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        act_trip_time.setOnClickListener {
            println(trip.toString())
            println(commentMap.toString())
        }

        act_trip_save.setOnClickListener {
            save()
        }

        // if we're creating, then it's likely we don't want to delete it right away.
        if(CREATING)
            act_trip_delete.visibility = View.GONE

        act_trip_delete.setOnClickListener {
            val build = AlertDialog.Builder(this)
            build.setMessage("Are you sure you wish to delete this trip?")
            build.setPositiveButton("Yes!") { dialogInterface, i ->
                delete()
            }
            build.setNegativeButton("No!") { dialogInterface, i ->
                dialogInterface.cancel()
            }
            build.show()
        }
    }

    fun delete() {
        contentResolver.delete(Contract.ALL_TRIPS, "", arrayOf(trip.id.toString()))
        finish()
    }

    fun save() {
        val cv = ContentValues()

        cv.put("name",  act_trip_name.text.toString())
        cv.put("notes", trip.writeToJson())

        println("Saving data: \n ${trip.writeToJson()}")

        if(CREATING) {
            contentResolver.insert(Contract.ALL_TRIPS, cv)

            for(x in trackpoints) {
                val tkCV = ContentValues()

                tkCV.put("tID",  trip.id)
                tkCV.put("seq",  x.seq)
                tkCV.put("lat",  x.lat)
                tkCV.put("lng",  x.lng)
                tkCV.put("elev", x.elev)
                tkCV.put("time", x.time)

                println(tkCV)

                contentResolver.insert(Contract.ALL_MOVEMENT, tkCV)
            }

            CREATING = false

        } else {
            contentResolver.update(Contract.ALL_TRIPS, cv, "_id=?", arrayOf(trip.id.toString()))
        }

        if(CREATING)
            CREATING = false
        if(MODIFIED)
            MODIFIED = false
    }

    override fun onBackPressed() {

        println("CREATING = $CREATING MODIFIED = $MODIFIED")

        // if we're simply viewing this trip, then we don't want to leave
        if(!CREATING && !MODIFIED)
            super.onBackPressed()

        else {
            val warnUser = AlertDialog.Builder(this)


            // set the appropriate warning message
            if (CREATING)
                warnUser.setMessage("Careful! You'll be leaving an unsaved trip \n\n Are you sure you wish to discard changes?")
            if (MODIFIED)
                warnUser.setMessage("Careful! You'll be discarding unsaved changes \n\n Are you sure you wish to discard changes?")


            warnUser.setPositiveButton("Save") { d, i ->
                save()
                finish()
            }

            warnUser.setNegativeButton("Yes, exit") { d, i ->
                super.onBackPressed()
            }

            warnUser.show()
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
            MarkerOptions().position(track.latLng)
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

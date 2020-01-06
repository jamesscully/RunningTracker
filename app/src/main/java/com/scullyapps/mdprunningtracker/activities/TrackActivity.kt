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
import com.google.maps.android.SphericalUtil
import com.scullyapps.mdprunningtracker.R
import com.scullyapps.mdprunningtracker.model.Trackpoint
import com.scullyapps.mdprunningtracker.services.TrackService
import kotlinx.android.synthetic.main.activity_track.*
import kotlin.math.round

class TrackActivity : AppCompatActivity(), OnMapReadyCallback{


    lateinit var locationManager : LocationManager
    lateinit var googleMap : GoogleMap

    val TAG = "TrackActivityTAG"

    var SERVICE_RUNNING = false

    var trackService : TrackService? = null
    var binder : TrackService.OurLocBinder? = null


    var currentLatLng: LatLng = LatLng(0.0,0.0)

    var servConnection : TrackConnection = TrackConnection()

    lateinit var START_TRACKPOINT : Trackpoint
    lateinit var LAST_TRACKPOINT  : Trackpoint

    var distanceTravelled : Double = 0.0

    var trackpoints = ArrayList<Trackpoint>()

    var startTime : Long = 0
    var runTime   : Long = 0

    override fun onMapReady(map: GoogleMap?) {
        if(map != null) {
            googleMap = map
        }
    }

    inner class TrackConnection : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.d(TAG, "We've disconnected from the service")
            trackService = null
        }

        override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
            Log.d(TAG, "We've connected to the service")
            trackService = (service as TrackService.OurLocBinder).getService()
            binder = service
            onResume()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track)

        Log.d(TAG, "onCreate")

        val int = Intent(this.application, TrackService::class.java)
        startService(int)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val mapFrag : SupportMapFragment = supportFragmentManager.findFragmentById(R.id.trackTripMap) as SupportMapFragment
            mapFrag.getMapAsync(this)

        track_toggle.setOnClickListener {
            val btn = track_toggle

            if(btn.text == "Start") {
                track_toggle.text = "Stop"
                trackService?.startTracking()
                startTime = System.currentTimeMillis() / 1000L
            } else {
                track_toggle.text = "Start"
                stopAndDestroy()
            }
        }

    }

    fun stopAndDestroy() {
        val int = Intent(this.application, TrackService::class.java)
        trackService?.stop()
    }

    fun startAndBind() {
        val int = Intent(this.application, TrackService::class.java)
        this.application.bindService(int, servConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbind() {
        application.unbindService(servConnection)
    }

    override fun onPause() {
        super.onPause()
        trackService?.startForeground()
        unbind()
    }

    override fun onResume() {
        super.onResume()

        startAndBind()

        if(binder != null) {

            trackService?.removeForeground()

            println("Binder = $binder")

            // if we have new trackpoints since being unbound, then we'll need to add them
            if(binder?.haveNewTrackpoints()!!) {
                val new = binder?.getNewTrackpoints()!!

                println("Received trackpoints: $new")

                trackpoints.addAll(new)
            }

            if(trackpoints.size > 0) {
                START_TRACKPOINT = trackpoints[0]
                LAST_TRACKPOINT  = trackpoints.last()
            }

            // since we've now caught up, we should plot the existing line again + new ones
            plot()

            trackService?.onNewLocation = { track ->
                trackpoints.add(track)
                plotNew(track)
                calculateStats()
            }
        }
    }

    fun calculateStats() {
        // track_distance.text = SphericalUtil.computeDistanceBetween(START_TRACKPOINT.latLng, LAST_TRACKPOINT.latLng).toString().plus("m")
    }

    fun plot() {
        val opts = PolylineOptions().color(Color.MAGENTA)

        for(x in trackpoints) {
            opts.add(x.latLng)
        }

        googleMap.addPolyline(opts)
    }

    // we'll just plot the new one, instead of re-drawing the plotline for each trackpoint added
    fun plotNew(track : Trackpoint) {

        // if we haven't initialized LAST yet, then this should be last.
        if(!::LAST_TRACKPOINT.isInitialized) {
            START_TRACKPOINT = track
            LAST_TRACKPOINT = track
        } else {
            distanceTravelled += SphericalUtil.computeDistanceBetween(LAST_TRACKPOINT.latLng, track.latLng)

            track_distance.text = String.format("%.2fm", distanceTravelled)
        }

        track_time.text = getTimeStamp()
        track_speed.text = getAverageDistance()


        googleMap.addCircle(
            CircleOptions().radius(1.0).fillColor(Color.RED).center(track.latLng)
        )


        val opts = PolylineOptions().color(Color.MAGENTA)

        // we'll need to add from our last trackpoint to the new one, then reset the last trackpoint
        opts.add(LAST_TRACKPOINT.latLng)
        opts.add(track.latLng)

        googleMap.addPolyline(opts)

        LAST_TRACKPOINT = track

        highlightTrackpoint(track)
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



    fun highlightTrackpoint(track : Trackpoint) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(track.latLng, 17.5f))
    }

    fun getTimeStamp() : String {
        runTime = System.currentTimeMillis() / 1000L - startTime

        // TODO check this for errors;

        var temp : Long = runTime

        val hours = temp / (60 * 60)
        temp %= (60 * 60)
        val mins  = temp / 60
        temp %= 60
        val secs  = temp

        var out = ""

        // concatenate if we need to; so we dont have 0h0m59s, just 59s
        if(hours > 0) out += "${hours}h "
        if(mins  > 0) out += "${mins}m "
        if(secs  > 0) out += "${secs}s"

        return out
    }

    fun getAverageDistance() : String {
        val distance = distanceTravelled
        val time = runTime

        // avoid dividing by zero in the case of an error
        if(distance == 0.0 || time == 0L)
            return ""

        // since we're using seconds, and we want 1337m(etres) / min, we multiply the distance by 60
        // else, time / 60 could result in a float
        var average = (distance * 60) / time

        return (getDistanceStamp(average) + " / min")
    }

    fun getDistanceStamp(dist : Double = -1.0) : String {

        var d : Double

        if(dist == -1.0)
            d = distanceTravelled
        else
            d = dist

        val km = d / 1000

        // km or m
        if(d > 1000)
            return String.format("%.2fkm", km)
        else
            return String.format("%.2fm", d)
    }

}


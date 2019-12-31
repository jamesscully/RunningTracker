package com.scullyapps.mdprunningtracker.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.scullyapps.mdprunningtracker.database.Contract
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.round

data class Trip(val id: Int, var name: String, var notes: String) : Parcelable{

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(name)
        dest.writeString(notes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Trip> {
        override fun createFromParcel(source: Parcel): Trip {
            return Trip(source.readInt(), source.readString()!!, source.readString()!!)
        }

        override fun newArray(size: Int): Array<Trip?> {
            return arrayOfNulls(size)
        }

    }

    lateinit var movement : Movement

    // getters to make things look nicer
    val totalUnixTime : Long
        get() = movement.getTotalUnixTime()

    val plotLineOptions : PolylineOptions
        get() = movement.getPlotLine()




    // self explanatory
    fun getTimeStamp() : String {
        val elapsed : Long = totalUnixTime // ie 40

        // we can use Calendar to get our h/m/s in the format we want.
        // in this case, we want e.g. 1h2m30s for aesthetics
        val time = Calendar.getInstance()

        time.clear()

        // this will adjust other values if > 60
        time.timeInMillis = elapsed * 1000L

//        val hours = time.get(Calendar.HOUR)
//        val mins  = time.get(Calendar.MINUTE)
//        val secs  = time.get(Calendar.SECOND)

        var temp : Long = elapsed

        val hours = temp / (60 * 60)
        temp %= (60 * 60)
        val mins  = temp / 60
        temp %= 60
        val secs  = temp

        var out = ""

        // concatenate if we need to; so we dont have 0h0m59s, just 59s
        if(hours > 0)
            out += "${hours}h "
        if(mins  > 0)
            out += "${mins}m "
        if(secs  > 0)
            out += "${secs}s"

        return out
    }

    // this gets our start date, which will be used for displaying when
    // each run took place independent of the name
    fun getStartDate() : String {
        val firstPoint = movement.trackpoints[0].time

        // we'll need to get and clear our calendar
        val time = Calendar.getInstance()
        time.clear()

        time.timeInMillis = firstPoint * 1000L

        val date : Date = time.time

        // according to Android Studio, this will give the local time format for each country.
        return DateFormat.getDateInstance().format(date)
    }

    // this function generates the stamp for distance, i.e. 1.31km or 300m
    fun getDistanceStamp() : String {
        val d = movement.getTotalDistance()

        val km = round(d / 1000)

        // km or m
        if(d > 1000)
            return "${km}km"
        else
            return "${d}m"
    }

    // not sure this will be implemented; elevation gain is apparently very tricky
    fun getElevationGain() : Double {

        var gain = 0.0
        val points = movement.trackpoints

        var start = points[0].elev

        var increasing = false

        for(x in 1 until points.size) {
            val current = points[x].elev

            if(current > start) {
                gain += (current - start)
                increasing = true
            } else {
                increasing = false
            }

            if(!increasing) {
                start = current
            }

        }

        return gain
    }

    // this function generates the bounds needed to include all points on the map
    // We can pass this to the Google Maps camera for movement
    fun getLatLngBounds() : LatLngBounds {
        val bounds = LatLngBounds.Builder()

        for(x in movement.trackpoints) {
            bounds.include(x.latLng)
        }
        return bounds.build()
    }


    fun getMovement(context: Context) {

        val projection = arrayOf(
            Contract.MOVEMENT.T_ID,
            Contract.MOVEMENT.SEQ,
            Contract.MOVEMENT.LAT,
            Contract.MOVEMENT.LNG,
            Contract.MOVEMENT.ELEV,
            Contract.MOVEMENT.TIME
        )

        // ID_URI is a function in contract that returns the Uri for a specific ID.
        val cur = context.contentResolver.query(Contract.ID_URI(Contract.ALL_MOVEMENT, id), projection, null, null, "seq ASC")

        var tracks = ArrayList<Trackpoint>()

        if(cur != null) {
            cur.moveToFirst()

            while(!cur.isAfterLast) {
                tracks.add(Trackpoint.fromCursor(cur))
                cur.moveToNext()
            }
        }

        movement = Movement(tracks)
    }
}
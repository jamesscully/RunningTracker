package com.scullyapps.mdprunningtracker.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.SphericalUtil
import com.scullyapps.mdprunningtracker.database.Contract
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.round

data class Trip(val id: Int, var name: String, var notes: String) : Parcelable {

    lateinit var movement : Movement

    var rating   = 0
    var comments = HashMap<Int, String>()



    // getters to make things look nicer
    val totalUnixTime : Long
        get() = movement.getTotalSeconds()

    val plotLineOptions : PolylineOptions
        get() = movement.getPlotLine()


    init {
        readFromJson()
    }


    // self explanatory
    fun getTimeStamp() : String {
        val elapsed : Long = totalUnixTime // ie 40


        // TODO check this for errors;

        var temp : Long = elapsed

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

    // this gets our start date, which will be used for displaying when
    // each run took place independent of the name
    fun getStartDate() : String {
        // according to Android Studio, this will give the local time format for each country.
        return DateFormat.getDateInstance().format(Date(movement.trackpoints[0].time * 1000L))
    }

    // this function generates the stamp for distance, i.e. 1.31km or 300m
    fun getDistanceStamp(dist : Double = -1.0) : String {

        var d : Double

        if(dist == -1.0)
            d = movement.getTotalDistance()
        else
            d = dist

        val km = round(d / 1000)
             d = round(d)

        // km or m
        if(d > 1000)
            return "${km}km"
        else
            return "${d}m"
    }

    fun getAverageDistance() : String {
        val distance = movement.getTotalDistance()
        val time = movement.getTotalSeconds()

        // avoid dividing by zero in the case of an error
        if(distance == 0.0 || time == 0L)
            return ""

        // since we're using seconds, and we want 1337m(etres) / min, we multiply the distance by 60
        // else, time / 60 could result in a float
        var average = (distance * 60) / time

        return (getDistanceStamp(average) + " / min")
    }

    fun getDistance() : Double {
        val first = movement.trackpoints[0].latLng
        val last  = movement.trackpoints.last().latLng

        return round(SphericalUtil.computeDistanceBetween(first, last))
    }



    // not sure this will be implemented; elevation gain is apparently very tricky
    fun getElevationGain() : Double {
        return -1.0
    }

    // this function generates the bounds needed to include all trackpoints on the map
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

    /*\
    ///  These two functions read and write the extras from the notes column; rating and comments.
    \*/

    fun writeToJson() : String {
        val outJSON      = JSONObject()
        val commentArray = JSONArray()

        outJSON.put("rating", rating)

        comments.forEach {seq, comment ->
            commentArray.put(seq, comment)
        }

        outJSON.put("comments", commentArray)

        return outJSON.toString()
    }

    fun readFromJson() {

        try {

            val extras = JSONObject(notes)

            rating = extras.get("rating") as Int

            val coms = extras.get("comments") as JSONArray

            // for all comments in the array, we'll add them to our local array
            for (x in 0 until coms.length()) {
                if (coms.get(x).toString() == "null")
                    continue

                val comment = coms.get(x) as String

                // the above will convert null to a string, which we don't want!
                if (comment == "null" || comment == null)
                    continue

                comments.put(x, comment)
            }
        } catch (e : JSONException) {
            System.err.println("JSON ${notes} is not valid, ignoring...")
        }
    }



    /*\
    ///  Parcelable Code
    \*/

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(name)
        dest.writeString(notes)
    }

    override fun describeContents(): Int = 0


    companion object CREATOR : Parcelable.Creator<Trip> {
        override fun createFromParcel(source: Parcel): Trip {
            return Trip(source.readInt(), source.readString()!!, source.readString()!!)
        }

        override fun newArray(size: Int): Array<Trip?> {
            return arrayOfNulls(size)
        }

    }

    override fun toString(): String {
        return "Trip(id=$id, name='$name', notes='$notes', movement=$movement, rating=$rating, comments=$comments)"
    }


}
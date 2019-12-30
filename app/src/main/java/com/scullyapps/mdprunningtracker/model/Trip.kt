package com.scullyapps.mdprunningtracker.model

import android.content.Context
import com.google.android.gms.maps.model.PolylineOptions
import com.scullyapps.mdprunningtracker.database.Contract
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

data class Trip (val context: Context, val id : Int, var name : String, var notes : String) {

    lateinit var movement : Movement

    val totalUnixTime : Int
        get() = movement.getTotalUnixTime()

    val plotLineOptions : PolylineOptions
        get() = movement.getPlotLine()


    init {
        getMovement()
    }


    fun getTimeStamp() : String {
        val elapsed = totalUnixTime // ie 40

        // we can use Calendar to get our h/m/s in the format we want.
        // in this case, we want e.g. 1h2m30s for aesthetics
        val time = Calendar.getInstance()

        time.clear()

        // this will adjust other values if > 60
        time.set(Calendar.SECOND, elapsed)

        val hours = time.get(Calendar.HOUR)
        val mins  = time.get(Calendar.MINUTE)
        val secs  = time.get(Calendar.SECOND)

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

    fun getStartDate() : String {
        val firstPoint = movement.trackpoints[0].time

        val time = Calendar.getInstance()
        time.clear()

        time.set(Calendar.SECOND, firstPoint)

        val date : Date = time.time

        return DateFormat.getDateInstance().format(date)
    }


    fun getMovement() {

        val projection = arrayOf(
            Contract.MOVEMENT.T_ID,
            Contract.MOVEMENT.SEQ,
            Contract.MOVEMENT.LAT,
            Contract.MOVEMENT.LNG,
            Contract.MOVEMENT.ELEV,
            Contract.MOVEMENT.TIME
        )

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


//        Toast.makeText(context, "Total Distance: ${movement.getTotalDistance()}", Toast.LENGTH_LONG).show()
    }
}
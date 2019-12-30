package com.scullyapps.mdprunningtracker.model

import android.content.Context
import android.widget.Toast
import com.google.android.gms.maps.model.PolylineOptions
import com.scullyapps.mdprunningtracker.database.Contract

data class Trip (val context: Context, val id : Int, var name : String, var notes : String) {

    lateinit var movement : Movement

    val totalUnixTime : Int
        get() = movement.getTotalUnixTime()

    val plotLineOptions : PolylineOptions
        get() = movement.getPlotLine()


    init {
        getMovement()
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
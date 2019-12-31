package com.scullyapps.mdprunningtracker.views

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.scullyapps.mdprunningtracker.R
import com.scullyapps.mdprunningtracker.model.Trackpoint
import java.util.*


class TrackpointView(context: Context, track : Trackpoint) : ConstraintLayout(context) {


    init {
        val root = LayoutInflater.from(context).inflate(R.layout.view_trackpoint, this, true)

        val txtSequence : TextView = root.findViewById(R.id.view_track_seq)
        val txtCoords   : TextView = root.findViewById(R.id.view_track_coords)
        val txtTimeStamp: TextView = root.findViewById(R.id.view_track_timestamp)

        txtSequence.text = track.seq.toString()
        txtCoords.text = track.latLng.toString()


        val time = Calendar.getInstance()

        time.clear()
        time.set(Calendar.SECOND, track.time)

        val stamp : String = String.format("%02d:%02d:%02d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.SECOND)).toString()

        txtTimeStamp.text = stamp






    }

}
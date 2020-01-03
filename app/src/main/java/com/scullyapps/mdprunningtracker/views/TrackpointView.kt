package com.scullyapps.mdprunningtracker.views

import android.content.Context
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.scullyapps.mdprunningtracker.R
import com.scullyapps.mdprunningtracker.model.Trackpoint
import kotlinx.android.synthetic.main.view_trackpoint.view.*
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
        time.timeInMillis = (track.time * 1000L)

        time.timeZone = TimeZone.getDefault()

        val stamp : String = String.format("%02d:%02d:%02d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.SECOND))

        txtTimeStamp.text = stamp

        view_track_btncomment.setOnClickListener {
            val isVisible = view_track_comment.visibility == View.VISIBLE

            if(isVisible)
                view_track_comment.visibility = View.GONE
            else
                view_track_comment.visibility = View.VISIBLE
        }

    }

    fun setCommentListener(watcher: TextWatcher) {
        view_track_comment.addTextChangedListener(watcher)
    }

}
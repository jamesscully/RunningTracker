package com.scullyapps.mdprunningtracker.recyclers

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.scullyapps.mdprunningtracker.R
import com.scullyapps.mdprunningtracker.model.Trackpoint
import com.scullyapps.mdprunningtracker.model.Trip
import kotlinx.android.synthetic.main.view_trackpoint.view.*
import java.util.*
import kotlin.collections.ArrayList

class TrackAdapter (private val dataset : ArrayList<Trackpoint>) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {
    private val TAG: String = "TrackAdapter"


    inner class TrackViewHolder(v : ConstraintLayout) : RecyclerView.ViewHolder(v) {
        val seq     : TextView = v.view_track_seq
        val coords  : TextView = v.view_track_coords
        val time    : TextView = v.view_track_timestamp
        val comment : EditText = v.view_track_comment

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val tView = LayoutInflater.from(parent.context).inflate(R.layout.view_trackpoint, parent, false) as ConstraintLayout

        return TrackViewHolder(tView)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = dataset[position]

        holder.seq.text = track.seq.toString()
        holder.coords.text = track.latLng.toString()

        val time = Calendar.getInstance()

        time.clear()
        time.timeInMillis = (track.time * 1000L)

        time.timeZone = TimeZone.getDefault()

        val stamp : String = String.format("%02d:%02d:%02d", time.get(Calendar.HOUR_OF_DAY), time.get(
            Calendar.MINUTE), time.get(Calendar.SECOND))

        holder.time.text = stamp

    }

    override fun getItemCount(): Int = dataset.size

}
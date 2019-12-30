package com.scullyapps.mdprunningtracker.recyclers

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.scullyapps.mdprunningtracker.R
import com.scullyapps.mdprunningtracker.model.Trip
import com.scullyapps.mdprunningtracker.views.TripView
import kotlinx.android.synthetic.main.view_trip.view.*

class TripAdapter (private val dataset : ArrayList<Trip>) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    class TripViewHolder(v : FrameLayout) : RecyclerView.ViewHolder(v) {
        val name     : TextView = v.view_trip_name
        val date     : TextView = v.view_trip_date
        val distance : TextView = v.view_trip_distance
        val elevGain : TextView = v.view_trip_elevgain
        val time     : TextView = v.view_trip_time
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripAdapter.TripViewHolder {
        val TripView = LayoutInflater.from(parent.context).inflate(R.layout.view_trip, parent, false) as FrameLayout

        return TripViewHolder(TripView)
    }

    override fun onBindViewHolder(holder: TripAdapter.TripViewHolder, position: Int) {
        val data : Trip = dataset[position]

        holder.name.text = data.name
        holder.time.text = data.getTimeStamp()
        holder.date.text = data.getStartDate()

    }

    override fun getItemCount() = dataset.size



}
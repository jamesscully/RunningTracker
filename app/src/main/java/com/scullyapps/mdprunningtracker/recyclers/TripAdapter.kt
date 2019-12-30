package com.scullyapps.mdprunningtracker.recyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.scullyapps.mdprunningtracker.R
import com.scullyapps.mdprunningtracker.model.Trip
import com.scullyapps.mdprunningtracker.views.TripView
import kotlinx.android.synthetic.main.view_trip.view.*


class TripAdapter (private val dataset : ArrayList<Trip>) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {


    // found at https://stackoverflow.com/questions/54219825/android-kotlin-how-to-add-click-listener-to-recyclerview-adapter
    // kotlin makes somethings harder than java I guess
    var onItemClick: ((pos:Int, view : View) -> Unit)? = null

    inner class TripViewHolder(v : FrameLayout) : RecyclerView.ViewHolder(v), View.OnClickListener {
        val name     : TextView = v.view_trip_name
        val date     : TextView = v.view_trip_date
        val distance : TextView = v.view_trip_distance
        val elevGain : TextView = v.view_trip_elevgain
        val time     : TextView = v.view_trip_time

        override fun onClick(v: View) {
            onItemClick?.invoke(adapterPosition, v)
        }

        init {
            v.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripAdapter.TripViewHolder {
        val TripView = LayoutInflater.from(parent.context).inflate(R.layout.view_trip, parent, false) as FrameLayout

        return TripViewHolder(TripView)
    }

    // this is where we actually fill in the data; typically I'd do this in TripView, but recyclerviews n stuff
    override fun onBindViewHolder(holder: TripAdapter.TripViewHolder, position: Int) {
        val data : Trip = dataset[position]

        holder.name.text = data.name
        holder.time.text = data.getTimeStamp()
        holder.date.text = data.getStartDate()
        holder.distance.text = data.getDistanceStamp()
        holder.elevGain.text = "N/A"

    }
    override fun getItemCount() = dataset.size
}
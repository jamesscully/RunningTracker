package com.scullyapps.mdprunningtracker.views

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.scullyapps.mdprunningtracker.R
import com.scullyapps.mdprunningtracker.model.Trip

class TripView(context: Context, trip : Trip) : FrameLayout(context) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_trip, this, true)
    }
}
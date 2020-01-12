package com.scullyapps.mdprunningtracker.model

import android.graphics.Color
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions

class OurGMAP {
    fun getPolyLine(options : PolylineOptions) : PolylineOptions {
        return options.width(25f).color(Color.BLUE)
    }
}
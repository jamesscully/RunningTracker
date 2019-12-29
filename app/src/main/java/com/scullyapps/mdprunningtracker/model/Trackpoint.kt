package com.scullyapps.mdprunningtracker.model

import com.google.android.gms.maps.model.LatLng

// this class functions like a GPX trkpt
data class Trackpoint(var lat : Double,
                      var lng : Double,
                      var elev : Double = -1.0,
                      var time : Double = 0.0) {


}
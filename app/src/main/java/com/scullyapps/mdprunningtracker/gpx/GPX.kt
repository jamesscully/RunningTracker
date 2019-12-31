package com.scullyapps.mdprunningtracker.gpx

import android.content.Context
import android.net.Uri
import com.scullyapps.mdprunningtracker.model.Movement
import com.scullyapps.mdprunningtracker.model.Trackpoint
import com.scullyapps.mdprunningtracker.model.Trip
import java.io.InputStream
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.collections.ArrayList

// this class takes a GPX file, and parses it to take out the necessary data.
class GPX (context: Context, proposedID : Int, uri : Uri) {
    var trackpoints = ArrayList<Trackpoint>()
    var GPX : String = ""

    val context = context
    val newID   = proposedID

    init {
        val io : InputStream = context.contentResolver.openInputStream(uri)!!

        val bFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = bFactory.newDocumentBuilder()

        val document = dBuilder.parse(io)

        val tkList = document.getElementsByTagName("trkpt")

        for(x in 0 until tkList.length) {
            val element = tkList.item(x) as Element



            val elevEl = element.getElementsByTagName("ele")
            val timeEl = element.getElementsByTagName("time")

            print("lat: " + element.getAttribute("lat"))
            print("lon: " + element.getAttribute("lat"))

            print("elev: " + elevEl.item(0).textContent)
            print("time: " + timeEl.item(0).textContent)
            println()

            val lat = element.getAttribute("lat").toDoubleOrNull()
            val lng = element.getAttribute("lon").toDoubleOrNull()
            val elev = elevEl.item(0).textContent.toDoubleOrNull()
            val time = timeEl.item(0).textContent.toIntOrNull()

            if(lat == null || lng == null || elev == null || time == null) {
                error("Error retrieving appropriate value from GPX data, skipping trkpt ${x}")
                continue
            }

            val trackpoint = Trackpoint(proposedID, x, lat, lng, elev, time)
            trackpoints.add(trackpoint)
        }
    }

    fun build() : Trip {
        val movement : Movement = Movement(trackpoints)

        return Trip(newID, "New GPX", "none")
    }


}
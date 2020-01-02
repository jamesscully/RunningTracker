package com.scullyapps.mdprunningtracker.gpx

import android.content.Context
import android.net.Uri
import com.scullyapps.mdprunningtracker.model.Movement
import com.scullyapps.mdprunningtracker.model.Trackpoint
import com.scullyapps.mdprunningtracker.model.Trip
import java.io.InputStream
import org.w3c.dom.Element;
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.collections.ArrayList

// this class takes a GPX file, and parses it to take out the necessary data.
class GPX (context: Context, proposedID : Int, uri : Uri) {
    var trackpoints = ArrayList<Trackpoint>()

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

            // we'll check these later to see if they're null
            val lat = element.getAttribute("lat").toDoubleOrNull()
            val lng = element.getAttribute("lon").toDoubleOrNull()
            val elev = elevEl.item(0).textContent.toDoubleOrNull()

            // GPX 1.1 uses the ISO8601 timeformat, so we'll convert this to unix time
            val ISO8601 = timeEl.item(0).textContent


            // this seems to be the only easiest and non-mindbending solution, thanks to
            // https://stackoverflow.com/a/27071102
            val time = Instant.parse(ISO8601).epochSecond

//            val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss") as DateFormat
//            val result = df.parse(ISO8601)
//
//            var time = result.time


            if(lat == null || lng == null || elev == null || time == null) {
                System.err.println("Error retrieving appropriate value from GPX data, skipping trkpt ${x}")
                continue
            }

            val trackpoint = Trackpoint(proposedID, x, lat, lng, elev, time)
            trackpoints.add(trackpoint)
        }
    }

    fun getTrip() : Trip {
        return Trip(newID, "New GPX", "none")
    }

    fun getMovement() : Movement {
        return Movement(trackpoints)
    }


}
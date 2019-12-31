package com.scullyapps.mdprunningtracker.gpx

import android.content.Context
import android.net.Uri
import com.scullyapps.mdprunningtracker.model.Trackpoint
import java.io.InputStream
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.io.Reader
import java.io.StringReader
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.collections.ArrayList

// this class takes a GPX file, and parses it to take out the necessary data.
class GPX (context: Context, uri : Uri) {
    var trackpoints = ArrayList<Trackpoint>()
    var GPX : String = ""

    init {
        val io : InputStream = context.contentResolver.openInputStream(uri)!!

        val bFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = bFactory.newDocumentBuilder()

        val document = dBuilder.parse(io)

        val tkList = document.getElementsByTagName("trkpt")

        for(x in 0 until tkList.length) {
            val element = tkList.item(x) as Element

            val elev = element.getElementsByTagName("ele")
            val time = element.getElementsByTagName("time")

            print("lat: " + element.getAttribute("lat"))
            print("lon: " + element.getAttribute("lat"))

            print("elev: " + elev.item(0).textContent)
            print("time: " + time.item(0).textContent)

            println()
        }


    }


}
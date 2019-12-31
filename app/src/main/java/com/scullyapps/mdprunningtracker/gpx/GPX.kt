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
import kotlin.collections.ArrayList

// this class takes a GPX file, and parses it to take out the necessary data.
class GPX (context: Context, uri : Uri) {
    var trackpoints = ArrayList<Trackpoint>()
    var GPX : String = ""


    init {

        val factory = XPathFactory.newInstance()
        val xp : XPath = factory.newXPath()

        val io : InputStream = context.contentResolver.openInputStream(uri)!!

        val nodes : NodeList = xp.evaluate("/gpx", InputSource(io), XPathConstants.NODESET) as NodeList

        for(x in 0 until nodes.length) {
            val element = nodes.item(x) as Element

            System.err.println(element)

        }

    }

    fun convertStreamToString(`is`: java.io.InputStream): String {
        val s = java.util.Scanner(`is`).useDelimiter("\\A")
        return if (s.hasNext()) s.next() else ""
    }



}
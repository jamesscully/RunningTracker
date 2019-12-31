import org.w3c.dom.Element
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.File
import java.io.FileReader
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

fun main (args : Array<String>) {
    val file = File("data.gpx")
    val reader = FileReader(file)

    val bFactory = DocumentBuilderFactory.newInstance()
    val dBuilder = bFactory.newDocumentBuilder()

    val document = dBuilder.parse(file)

    val tkList = document.getElementsByTagName("trkpt")

    println(tkList.length)

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
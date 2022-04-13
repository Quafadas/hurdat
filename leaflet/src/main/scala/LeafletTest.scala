package hurdat.map

import org.scalajs.dom
import org.scalajs.dom.document
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.js.annotation.JSImport
import org.scalajs.dom.html
import com.leaflet.leaflet.mod as L
import com.leaflet.leaflet.mod.LatLngLiteral
import scala.concurrent.ExecutionContext.Implicits.global
import scalajs.js.Thenable.Implicits.thenable2future
import hurdat.HurdatSystem
import scala.concurrent.Await
import upickle.default.*
import com.leaflet.leaflet.mod.CircleMarkerOptions
import scala.scalajs.js
import scala.scalajs.js.JSConverters.*
import com.leaflet.leaflet.leafletStrings.track
import com.leaflet.leaflet.mod.Map_
import org.scalajs.dom.Worker
import scala.scalajs.js.JSON
import hurdat.JSHurdatSystem
import io.circe.parser.decode

val dataLocation = "hurdat.json"
var hurdatDB: Option[js.Array[HurdatSystem]] = None
// https://github.com/ScalablyTyped/Demos/blob/master/leaflet/src/main/scala/demo.scala

def setupMap(url: String, parent: Option[html.Div] = None): Unit =

  val appendToT = parent match
    case None =>
      val tempDiv = document.createElement("div").asInstanceOf[html.Div]
      tempDiv.setAttribute("id", "map")
      tempDiv.style.height = "50vmin"
      tempDiv.style.width = "98vmin"
      document.body.appendChild(tempDiv)
      tempDiv
    case Some(div) => div

/*   for    
    response <- dom.fetch("../assets/firstTC.json")
    text <- response.text()
  yield
    println("reading data")
    val data = upickle.default.read[Seq[HurdatSystem]](text)
    val leaflet = addMapToDiv(appendToT)
    println("reading data - add map")
    addTrackToMap(leaflet, data.headOption) */

  for
    response <- dom.fetch(url)
    text <- response.text()
  yield
    println("did reload? 4")
    println("request done, parsing hurdat")
    //val worker = new Worker("")
    //val data = upickle.default.read[js.Array[HurdatSystem]](text)
    //val data = JSON.parse(text).asInstanceOf[js.Array[js.Object]]
    val data = JSON.parse(text).asInstanceOf[js.Array[HurdatSystem]]
    //val data = decode[Vector[HurdatSystem]](text)
    //val data = upickle.default.read[Vector[HurdatSystem]](text)
    println("did parse")
    println(data.last.year)
    println("check complete")
    //println(JSON.stringify(data.head))
    
    //hurdatDB = Some(data)
    val leaflet = addMapToDiv(appendToT)
    println("added map")
    addTrackToMap(leaflet, Some(data.head))
    println("added track")
    ()

def addMapToDiv(parent: html.Div) =
  //val el = document.getElementById("map").asInstanceOf[html.Element]
  println(parent)
  val map = L.map(parent.id).setView(L.LatLngLiteral(25, -65), zoom = 4)
  L.tileLayer(
    s"https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}",
    L.TileLayerOptions()
      .setId("mapbox.satellite")
      .setAccessToken(
        """pk.eyJ1IjoicXVhZmFkYXMiLCJhIjoiY2wwYmdleTdwMDlyYjNibWptb2Q5dXdyMSJ9.NSpKapVh6sVZJi0pTPH8pg"""
      )
      .setMaxZoom(17)
      .setAttribution(
        """Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors,
                            |<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>,
                            |Imagery © <a href="http://mapbox.com">Mapbox</a>""".stripMargin
      )
  ).addTo(map)
  map

def addTrackToMap(map: Map_, tc: Option[HurdatSystem]) =
  // Circles for observations
  tc.map { aTC =>
    println("TC")
    println(aTC)
    val layerGroup = L.layerGroup()
    aTC.track.map(trackEntry =>
      println(trackEntry)
      val color: String = hurdat.trackColour(trackEntry)
      layerGroup.addLayer(
        L.circleMarker(
          L.LatLngLiteral(trackEntry.latitude, trackEntry.longditude * -1),
          L.CircleMarkerOptions()
            .setColor(color)
            .setRadius(5)
            .setWeight(2)
        )
      )
    )
    // Lines for the track
    aTC.track.sliding(2).foreach { trackEntries =>
      val first = L.LatLngLiteral(trackEntries.head.latitude, trackEntries.head.longditude * -1)
      val second = L.LatLngLiteral(trackEntries.last.latitude, trackEntries.last.longditude * -1)
      layerGroup.addLayer(
        L.polyline(
          js.Array(first, second)
        )
      )
    }
    layerGroup.addTo(map)
    layerGroup
  }


object TutorialApp:
  def main(args: Array[String]): Unit =
    document.addEventListener(
      "DOMContentLoaded",
      (e: dom.Event) => setupMap(dataLocation)
    )

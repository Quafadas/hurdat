package hurdat.map

import org.scalajs.dom
import org.scalajs.dom.document
import scala.scalajs.js.annotation.JSExportTopLevel
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

val dataLocation = "hurdat.json"
var hurdatDB: Option[Seq[HurdatSystem]] = None
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

  for
    response <- dom.fetch("../assets/firstTC.json")
    text <- response.text()
  yield
    val data = upickle.default.read[Seq[HurdatSystem]](text)
    val leaflet = addMapToDiv(appendToT)
    addTrackToMap(leaflet, data.headOption)    

  for
    response <- dom.fetch(url)
    text <- response.text()
  yield
    println("request done, parsing hurdat")
    val data = upickle.default.read[Seq[HurdatSystem]](text)
    println("parsing complete")
    hurdatDB = Some(data)


  

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
    aTC.track.map(trackEntry =>
      val color: String = hurdat.trackColour(trackEntry)
      L.circleMarker(
        L.LatLngLiteral(trackEntry.latitude, trackEntry.longditude * -1),
        L.CircleMarkerOptions()
          .setColor(color)
          .setRadius(5)
          .setWeight(2)
      ).addTo(map)
    )
    // Lines for the track
    aTC.track.sliding(2).foreach { trackEntries =>        
      val first = L.LatLngLiteral(trackEntries.head.latitude, trackEntries.head.longditude * -1)
      val second = L.LatLngLiteral(trackEntries.last.latitude, trackEntries.last.longditude * -1)
      L.polyline(
        js.Array(first, second)
      ).addTo(map)
    }
  }

object TutorialApp:
  def main(args: Array[String]): Unit =
    document.addEventListener(
      "DOMContentLoaded",
      (e: dom.Event) => setupMap(dataLocation)
    )

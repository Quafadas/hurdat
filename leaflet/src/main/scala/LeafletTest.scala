package hurdat.map

import org.scalajs.dom
import org.scalajs.dom.document
import scala.scalajs.js.annotation.JSExportTopLevel
import org.scalajs.dom.html
import com.leaflet.leaflet.mod as L
import com.leaflet.leaflet.mod.LatLngLiteral
import scala.concurrent.ExecutionContext.Implicits.global
import scalajs.js.Thenable.Implicits.thenable2future
import hurdat.*
import scala.concurrent.Await
import upickle.default.*
import com.leaflet.leaflet.mod.CircleMarkerOptions
import scala.scalajs.js
import scala.scalajs.js.JSConverters.*
import com.leaflet.leaflet.leafletStrings.track

val dataLocation = "hurdat.json"
// https://github.com/ScalablyTyped/Demos/blob/master/leaflet/src/main/scala/demo.scala
def setupMap(url: String, parent: Option[html.Div] = None): Unit =
  val responseText = for
    response <- dom.fetch(url)
    text <- response.text()
  yield
    val data = upickle.default.read[Seq[HurdatSystem]](text)
    println(data.head)
    data

  val appendToT = parent match
    case None =>
      val tempDiv = document.createElement("div").asInstanceOf[html.Div]
      tempDiv.setAttribute("id", "map")
      tempDiv.style.height = "50vmin"
      tempDiv.style.width = "98vmin"
      document.body.appendChild(tempDiv)
      tempDiv
    case Some(div) => div  
  println(appendToT)
  responseText.map(tcs => addMapToDiv(appendToT, tcs.headOption))

def addMapToDiv(parent: html.Div, tc: Option[HurdatSystem]) =
  //val el = document.getElementById("map").asInstanceOf[html.Element]
  println(parent)
  val map = L.map(parent).setView(L.LatLngLiteral(30, -50), zoom = 4)
  // Circles for observations
  tc.map { aTC =>
    println("TC")
    aTC.track.map(trackEntry =>
      val color: String = trackColour(trackEntry)
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
      println(trackEntries)

      val first = L.LatLngLiteral(trackEntries.head.latitude, trackEntries.head.longditude * -1)
      val second = L.LatLngLiteral(trackEntries.last.latitude, trackEntries.last.longditude * -1)
      L.polyline(
        js.Array(first, second)
      ).addTo(map)
    }

  }
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

object TutorialApp:
  def main(args: Array[String]): Unit =
    document.addEventListener(
      "DOMContentLoaded",
      (e: dom.Event) => setupMap(dataLocation)
    )

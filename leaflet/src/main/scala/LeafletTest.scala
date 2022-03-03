package tutorial.webapp

import org.scalajs.dom
import org.scalajs.dom.document
import scala.scalajs.js.annotation.JSExportTopLevel
import org.scalajs.dom.html
import com.leaflet.leaflet.mod as L
import com.leaflet.leaflet.mod.LatLngLiteral
import scala.concurrent.ExecutionContext.Implicits.global
import scalajs.js.Thenable.Implicits.thenable2future
import hurdat._
import scala.concurrent.Await

object TutorialApp:
  def main(args: Array[String]): Unit =
    document.addEventListener(
      "DOMContentLoaded",
      (e: dom.Event) => setupUI()
    )

// https://github.com/ScalablyTyped/Demos/blob/master/leaflet/src/main/scala/demo.scala
  def setupUI(): Unit =
    println("here")
    val url = "https://github.com/Quafadas/hurdat/blob/main/hurdat.json"
    val responseText = for {
        response <- dom.fetch(url)
        text <- response.text()
    } yield {
        val data = upickle.default.read[Seq[HurdatSystem]](text)                        
        data
    }         
    val div = document.createElement("div").asInstanceOf[html.Div]
    div.setAttribute("id", "map")
    div.style.height = "50vmin"
    div.style.width = "100vmin"
    document.body.appendChild(div)
    addMapToDiv(div)

  def addMapToDiv(parent: html.Div) =
    val el = document.getElementById("map").asInstanceOf[html.Element]
    val map = L.map(el).setView(L.LatLngLiteral(30, -50), zoom = 4)
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

/*
 * Copyright 2022 quafadas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import hurdat.*
import viz.PlotTarget

@main def readData() =
  val raw = scala.io.Source.fromFile("hurdat.json").mkString("")
  val hurdat = upickle.default.read[Seq[HurdatSystem]](raw)

  println(hurdat.last)

@main def acquireData() =
  println("Running data import")
  // https://www.aoml.noaa.gov/hrd/hurdat/hurdat2-format.pdf
  val rawTcData = requests.get("https://www.aoml.noaa.gov/hrd/hurdat/hurdat2.html")

  println("got raw data")
  val s = rawTcData.text()
  val lines = s.split("\r\n").toSeq
  val length = lines.length

  // //  Remove the html tags and other guff
  val cleanedLines = lines.drop(3).dropRight(5)

  val firstTwoAreLetters = "^[A-Za-z]{2}".r
  val headerLines = cleanedLines.map { x =>
    //     println(firstTwoAreLetters.findFirstIn(x))
    firstTwoAreLetters.findFirstIn(x) match
      case Some(x) => 1
      case _       => 0
  }

  val hurricaneSections = headerLines.scanLeft(0)(_ + _).drop(1)
  val allData = (hurricaneSections zip headerLines zip cleanedLines) map { case ((a, b), c) =>
    (a, b, c)
  }

  def entryToTrack(e: String) =
    val record = if e(16).toString().isBlank then RecordIdentifier.B else RecordIdentifier.valueOf(e(16).toString())
    val year = e.substring(0, 4).toInt
    val month = e.substring(4, 6).toInt
    val day = e.substring(6, 8).toInt
    val hour = e.substring(10, 12).toInt
    val minute = e.substring(12, 14).toInt
    val recordType: RecordIdentifier = record
    val status: SystemStatus = SystemStatus.valueOf(e.substring(19, 21))
    val latitude: Double = e.substring(23, 27).toDouble
    val longditude: Double = e.substring(30, 35).toDouble
    val latHem: String = e(27).toString()
    val longHem: String = e(35).toString()
    val maxSustainedWind = e.substring(38, 41).toDouble
    val cp = e.substring(43, 47).toDouble
    TrackInfo(
      year = year,
      month = month,
      day = day,
      hour = hour,
      minute = minute,
      recordType = recordType,
      systemStatus = status,
      longditude = longditude,
      latitude = latitude,
      latHemisphere = latHem,
      longHemisphere = longHem,
      maxSustainedWind = maxSustainedWind,
      minPressure = cp
    )

  def toTC(rawHurdat1: String): HurdatSystem =
    val rawHurdat = rawHurdat1.split(",")
    val basin = rawHurdat(0).take(2)
    val numberTemp = rawHurdat(0).take(4).takeRight(2).toInt
    val year = rawHurdat(0).takeRight(4).toInt
    val name = rawHurdat(1).replaceAll("\\s", "")
    //val trackEntries = rawHurdat(2).replaceAll("\\s", "").toInt
    HurdatSystem(year = year, name = name, basin = basin, number = numberTemp)

  val allTC = allData.filter(_._2 == 1).map { case (section, header, data) =>
    (section, toTC(data))
  }

  val tracks1 = allData.groupBy(_._1).view
  val tracks = tracks1.mapValues { list =>
    list.drop(1).map { case (a, b, track) => entryToTrack(track) }
  }.view

  val finalDbTes = allTC.head
  val extract = tracks.view(1)

  finalDbTes._2.copy(track = extract)

  val finalTCDb: Seq[HurdatSystem] = allTC.map { case (tempId, hurricane) =>
    val hurTrack = tracks(tempId)

    hurricane.copy(track = hurTrack)
  }

  //println("saving to JSON file")
  os.write.over(os.pwd / "hurdat.json", upickle.default.write(finalTCDb, 2))
  os.write.over(os.pwd / "docs" / "assets" / "huvrdat.json", upickle.default.write(finalTCDb, 2))

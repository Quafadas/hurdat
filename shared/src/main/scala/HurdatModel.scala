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

package hurdat

import upickle.default.ReadWriter

enum RecordIdentifier(val description: String) derives ReadWriter:
  case C extends RecordIdentifier("Closest approach to a coast, not followed by a landfall")
  case G extends RecordIdentifier("Genesis")
  case I extends RecordIdentifier("An intensity peak in terms of both pressure and wind")
  case L extends RecordIdentifier("Landfall (center of system crossing a coastline)")
  case P extends RecordIdentifier("Minimum in central pressure")
  case R
      extends RecordIdentifier(
        "Provides additional detail on the intensity of the cyclone when rapid changes are underway"
      )
  case S extends RecordIdentifier("Change of status of the system")
  case T extends RecordIdentifier("Provides additional detail on the track (position) of the cyclone")
  case W extends RecordIdentifier("Maximum sustained wind speed")
  case N extends RecordIdentifier("None given")
  case B extends RecordIdentifier("Blank")
end RecordIdentifier

enum SystemStatus(val entryName: String, val value: String, val description: String) derives ReadWriter:
  case TD extends SystemStatus("TD", "TD", "Tropical cyclone of tropical depression intensity (< 34 knots)")
  case TS extends SystemStatus("TS", "TS", "Tropical cyclone of tropical storm intensity (34-63 knots)")
  case HU extends SystemStatus("HU", "HU", "Tropical cyclone of hurricane intensity (> 64 knots) ")
  case EX extends SystemStatus("EX", "EX", "Extratropical cyclone (of any intensity) ")
  case SD extends SystemStatus("SD", "SD", "Subtropical cyclone of subtropical depression intensity (< 34 knots) ")
  case SS extends SystemStatus("SS", "SS", "Subtropical cyclone of subtropical storm intensity (> 34 knots) ")
  case PT extends SystemStatus("PT", "PT", "Post tropical")
  case LO
      extends SystemStatus(
        "LO",
        "LO",
        "A low that is neither a tropical cyclone, a subtropical cyclone, nor an extratropical cyclone (of any intensity) "
      )
  case WV extends SystemStatus("WV", "WV", "Tropical Wave (of any intensity)")
  case DB extends SystemStatus("DB", "DB", "Disturbance (of any intensity)")
  case RL extends SystemStatus("RL", "RL", "Remanent Low")
end SystemStatus

case class TrackInfo(
    year: Int,
    month: Int,
    day: Int,
    hour: Int,
    minute: Int,
    recordType: RecordIdentifier,
    systemStatus: SystemStatus,
    latitude: Double,
    longditude: Double,
    latHemisphere: String,
    longHemisphere: String,
    maxSustainedWind: Double,
    minPressure: Double
) derives ReadWriter
case class HurdatSystem(
    basin: String = "AL",
    number: Int = 0,
    year: Int = 0,
    name: String = "",
    track: Seq[TrackInfo] = List[TrackInfo]()
) derives ReadWriter

case class HurdatSystems(hurricanes: Seq[HurdatSystem]) derives ReadWriter


// https://en.wikipedia.org/wiki/Template:Storm_colour

def findCategory(trackInfo: TrackInfo) =
  (trackInfo.maxSustainedWind, trackInfo.minPressure) match
    case (s, p) if (s >= 157f || p <= 920) => 5
    case (s, p) if (s >= 130f || p <= 945) => 4
    case (s, p) if (s >= 111f || p <= 965) => 3
    case (s, p) if (s >= 96f || p <= 980)  => 2
    case (s, p) if (s >= 74f || p <= 990)  => 1
    case (s, p) if (s <= 74f && p > 990)   => 0

def trackColour(trackInfo: TrackInfo) =
  val code = trackInfo.systemStatus match
    case SystemStatus.RL                                                       => "cccccc"
    case SystemStatus.PT                                                       => "80ccff"
    case SystemStatus.EX | SystemStatus.LO | SystemStatus.WV | SystemStatus.DB => "c0c0c0"
    case SystemStatus.TD | SystemStatus.SD                                     => "5ebaff"
    case SystemStatus.TS | SystemStatus.SS                                     => "00faf4"
    case SystemStatus.HU =>
      findCategory(trackInfo) match
        case 5 => "ff6060"
        case 4 => "ff8f20"
        case 3 => "ffc140"
        case 2 => "ffe775"
        case 1 => "ffffcc"
  s"#$code"
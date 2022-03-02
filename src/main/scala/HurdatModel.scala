package hurdat

import upickle.default.ReadWriter

enum RecordIdentifier(val description: String) derives ReadWriter:
  case C extends RecordIdentifier("Closest approach to a coast, not followed by a landfall")
  case G extends RecordIdentifier("Genesis")
  case I extends RecordIdentifier("An intensity peak in terms of both pressure and wind")
  case L extends RecordIdentifier("Landfall (center of system crossing a coastline)")
  case P extends RecordIdentifier("Minimum in central pressure")
  case R extends RecordIdentifier("Provides additional detail on the intensity of the cyclone when rapid changes are underway")
  case S extends RecordIdentifier("Change of status of the system")
  case T extends RecordIdentifier("Provides additional detail on the track (position) of the cyclone")
  case W extends RecordIdentifier("Maximum sustained wind speed")
  case N extends RecordIdentifier("None given")
  case B extends RecordIdentifier("Blank")  
end RecordIdentifier

enum SystemStatus (val entryName :String, val value:String, val description:String) derives ReadWriter:
  case TD extends SystemStatus( "TD", "TD","Tropical cyclone of tropical depression intensity (< 34 knots)")
  case TS extends SystemStatus( "TS", "TS", "Tropical cyclone of tropical storm intensity (34-63 knots)")
  case HU extends SystemStatus("HU", "HU", "Tropical cyclone of hurricane intensity (> 64 knots) ")
  case EX extends SystemStatus("EX", "EX", "Extratropical cyclone (of any intensity) ")
  case SD extends SystemStatus("SD", "SD", "Subtropical cyclone of subtropical depression intensity (< 34 knots) ")
  case SS extends SystemStatus("SS", "SS", "Subtropical cyclone of subtropical storm intensity (> 34 knots) ")
  case LO extends SystemStatus("LO", "LO", "A low that is neither a tropical cyclone, a subtropical cyclone, nor an extratropical cyclone (of any intensity) ")
  case WV extends SystemStatus("WV", "WV", "Tropical Wave (of any intensity)")
  case DB extends SystemStatus("DB", "DB", "Disturbance (of any intensity)")
end SystemStatus


 case class TrackInfo(
            year: Int, 
            month: Int, 
            day: Int, 
            hour:Int, 
            minute: Int,
            recordType : RecordIdentifier,
            systemStatus:SystemStatus,
            latitude: Double, 
            longditude:Double, 
            latHemisphere:String, 
            longHemisphere:String,
            maxSustainedWind:Double, 
            minPressure:Double, 
        ) derives ReadWriter
case class HurdatSystem(basin: String = "AL", number:Int = 0, year:Int = 0, name : String = "", track:Seq[TrackInfo] = List[TrackInfo]() ) derives ReadWriter
case class HurdatSystems(hurricanes: Seq[HurdatSystem]) derives ReadWriter

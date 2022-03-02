# Data format

Hurdat is published by noaa. We're interested in the "best track" dataset, which rolls it own data format. 

[Best Track Data](https://www.aoml.noaa.gov/hrd/hurdat/hurdat2.html)  -  [format](https://www.aoml.noaa.gov/hrd/hurdat/hurdat2-format.pdf)

We aim to parse into some structure that is more natural for scala to take advantage of. We'll want to end up with these definitions

```scala 
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

case class TrackInfo(
        year: Int, 
        month: Int, 
        day: Int, 
        hour:Int, 
        minute: Int,
        recordType : RecordIdentifier,
        systemStatus: SystemStatus,
        latitude: Double, 
        longditude:Double, 
        latHemisphere:String, 
        longHemisphere:String,
        maxSustainedWind:Double, 
        minPressure:Double, 
    ) derives ReadWriter

case class HurdatSystem(
    basin: String = "AL",
    number: Int = 0,
    year: Int = 0,
    name: String = "",
    track: Seq[TrackInfo] = List[TrackInfo]()
) derives ReadWriter

case class HurdatSystems(hurricanes: Seq[HurdatSystem]) derives ReadWriter
```
Complete source here; @:source(HurdatModel)

A couple of enums aren't defined here, but you get the idea that ultimately, we want a collection of case classes. 

Hurdat is a public dataset, but it changes very slowly (every 6 months). We'll write a script to parse the raw data, and save it as a 

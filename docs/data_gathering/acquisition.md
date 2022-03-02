# Data format

Hurdat is published by noaa. We're interested in the "best track" dataset, which rolls it own data format. 

[source](https://www.aoml.noaa.gov/hrd/hurdat/hurdat2-format.pdf)

The "source" may be found here; 

[Best Track - Hurdat](https://www.aoml.noaa.gov/hrd/hurdat/hurdat2.html)

We aim to parse into some structure that is more natural for scala to take advantage of. We'll want to end up with this; 

```scala
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

case class HurdatSystem(basin: String = "AL", number:Int = 0, year:Int = 0, name : String = "", track:Seq[TrackInfo] = List[TrackInfo]() ) derives ReadWriter

case class HurdatSystems(hurricanes: Seq[HurdatSystem]) derives ReadWriter
```
A couple of enums aren't defined here, but you get the idea (look at the [source](https://github.com/Quafadas/hurdat) ), that ultimately, we're going to end up with a collection of case classes. 

Hurdat is a public dataset, but it changes very slowly (every 6 months). We'll write a script to parse the raw data, and save it as a [json file](../assets/hurdat.json)...

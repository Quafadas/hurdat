package hurdat

import upickle.default.ReadWriter


case class HurdatSystemJVM(
    basin: String = "AL",
    number: Int = 0,
    year: Int = 0,
    name: String = "",
    track: Seq[TrackInfo] = List[TrackInfo]()
) derives ReadWriter

case class HurdatSystemsJVM(hurricanes: Seq[HurdatSystem]) derives ReadWriter
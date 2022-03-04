package hurdat

import hurdat.TrackInfo
class HurdatColorSuite extends munit.FunSuite:
  test("hurdat colors") {

    val tI = upickle.default.read[TrackInfo]("""{
        "year": 2020,
        "month": 11,
        "day": 12,
        "hour": 12,
        "minute": 0,
        "recordType": {
          "$type": "hurdat.RecordIdentifier.C"
        },
        "systemStatus": {
          "$type": "hurdat.SystemStatus.TD"
        },
        "latitude": 15.5,
        "longditude": 70.9,
        "latHemisphere": "N",
        "longHemisphere": "W",
        "maxSustainedWind": 25,
        "minPressure": 1009
      }""")

    //https://en.wikipedia.org/wiki/Template:Storm_colour
    val tI_TS = tI.copy(systemStatus = SystemStatus.TS)
    assertEquals(trackColour(tI_TS), "00faf4")

    assertEquals(findCategory(tI), 0)
  }

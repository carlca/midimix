package com.carlca.midimix

import scala.math._
import com.bitwig.extension.controller.api.*
import com.bitwig.extension.callback.DoubleValueChangedCallback
import com.bitwig.extension.controller.api.SettableRangedValue
// import com.carlca.logger.Log

object MidiMixSettings:
  enum PanSendMode derives CanEqual:
    case `FX Send`, `Pan`
  enum TrackMode derives CanEqual:
    case `One to One`, `No Groups`, `Groups Only`, `Tagged "<>" Only`
  var exclusiveSolo: Boolean = false
  var panSendMode: PanSendMode = PanSendMode.`FX Send`
  var trackMode:  TrackMode = TrackMode.`One to One`

  // // Track 1
  // var track1MinDb: Double = Double.NegativeInfinity
  // var track1MaxDb: Double = 6.0
  // // Track 2
  // var track2MinDb: Double = Double.NegativeInfinity
  // var track2MaxDb: Double = 6.0
  // // Track 3
  // var track3MinDb: Double = Double.NegativeInfinity
  // var track3MaxDb: Double = 6.0
  // // Track 1
  // var track4MinDb: Double = Double.NegativeInfinity
  // var track4MaxDb: Double = 6.0
  // // Track 5
  // var track5MinDb: Double = Double.NegativeInfinity
  // var track5MaxDb: Double = 6.0
  // // Track 6
  // var track6MinDb: Double = Double.NegativeInfinity
  // var track6MaxDb: Double = 6.0
  // // Track 7
  // var track7MinDb: Double = Double.NegativeInfinity
  // var track7MaxDb: Double = 6.0
  // // Track 8
  // var track8MinDb: Double = Double.NegativeInfinity
  // var track8MaxDb: Double = 6.0
  // // Master
  // var masterMinDb: Double = Double.NegativeInfinity
  // var masterMaxDb: Double = 6.0

  val MIN = "min"
  val MAX = "max"
  val MASTER = 0
  val NUM_TRACKS = 8  // Number of tracks

  // min/max dB values for each track
  private val trackMinDb = Array.fill[Double](NUM_TRACKS + 1)(Double.NegativeInfinity)  // +1 for Master
  private val trackMaxDb = Array.fill[Double](NUM_TRACKS + 1)(6.0)                      // +1 for Master

  def init(host: ControllerHost) =
    initPreferences(host)

  def initPreferences(host: ControllerHost): Unit =
    val prefs = host.getPreferences

    val soloSetting = prefs.getBooleanSetting("Exclusive Solo", "Solo Behaviour", false)
    soloSetting.addValueObserver((value) => MidiMixSettings.exclusiveSolo = value)

    val values = PanSendMode.values.map(_.toString).toArray
    val panSetting = prefs.getEnumSetting("Send/Pan Mode", "Third Row Behaviour", values, PanSendMode.`FX Send`.toString())
    panSetting.addValueObserver((value) => MidiMixSettings.panSendMode = PanSendMode.valueOf(value))

    val trackModes = TrackMode.values.map(_.toString).toArray
    val trackSetting = prefs.getEnumSetting("Track Mode", "Track Mapping Behaviour", trackModes, TrackMode.`One to One`.toString())
    trackSetting.addValueObserver((value) => MidiMixSettings.trackMode = TrackMode.valueOf(value))

    def createTrackDbSetting(minMax: String, trackNumber: Int): SettableRangedValue =
      val settingName = if trackNumber == 0 then "Master dB" else s"Fader $trackNumber dB"
      prefs.getNumberSetting(minMax, settingName, Double.NegativeInfinity, 6.0, 0.1, null,
        if minMax == MIN then Double.NegativeInfinity else 6.0
      )

    def addTrackDbObserver(setting: SettableRangedValue, minMax: String, trackNumber: Int): Unit =
      setting.addRawValueObserver(new DoubleValueChangedCallback:
        override def valueChanged(newValue: Double): Unit =
          if minMax == MIN then
              if newValue > trackMaxDb(trackNumber) then
                  createTrackDbSetting(MAX, trackNumber).set(newValue)
                  trackMaxDb(trackNumber) = newValue
                  trackMinDb(trackNumber) = newValue
              else
                  trackMinDb(trackNumber) = newValue
          else
              if newValue < trackMinDb(trackNumber) then
                  createTrackDbSetting(MIN, trackNumber).set(newValue)
                  trackMaxDb(trackNumber) = newValue
                  trackMinDb(trackNumber) = newValue
              else
                trackMaxDb(trackNumber) = newValue
      )

    // Create settings and observers for each track
    for trackNumber <- 1 to NUM_TRACKS do
      val minSetting = createTrackDbSetting(MIN, trackNumber)
      trackMinDb(trackNumber) = minSetting.getRaw // Initial values
      addTrackDbObserver(minSetting, MIN, trackNumber)

      val maxSetting = createTrackDbSetting(MAX, trackNumber)
      trackMaxDb(trackNumber) = maxSetting.getRaw // Initial values
      addTrackDbObserver(maxSetting, MAX, trackNumber)

    // Master Track
    val masterMinSetting = createTrackDbSetting(MIN, MASTER)
    trackMinDb(MASTER) = masterMinSetting.getRaw
    addTrackDbObserver(masterMinSetting, MIN, MASTER)

    val masterMaxSetting = createTrackDbSetting(MAX, MASTER)
    trackMaxDb(MASTER) = masterMaxSetting.getRaw
    addTrackDbObserver(masterMaxSetting, MAX, MASTER)

  def getVolumeRange(track: Int): (Double, Double) =
    (dbToVolume(trackMinDb(track + 1)), dbToVolume(trackMaxDb(track + 1)))

  private val MIN_DB = -160.0
  private val MAX_DB = 6.0
  private val DB_INCREMENT = 0.1

  private val dbToVolumeLookupTable: Array[Double] =
    val steps = ((MAX_DB - MIN_DB) * 10).toInt
    val table = new Array[Double](steps + 1)
    for i <- 0 to steps do
      val db = MIN_DB + i * DB_INCREMENT
      table(i) = pow(E, ((db + 120.2) / 26.056))
    table

  private def dbToVolume(db: Double): Double =
    if db < MIN_DB then
      dbToVolumeLookupTable(0)
    else if db > MAX_DB then
      dbToVolumeLookupTable(dbToVolumeLookupTable.length - 1)
    else
      val index = math.round(((db - MIN_DB) / DB_INCREMENT)).toInt
      dbToVolumeLookupTable(index)

end MidiMixSettings

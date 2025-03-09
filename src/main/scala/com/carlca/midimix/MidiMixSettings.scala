package com.carlca.midimix

import scala.math._
import com.bitwig.extension.controller.api.*
import com.bitwig.extension.callback.DoubleValueChangedCallback
// import com.carlca.logger.Log

object MidiMixSettings:
  enum PanSendMode derives CanEqual:
    case `FX Send`, `Pan`
  enum TrackMode derives CanEqual:
    case `One to One`, `No Groups`, `Groups Only`, `Tagged "<>" Only`
  var exclusiveSolo: Boolean = false
  var panSendMode: PanSendMode = PanSendMode.`FX Send`
  var trackMode:  TrackMode = TrackMode.`One to One`

  // Track 1
  var track1MinDb: Double = Double.NegativeInfinity
  var track1MaxDb: Double = 6.0
  // Track 2
  var track2MinDb: Double = Double.NegativeInfinity
  var track2MaxDb: Double = 6.0
  // Track 3
  var track3MinDb: Double = Double.NegativeInfinity
  var track3MaxDb: Double = 6.0
  // Track 1
  var track4MinDb: Double = Double.NegativeInfinity
  var track4MaxDb: Double = 6.0
  // Track 5
  var track5MinDb: Double = Double.NegativeInfinity
  var track5MaxDb: Double = 6.0
  // Track 6
  var track6MinDb: Double = Double.NegativeInfinity
  var track6MaxDb: Double = 6.0
  // Track 7
  var track7MinDb: Double = Double.NegativeInfinity
  var track7MaxDb: Double = 6.0
  // Track 8
  var track8MinDb: Double = Double.NegativeInfinity
  var track8MaxDb: Double = 6.0
  // Master
  var masterMinDb: Double = Double.NegativeInfinity
  var masterMaxDb: Double = 6.0

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

    // Track 1
    val track1MinDbSetting = prefs.getNumberSetting("Min", "Fader 1 dB", Double.NegativeInfinity, 6.0, 0.1, null, Double.NegativeInfinity)
    track1MinDb = track1MinDbSetting.getRaw
    track1MinDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        track1MinDb = newValue
    )
    val track1MaxDbSetting = prefs.getNumberSetting("Max", "Fader 1 dB", Double.NegativeInfinity, 6.0, 0.1, null, 6.0)
    track1MaxDb = track1MaxDbSetting.getRaw
    track1MaxDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        track1MaxDb = newValue
    )

    // Track 2
    val track2MinDbSetting = prefs.getNumberSetting("Min", "Fader 2 dB", Double.NegativeInfinity, 6.0, 0.1, null, Double.NegativeInfinity)
    track2MinDb = track2MinDbSetting.getRaw
    track2MinDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        track2MinDb = newValue
    )
    val track2MaxDbSetting = prefs.getNumberSetting("Max", "Fader 2 dB", Double.NegativeInfinity, 6.0, 0.1, null, 6.0)
    track2MaxDb = track2MaxDbSetting.getRaw
    track2MaxDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        track2MaxDb = newValue
    )

    // Track 3
    val track3MinDbSetting = prefs.getNumberSetting("Min", "Fader 3 dB", Double.NegativeInfinity, 6.0, 0.1, null, Double.NegativeInfinity)
    track3MinDb = track3MinDbSetting.getRaw
    track3MinDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        track3MinDb = newValue
    )
    val track3MaxDbSetting = prefs.getNumberSetting("Max", "Fader 3 dB", Double.NegativeInfinity, 6.0, 0.1, null, 6.0)
    track3MaxDb = track3MaxDbSetting.getRaw
    track3MaxDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        track3MaxDb = newValue
    )

    // Track 4
    val track4MinDbSetting = prefs.getNumberSetting("Min", "Fader 4 dB", Double.NegativeInfinity, 6.0, 0.1, null, Double.NegativeInfinity)
    track4MinDb = track4MinDbSetting.getRaw
    track4MinDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        track4MinDb = newValue
    )
    val track4MaxDbSetting = prefs.getNumberSetting("Max", "Fader 4 dB", Double.NegativeInfinity, 6.0, 0.1, null, 6.0)
    track4MaxDb = track4MaxDbSetting.getRaw
    track4MaxDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        track4MaxDb = newValue
    )

    // Track 5
    val track5MinDbSetting = prefs.getNumberSetting("Min", "Fader 5 dB", Double.NegativeInfinity, 6.0, 0.1, null, Double.NegativeInfinity)
    track5MinDb = track5MinDbSetting.getRaw
    track5MinDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        track5MinDb = newValue
    )
    val track5MaxDbSetting = prefs.getNumberSetting("Max", "Fader 5 dB", Double.NegativeInfinity, 6.0, 0.1, null, 6.0)
    track5MaxDb = track5MaxDbSetting.getRaw
    track5MaxDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        track5MaxDb = newValue
    )

    // Track 6
    val track6MinDbSetting = prefs.getNumberSetting("Min", "Fader 6 dB", Double.NegativeInfinity, 6.0, 0.1, null, Double.NegativeInfinity)
    track6MinDb = track6MinDbSetting.getRaw
    track6MinDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        track6MinDb = newValue
    )
    val track6MaxDbSetting = prefs.getNumberSetting("Max", "Fader 6 dB", Double.NegativeInfinity, 6.0, 0.1, null, 6.0)
    track6MaxDb = track6MaxDbSetting.getRaw
    track6MaxDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        track6MaxDb = newValue
    )

    // Track 7
    val track7MinDbSetting = prefs.getNumberSetting("Min", "Fader 7 dB", Double.NegativeInfinity, 6.0, 0.1, null, Double.NegativeInfinity)
    track7MinDb = track7MinDbSetting.getRaw
    track7MinDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        track7MinDb = newValue
    )
    val track7MaxDbSetting = prefs.getNumberSetting("Max", "Fader 7 dB", Double.NegativeInfinity, 6.0, 0.1, null, 6.0)
    track7MaxDb = track7MaxDbSetting.getRaw
    track7MaxDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        track7MaxDb = newValue
    )

    // Track 8
    val track8MinDbSetting = prefs.getNumberSetting("Min", "Fader 8 dB", Double.NegativeInfinity, 6.0, 0.1, null, Double.NegativeInfinity)
    track8MinDb = track8MinDbSetting.getRaw
    track8MinDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        track8MinDb = newValue
    )
    val track8MaxDbSetting = prefs.getNumberSetting("Max", "Fader 8 dB", Double.NegativeInfinity, 6.0, 0.1, null, 6.0)
    track8MaxDb = track8MaxDbSetting.getRaw
    track8MaxDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        track8MaxDb = newValue
    )

    // Master
    val masterMinDbSetting = prefs.getNumberSetting("Min", "Master dB", Double.NegativeInfinity, 6.0, 0.1, null, Double.NegativeInfinity)
    masterMinDb = masterMinDbSetting.getRaw
    masterMinDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        masterMinDb = newValue
    )
    val masterMaxDbSetting = prefs.getNumberSetting("Max", "Master dB", Double.NegativeInfinity, 6.0, 0.1, null, 6.0)
    masterMaxDb = masterMaxDbSetting.getRaw
    masterMaxDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        masterMaxDb = newValue
    )

  // Move this to Tracks.scala?
  def getVolumeRange(track: Int): (Double, Double) =
    val minDb = track match
      case 0 => track1MinDb
      case 1 => track2MinDb
      case 2 => track3MinDb
      case 3 => track4MinDb
      case 4 => track5MinDb
      case 5 => track6MinDb
      case 6 => track7MinDb
      case 7 => track8MinDb
      case _ => 0.0
    val maxDb = track match
      case 0 => track1MaxDb
      case 1 => track2MaxDb
      case 2 => track3MaxDb
      case 3 => track4MaxDb
      case 4 => track5MaxDb
      case 5 => track6MaxDb
      case 6 => track7MaxDb
      case 7 => track8MaxDb
      case _ => 0.0
    // Log.send(s"Track $track max dB: $maxDb")
    // val maxVolume = dbToVolume(maxDb)
    // Log.send(s"Track $track max volume: $maxVolume")
    (dbToVolume(minDb), dbToVolume(maxDb))

  // private def dbToVolume(db: Double): Double =
  //   pow(E, ((db + 120.2) / 26.056))

  private val dbToVolumeLookupTable: Array[Double] =
    val minDb = -120.0
    val maxDb = 6.0
    val steps = ((maxDb - minDb) * 10).toInt
    val table = new Array[Double](steps + 1)
    val dbIncrement = 0.1

    for i <- 0 to steps do
      val db = minDb + i * dbIncrement
      table(i) = pow(E, ((db + 120.2) / 26.056))
    table

  private val minDbTable = -120.0
  private val maxDbTable = 6.0
  private val dbIncrementTable = 0.1

  private def dbToVolume(db: Double): Double =
    if db < minDbTable then
      dbToVolumeLookupTable(0)
    else if db > maxDbTable then
      dbToVolumeLookupTable(dbToVolumeLookupTable.length - 1)
    else
      val index = math.round(((db - minDbTable) / dbIncrementTable)).toInt
      dbToVolumeLookupTable(index)

end MidiMixSettings

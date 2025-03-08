package com.carlca.midimix

import scala.math._
import com.bitwig.extension.controller.api.*
import com.carlca.logger.Log

object MidiMixSettings:
  enum PanSendMode derives CanEqual:
    case `FX Send`, `Pan`
  enum TrackMode derives CanEqual:
    case `One to One`, `No Groups`, `Groups Only`, `Tagged "<>" Only`
  var exclusiveSolo: Boolean = false
  var panSendMode: PanSendMode = PanSendMode.`FX Send`
  var trackMode:  TrackMode = TrackMode.`One to One`

  // Track 1
  var track1MinDb: Double = -0.6
  var track1MaxDb: Double = 6.0
  // Track 2
  var track2MinDb: Double = -0.6
  var track2MaxDb: Double = 6.0
  // Track 3
  var track3MinDb: Double = -0.6
  var track3MaxDb: Double = 6.0
  // Track 4
  var track4MinDb: Double = -0.6
  var track4MaxDb: Double = 6.0
  // Track 5
  var track5MinDb: Double = -0.6
  var track5MaxDb: Double = 6.0
  // Track 6
  var track6MinDb: Double = -0.6
  var track6MaxDb: Double = 6.0
  // Track 7
  var track7MinDb: Double = -0.6
  var track7MaxDb: Double = 6.0
  // Track 8
  var track8MinDb: Double = -0.6
  var track8MaxDb: Double = 6.0
  // Master
  var masterMinDb: Double = -0.6
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
    val track1MinDbSetting = prefs.getNumberSetting("Min", "Fader 1 dB", -999, 6.0, 0.1, null, -0.6)
    track1MinDbSetting.addValueObserver((value) => MidiMixSettings.track1MinDb = value.toDouble)
    val track1MaxDbSetting = prefs.getNumberSetting("Max", "Fader 1 dB", -999, 6.0, 0.1, null, 6.0)
    track1MaxDbSetting.addValueObserver((value) => MidiMixSettings.track1MaxDb = value.toDouble)

    // Track 2
    val track2MinDbSetting = prefs.getNumberSetting("Min", "Fader 2 dB", -999, 6.0, 0.1, null, -0.6)
    track2MinDbSetting.addValueObserver((value) => MidiMixSettings.track2MinDb = value.toDouble)
    val track2MaxDbSetting = prefs.getNumberSetting("Max", "Fader 2 dB", -999, 6.0, 0.1, null, 6.0)
    track2MaxDbSetting.addValueObserver((value) => MidiMixSettings.track2MaxDb = value.toDouble)

    // Track 3
    val track3MinDbSetting = prefs.getNumberSetting("Min", "Fader 3 dB", -999, 6.0, 0.1, null, -0.6)
    track3MinDbSetting.addValueObserver((value) => MidiMixSettings.track3MinDb = value.toDouble)
    val track3MaxDbSetting = prefs.getNumberSetting("Max", "Fader 3 dB", -999, 6.0, 0.1, null, 6.0)
    track3MaxDbSetting.addValueObserver((value) => MidiMixSettings.track3MaxDb = value.toDouble)

    // Track 4
    val track4MinDbSetting = prefs.getNumberSetting("Min", "Fader 4 dB", -999, 6.0, 0.1, null, -0.6)
    track4MinDbSetting.addValueObserver((value) => MidiMixSettings.track4MinDb = value.toDouble)
    val track4MaxDbSetting = prefs.getNumberSetting("Max", "Fader 4 dB", -999, 6.0, 0.1, null, 6.0)
    track4MaxDbSetting.addValueObserver((value) => MidiMixSettings.track4MaxDb = value.toDouble)

    // Track 5
    val track5MinDbSetting = prefs.getNumberSetting("Min", "Fader 5 dB", -999, 6.0, 0.1, null, -0.6)
    track5MinDbSetting.addValueObserver((value) => MidiMixSettings.track5MinDb = value.toDouble)
    val track5MaxDbSetting = prefs.getNumberSetting("Max", "Fader 5 dB", -999, 6.0, 0.1, null, 6.0)
    track5MaxDbSetting.addValueObserver((value) => MidiMixSettings.track5MaxDb = value.toDouble)

    // Track 6
    val track6MinDbSetting = prefs.getNumberSetting("Min", "Fader 6 dB", -999, 6.0, 0.1, null, -0.6)
    track6MinDbSetting.addValueObserver((value) => MidiMixSettings.track6MinDb = value.toDouble)
    val track6MaxDbSetting = prefs.getNumberSetting("Max", "Fader 6 dB", -999, 6.0, 0.1, null, 6.0)
    track6MaxDbSetting.addValueObserver((value) => MidiMixSettings.track6MaxDb = value.toDouble)

    // Track 7
    val track7MinDbSetting = prefs.getNumberSetting("Min", "Fader 7 dB", -999, 6.0, 0.1, null, -0.6)
    track7MinDbSetting.addValueObserver((value) => MidiMixSettings.track7MinDb = value.toDouble)
    val track7MaxDbSetting = prefs.getNumberSetting("Max", "Fader 7 dB", -999, 6.0, 0.1, null, 6.0)
    track7MaxDbSetting.addValueObserver((value) => MidiMixSettings.track7MaxDb = value.toDouble)

    // Track 8
    val track8MinDbSetting = prefs.getNumberSetting("Min", "Fader 8 dB", -999, 6.0, 0.1, null, -0.6)
    track8MinDbSetting.addValueObserver((value) => MidiMixSettings.track8MinDb = value.toDouble)
    val track8MaxDbSetting = prefs.getNumberSetting("Max", "Fader 8 dB", -999, 6.0, 0.1, null, 6.0)
    track8MaxDbSetting.addValueObserver((value) => MidiMixSettings.track8MaxDb = value.toDouble)

    // Master
    val masterMinDbSetting = prefs.getNumberSetting("Min", "Master dB", -999, 6.0, 0.1, null, -0.6)
    masterMinDbSetting.addValueObserver((value) => MidiMixSettings.masterMinDb = value.toDouble)
    val masterMaxDbSetting = prefs.getNumberSetting("Max", "Master dB", -999, 6.0, 0.1, null, 6.0)
    masterMaxDbSetting.addValueObserver((value) => MidiMixSettings.masterMaxDb = value.toDouble)

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
    Log.send(s"Track $track: minDb=$minDb, maxDb=$maxDb")
    val minVol = dbToVolume(minDb)
    val maxVol = dbToVolume(maxDb)
    Log.send(s"Track $track: minVol=$minVol, maxVol=$maxVol")
    (dbToVolume(minDb), dbToVolume(maxDb))

  def getDbRange(track: Int): (Double, Double) =
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
    (minDb, maxDb)

  private def dbToVolume(db: Double): Double =
    pow(E, ((db + 120.2) / 26.056))

end MidiMixSettings

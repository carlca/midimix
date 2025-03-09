package com.carlca.midimix

import scala.math._
import com.bitwig.extension.controller.api.*
import com.bitwig.extension.callback.DoubleValueChangedCallback
import com.carlca.logger.Log

object MidiMixSettings:
  enum PanSendMode derives CanEqual:
    case `FX Send`, `Pan`
  enum TrackMode derives CanEqual:
    case `One to One`, `No Groups`, `Groups Only`, `Tagged "<>" Only`
  var exclusiveSolo: Boolean = false
  var panSendMode: PanSendMode = PanSendMode.`FX Send`
  var trackMode:  TrackMode = TrackMode.`One to One`

  var track1MaxDb: Double = 6.0
  var track2MaxDb: Double = 6.0
  var track3MaxDb: Double = 6.0
  var track4MaxDb: Double = 6.0
  var track5MaxDb: Double = 6.0
  var track6MaxDb: Double = 6.0
  var track7MaxDb: Double = 6.0
  var track8MaxDb: Double = 6.0
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
    val track1MaxDbSetting = prefs.getNumberSetting("Max", "Fader 1 dB", -999, 6.0, 1.0, null, 6.0)
    track1MaxDb = track1MaxDbSetting.getRaw
    track1MaxDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        track1MaxDb = newValue
    )

    // Track 2
    val track2MaxDbSetting = prefs.getNumberSetting("Max", "Fader 2 dB", -999, 6.0, 1.0, null, 6.0)
    track2MaxDb = track2MaxDbSetting.getRaw
    track2MaxDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        track2MaxDb = newValue
    )

    // Track 3
    val track3MaxDbSetting = prefs.getNumberSetting("Max", "Fader 3 dB", -999, 6.0, 1.0, null, 6.0)
    track3MaxDb = track3MaxDbSetting.getRaw
    track3MaxDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        track3MaxDb = newValue
    )

    // Track 4
    val track4MaxDbSetting = prefs.getNumberSetting("Max", "Fader 4 dB", -999, 6.0, 1.0, null, 6.0)
    track4MaxDb = track4MaxDbSetting.getRaw
    track4MaxDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        track4MaxDb = newValue
    )

    // Track 5
    val track5MaxDbSetting = prefs.getNumberSetting("Max", "Fader 5 dB", -999, 6.0, 1.0, null, 6.0)
    track5MaxDb = track5MaxDbSetting.getRaw
    track5MaxDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        track5MaxDb = newValue
    )

    // Track 6
    val track6MaxDbSetting = prefs.getNumberSetting("Max", "Fader 6 dB", -999, 6.0, 1.0, null, 6.0)
    track6MaxDb = track6MaxDbSetting.getRaw
    track6MaxDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        track6MaxDb = newValue
    )

    // Track 7
    val track7MaxDbSetting = prefs.getNumberSetting("Max", "Fader 7 dB", -999, 6.0, 1.0, null, 6.0)
    track7MaxDb = track7MaxDbSetting.getRaw
    track7MaxDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        track7MaxDb = newValue
    )

    // Track 8
    val track8MaxDbSetting = prefs.getNumberSetting("Max", "Fader 8 dB", -999, 6.0, 1.0, null, 6.0)
    track8MaxDb = track8MaxDbSetting.getRaw
    track8MaxDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        track8MaxDb = newValue
    )

    // Master
    val masterMaxDbSetting = prefs.getNumberSetting("Max", "Master dB", -999, 6.0, 1.0, null, 6.0)
    masterMaxDb = masterMaxDbSetting.getRaw
    masterMaxDbSetting.addRawValueObserver(new DoubleValueChangedCallback:
      override def valueChanged(newValue: Double): Unit =
        masterMaxDb = newValue
    )

  def getMaxVolume(track: Int): Double =
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
    Log.send(s"Track $track max dB: $maxDb")
    val maxVolume = dbToVolume(maxDb)
    Log.send(s"Track $track max volume: $maxVolume")
    dbToVolume(maxDb)

  private def dbToVolume(db: Double): Double =
    pow(E, ((db + 120.2) / 26.056))

end MidiMixSettings

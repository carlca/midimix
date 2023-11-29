package com.carlca.midimix

import com.bitwig.extension.controller.api.*

object Settings: 
  enum PanSendMode derives CanEqual:
    case `FX Send`, `Pan`
  enum TrackMode derives CanEqual:
    case `One to One`, `No Groups`, `Groups Only`

  var exclusiveSolo: Boolean = false
  var panSendMode: PanSendMode = PanSendMode.`FX Send`
  var trackMode:  TrackMode = TrackMode.`One to One`
 
  def init(host: ControllerHost) = 
    initPreferences(host)
  
  def initPreferences(host: ControllerHost): Unit = 
    var prefs = host.getPreferences

    var soloSetting = prefs.getBooleanSetting("Exclusive Solo", "Solo Behaviour", false)
    soloSetting.addValueObserver((value) => Settings.exclusiveSolo = value)

    var values = PanSendMode.values.map(_.toString).toArray
    var panSetting = prefs.getEnumSetting("Send/Pan Mode", "Third Row Behaviour", values, PanSendMode.`FX Send`.toString())
    panSetting.addValueObserver((value) => Settings.panSendMode = PanSendMode.valueOf(value))

    var trackModes = TrackMode.values.map(_.toString).toArray
    var trackSetting = prefs.getEnumSetting("Track Mode", "Track Mapping Behaviour", trackModes, TrackMode.`One to One`.toString())
    trackSetting.addValueObserver((value) => Settings.trackMode = TrackMode.valueOf(value))


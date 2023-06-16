package com.carlca.midimix

import com.bitwig.extension.controller.api.*

object MidiMixLights:
  var mHost: ControllerHost = null
  def init(host: ControllerHost): Unit =
    mHost = host
  def flushLights: Unit = 
    flushArmLights
    EventHandler.getButtonMode match
      case ButtonMode.Mute => flushMuteLights
      case ButtonMode.Solo => flushSoloLights
  def flushArmLights: Unit = (0 to 7).foreach (t => Tracks.flushArmLight(t))
  def flushMuteLights: Unit = (0 to 7).foreach (t => Tracks.flushMuteLight(t))
  def flushSoloLights: Unit = (0 to 7).foreach (t => Tracks.flushSoloLight(t))
  
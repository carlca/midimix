package com.carlca.midimix

import com.bitwig.extension.controller.api.*

object MidiMixLights:
  var mHost: ControllerHost = null

  def init(host: ControllerHost): Unit =
    mHost = host
  def flushLights: Unit = flushArmLights
  def flushArmLights: Unit = (0 to 7).foreach (t => Tracks.flushArmLight(mHost, t))

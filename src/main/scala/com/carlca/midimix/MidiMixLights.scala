package com.carlca.midimix

object MidiMixLights: 
  def flushLights: Unit = flushArmLights
  def flushArmLights: Unit = (0 to 7).foreach(t => flushArmLight(t))
  def flushArmLight(t: Int): Unit = None
end MidiMixLights

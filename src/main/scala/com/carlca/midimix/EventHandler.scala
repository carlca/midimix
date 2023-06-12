package com.carlca.midimix

import com.carlca.logger.Log

object EventHandler:
  def volumeChanged(e: MidiEvent): Unit = 
    Log.send(e.toString); Tracks.setVolume(e.track, e.volume)
  def sendAChanged(e: MidiEvent): Unit = 
    Log.send(e.toString)
  def sendBChanged(e: MidiEvent): Unit = 
    Log.send(e.toString)
  def sendCChanged(e: MidiEvent): Unit = 
    Log.send(e.toString)
  def masterChanged(e: MidiEvent): Unit =
    Log.send(e.toString); Tracks.setMasterVolume(e.volume)
  def mutePressed(e: MidiEvent): Unit =
    Log.send(e.toString)
  def armPressed(e: MidiEvent): Unit = 
    Log.send(e.toString)
  def soloPressed(e: MidiEvent): Unit = 
    Log.send(e.toString)
  def bankLeftPressed(e: MidiEvent): Unit = 
    Log.send(e.toString)
  def bankRightPressed(e: MidiEvent): Unit = 
    Log.send(e.toString)
end EventHandler  


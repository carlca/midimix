package com.carlca.midimix

import com.carlca.logger.Log

object EventHandler:
  def volumeChanged(e: MidiEvent): Unit = 
    Log.send(e.toString); Tracks.setVolume(e.track, e.volume)
  def sendAChanged(e: MidiEvent): Unit = 
    Log.send(e.toString); Tracks.setSendA(e.track, 0, e.volume)
  def sendBChanged(e: MidiEvent): Unit = 
    Log.send(e.toString); Tracks.setSendB(e.track, 1, e.volume)
  def sendCChanged(e: MidiEvent): Unit = 
    Log.send(e.toString); Tracks.setSendC(e.track, 2, e.volume)
  def masterChanged(e: MidiEvent): Unit =
    Log.send(e.toString); Tracks.setMasterVolume(e.volume)
  def mutePressed(e: MidiEvent): Unit =
    Log.send(e.toString); Tracks.setMute(e.track)
  def armPressed(e: MidiEvent): Unit = 
    Log.send(e.toString); Tracks.setArm(e.track)
  def soloPressed(e: MidiEvent): Unit = 
    Log.send(e.toString); Tracks.setSolo()
  def bankLeftPressed(e: MidiEvent): Unit = 
    Log.send(e.toString); Tracks.setBankLeft()
  def bankRightPressed(e: MidiEvent): Unit = 
    Log.send(e.toString); Tracks.setBankRight()
end EventHandler  


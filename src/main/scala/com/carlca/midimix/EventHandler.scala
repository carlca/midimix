package com.carlca.midimix

import com.carlca.logger.Log

enum ButtonMode derives CanEqual:
  case Mute, Solo

object EventHandler:
  import ButtonMode._
  private var mMode = ButtonMode.Mute
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
    Log.send(e.toString);  if mMode == Mute then Tracks.toggleMute(e.track) else Tracks.toggleSolo(e.track)
  def soloPressed(e: MidiEvent): Unit = 
    Log.send(e.toString); mMode = Solo
  def soloReleased(e: MidiEvent): Unit = 
    Log.send(e.toString); mMode = Mute
  def getButtonMode: ButtonMode = mMode
  def armPressed(e: MidiEvent): Unit = 
    Log.send(e.toString); Tracks.toggleArm(e.track)
  def bankLeftPressed(e: MidiEvent): Unit = 
    Log.send(e.toString); Tracks.setBankLeft
  def bankRightPressed(e: MidiEvent): Unit = 
    Log.send(e.toString); Tracks.setBankRight
end EventHandler  


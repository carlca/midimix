package com.carlca.midimix

import com.carlca.config.Config
import com.carlca.logger.Log

object EventHandler:
  def volumeChanged(e: MidiEvent): Unit = Log.send(s"Volume changed - track: ${e.track.get}  volume: ${e.volume}")
  def sendAChanged(e: MidiEvent): Unit = Log.send(s"Send A changed - track: ${e.track.get}  volume: ${e.volume}")
  def sendBChanged(e: MidiEvent): Unit = Log.send(s"Send B changed - track: ${e.track.get}  volume: ${e.volume}")
  def sendCChanged(e: MidiEvent): Unit = Log.send(s"Send C changed - track: ${e.track.get}  volume: ${e.volume}")
  def masterChanged(e: MidiEvent): Unit = Log.send(s"Master changed - volume: ${e.volume}")
  def mutePressed(e: MidiEvent): Unit = Log.send(s"Mute pressed - track: ${e.track}")
  def armPressed(e: MidiEvent): Unit = Log.send(s"Rec Arm pressed - track: ${e.track}")
  def soloPressed(e: MidiEvent): Unit = Log.send("Solo All pressed")
  def bankLeftPressed(e: MidiEvent): Unit = Log.send("Bank Left pressed")
  def bankRightPressed(e: MidiEvent): Unit = Log.send("Bank Right pressed")
end EventHandler  


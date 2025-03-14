package com.carlca.midimix

import com.carlca.logger.Log

import com.carlca.bitwiglibrary.Tracks

enum ButtonMode derives CanEqual:
  case Mute, Solo

object EventHandler:

  private val LOG_MIDI_EVENTS = true

  private var mMode = ButtonMode.Mute

  def getButtonMode: ButtonMode = mMode

  def volumeChanged(e: MidiEvent): Unit =
    if LOG_MIDI_EVENTS then Log.send(e.toString)
    Tracks.setVolume(e.track, e.volume)

  def sendAChanged(e: MidiEvent): Unit =
    if LOG_MIDI_EVENTS then Log.send(e.toString)
    Tracks.setSendA(e.track, 0, e.volume)

  def sendBChanged(e: MidiEvent): Unit =
    if LOG_MIDI_EVENTS then Log.send(e.toString)
    Tracks.setSendB(e.track, 1, e.volume)

  def sendCChanged(e: MidiEvent): Unit =
    if LOG_MIDI_EVENTS then Log.send(e.toString)
    Tracks.setSendC(e.track, 2, e.volume)

  def masterChanged(e: MidiEvent): Unit =
    if LOG_MIDI_EVENTS then Log.send(e.toString)
    Tracks.setMasterVolume(e.volume)

  def mutePressed(e: MidiEvent): Unit =
    if LOG_MIDI_EVENTS then Log.send(e.toString)
    Tracks.toggleMute(e.track)

  def soloPressed(e: MidiEvent): Unit =
    if LOG_MIDI_EVENTS then Log.send(e.toString)
    Tracks.toggleSolo(e.track)

  def soloDown(e: MidiEvent): Unit =
    if LOG_MIDI_EVENTS then Log.send(e.toString)
    mMode = ButtonMode.Solo;

  def soloUp(e: MidiEvent): Unit =
    if LOG_MIDI_EVENTS then Log.send(e.toString)
    mMode = ButtonMode.Mute;

  def armPressed(e: MidiEvent): Unit =
    if LOG_MIDI_EVENTS then Log.send(e.toString)
    Tracks.toggleArm(e.track)

  def bankLeftPressed(e: MidiEvent): Unit =
    if LOG_MIDI_EVENTS then Log.send(e.toString)
    Tracks.setBankLeft()

  def bankRightPressed(e: MidiEvent): Unit =
    if LOG_MIDI_EVENTS then Log.send(e.toString)
    Tracks.setBankRight()
end EventHandler

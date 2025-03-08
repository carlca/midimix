package com.carlca.midimix

import com.carlca.logger.Log

import Tracks.*

enum ButtonMode derives CanEqual:
  case Mute, Solo

object EventHandler:

  private val LOG_MIDI_EVENTS = false

  private var mMode = ButtonMode.Mute

  private def scaleVolume(v: Int, minVol: Double, maxVol: Double): Double =
    minVol + (maxVol - minVol) * (v / 127.0)

  def getButtonMode: ButtonMode = mMode

  def volumeChanged(e: MidiEvent): Unit =
    if LOG_MIDI_EVENTS then Log.send(e.toString)
    val v = e.volume

    val (minVol, maxVol) = MidiMixSettings.getVolumeRange(e.track)
    val vv = v

    val (minDb, maxDb) = MidiMixSettings.getDbRange(e.track)
    Log.send(s"Track ${e.track}: minDb=$minDb, maxDb=$maxDb")

    // val vv = scaleVolume(v, minVol, maxVol)

    Log.send(s"Volume changed: v=$v, vv=$vv, minVol=$minVol, maxVol=$maxVol")
    Log.send("")

    Tracks.setVolume(e.track, vv)

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

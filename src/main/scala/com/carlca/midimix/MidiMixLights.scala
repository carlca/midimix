package com.carlca.midimix

import com.bitwig.extension.controller.api.*

object MidiMixLights:
  var mHost: ControllerHost = null

  // Internal state to track the current light status for each button
  private val armLightsState  = Array.fill(8)(false)
  private val muteLightsState = Array.fill(8)(false)
  private val soloLightsState = Array.fill(8)(false)

  def init(host: ControllerHost): Unit =
    mHost = host


  /** Flushes all button lights, checking for state changes */
  def flushLights: Unit =
    flushArmLights
    import ButtonMode._
    EventHandler.getButtonMode match
      case Mute => flushMuteLights
      case Solo => flushSoloLights

  /** Flushes all Arm button lights, checking for state changes */
  private def flushArmLights: Unit =
    (0 to 7).foreach(t => flushArmLight(t))

  /** Flushes all Mute button lights, checking for state changes */
  private def flushMuteLights: Unit =
    (0 to 7).foreach(t => flushMuteLight(t))

  /** Flushes all Solo button lights, checking for state changes */
  private def flushSoloLights: Unit =
    (0 to 7).foreach(t => flushSoloLight(t))

  /** Flushes Mute button light for one track, checking for state change */
  private def flushMuteLight(t: Int): Unit =
    val isMuted = Tracks.getIsMuted(t) // Get the current muted state
    if muteLightsState(t) != isMuted then // Check if the state has changed
      mHost.getMidiOutPort(0).sendMidi(0x90, Maps.getMuteMidi(t).getOrElse(0x00), if isMuted then 0x00 else 0x7F)
      muteLightsState(t) = isMuted // Update the state
    end if

  /** Flushes Solo button light for one track, checking for state change */
  private def flushSoloLight(t: Int): Unit =
    val isSolo = Tracks.getIsSolo(t)
    if soloLightsState(t) != isSolo then
      mHost.getMidiOutPort(0).sendMidi(0x90, Maps.getSoloMidi(t).get, if isSolo then 0x7F else 0x00)
      soloLightsState(t) = isSolo
    end if

  /** Flushes Arm button light for one track, checking for state change */
  private def flushArmLight(t: Int): Unit =
    val isArmed = Tracks.getIsArmed(t)
    if armLightsState(t) != isArmed then
      mHost.getMidiOutPort(0).sendMidi(0x90, Maps.getArmMidi(t).get, if isArmed then 0x7F else 0x00)
      armLightsState(t) = isArmed
    end if

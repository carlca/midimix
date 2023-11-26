package com.carlca.midimix

import com.bitwig.extension.controller.api.*

object MidiMixLights:
	var mHost: ControllerHost = null
	def init(host: ControllerHost): Unit =
		mHost = host

 /** Flushes all button lights */ 
	def flushLights: Unit = 
		flushArmLights
		import ButtonMode._
		EventHandler.getButtonMode match
			case Mute => flushMuteLights
			case Solo => flushSoloLights

 /** Flushes all Arm button lights */
	private def flushArmLights: Unit = (0 to 7).foreach (t => flushArmLight(t))

 /** Flushes all Mute button lights */  
	private def flushMuteLights: Unit = (0 to 7).foreach (t => flushMuteLight(t))

 /** Flushes all Solo button lights */    
	private def flushSoloLights: Unit = (0 to 7).foreach (t => flushSoloLight(t))

	/** Flushes Mute button light for one track */
	private def flushMuteLight(t: Int): Unit = mHost.getMidiOutPort(0).sendMidi(0x90, Maps.getMuteMidi(t).get, if Tracks.getIsMuted(t) then 0x00 else 0x7F);

 /** Flushes Solo button light for one track */
	private def flushSoloLight(t: Int): Unit = mHost.getMidiOutPort(0).sendMidi(0x90, Maps.getSoloMidi(t).get, if Tracks.getIsSolo(t) then 0x7F else 0x00);

 /** Flushes Arm button light for one track */    
	private def flushArmLight(t: Int): Unit  = mHost.getMidiOutPort(0).sendMidi(0x90, Maps.getArmMidi(t).get, if Tracks.getIsArmed(t) then 0x7F else 0x00);
	
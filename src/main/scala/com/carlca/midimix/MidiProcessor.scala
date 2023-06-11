package com.carlca.midimix

import com.bitwig.extension.api.util.midi.ShortMidiMessage

case class MidiEvent(track: Option[Int], volume: Int) 

object MidiProcessor: 
  // Public process method - takes in msg: ShortMidiMessage's and dispatched appropriate event...  
  def process(msg: ShortMidiMessage): Unit =
    import EventHandler.*
    val e: MidiEvent = MidiEvent(Maps.getTrack(msg), msg.getData2())
    val e0: MidiEvent = MidiEvent(None, 0)
    if msg.isControlChange then
      Maps.getCCKind(msg).get match
        case CCKind.SendA         => sendAChanged(e)
        case CCKind.SendB         => sendBChanged(e)
        case CCKind.SendC         => sendCChanged(e)
        case CCKind.Volume        => volumeChanged(e)
        case CCKind.Master        => masterChanged(e)
    else if msg.isNoteOff then
      Maps.getButtonType(msg) match 
        case ButtonType.Mute      => mutePressed(MidiEvent(Maps.getMute(msg), 0))
        case ButtonType.Arm       => armPressed(MidiEvent(Maps.getArm(msg), 0))
        case ButtonType.Solo      => soloPressed(e0)
        case ButtonType.BankLeft  => bankLeftPressed(e0)
        case ButtonType.BankRight => bankRightPressed(e0)
        
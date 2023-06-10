package com.carlca.midimix

import com.bitwig.extension.api.util.midi.ShortMidiMessage

case class MidiEvent(track: Option[Int], volume: Int) 

object MidiProcessor: 
  // Public process method - takes in msg: ShortMidiMessage's and dispatched appropriate event...  
  def process(msg: ShortMidiMessage): Unit =
    import Handler.*
    val e: MidiEvent = MidiEvent(Maps.getTrack(msg), msg.getData2())
    if msg.isControlChange then
      Maps.getCCKind(msg).get match
        case CCKind.SendA         => sendAChanged(e)
        case CCKind.SendB         => sendBChanged(e)
        case CCKind.SendC         => sendCChanged(e)
        case CCKind.Volume        => volumeChanged(e)
        case CCKind.Master        => masterChanged(e)
    else if msg.isNoteOff then
      Maps.getButtonType(msg) match 
        case ButtonType.Mute      => mutePressed(e)
        case ButtonType.Arm       => armPressed(e)
        case ButtonType.Solo      => soloPressed(e)
        case ButtonType.BankLeft  => bankLeftPressed(e)
        case ButtonType.BankRight => bankRightPressed(e)
        
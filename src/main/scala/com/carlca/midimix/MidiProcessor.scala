package com.carlca.midimix

import com.bitwig.extension.api.util.midi.ShortMidiMessage

case class MidiEvent(track: Int, volume: Int, sort: String) 

object MidiProcessor: 
  import CCKind.*
  import ButtonType.*
  def makeMuteEvent(msg: ShortMidiMessage, sort: String): MidiEvent = MidiEvent(Maps.getMute(msg).get, 0, Mute.toString)
  def makeArmEvent(msg: ShortMidiMessage, sort: String): MidiEvent = MidiEvent(Maps.getArm(msg).get, 0, Arm.toString)
  def process(msg: ShortMidiMessage): Unit =
    import EventHandler.*
    if msg.isControlChange then
      val kind = Maps.getCCKind(msg).get 
      val eCC: MidiEvent = MidiEvent(Maps.getTrack(msg).get, msg.getData2(), kind.toString)
      kind match
        case SendA     => sendAChanged(eCC)
        case SendB     => sendBChanged(eCC)
        case SendC     => sendCChanged(eCC)
        case Volume    => volumeChanged(eCC)
        case Master    => masterChanged(eCC)
    else if msg.isNoteOff then
      val buttonType = Maps.getButtonType(msg)
      val e0: MidiEvent = MidiEvent(0, 0, buttonType.toString)
      buttonType match 
        case Mute      => mutePressed(makeMuteEvent(msg, buttonType.toString))
        case Arm       => armPressed(makeArmEvent(msg, buttonType.toString))
        case Solo      => soloPressed(e0)
        case BankLeft  => bankLeftPressed(e0)
        case BankRight => bankRightPressed(e0)
        
package com.carlca.midimix

import com.bitwig.extension.api.util.midi.ShortMidiMessage

case class MidiEvent(track: Int, volume: Int, sort: String) 

object MidiProcessor: 
  import CCKind.*
  import ButtonType.*
  private def makeMuteEvent(msg: ShortMidiMessage, sort: String): MidiEvent = MidiEvent(Maps.getMute(msg).get, 0, Mute.toString)
  private def makeSoloEvent(msg: ShortMidiMessage, sort: String): MidiEvent = MidiEvent(Maps.getSolo(msg).get, 0, Solo.toString)
  private def makeArmEvent(msg: ShortMidiMessage, sort: String): MidiEvent = MidiEvent(Maps.getArm(msg).get, 0, Arm.toString)
  def process(msg: ShortMidiMessage): Unit =
    import EventHandler.*
    if msg.isControlChange then
      val kind = Maps.getCCKind(msg).get 
      val eCC: MidiEvent = MidiEvent(Maps.getTrack(msg).get, msg.getData2, kind.toString)
      kind match
        case SendA     => sendAChanged(eCC)
        case SendB     => sendBChanged(eCC)
        case SendC     => sendCChanged(eCC)
        case Volume    => volumeChanged(eCC)
        case Master    => masterChanged(eCC)
    else
      val buttonType = Maps.getButtonType(msg)
      val e0: MidiEvent = MidiEvent(0, 0, buttonType.toString)
      if msg.isNoteOn then
        if buttonType == SoloMode then soloDown(e0)
      else
        if msg.isNoteOff then
          buttonType match 
            case Mute      => mutePressed(makeMuteEvent(msg, buttonType.toString))
            case Arm       => armPressed(makeArmEvent(msg, buttonType.toString))
            case Solo      => soloPressed(makeSoloEvent(msg, buttonType.toString))
            case SoloMode  => soloUp(e0)
            case BankLeft  => bankLeftPressed(e0)
            case BankRight => bankRightPressed(e0)
        
package com.carlca.midimix

import com.bitwig.extension.api.util.midi.ShortMidiMessage

trait MidiEventHandler: 
  def handleEvent(event: MidiEvent): Unit = None

case class MidiEvent(track: Option[Int], kind: Option[Int], volume: Int) extends MidiEventHandler

object MidiProcessor: 
  private var volumeChangedObserver: Option[MidiEventHandler] = None
  private var sendAChangedObserver: Option[MidiEventHandler] = None
  private var sendBChangedObserver: Option[MidiEventHandler] = None
  private var sendCChangedObserver: Option[MidiEventHandler] = None
  private var mutePressedObserver: Option[MidiEventHandler] = None
  private var armPressedObserver: Option[MidiEventHandler] = None
  private var soloPressedObserver: Option[MidiEventHandler] = None
  private var bankLeftPressedObserver: Option[MidiEventHandler] = None
  private var bankRightPressedObserver: Option[MidiEventHandler] = None

  def onVolumeChanged(observer: MidiEventHandler): Unit =
    volumeChangedObserver = Some(observer) 
  def onSendAChanged(observer: MidiEventHandler): Unit =
    sendAChangedObserver = Some(observer) 
  def onSendBChanged(observer: MidiEventHandler): Unit =
    sendBChangedObserver = Some(observer) 
  def onSendCChanged(observer: MidiEventHandler): Unit =
    sendCChangedObserver = Some(observer) 
  def onMutePressed(observer: MidiEventHandler): Unit =
    mutePressedObserver = Some(observer)
  def onArmPressed(observer: MidiEventHandler): Unit =
    armPressedObserver = Some(observer)
  def onSoloPressed(observer: MidiEventHandler): Unit =
    soloPressedObserver = Some(observer)
  def onBankLeftPressed(observer: MidiEventHandler): Unit =
    bankLeftPressedObserver = Some(observer)
  def onBankRightPressed(observer: MidiEventHandler): Unit =
    bankRightPressedObserver = Some(observer)
  
  def process(msg: ShortMidiMessage): Unit =
    if msg.isControlChange then
      volumeChangedObserver.foreach(_.handleEvent(MidiEvent(Maps.getTrack(msg), Maps.getKind(msg), msg.getData2())))
    else if msg.isNoteOff then
      Maps.getButtonType(msg) match 
        case ButtonType.Mute      => mutePressedObserver.foreach(_.handleEvent(MidiEvent(Maps.getMute(msg), None, 0)))
        case ButtonType.Arm       => armPressedObserver.foreach(_.handleEvent(MidiEvent(Maps.getArm(msg), None, 0)))
        case ButtonType.Solo      => soloPressedObserver.foreach(_.handleEvent(MidiEvent(None, None, 0)))
        case ButtonType.BankLeft  => bankLeftPressedObserver.foreach(_.handleEvent(MidiEvent(None, None, 0)))
        case ButtonType.BankRight => bankRightPressedObserver.foreach(_.handleEvent(MidiEvent(None, None, 0)))
        
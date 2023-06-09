package com.carlca.midimix

import com.bitwig.extension.api.util.midi.ShortMidiMessage

trait MidiEventHandler: 
  def handleEvent(event: MidiEvent): Unit = None

case class MidiEvent(track: Option[Int], volume: Int) extends MidiEventHandler

object MidiProcessor: 
  private var volumeChangedObserver: Option[MidiEventHandler] = None
  private var masterChangedObserver: Option[MidiEventHandler] = None
  private var sendAChangedObserver: Option[MidiEventHandler] = None
  private var sendBChangedObserver: Option[MidiEventHandler] = None
  private var sendCChangedObserver: Option[MidiEventHandler] = None
  private var mutePressedObserver: Option[MidiEventHandler] = None
  private var armPressedObserver: Option[MidiEventHandler] = None
  private var soloPressedObserver: Option[MidiEventHandler] = None
  private var bankLeftPressedObserver: Option[MidiEventHandler] = None
  private var bankRightPressedObserver: Option[MidiEventHandler] = None
  // Public event assignment methods
  def onVolumeChanged(observer: MidiEventHandler): Unit =
    volumeChangedObserver = Some(observer) 
  def onMasterChanged(observer: MidiEventHandler): Unit =
    masterChangedObserver = Some(observer) 
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
  // Public process method - takes in msg: ShortMidiMessage's and dispatched appropriate event...  
  def process(msg: ShortMidiMessage): Unit =
    if msg.isControlChange then
      Maps.getCCKind(msg).get match
        case CCKind.SendA         => sendAChangedObserver.foreach(_.handleEvent(MidiEvent(Maps.getTrack(msg), msg.getData2())))
        case CCKind.SendB         => sendBChangedObserver.foreach(_.handleEvent(MidiEvent(Maps.getTrack(msg), msg.getData2())))
        case CCKind.SendC         => sendCChangedObserver.foreach(_.handleEvent(MidiEvent(Maps.getTrack(msg), msg.getData2())))
        case CCKind.Volume        => volumeChangedObserver.foreach(_.handleEvent(MidiEvent(Maps.getTrack(msg), msg.getData2())))
        case CCKind.Master        => masterChangedObserver.foreach(_.handleEvent(MidiEvent(Maps.getTrack(msg), msg.getData2())))
    else if msg.isNoteOff then
      Maps.getButtonType(msg) match 
        case ButtonType.Mute      => mutePressedObserver.foreach(_.handleEvent(MidiEvent(Maps.getMute(msg), 0)))
        case ButtonType.Arm       => armPressedObserver.foreach(_.handleEvent(MidiEvent(Maps.getArm(msg), 0)))
        case ButtonType.Solo      => soloPressedObserver.foreach(_.handleEvent(MidiEvent(None, 0)))
        case ButtonType.BankLeft  => bankLeftPressedObserver.foreach(_.handleEvent(MidiEvent(None, 0)))
        case ButtonType.BankRight => bankRightPressedObserver.foreach(_.handleEvent(MidiEvent(None, 0)))
        
package com.carlca
package midimix

import com.bitwig.extension.api.util.midi.ShortMidiMessage
import com.bitwig.extension.controller.ControllerExtension
import com.bitwig.extension.controller.api.*
import com.carlca.config.Config
import com.carlca.logger.Log
import com.carlca.utils.StringUtils

import scala.collection.mutable
import scala.collection.mutable.Stack

class MidiMixExtension(definition: MidiMixExtensionDefinition, host: ControllerHost)
    extends ControllerExtension(definition, host):
  // Class instances
  private var mTransport: Transport       = null
  private var mTrackBank: TrackBank       = null
  private var mMainTrackBank: TrackBank   = null
  private var mEffectTrackBank: TrackBank = null
  private var mMasterTrack: Track         = null
  private var mCursorTrack: CursorTrack   = null
  // Consts
  private val APP_NAME                    = "com.carlca.MidiMix"
  private val MAX_TRACKS: Int             = 0x10
  private val MAX_SENDS:  Int             = 0x03
  private val MAX_SCENES: Int             = 0x10
  
  override def init: Unit =
    val host = getHost
    initWork(host)
    MidiProcessor.onVolumeChanged(new VolumeChangedWatcher)
    MidiProcessor.onSendAChanged(new SendAChangedWatcher)
    MidiProcessor.onSendBChanged(new SendBChangedWatcher)
    MidiProcessor.onSendCChanged(new SendCChangedWatcher)
    MidiProcessor.onMutePressed(new MutePressedWatcher)
    MidiProcessor.onArmPressed(new ArmPressedWatcher)
    MidiProcessor.onSoloPressed(new SoloPressedWatcher)
    MidiProcessor.onBankLeftPressed(new BankLeftPressedWatcher)
    MidiProcessor.onBankRightPressed(new BankRightPressedWatcher)

  private def initWork(host: ControllerHost): Unit =
    Config.init(APP_NAME)
    Log.cls
    Log.send(Maps.tracksLog)
    Log.send(Maps.kindsLog)
    Log.send(Maps.mutesLog)
    Log.send(Maps.armsLog)
    Log.line
    initTransport(host)
    initOnMidiCallback(host)
    initOnSysexCallback(host)
    initTrackBanks(host)
    initMasterTrack(host)
    initCursorTrack(host)
  end initWork
  override def exit: Unit = Log.send("MidiMix Exited")

  override def flush: Unit = ()

  private def initOnMidiCallback(host: ControllerHost): Unit =
    host.getMidiInPort(0).setMidiCallback((a, b, c) => onMidi0(ShortMidiMessage(a, b, c)))

  private def initOnSysexCallback(host: ControllerHost): Unit =
    host.getMidiInPort(0).setSysexCallback(onSysex0)

  private def initTransport(host: ControllerHost): Unit = mTransport = host.createTransport

  private def initTrackBanks(host: ControllerHost): Unit =
    mTrackBank = host.createTrackBank(MAX_TRACKS, MAX_SENDS, MAX_SCENES)
    mMainTrackBank = host.createMainTrackBank(MAX_TRACKS, MAX_SENDS, MAX_SCENES)
    mEffectTrackBank = host.createEffectTrackBank(MAX_SENDS, MAX_SENDS, MAX_SCENES)
    initInterest(mTrackBank)
    initInterest(mMainTrackBank)
    initInterest(mEffectTrackBank)
  end initTrackBanks

  private def initMasterTrack(host: ControllerHost): Unit =
    mMasterTrack = host.createMasterTrack(0)

  private def initInterest(bank: TrackBank): Unit =
    bank.itemCount.markInterested
    bank.channelCount.markInterested
    for i <- 0 until bank.getCapacityOfBank do
      val track = bank.getItemAt(i)
      track.name.markInterested
      track.isGroup.markInterested
      track.canHoldNoteData.markInterested
      track.canHoldAudioData.markInterested
      track.trackType.markInterested
      track.position.markInterested
      track.exists.markInterested
      val parent = track.createParentTrack(0, 0)
      parent.name.markInterested
  end initInterest

  private def initCursorTrack(host: ControllerHost): Unit =
    mCursorTrack = host.createCursorTrack(1, 0)

  private def onMidi0(msg: ShortMidiMessage): Unit =
    MidiProcessor.process(msg)
  end onMidi0

  @FunctionalInterface
  private def onSysex0(data: String): Unit = ()

end MidiMixExtension

class VolumeChangedWatcher extends MidiEventHandler: 
  override def handleEvent(event: MidiEvent): Unit = event match 
    case MidiEvent(track, kind, volume) => Log.send(s"Volume changed - track: ${track.get}  kind: ${kind.get}  volume: $volume")
    case null =>

class SendAChangedWatcher extends MidiEventHandler: 
  override def handleEvent(event: MidiEvent): Unit = event match 
    case MidiEvent(track, kind, volume) => Log.send(s"Send A changed - track: ${track.get}  kind: ${kind.get}  volume: $volume")
    case null =>

class SendBChangedWatcher extends MidiEventHandler: 
  override def handleEvent(event: MidiEvent): Unit = event match 
    case MidiEvent(track, kind, volume) => Log.send(s"Send B changed - track: ${track.get}  kind: ${kind.get}  volume: $volume")
    case null =>

class SendCChangedWatcher extends MidiEventHandler: 
  override def handleEvent(event: MidiEvent): Unit = event match 
    case MidiEvent(track, kind, volume) => Log.send(s"Send C changed - track: ${track.get}  kind: ${kind.get}  volume: $volume")
    case null =>

class MutePressedWatcher extends MidiEventHandler:
  override def handleEvent(event: MidiEvent): Unit = event match
    case MidiEvent(track, _, _) => Log.send(s"Mute pressed - track: ${track}")
    case null =>

class ArmPressedWatcher extends MidiEventHandler:
  override def handleEvent(event: MidiEvent): Unit = event match
    case MidiEvent(track, _, _) => Log.send(s"Arm pressed - track: ${track}")
    case null =>

class SoloPressedWatcher extends MidiEventHandler:
  override def handleEvent(event: MidiEvent): Unit = event match
    case MidiEvent(_, _, _) => Log.send(s"Solo All pressed")
    case null =>

class BankLeftPressedWatcher extends MidiEventHandler:
  override def handleEvent(event: MidiEvent): Unit = event match
    case MidiEvent(_, _, _) => Log.send(s"Bank Left pressed")
    case null =>

class BankRightPressedWatcher extends MidiEventHandler:
  override def handleEvent(event: MidiEvent): Unit = event match
    case MidiEvent(_, _, _) => Log.send(s"Bank Right pressed")
    case null =>

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

// private def processControlChange(msg: ShortMidiMessage): Unit =
//   val track: Option[Int] = mTracks.get(msg.getData1)
//   val kindNum: Option[Int]  = mKinds.get(msg.getData1)
//   val valueNum: Int         = msg.getData2
//   Log.send(s"Track: ${track.get}  Kind: ${kindNum.get}  Value: $valueNum")
//   val volume = valueNum / 127.0
//   if track.contains(MASTER) then mMasterTrack.volume.set(volume)
//   else
//     val track = mTrackBank.getItemAt(track.get)
//     track.volume.set(volume)
// end processControlChange

class MidiMixExtension(definition: MidiMixExtensionDefinition, host: ControllerHost)
    extends ControllerExtension(definition, host):
  // Class instances
  private var mTransport: Transport                          = null
  private var mPending: mutable.Stack[Int]                   = mutable.Stack.empty
  private var mTrackBank: TrackBank                          = null
  private var mMainTrackBank: TrackBank                      = null
  private var mEffectTrackBank: TrackBank                    = null
  private var mMasterTrack: Track                            = null
  private var mCursorTrack: CursorTrack                      = null
  // Consts
  private val APP_NAME        = "com.carlca.MidiMix"
  private val MAX_TRACKS: Int = 0x10
  private val MAX_SENDS:  Int = 0x03
  private val MAX_SCENES: Int = 0x10
  
  override def init: Unit =
    val host = getHost
    initWork(host)
    MidiProcessor.onVolumeChanged(new VolumeChangeWatcher)

  private def initWork(host: ControllerHost): Unit =
    Config.init(APP_NAME)
    Log.cls
    Log.send(Maps.tracksLog)
    Log.send(Maps.kindsLog);
    Log.line
    Log.send("initWork begun")
    initTransport(host)
    initOnMidiCallback(host)
    initOnSysexCallback(host)
    initTrackBanks(host)
    initMasterTrack(host)
    initCursorTrack(host)
    Log.send("initWork ended")
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

  private def processControlChange(msg: ShortMidiMessage): Unit =
    val track: Option[Int] = ??? // mTracks.get(msg.getData1)
    val kind: Option[Int]  = ??? // mKinds.get(msg.getData1)
    val value: Int = msg.getData2
    Log.send(s"Track: $track  Kind: $kind  Value: $value")
    val volume = value / 127.0
    if track.contains(???) then mMasterTrack.volume.set(volume)
    else
      mTrackBank.getItemAt(track.get).volume.set(volume)
  end processControlChange

  private def processNoteOff: Unit =
    if mPending.nonEmpty then processPending(mPending.pop)

  private def processPending(pending: Int): Unit =
    Log.line
    Log.send(s"pending: $pending")
    Log.send(s"mMainTrackBank.itemCount.getAsInt: ${mMainTrackBank.itemCount.getAsInt}")
    Log.send(s"mMainTrackBank.getSizeOfBank: ${mMainTrackBank.getSizeOfBank}")
    Log.send(s"mMainTrackBank.getCapacityOfBank: ${mMainTrackBank.getCapacityOfBank}")
    Log.line
    Log.send(s"mTrackBank.itemCount.getAsInt: ${mTrackBank.itemCount.getAsInt}")
    Log.send(s"mTrackBank.getSizeOfBank: ${mTrackBank.getSizeOfBank}")
    Log.send(s"mTrackBank.getCapacityOfBank: ${mTrackBank.getCapacityOfBank}")
    Log.line
    Log.send(s"mEffectTrackBank.itemCount.getAsInt: ${mEffectTrackBank.itemCount.getAsInt}")
    Log.send(s"mEffectTrackBank.getSizeOfBank: ${mEffectTrackBank.getSizeOfBank}")
    Log.send(s"mEffectTrackBank.getCapacityOfBank: ${mEffectTrackBank.getCapacityOfBank}")
    Log.line
    iterateTracks(mMainTrackBank)
    Log.line
    iterateTracks(mTrackBank)
    Log.line
    iterateTracks(mEffectTrackBank)
    Log.line
    iterateTracks
    Log.line
  end processPending

  private def iterateTracks: Unit = for i <- 0 until mTrackBank.getSizeOfBank do
    val track = mTrackBank.getItemAt(i)
    if track.isGroup.get then Log.send(s"Group: ${track.name.get}")
    else
      val trackType = track.trackType.get
      if trackType.nonEmpty then
        if "InstrumentAudioEffectMaster".contains(trackType) then
          Log.send(s"{$trackType}: ${track.name.get}  Position: ${track.position.get}")
  end iterateTracks

  private def iterateTracks(bank: TrackBank): Unit =
    val trackCount = Math.min(bank.itemCount.getAsInt, bank.getSizeOfBank)
    val bankClass  = StringUtils.unadornedClassName(bank)
    Log.send(s"$bankClass.(Effective)itemCount $trackCount")
    for i <- 0 until trackCount do
      val track = bank.getItemAt(i)
      Log.send(s"Track #: $i, Name: ${track.name.get}, Positions: ${track.position.get}, IsSubscribed: ${track.name.isSubscribed}")
  end iterateTracks

  // private def processNoteOn(msg: ShortMidiMessage): Unit = mPending.push(msg.getData1)

  private def onMidi0(msg: ShortMidiMessage): Unit =
    MidiProcessor.process(msg)
    // if msg.isControlChange then processControlChange(msg)
    // else if msg.isNoteOff then processNoteOff
    // else if msg.isNoteOn then processNoteOn(msg)
  end onMidi0

  @FunctionalInterface
  private def onSysex0(data: String): Unit = ()

end MidiMixExtension

class VolumeChangeWatcher extends MidiEventHandler: 
  override def handleEvent(event: MidiEvent): Unit = 
    event match 
      case VolumeChangeEvent(track, kind, volume) =>
        Log.send(s"Volume changed: $volume")
      case _ =>

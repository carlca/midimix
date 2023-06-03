package com.carlca
package midimix

import com.bitwig.extension.api.util.midi.ShortMidiMessage
import com.bitwig.extension.controller.ControllerExtension
import com.bitwig.extension.controller.api.*
import com.carlca.config.Config
import com.carlca.logger.Log
import com.carlca.utils.StringUtils

import java.io.IOException
import scala.collection.mutable
import scala.collection.mutable.HashMap
import scala.collection.mutable.Stack
import midimix.MidiMixExtensionDefinition

class MidiMixExtension(definition: MidiMixExtensionDefinition, host: ControllerHost)
    extends ControllerExtension(definition, host):
  // Class instances
  private var mTransport: Transport                          = null
  private var mTracks: mutable.HashMap[Int, Int]             = mutable.HashMap.empty
  private var mTypes: mutable.HashMap[Int, Int]              = mutable.HashMap.empty
  private var mPending: mutable.Stack[Int]                   = mutable.Stack.empty
  private var mTrackBank: TrackBank                          = null
  private var mMainTrackBank: TrackBank                      = null
  private var mEffectTrackBank: TrackBank                    = null
  private var mMasterTrack: Track                            = null
  private var mCursorTrack: CursorTrack                      = null
  private var mParentCounts: mutable.HashMap[Track, Integer] = mutable.HashMap.empty
  // Consts
  private val APP_NAME                     = "com.carlca.MidiMix"
  private val MAX_TRACKS: Int              = 0x10
  private val MAX_SENDS: Int               = 0x03
  private val MAX_SCENES: Int              = 0x10
  private val HAS_FLAT_TRACK_LIST: Boolean = true
  private val TRACK_1: Int                 = 0x10
  private val TRACK_2: Int                 = 0x14
  private val TRACK_3: Int                 = 0x18
  private val TRACK_4: Int                 = 0x1c
  private val TRACK_5: Int                 = 0x2e
  private val TRACK_6: Int                 = 0x32
  private val TRACK_7: Int                 = 0x36
  private val TRACK_8: Int                 = 0x3a
  private val MAST_MIDI: Int               = 0x3e
  private val SEND_A: Int                  = 0
  private val SEND_B: Int                  = 1
  private val SEND_C: Int                  = 2
  private val VOLUME: Int                  = 3
  private val MASTER: Int                  = 0xff
  private val TRACKS: Seq[Int]             = Seq(TRACK_1, TRACK_2, TRACK_3, TRACK_4, TRACK_5, TRACK_6, TRACK_7, TRACK_8)

  override def init(): Unit =
    val host = getHost
    initWork(host)

  private def initWork(host: ControllerHost): Unit =
    Config.init(APP_NAME)
    Log.cls()
    initTransport(host)
    initOnMidiCallback(host)
    initOnSysexCallback(host)
    initTrackMap()
    initTypeMap()
    initTrackBanks(host)
    initMasterTrack(host)
    initCursorTrack(host)
  end initWork

  override def exit(): Unit = Log.send("MidiMix Exited")

  override def flush(): Unit = ()

  private def initOnMidiCallback(host: ControllerHost): Unit =
    host.getMidiInPort(0).setMidiCallback((a, b, c) => onMidi0(ShortMidiMessage(a, b, c)))

  private def initOnSysexCallback(host: ControllerHost): Unit =
    host.getMidiInPort(0).setSysexCallback(onSysex0)

  private def initTransport(host: ControllerHost): Unit = mTransport = host.createTransport

  private def initTrackMap(): Unit =
    mTracks ++= (makeTrackHash(SEND_A) ++ makeTrackHash(SEND_B) ++ makeTrackHash(SEND_C) ++ makeTrackHash(VOLUME))
    mTracks.put(MAST_MIDI, MASTER)

  private def initTypeMap(): Unit =
    mTypes ++= (makeTypeHash(SEND_A) ++ makeTypeHash(SEND_B) ++ makeTypeHash(SEND_C) ++ makeTypeHash(VOLUME))
    mTypes.put(MAST_MIDI, VOLUME)

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
    bank.itemCount.markInterested()
    bank.channelCount.markInterested()
    for i <- 0 until bank.getCapacityOfBank do
      val track = bank.getItemAt(i)
      track.name.markInterested()
      track.isGroup.markInterested()
      track.canHoldNoteData.markInterested()
      track.canHoldAudioData.markInterested()
      track.trackType.markInterested()
      track.position.markInterested()
      track.exists.markInterested()
      val parent = track.createParentTrack(0, 0)
      parent.name.markInterested()
  end initInterest

  private def initCursorTrack(host: ControllerHost): Unit =
    mCursorTrack = host.createCursorTrack(1, 0)

  private def makeTrackHash(offset: Int) = TRACKS.map(i => (TRACKS(i) + offset, i))

  private def makeTypeHash(offset: Int) = TRACKS.map(i => (i + offset, offset))

  private def processControlChange(msg: ShortMidiMessage): Unit =
    val trackNum: Option[Int] = mTracks.get(msg.getData1)
    val typeNum: Option[Int]  = mTypes.get(msg.getData1)
    val valueNum: Int         = msg.getData2
    Log.send("Track: %d  Type: %d  Value: %d", trackNum, typeNum, valueNum)
    val volume = valueNum / 127.0
    if trackNum.contains(MASTER) then mMasterTrack.volume.set(volume)
    else
      val track = mTrackBank.getItemAt(trackNum.get)
      track.volume.set(volume)
  end processControlChange

  private def processNoteOff(msg: ShortMidiMessage): Unit =
    if !mPending.isEmpty then processPending(mPending.pop)

  private def processPending(pending: Int): Unit =
    //
    // TODO: Think about paging
    // TODO: Work out how to handle folders
    //
    Log.line()
    Log.send("mMainTrackBank.itemCount().getAsInt(): %d", mMainTrackBank.itemCount().getAsInt)
    Log.send("mMainTrackBank.getSizeOfBank: %d", mMainTrackBank.getSizeOfBank)
    Log.send("mMainTrackBank.getCapacityOfBank: %d", mMainTrackBank.getCapacityOfBank)
    Log.line()
    Log.send("mTrackBank.itemCount().getAsInt(): %d", mTrackBank.itemCount.getAsInt)
    Log.send("mTrackBank.getSizeOfBank: %d", mTrackBank.getSizeOfBank)
    Log.send("mTrackBank.getCapacityOfBank: %d", mTrackBank.getCapacityOfBank)
    Log.line()
    Log.send("mEffectTrackBank.itemCount().getAsInt(): %d", mEffectTrackBank.itemCount.getAsInt)
    Log.send("mEffectTrackBank.getSizeOfBank: %d", mEffectTrackBank.getSizeOfBank)
    Log.send("mEffectTrackBank.getCapacityOfBank: %d", mEffectTrackBank.getCapacityOfBank)
    Log.line()
    iterateTracks(mMainTrackBank)
    Log.line()
    iterateTracks(mTrackBank)
    Log.line()
    iterateTracks(mEffectTrackBank)
    Log.line()
    iterateTracks()
    Log.line()
  end processPending

  private def iterateTracks(): Unit = for i <- 0 until mTrackBank.getSizeOfBank do
    val track = mTrackBank.getItemAt(i)
    if track.isGroup.get then Log.send("Group: %s", track.name.get)
    else
      val trackType = track.trackType.get
      if trackType.nonEmpty then
        if "InstrumentAudioEffectMaster".contains(trackType) then
          Log.send("%s: %s  Position: %d", trackType, track.name.get, track.position.get)
  end iterateTracks

  private def iterateTracks(bank: TrackBank): Unit =
    val trackCount = Math.min(bank.itemCount.getAsInt, bank.getSizeOfBank)
    val bankClass  = StringUtils.unadornedClassName(bank)
    Log.send("%s.(Effective)itemCount() %d", bankClass, trackCount)
    for i <- 0 until trackCount do
      val track = bank.getItemAt(i)
      Log.send(
        "Track #: %d, Name: %s, Positions: %d, IsSubscribed: %b",
        i,
        track.name.get,
        track.position.get,
        track.name.isSubscribed
      )
  end iterateTracks

  private def processNoteOn(msg: ShortMidiMessage): Unit = mPending.push(msg.getData1)

  private def onMidi0(msg: ShortMidiMessage): Unit =
    if msg.isControlChange then processControlChange(msg)
    else if msg.isNoteOff then processNoteOff(msg)
    else if msg.isNoteOn then processNoteOn(msg)
  end onMidi0

  @FunctionalInterface
  private def onSysex0(data: String): Unit = ()

end MidiMixExtension

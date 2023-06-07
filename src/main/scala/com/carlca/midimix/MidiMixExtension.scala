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

import scala.collection.SortedMap

// private def processControlChange(msg: ShortMidiMessage): Unit =
//   val trackNum: Option[Int] = mTracks.get(msg.getData1)
//   val typeNum: Option[Int]  = mTypes.get(msg.getData1)
//   val valueNum: Int         = msg.getData2
//   Log.send(s"Track: ${trackNum.get}  Type: ${typeNum.get}  Value: $valueNum")
//   val volume = valueNum / 127.0
//   if trackNum.contains(MASTER) then mMasterTrack.volume.set(volume)
//   else
//     val track = mTrackBank.getItemAt(trackNum.get)
//     track.volume.set(volume)
// end processControlChange

object MidiProcessor: 
  def process(msg: ShortMidiMessage) =
    var trackNum: Option[Int] = Maps.getTrackNum(msg)
    var typeNum:  Option[Int] = Maps.getTypeNum(msg)
end MidiProcessor

object Maps:
  private val TRACK_1: Int                 = 0x10
  private val TRACK_2: Int                 = 0x14
  private val TRACK_3: Int                 = 0x18
  private val TRACK_4: Int                 = 0x1c
  private val TRACK_5: Int                 = 0x2e
  private val TRACK_6: Int                 = 0x32
  private val TRACK_7: Int                 = 0x36
  private val TRACK_8: Int                 = 0x3a
  private val TRACKS: Seq[Int]             = Seq(TRACK_1, TRACK_2, TRACK_3, TRACK_4, TRACK_5, TRACK_6, TRACK_7, TRACK_8)
  private val MAST_MIDI: Int               = 0x3e
  private val SEND_A: Int                  = 0
  private val SEND_B: Int                  = 1
  private val SEND_C: Int                  = 2
  private val VOLUME: Int                  = 3
  private val MASTER: Int                  = 0xff
 /**
  * mTracks[Int, Int]
  *
  * This key for this hash map is the MIDI number which uniquely
  * represents one of the 8 rotary MIDIMix knobs or the sliders
  * The value it returns is 0 to 7 which represents the track number for that knob
  * The final entry maps MIDI message 62 to value 255 for the Master track
  */
  private lazy val mTracks: Map[Int, Int] =
    def hash(typeOffset: Int): Seq[(Int, Int)] = TRACKS.zipWithIndex.map((track, i) => (track + typeOffset, i))
    Map.from(Seq(SEND_A, SEND_B, SEND_C, VOLUME).flatMap(hash) :+ (MAST_MIDI, MASTER))
 /**
  * mTypes: Map[Int, Int]
  *
  * This key for this map is the MIDI number which uniquely
  * represents one of the 8 rotary MIDIMix knobs or the sliders
  * The value it returns is 0 to 3 which represents the type number for that knob
  * The types are: Type 0 - Top row of Send knobs    - SEND_A
  *                Type 1 - Middle row of Send knobs - SEND_B
  *                Type 2 - Lower row of send knobs  - SEND_C
  *                Type 3 - Volume slider            - VOLUME
  */ 
  private lazy val mTypes: Map[Int, Int] =
    def hash(typeOffset: Int): Seq[(Int, Int)] = TRACKS.map(i => (i + typeOffset, typeOffset))
    Map.from(Seq(SEND_A, SEND_B, SEND_C, VOLUME).flatMap(hash) :+ (MAST_MIDI, MASTER))
  def tracksLog: String = s"mTracks: ${SortedMap.from(mTracks).toString}"
  def typesLog: String = s"mTypes: ${SortedMap.from(mTypes).toString}"
  def getTrackNum(msg: ShortMidiMessage): Option[Int] = mTracks.get(msg.getData1)
  def getTypeNum(msg: ShortMidiMessage): Option[Int] = mTypes.get(msg.getData1)
end Maps

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

  private def initWork(host: ControllerHost): Unit =
    Config.init(APP_NAME)
    Log.cls
    Log.send(Maps.tracksLog)
    Log.send(Maps.typesLog);
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
    val trackNum: Option[Int] = ??? // mTracks.get(msg.getData1)
    val typeNum: Option[Int]  = ??? // mTypes.get(msg.getData1)
    val valueNum: Int = msg.getData2
    Log.send(s"Track: $trackNum  Type: $typeNum  Value: $valueNum")
    val volume = valueNum / 127.0
    if trackNum.contains(???) then mMasterTrack.volume.set(volume)
    else
      val track = mTrackBank.getItemAt(trackNum.get)
      track.volume.set(volume)
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

  private def processNoteOn(msg: ShortMidiMessage): Unit = mPending.push(msg.getData1)

  private def onMidi0(msg: ShortMidiMessage): Unit =
    if msg.isControlChange then processControlChange(msg)
    else if msg.isNoteOff then processNoteOff
    else if msg.isNoteOn then processNoteOn(msg)
  end onMidi0

  @FunctionalInterface
  private def onSysex0(data: String): Unit = ()

end MidiMixExtension

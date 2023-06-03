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
    Log.send("initWork begun")
    initTransport(host)
    initOnMidiCallback(host)
    initOnSysexCallback(host)
    /* 
     * initTrackMap generates - mTracks: mutable.HashMap[Int, Int] 
     * This key for this hash map is the MIDI number which uniquely 
     * represents one of the 8 rotary MIDIMix knobs or the sliders
     * The value it returns is 0 to 7 which represents the track number for that knob
     * The final entry maps MIDI message 62 to value 255 for the Master track
     * 
     * mTrack should look like this...
     * {16=0, 17=0, 18=0, 19=0, 20=1, 21=1, 22=1, 23=1, 24=2, 25=2, 26=2, 27=2, 28=3, 29=3, 30=3, 31=3, 
     *  46=4, 47=4, 48=4, 49=4, 50=5, 51=5, 52=5, 53=5, 54=6, 55=6, 56=6, 57=6, 58=7, 59=7, 60=7, 61=7, 
     *  62=255}
     * 
     * We are getting this at the moment...
     * (16=16, 17=16, 18=16, 19=16, 20=20, 21=20, 22=20, 23=20, 24=24, 25=24, 26=24, 27=24, 28=28, 29=28, 30=28, 31=28, 
     *  46=46, 47=46, 48=46, 49=46, 50=50, 51=50, 52=50, 53=50, 54=54, 55=54, 56=54, 57=54, 58=58, 59=58, 60=58, 61=58, 
     *  62=255)
     * 
     *  Conclusion: initTrackMap fails. Unit tests to follow!
     */
    initTrackMap()
    var tracks = mTracks.toString()
    Log.send("mTracks: %s", tracks);
    /* 
     * initTypeMap generates - mTypes: mutable.HashMap[Int, Int] 
     * This key for this hash map is the MIDI number which uniquely 
     * represents one of the 8 rotary MIDIMix knobs or the sliders
     * The value it returns is 0 to 3 which represents the type number for that knob
     * The types are: Type 0 - Top row of Send knobs    - SEND_A
     *                Type 1 - Middle row of Send knobs - SEND_B
     *                Type 2 - Lower row of send knobs  - SEND_C
     *                Type 3 - Volume slider            - VOLUME
     * 
     * mTrack should look like this
     * {16=0, 17=1, 18=2, 19=3, 20=0, 21=1, 22=2, 23=3, 24=0, 25=1, 26=2, 27=3, 28=0, 29=1, 30=2, 31=3, 
     *  46=0, 47=1, 48=2, 49=3, 50=0, 51=1, 52=2, 53=3, 54=0, 55=1, 56=2, 57=3, 58=0, 59=1, 60=2, 61=3, 
     *  62=3}
     * 
     * We are getting this at the moment...
     * (16=0, 17=1, 18=2, 19=3, 20=0, 21=1, 22=2, 23=3, 24=0, 25=1, 26=2, 27=3, 28=0, 29=1, 30=2, 31=3, 
     *  46=0, 47=1, 48=2, 49=3, 50=0, 51=1, 52=2, 53=3, 54=0, 55=1, 56=2, 57=3, 58=0, 59=1, 60=2, 61=3, 
     *  62=3)
     * 
     * Conclusion: initTypeMap works as intended. Unit tests to follow!
     */
    initTypeMap()
    var types = mTypes.toString()
    Log.send("mTypes: %s", types);
    /* 
     * Between them the two HashMaps - mTracks and mTypes - 
     *  allow you to identify the track number and the track type 
     * for any control in the MIDIMix which can return a 0-127 value.
     */
    initTrackBanks(host)
    initMasterTrack(host)
    initCursorTrack(host)
    Log.send("initWork ended")
  end initWork

  private def initTrackMap(): Unit =
    mTracks ++= (makeTrackHash(SEND_A) ++ makeTrackHash(SEND_B) ++ makeTrackHash(SEND_C) ++ makeTrackHash(VOLUME))
    mTracks.put(MAST_MIDI, MASTER)
    
  private def initTypeMap(): Unit =
    mTypes ++= (makeTypeHash(SEND_A) ++ makeTypeHash(SEND_B) ++ makeTypeHash(SEND_C) ++ makeTypeHash(VOLUME))
    mTypes.put(MAST_MIDI, VOLUME)

  private def makeTrackHash(offset: Int) = TRACKS.map(i => (i + offset, i))

  private def makeTypeHash(offset: Int) = TRACKS.map(i => (i + offset, offset))
  
  override def exit(): Unit = Log.send("MidiMix Exited")

  override def flush(): Unit = ()

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
    if mPending.nonEmpty then processPending(mPending.pop)

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

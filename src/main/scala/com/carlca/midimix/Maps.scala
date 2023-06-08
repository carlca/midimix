package com.carlca.midimix

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import scala.collection.SortedMap

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
    def hash(kindOffset: Int): Seq[(Int, Int)] = TRACKS.zipWithIndex.map((track, i) => (track + kindOffset, i))
    Map.from(Seq(SEND_A, SEND_B, SEND_C, VOLUME).flatMap(hash) :+ (MAST_MIDI, MASTER))
 /**
  * mKinds: Map[Int, Int]
  *
  * This key for this map is the MIDI number which uniquely
  * represents one of the 8 rotary MIDIMix knobs or the sliders
  * The value it returns is 0 to 3 which represents the Kind number for that knob
  * The Kinds are: Kind 0 - Top row of Send knobs    - SEND_A
  *                Kind 1 - Middle row of Send knobs - SEND_B
  *                Kind 2 - Lower row of send knobs  - SEND_C
  *                Lind 3 - Volume slider            - VOLUME
  */ 
  private lazy val mKinds: Map[Int, Int] =
    def hash(kindOffset: Int): Seq[(Int, Int)] = TRACKS.map(i => (i + kindOffset, kindOffset))
    Map.from(Seq(SEND_A, SEND_B, SEND_C, VOLUME).flatMap(hash) :+ (MAST_MIDI, MASTER))
  def tracksLog: String = s"mTracks: ${SortedMap.from(mTracks).toString}"
  def kindsLog: String = s"mKinds: ${SortedMap.from(mKinds).toString}"
  def getTrack(msg: ShortMidiMessage): Option[Int] = mTracks.get(msg.getData1)
  def getKind(msg: ShortMidiMessage): Option[Int] = mKinds.get(msg.getData1)
end Maps

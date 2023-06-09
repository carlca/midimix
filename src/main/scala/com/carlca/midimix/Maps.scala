package com.carlca.midimix

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import scala.collection.SortedMap

enum ButtonType derives CanEqual:
  case Arm, Mute, Solo, BankLeft, BankRight

enum CCKind derives CanEqual:
  case Volume, SendA, SendB, SendC

object Maps:
  // General Constants
  private val MAST_MIDI: Int       = 0x3e
  private val MIDI_BANK_LEFT: Int  = 0x19
  private val MIDI_BANK_RIGHT: Int = 0x1A    
  private val MIDI_SOLO: Int       = 0x1B
  private val SEND_A: Int          = 0
  private val SEND_B: Int          = 1
  private val SEND_C: Int          = 2
  private val VOLUME: Int          = 3
  private val MASTER: Int          = 0xff
  // Tracks Constants    
  private val TRACK_1: Int         = 0x10
  private val TRACK_2: Int         = 0x14
  private val TRACK_3: Int         = 0x18
  private val TRACK_4: Int         = 0x1c
  private val TRACK_5: Int         = 0x2e
  private val TRACK_6: Int         = 0x32
  private val TRACK_7: Int         = 0x36
  private val TRACK_8: Int         = 0x3a
  private val TRACKS: Seq[Int]     = Seq(TRACK_1, TRACK_2, TRACK_3, TRACK_4, TRACK_5, TRACK_6, TRACK_7, TRACK_8)
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
  // Mutes Consts
  private val MUTE_1: Int      = 0x01
  private val MUTE_2: Int      = 0x04
  private val MUTE_3: Int      = 0x07
  private val MUTE_4: Int      = 0x0A
  private val MUTE_5: Int      = 0x0D
  private val MUTE_6: Int      = 0x10
  private val MUTE_7: Int      = 0x13
  private val MUTE_8: Int      = 0x16
  private val MUTES:  Seq[Int] = Seq(MUTE_1, MUTE_2, MUTE_3, MUTE_4, MUTE_5, MUTE_6, MUTE_7, MUTE_8)   
 /**
  * mMutes: Map[Int, Int]
  *
  * This key for this map is the MIDI number which uniquely
  * represents one of the 8 Mute buttons.
  * The value it returns is 0 to 7 which represents the Track number for that button.
  */ 
  private lazy val mMutes: Map[Int, Int] =
    Map.from(MUTES.zipWithIndex.map((mute, index) => mute -> (index)))
  // Arms Constants
  private val ARM_1: Int       = 0x03
  private val ARM_2: Int       = 0x06
  private val ARM_3: Int       = 0x09
  private val ARM_4: Int       = 0x0C
  private val ARM_5: Int       = 0x0F
  private val ARM_6: Int       = 0x12
  private val ARM_7: Int       = 0x15
  private val ARM_8: Int       = 0x18
  private val ARMS:  Seq[Int]  = Seq(ARM_1, ARM_2, ARM_3, ARM_4, ARM_5, ARM_6, ARM_7, ARM_8)            
 /**
  * mArms: Map[Int, Int]
  *
  * This key for this map is the MIDI number which uniquely
  * represents one of the 8 Arm buttons.
  * The value it returns is 0 to 7 which represents the Track number for that button.
  */ 
  private lazy val mArms: Map[Int, Int] =
    Map.from(ARMS.zipWithIndex.map((arm, index) => arm -> (index)))
  // static methods of the Maps object
  def getButtonType(msg: ShortMidiMessage): ButtonType =
    if Maps.getMute(msg) != None then ButtonType.Mute 
    else if Maps.getArm(msg) != None then ButtonType.Arm
    else msg.getData1 match
      case MIDI_SOLO       => ButtonType.Solo
      case MIDI_BANK_LEFT  => ButtonType.BankLeft
      case MIDI_BANK_RIGHT => ButtonType.BankRight
  def tracksLog: String = s"mTracks: ${SortedMap.from(mTracks).toString}"
  def kindsLog: String = s"mKinds: ${SortedMap.from(mKinds).toString}"
  def mutesLog: String = s"mMutes: ${SortedMap.from(mMutes).toString}"
  def armsLog: String = s"mArms: ${SortedMap.from(mArms).toString}"
  def getTrack(msg: ShortMidiMessage): Option[Int] = mTracks.get(msg.getData1)
  def getKind(msg: ShortMidiMessage): Option[Int] = mKinds.get(msg.getData1)
  def getMute(msg: ShortMidiMessage): Option[Int] = mMutes.get(msg.getData1)
  def getArm(msg: ShortMidiMessage): Option[Int] = mArms.get(msg.getData1)
end Maps

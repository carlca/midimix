package com.carlca.midimix

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import scala.collection.SortedMap

enum ButtonType derives CanEqual:
  case Arm, Mute, Solo, BankLeft, BankRight

enum CCKind derives CanEqual:
  case SendA, SendB, SendC, Volume, Master

object Maps:
  import MapsConstants.*
 /** mTracks[Int, Int] - MidiNumber => TrackNumber for tracks */
  private lazy val mTracks: Map[Int, Int] =
    def hash(kindOffset: Int): Seq[(Int, Int)] = TRACKS.zipWithIndex.map((track, i) => (track + kindOffset, i))
      Map.from(Seq(K_A, K_B, K_C, K_V).flatMap(hash) :+ (M_MAST, K_M))
 /** mKinds: Map[Int, Int] - MidiNumber => Kind for tracks */
  private lazy val mKinds: Map[Int, Int] =
    def hash(kindOffset: Int): Seq[(Int, Int)] = TRACKS.map(i => (i + kindOffset, kindOffset))
      Map.from(Seq(K_A, K_B, K_C, K_V).flatMap(hash) :+ (M_MAST, K_M))
 /** mMutes: Map[Int, Int] - MidiNumber => TrackNumber for mutes */ 
  private lazy val mMutes: Map[Int, Int] = Map.from(MUTES.zipWithIndex.map((mute, index) => mute -> (index)))
 /** mArms: Map[Int, Int] - MidiNumber => TrackNumber for arms */ 
  private lazy val mArms: Map[Int, Int] = Map.from(ARMS.zipWithIndex.map((arm, index) => arm -> (index)))
  // Helper for getCCKind
  private val mCCKinds: Map[Int, CCKind] = 
    Map(K_A -> CCKind.SendA, K_B -> CCKind.SendB, K_C -> CCKind.SendC, K_V -> CCKind.Volume, K_M -> CCKind.Master) 
  // static methods of the Maps object
  def getButtonType(msg: ShortMidiMessage): ButtonType =
    if Maps.getMute(msg)     != None then ButtonType.Mute 
    else if Maps.getArm(msg) != None then ButtonType.Arm
    else msg.getData1 match
        case M_SOLO  => ButtonType.Solo
        case M_LEFT  => ButtonType.BankLeft
        case M_RIGHT => ButtonType.BankRight
  def tracksLog: String = s"mTracks: ${SortedMap.from(mTracks).toString}"
  def kindsLog: String  = s"mKinds: ${SortedMap.from(mKinds).toString}"
  def mutesLog: String  = s"mMutes: ${SortedMap.from(mMutes).toString}"
  def armsLog: String   = s"mArms: ${SortedMap.from(mArms).toString}"
  def getCCKind(msg: ShortMidiMessage): Option[CCKind] = getKind(msg).flatMap(mCCKinds.get)
  def getTrack(msg: ShortMidiMessage): Option[Int] = mTracks.get(msg.getData1)
  def getKind(msg: ShortMidiMessage): Option[Int] = mKinds.get(msg.getData1)
  def getMute(msg: ShortMidiMessage): Option[Int] = mMutes.get(msg.getData1)
  def getArm(msg: ShortMidiMessage): Option[Int] = mArms.get(msg.getData1)
end Maps

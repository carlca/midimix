package com.carlca.midimix

import com.bitwig.extension.controller.api.*
import com.carlca.midimix.Settings
import com.carlca.midimix.Settings.TrackMode
import com.carlca.logger.Log

class TrackBankWrapper(val trackBank: TrackBank): 
 
  def getItemAt(index: Int): Option[Track] =
    if Settings.trackMode == TrackMode.`One to One` then
      Some(trackBank.getItemAt(index))
    else
      getTracks(Settings.trackMode)(index)
  end getItemAt

  def getTracks(trackMode: TrackMode): Map[Int, Option[Track]] =
    var tracksMap = Map[Int, Option[Track]]()
    var idx = 0
    for i <- 0 until trackBank.itemCount().get() do
      val track = trackBank.getItemAt(i)
      val shouldInclude = trackMode match
        case TrackMode.`Tagged "<>" Only` => track.name.get().contains("<>")
        case TrackMode.`Groups Only` => track.isGroup.get()
        case TrackMode.`No Groups` => !track.isGroup.get()
        case _ => false
      if shouldInclude && isActivated(i) then
        tracksMap = tracksMap + (idx -> Some(track))
        idx = idx + 1
    for i <- idx until trackBank.getCapacityOfBank do
      tracksMap = tracksMap + (i -> None)
    tracksMap
  end getTracks    


  def isActivated(trackIndex: Int): Boolean = 
    !Tracks.getBankAncestors(trackIndex).exists(t => t.exists.get() && !t.isActivated.get())

  def scrollPageBackwards(): Unit = trackBank.scrollPageBackwards()
  def scrollPageForwards(): Unit  = trackBank.scrollPageForwards()
end TrackBankWrapper

object Tracks:
 
 /** Class instances */ 
  private var mHost: ControllerHost                  = null
  private var mTransport: Transport                  = null
  private var mTrackBank: TrackBank                  = null
  private var mMainTrackBank: TrackBank              = null
  private var mEffectTrackBank: TrackBank            = null  
  private var mMasterTrack: Track                    = null
  private var mCursorTrack: CursorTrack              = null
  private var mWrapper: TrackBankWrapper             = null
  private var mBankAncestors: IndexedSeq[Seq[Track]] = null
  
 /** Property methods */
  def getTransport: Transport                  = mTransport
  def getTrackBank: TrackBank                  = mTrackBank  
  def getMainTrackBank: TrackBank              = mMainTrackBank
  def getEffectTrackBank: TrackBank            = mEffectTrackBank  
  def getMasterTrack: Track                    = mMasterTrack
  def getCursorTrack: CursorTrack              = mCursorTrack
  def getBankAncestors: IndexedSeq[Seq[Track]] = mBankAncestors

 /** Consts */
  private val MAX_TRACKS: Int       = 0x10
  private val MAX_SENDS:  Int       = 0x03
  private val MAX_SCENES: Int       = 0x10
  private val MAX_ANCESTORS: Int    = 0x10

 /** init method */
  def init(host: ControllerHost): Unit =
    mHost = host
    initTransport
    initTrackBanks
    initMasterTrack
    initCursorTrack
    mWrapper = new TrackBankWrapper(mMainTrackBank)
  end init

 /** Property methods */   
  def getIsMuted(t: Int): Boolean = 
    mWrapper.getItemAt(t).fold(false)(track => track.mute().get())
  def getIsSolo(t: Int): Boolean = 
    mWrapper.getItemAt(t).fold(false)(track => track.solo().get())
  def getIsArmed(t: Int): Boolean = 
    mWrapper.getItemAt(t).fold(false)(track => track.arm().get())
  def getIsDisabled(t: Int): Boolean = 
    mWrapper.getItemAt(t).fold(false)(track => track.exists().get())

 /** Set volume methods */ 
  def setVolume(t: Int, v: Int): Unit = 
    mWrapper.getItemAt(t).foreach(track => track.volume().set(v / 127.0))

  def setMasterVolume(v: Int): Unit = mMasterTrack.volume().set(v / 127.0)

 /** Set send methods */  
  def setSendA(t: Int, s: Int, v: Int): Unit = mWrapper.getItemAt(t).get.sendBank().getItemAt(s).set(v / 127.0)
  def setSendB(t: Int, s: Int, v: Int): Unit = mWrapper.getItemAt(t).get.sendBank().getItemAt(s).set(v / 127.0)
  def setSendC(t: Int, s: Int, v: Int): Unit = 
    Settings.panSendMode match
      case Settings.PanSendMode.`FX Send` => mWrapper.getItemAt(t).get.sendBank().getItemAt(s).set(v / 127.0)
      case Settings.PanSendMode.`Pan`     => mWrapper.getItemAt(t).get.pan().set(v / 127.0)

 /** Toggle methods */   
  def toggleMute(t: Int): Unit = mWrapper.getItemAt(t).get.mute().toggle()
  def toggleArm(t: Int): Unit = mWrapper.getItemAt(t).get.arm().toggle()
  def toggleSolo(t: Int): Unit = 
    if Settings.exclusiveSolo then
      (0 to 8).foreach(i => mWrapper.getItemAt(i).get.solo().set(false))
    mWrapper.getItemAt(t).get.solo().toggle();

 /** Set bank methods */ 
  def setBankLeft(): Unit = mWrapper.scrollPageForwards()
  def setBankRight(): Unit = mWrapper.scrollPageBackwards()

 /** Init methods called from MidiMixExtension.init - code must be run from init 
  * initTransport */  
  def initTransport: Unit = mTransport = mHost.createTransport

  import TrackMode.*

 /** initTrackBanks */ 
  private def initTrackBanks: Unit =
    mTrackBank = mHost.createTrackBank(MAX_TRACKS, MAX_SENDS, MAX_SCENES)
    mMainTrackBank = mHost.createMainTrackBank(MAX_TRACKS, MAX_SENDS, MAX_SCENES)
    mEffectTrackBank = mHost.createEffectTrackBank(MAX_SENDS, MAX_SENDS, MAX_SCENES)
    initInterest(mTrackBank)
    initInterest(mMainTrackBank)
    initInterest(mEffectTrackBank)
    initAncestors
    initAncestorInterest
  end initTrackBanks

 /** initMasterTrack */
  private def initMasterTrack: Unit =
    mMasterTrack = mHost.createMasterTrack(0)
    mMasterTrack.name.markInterested
  end initMasterTrack

 /** initInterest */   
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
      track.mute.markInterested
      track.solo.markInterested
      track.arm.markInterested
      track.isActivated.markInterested
      val parent = track.createParentTrack(0, 0)
      parent.name.markInterested
  end initInterest

 /** ancestors */ 
  def ancestors(track: Track): Seq[Track] =
    var res = List(track)
    for i <- 0 until MAX_ANCESTORS do
      res = res :+ res.last.createParentTrack(0, 0)
    res.toSeq

 /** initAncestors */ 
  private def initAncestors: Unit =
    mBankAncestors = (0 until MAX_TRACKS).map(mTrackBank.getItemAt).map(ancestors)

 /** initAncestorInterest */ 
  private def initAncestorInterest: Unit =
    for ba <- mBankAncestors; t <- ba do
      t.exists.markInterested()
      t.isActivated.markInterested()

 /** initCursorTrack */ 
  private def initCursorTrack: Unit =
    mCursorTrack = mHost.createCursorTrack(1, 0)

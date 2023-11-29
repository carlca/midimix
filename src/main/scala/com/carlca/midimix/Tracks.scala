package com.carlca.midimix

import scala.util.control.Breaks._
import com.bitwig.extension.controller.api.*
import com.carlca.midimix.Settings
import com.carlca.midimix.Settings.TrackMode
import com.carlca.logger.Log
import com.carlca.logger.LogTest

class TrackBankWrapper(val trackBank: TrackBank): 
 
  def getItemAt(index: Int): Option[Track] =
    val isGroup = true
    Settings.trackMode match
      case TrackMode.`One to One`  => Some(trackBank.getItemAt(index))
      case TrackMode.`No Groups`   => getTracks(!isGroup)(index)
      case TrackMode.`Groups Only` => getTracks(isGroup)(index)

  def getTracks(condition: Boolean): Map[Int, Option[Track]] =
    var tracksMap = Map[Int, Option[Track]]()
    var idx = 0
    for i <- 0 until trackBank.getCapacityOfBank do
      val track = trackBank.getItemAt(i)
      if (track.isGroup.get == condition) && track.isActivated.get then // isActivated(track) then
        tracksMap = tracksMap + (idx -> Some(track))
        idx = idx + 1
    for i <- idx until trackBank.getCapacityOfBank do
      tracksMap = tracksMap + (i -> None)
    Log2.line.send("Hello from getTracks").blank.line  
    tracksMap
  end getTracks

  def isActivated(track: Track): Boolean =
    if !track.isActivated.get then
      return false
    var checkTrack = track.createParentTrack(0, 0)
    while checkTrack.isGroup.get do
      if !checkTrack.isActivated.get then
        return false
      checkTrack = checkTrack.createParentTrack(0, 0)
    true
  end isActivated

  def scrollPageBackwards(): Unit = trackBank.scrollPageBackwards()
  def scrollPageForwards(): Unit  = trackBank.scrollPageForwards()
end TrackBankWrapper

object Tracks:
 
 /** Class instances */ 
  private var mHost: ControllerHost       = null
  private var mTransport: Transport       = null
  private var mTrackBank: TrackBank       = null
  private var mMainTrackBank: TrackBank   = null
  private var mEffectTrackBank: TrackBank = null
  private var mMasterTrack: Track         = null
  private var mCursorTrack: CursorTrack   = null
  private var mWrapper: TrackBankWrapper  = null
  
 /** Property methods */
  def getTransport: Transport       = mTransport
  def getTrackBank: TrackBank       = mTrackBank  
  def getMainTrackBank: TrackBank   = mMainTrackBank
  def getEffectTrackBank: TrackBank = mEffectTrackBank
  def getMasterTrack: Track         = mMasterTrack
  def getCursorTrack: CursorTrack   = mCursorTrack

 /** Consts */
  private val MAX_TRACKS: Int       = 0x10
  private val MAX_SENDS:  Int       = 0x03
  private val MAX_SCENES: Int       = 0x10

 /** init method */
  def init(host: ControllerHost): Unit =
    Log.line
    Log.send("Tracks.init")
    Log.time
    Log.line
    mHost = host
    initTransport
    initTrackBanks
    initMasterTrack
    initCursorTrack
    mWrapper = new TrackBankWrapper(mTrackBank)

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
  end initTrackBanks

 /** initMasterTrack */
  private def initMasterTrack: Unit =
    mMasterTrack = mHost.createMasterTrack(0)
    mMasterTrack.name.markInterested
    
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

 /** initCursorTrack */ 
  private def initCursorTrack: Unit =
    mCursorTrack = mHost.createCursorTrack(1, 0)

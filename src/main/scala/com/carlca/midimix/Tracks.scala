package com.carlca.midimix

import com.bitwig.extension.controller.api.*
import com.carlca.midimix.Settings

object Tracks:
 
 /** Class instances */ 
  private var mHost: ControllerHost       = null
  private var mTransport: Transport       = null
  private var mTrackBank: TrackBank       = null
  private var mMainTrackBank: TrackBank   = null
  private var mEffectTrackBank: TrackBank = null
  private var mMasterTrack: Track         = null
  private var mCursorTrack: CursorTrack   = null

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
  def init(host: ControllerHost) = 
    mHost = host
    initTransport
    initTrackBanks
    initMasterTrack
    initCursorTrack

 /** Property methods */   
  def getIsMuted(t: Int): Boolean = mTrackBank.getItemAt(t).mute().get()
  def getIsSolo(t: Int): Boolean = mTrackBank.getItemAt(t).solo().get()
  def getIsArmed(t: Int): Boolean = mTrackBank.getItemAt(t).arm().get()

 /** Set volume methods */ 
  def setVolume(t: Int, v: Int): Unit = mTrackBank.getItemAt(t).volume().set(v / 127.0)
  def setMasterVolume(v: Int): Unit = mMasterTrack.volume().set(v / 127.0)

 /** Set send methods */  
  def setSendA(t: Int, s: Int, v: Int): Unit = mTrackBank.getItemAt(t).sendBank().getItemAt(s).set(v / 127.0)
  def setSendB(t: Int, s: Int, v: Int): Unit = mTrackBank.getItemAt(t).sendBank().getItemAt(s).set(v / 127.0)
  def setSendC(t: Int, s: Int, v: Int): Unit = 
    Settings.panSendMode match
      case Settings.PanSendMode.`FX Send` => mTrackBank.getItemAt(t).sendBank().getItemAt(s).set(v / 127.0)
      case Settings.PanSendMode.`Pan`     => mTrackBank.getItemAt(t).pan().set(v / 127.0)

 /** Toggle methods */   
  def toggleMute(t: Int): Unit = mTrackBank.getItemAt(t).mute().toggle()
  def toggleArm(t: Int): Unit = mTrackBank.getItemAt(t).arm().toggle()
  def toggleSolo(t: Int): Unit = 
    if Settings.exclusiveSolo then
      (0 to 8).foreach(i => mTrackBank.getItemAt(i).solo().set(false))
    mTrackBank.getItemAt(t).solo().toggle();

 /** Set bank methods */ 
  def setBankLeft: Unit = ()
  def setBankRight: Unit = ()

 /** Init methods called from MidiMixExtension.init - code must be run from init 
  * initTransport */  
  private def initTransport: Unit = mTransport = mHost.createTransport

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
      val parent = track.createParentTrack(0, 0)
      parent.name.markInterested
  end initInterest

 /** initCursorTrack */ 
  private def initCursorTrack: Unit =
    mCursorTrack = mHost.createCursorTrack(1, 0)

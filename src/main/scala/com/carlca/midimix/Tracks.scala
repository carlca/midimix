package com.carlca.midimix

import com.bitwig.extension.controller.api.*

object Tracks:
  // Class instances  
  private var mTransport: Transport       = null
  private var mTrackBank: TrackBank       = null
  private var mMainTrackBank: TrackBank   = null
  private var mEffectTrackBank: TrackBank = null
  private var mMasterTrack: Track         = null
  private var mCursorTrack: CursorTrack   = null
  // property methods 
  def getTransport: Transport       = mTransport
  def getTrackBank: TrackBank       = mTrackBank  
  def getMainTrackBank: TrackBank   = mMainTrackBank
  def getEffectTrackBank: TrackBank = mEffectTrackBank
  def getMasterTrack: Track         = mMasterTrack
  def getCursorTrack: CursorTrack   = mCursorTrack
  // Consts
  private val MAX_TRACKS: Int             = 0x10
  private val MAX_SENDS:  Int             = 0x03
  private val MAX_SCENES: Int             = 0x10

  // Public methods
  def setVolume(track: Int, volume: Int): Unit = mTrackBank.getItemAt(track).volume().set(volume / 127.0)
  def setMasterVolume(volume: Int): Unit = mMasterTrack.volume().set(volume / 127.0)

  // Main init method
  def init(host: ControllerHost): Unit =
    initTransport(host)
    initTrackBanks(host)
    initMasterTrack(host)
    initCursorTrack(host)
  end init

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





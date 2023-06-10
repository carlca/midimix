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



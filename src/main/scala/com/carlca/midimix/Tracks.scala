package com.carlca.midimix

import com.bitwig.extension.controller.api.*
import com.carlca.midimix.Settings
import com.carlca.midimix.Settings.TrackMode
import com.carlca.logger.Log

class TrackBankWrapper(val trackBank: TrackBank): 
 /** init method */
	def getItemAt(index: Int): Track =
		Settings.trackMode match
			case TrackMode.`One to One`  => trackBank.getItemAt(index)
			case TrackMode.`No Groups`   => getUnGroupedTracks()(index)

	def getUnGroupedTracks(): Map[Int, Track] =
		Log.send("No Groups selected")
		var tracksMap = Map[Int, Track]()
		var idx = 0
		for i <- 0 until trackBank.getCapacityOfBank do
			val track = trackBank.getItemAt(i)
			if !track.isGroup.get then
				tracksMap = tracksMap + (idx -> track)
				idx = idx + 1
			end if
		end for
		tracksMap
	end getUnGroupedTracks

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
		mHost = host
		initTransport
		initTrackBanks
		initMasterTrack
		initCursorTrack
		mWrapper = new TrackBankWrapper(mTrackBank)

 /** Property methods */   
	def getIsMuted(t: Int): Boolean = mWrapper.getItemAt(t).mute().get()
	def getIsSolo(t: Int): Boolean = mWrapper.getItemAt(t).solo().get()
	def getIsArmed(t: Int): Boolean = mWrapper.getItemAt(t).arm().get()

 /** Set volume methods */ 
	def setVolume(t: Int, v: Int): Unit = mWrapper.getItemAt(t).volume().set(v / 127.0)
	def setMasterVolume(v: Int): Unit = mMasterTrack.volume().set(v / 127.0)

 /** Set send methods */  
	def setSendA(t: Int, s: Int, v: Int): Unit = mWrapper.getItemAt(t).sendBank().getItemAt(s).set(v / 127.0)
	def setSendB(t: Int, s: Int, v: Int): Unit = mWrapper.getItemAt(t).sendBank().getItemAt(s).set(v / 127.0)
	def setSendC(t: Int, s: Int, v: Int): Unit = 
		Settings.panSendMode match
			case Settings.PanSendMode.`FX Send` => mWrapper.getItemAt(t).sendBank().getItemAt(s).set(v / 127.0)
			case Settings.PanSendMode.`Pan`     => mWrapper.getItemAt(t).pan().set(v / 127.0)

 /** Toggle methods */   
	def toggleMute(t: Int): Unit = mWrapper.getItemAt(t).mute().toggle()
	def toggleArm(t: Int): Unit = mWrapper.getItemAt(t).arm().toggle()
	def toggleSolo(t: Int): Unit = 
		if Settings.exclusiveSolo then
			(0 to 8).foreach(i => mWrapper.getItemAt(i).solo().set(false))
		mWrapper.getItemAt(t).solo().toggle();

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

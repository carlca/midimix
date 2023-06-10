package com.carlca
package midimix

import com.bitwig.extension.api.util.midi.ShortMidiMessage
import com.bitwig.extension.controller.ControllerExtension
import com.bitwig.extension.controller.api.*
import com.carlca.config.Config
import com.carlca.logger.Log

class MidiMixExtension(definition: MidiMixExtensionDefinition, host: ControllerHost)
    extends ControllerExtension(definition, host):
  // Class instances
  private var mTransport: Transport       = null
  private var mTrackBank: TrackBank       = null
  private var mMainTrackBank: TrackBank   = null
  private var mEffectTrackBank: TrackBank = null
  private var mMasterTrack: Track         = null
  private var mCursorTrack: CursorTrack   = null
  // Consts
  private val APP_NAME                    = "com.carlca.MidiMix"
  private val MAX_TRACKS: Int             = 0x10
  private val MAX_SENDS:  Int             = 0x03
  private val MAX_SCENES: Int             = 0x10
  
  override def init: Unit =
    val host = getHost
    initWork(host)

  private def initWork(host: ControllerHost): Unit =
    Config.init(APP_NAME)
    Log.cls
    Log.send(Maps.tracksLog)
    Log.send(Maps.kindsLog)
    Log.send(Maps.mutesLog)
    Log.send(Maps.armsLog)
    Log.line
    initTransport(host)
    initOnMidiCallback(host)
    initOnSysexCallback(host)
    initTrackBanks(host)
    initMasterTrack(host)
    initCursorTrack(host)
  end initWork
  override def exit: Unit = Log.send("MidiMix Exited")

  override def flush: Unit = ()

  private def initOnMidiCallback(host: ControllerHost): Unit =
    host.getMidiInPort(0).setMidiCallback((a, b, c) => onMidi0(ShortMidiMessage(a, b, c)))

  private def initOnSysexCallback(host: ControllerHost): Unit =
    host.getMidiInPort(0).setSysexCallback(onSysex0(_))

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

  private def onMidi0(msg: ShortMidiMessage): Unit =
    MidiProcessor.process(msg)
  end onMidi0

  @FunctionalInterface
  private def onSysex0(data: String): Unit = ()

end MidiMixExtension

object Handler:
  def volumeChanged(e: MidiEvent): Unit = Log.send(s"Volume changed - track: ${e.track.get}  volume: ${e.volume}")
  def sendAChanged(e: MidiEvent): Unit = Log.send(s"Send A changed - track: ${e.track.get}  volume: ${e.volume}")
  def sendBChanged(e: MidiEvent): Unit = Log.send(s"Send B changed - track: ${e.track.get}  volume: ${e.volume}")
  def sendCChanged(e: MidiEvent): Unit = Log.send(s"Send C changed - track: ${e.track.get}  volume: ${e.volume}")
  def masterChanged(e: MidiEvent): Unit = Log.send(s"Master changed - volume: ${e.volume}")
  def mutePressed(e: MidiEvent): Unit = Log.send(s"Mute pressed - track: ${e.track}")
  def armPressed(e: MidiEvent): Unit = Log.send(s"Rec Arm pressed - track: ${e.track}")
  def soloPressed(e: MidiEvent): Unit = Log.send("Solo All pressed")
  def bankLeftPressed(e: MidiEvent): Unit = Log.send("Bank Left pressed")
  def bankRightPressed(e: MidiEvent): Unit = Log.send("Bank Right pressed")
end Handler
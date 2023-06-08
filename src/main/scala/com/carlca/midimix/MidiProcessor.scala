package com.carlca.midimix

import com.bitwig.extension.api.util.midi.ShortMidiMessage

trait MidiEventHandler: 
  def handleEvent(event: VolumeChangeEvent): Unit = None

case class VolumeChangeEvent(track: Option[Int], kind: Option[Int], volume: Int) extends MidiEventHandler

object MidiProcessor: 
  private var volumeChangeObserver: Option[MidiEventHandler] = None

  def onVolumeChanged(observer: MidiEventHandler): Unit =
    volumeChangeObserver = Some(observer)

  def process(msg: ShortMidiMessage): Unit =
    if msg.isControlChange then
      volumeChangeObserver.foreach(_.handleEvent(VolumeChangeEvent(Maps.getTrack(msg), Maps.getKind(msg), msg.getData2())))
//    else if msg.isNoteOff then


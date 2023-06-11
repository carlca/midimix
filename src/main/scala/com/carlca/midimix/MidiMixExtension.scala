package com.carlca
package midimix

import com.bitwig.extension.api.util.midi.ShortMidiMessage
import com.bitwig.extension.controller.ControllerExtension
import com.bitwig.extension.controller.api.*
import com.carlca.config.Config
import com.carlca.logger.Log

class MidiMixExtension(definition: MidiMixExtensionDefinition, host: ControllerHost)
    extends ControllerExtension(definition, host):
  private val APP_NAME                    = "com.carlca.MidiMix"

  override def init: Unit =
    val host = getHost
    Config.init(APP_NAME)
    Log.cls
    Log.send(Maps.tracksLog)
    Log.send(Maps.kindsLog)
    Log.send(Maps.mutesLog)
    Log.send(Maps.armsLog)
    Log.line
    Tracks.init(host)
    initEvents(host)
  override def exit: Unit = Log.send("MidiMix Exited")

  override def flush: Unit = ()

  private def initEvents(host: ControllerHost): Unit =
    initOnMidiCallback(host)
    initOnSysexCallback(host)
  end initEvents  

  private def initOnMidiCallback(host: ControllerHost): Unit =
    host.getMidiInPort(0).setMidiCallback((a, b, c) => onMidi0(ShortMidiMessage(a, b, c)))

  private def initOnSysexCallback(host: ControllerHost): Unit =
    host.getMidiInPort(0).setSysexCallback(onSysex0(_))

  private def onMidi0(msg: ShortMidiMessage): Unit =
    MidiProcessor.process(msg)
  end onMidi0

  @FunctionalInterface
  private def onSysex0(data: String): Unit = ()

end MidiMixExtension
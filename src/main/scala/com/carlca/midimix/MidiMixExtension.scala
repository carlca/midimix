package com.carlca
package midimix

import com.bitwig.extension.api.util.midi.ShortMidiMessage
import com.bitwig.extension.controller.ControllerExtension
import com.bitwig.extension.controller.api.*
import com.carlca.config.Config
import com.carlca.logger.Log

class MidiMixExtension(definition: MidiMixExtensionDefinition, host: ControllerHost)
    extends ControllerExtension(definition, host):
  private val APP_NAME = "com.carlca.MidiMix"
  // private val STATUS_RANGE = ShortMidiMessage.CONTROL_CHANGE to (ShortMidiMessage.CONTROL_CHANGE + 15)
  // private val CC_RANGE = 16 to 255

  override def init: Unit =
    val host = getHost
    MidiMixLights.init(host)
    Config.init(APP_NAME)
    MidiMixSettings.init(host)
    Tracks.init(host)
    initEvents(host)
  override def exit: Unit = None

  override def flush: Unit =
    MidiMixLights.flushLights

  private def initEvents(host: ControllerHost): Unit =
    initOnMidiCallback(host)
    initOnSysexCallback(host)
  end initEvents

  private def initOnMidiCallback(host: ControllerHost): Unit =
    host.getMidiInPort(0).setMidiCallback((a, b, c) => onMidi0(ShortMidiMessage(a, b, c)))

  private def initOnSysexCallback(host: ControllerHost): Unit =
    host.getMidiInPort(0).setSysexCallback(onSysex0(_))

  private def unpackMsg(msg: ShortMidiMessage, doLog: Boolean = false): (Int, Int, Int, Int) =
    val status = msg.getStatusByte()
    val channel = status & 0x0F
    val cc = msg.getData1()
    val data2 = msg.getData2()
    if doLog then Log.send(s"unpackMsg: status=$status, channel=$channel, cc=$cc, data2=$data2")
    (status, channel, cc, data2)

  private def onMidi0(msg: ShortMidiMessage): Unit =
    val (status, channel, cc, data2) = unpackMsg(msg, true)
    // Check status in range 176 to 191 (0xB0 to 0xBF) and CC in range 16 to 255
    // if (STATUS_RANGE contains status) && (CC_RANGE contains cc) then
    MidiProcessor.process(msg)

  @FunctionalInterface
  private def onSysex0(data: String): Unit = ()

end MidiMixExtension

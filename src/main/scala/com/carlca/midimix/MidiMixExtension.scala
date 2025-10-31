package com.carlca
package midimix

import com.bitwig.extension.api.util.midi.ShortMidiMessage
import com.bitwig.extension.controller.ControllerExtension
import com.bitwig.extension.controller.api.*
import com.carlca.logger.Log
import com.carlca.bitwigutils.ExtensionSettings
import com.carlca.bitwigutils.ExtensionSettings.SettingsCapability
import com.carlca.bitwigutils.Tracks

class MidiMixExtension(definition: MidiMixExtensionDefinition, host: ControllerHost)
    extends ControllerExtension(definition, host):

  override def init: Unit =
    val host = getHost
    MidiMixLights.init(host)
    ExtensionSettings.settingsCapabilities += SettingsCapability.`Solo Behaviour`
    ExtensionSettings.settingsCapabilities += SettingsCapability.`Third Row Behaviour`
    ExtensionSettings.settingsCapabilities += SettingsCapability.`Track Mapping Behaviour`
    ExtensionSettings.settingsCapabilities += SettingsCapability.`Fader dB Range`
    ExtensionSettings.settingsCapabilities += SettingsCapability.`Master dB Range`
    ExtensionSettings.init(host)
    Tracks.init(host)
    initEvents(host)

  override def exit: Unit = None

  override def flush: Unit =
    MidiMixLights.flushLights

  private def initEvents(host: ControllerHost): Unit =
    initOnMidiCallback(host)
    initOnSysexCallback(host)

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
    val (status, channel, cc, data2) = unpackMsg(msg, false)
    MidiProcessor.process(msg)

  @FunctionalInterface
  private def onSysex0(data: String): Unit = ()

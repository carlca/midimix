package com.carlca
package midimix

import com.bitwig.extension.api.PlatformType
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList
import com.bitwig.extension.controller.ControllerExtensionDefinition
import com.bitwig.extension.controller.api.ControllerHost

import java.util.UUID

class MidiMixExtensionDefinition extends ControllerExtensionDefinition:
  private val DRIVER_ID              = UUID.fromString("7e207ad6-30f9-4020-881e-d5f0411afa3a")
  override def getName               = "MidiMix"
  override def getAuthor             = "carlcaulkett"
  override def getVersion            = "0.1"
  override def getId: UUID           = DRIVER_ID
  override def getHardwareVendor     = "Akai"
  override def getHardwareModel      = "MidiMix"
  override def getRequiredAPIVersion = 18
  override def getNumMidiInPorts     = 1
  override def getNumMidiOutPorts    = 1
  override def listAutoDetectionMidiPortNames(list: AutoDetectionMidiPortNamesList, platformType: PlatformType): Unit =
    ()
  override def createInstance(host: ControllerHost) = new MidiMixExtension(this, host)
end MidiMixExtensionDefinition

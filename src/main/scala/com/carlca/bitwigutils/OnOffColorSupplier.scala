package com.carlca.bitwigutils

import com.bitwig.extension.api.Color

import java.util.function.Supplier

class OnOffColorSupplier(onColorInit: Color, offColorInit: Color) extends Supplier[Color]:
  def this(onColor: Color) = this(onColor, Color.blackColor())
  def this() = this(Color.blackColor(), Color.blackColor())

  private var onColor: Color = onColorInit
  private var offColor: Color = offColorInit
  private var isOn: Boolean = false

  def setOnColor(onColor: Color): Unit =
    this.onColor = onColor

  def setOffColor(offColor: Color): Unit =
    this.offColor = offColor

  def setOn(on: Boolean): Unit =
    this.isOn = on

  override def get(): Color =
    if isOn then onColor else offColor

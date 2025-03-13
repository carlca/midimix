package com.carlca.utils

object MathUtil:
  /**
   * Clamps the value to be between min and max.
   */
  def clamp(value: Float, min: Float, max: Float): Float =
    math.max(min, math.min(max, value))
  def clamp(value: Int, min: Int, max: Int): Int =
    math.max(min, math.min(max, value))
  def clamp(value: Double, min: Double, max: Double): Double =
    math.max(min, math.min(max, value))

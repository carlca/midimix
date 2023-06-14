package com.carlca.midimix

object MapsConstants:
  // MIDI Constants
  val M_MAST: Int = 0x3e; val M_LEFT: Int = 0x19; val M_RIGHT: Int = 0x1A; val M_SOLO: Int = 0x1B
  // Kind Constants - K_A = Send A, K_B = Send B, K_C = Send C, K_V = Volume, K_M = Master
  val K_A: Int = 0; val K_B: Int = 1; val K_C: Int = 2; val K_V: Int = 3; val K_M: Int = 0xff
  // Tracks Constants 
  val T1: Int = 0x10; val T2: Int = 0x14; val T3: Int = 0x18; val T4: Int = 0x1c
  val T5: Int = 0x2e; val T6: Int = 0x32; val T7: Int = 0x36; val T8: Int = 0x3a
  val TRACKS: Seq[Int] = Seq(T1, T2, T3, T4, T5, T6, T7, T8)
  // Mutes Constants
  val M1: Int = 0x01; val M2: Int = 0x04; val M3: Int = 0x07; val M4: Int = 0x0A
  val M5: Int = 0x0D; val M6: Int = 0x10; val M7: Int = 0x13; val M8: Int = 0x16
  val MUTES:  Seq[Int] = Seq(M1, M2, M3, M4, M5, M6, M7, M8)   
  // Arms Constants
  val A1: Int = 0x03; val A2: Int = 0x06; val A3: Int = 0x09; val A4: Int = 0x0C
  val A5: Int = 0x0F; val A6: Int = 0x12; val A7: Int = 0x15; val A8: Int = 0x18
  val ARMS:  Seq[Int]  = Seq(A1, A2, A3, A4, A5, A6, A7, A8)
end MapsConstants
  


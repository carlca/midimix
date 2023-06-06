package com.carlca
package logger

import java.io.*
import java.net.*
import config.Config

object Log:
  private var writer: BufferedWriter = _
  private[logger] var socket: Socket = _
  cls

  def cls: Unit =
    initSockets
    sendMessage("\u001B[H\u001B[2J")
  end cls

  def blank: Unit =
    initSockets
  end blank

  def line: Unit =
    initSockets
    val line = String.valueOf('-').repeat(80)
    sendMessage(line)
  end line

  def send(msg: String, args: Any*): Unit =
    initSockets
    sendMessage(String.format(msg, args))
  end send

  private def sendMessage(msg: String): Unit =
    if writer != null then
      writer.write(msg + System.lineSeparator)
      writer.flush
  end sendMessage

  private def initSockets: Unit =
    val port = Config.getLogPort
    if port > 0 then
      socket = new Socket("localhost", port)
      val outputStream = socket.getOutputStream
      writer = new BufferedWriter(new OutputStreamWriter(outputStream))
  end initSockets

end Log

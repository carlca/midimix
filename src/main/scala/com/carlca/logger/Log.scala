package com.carlca
package logger

import java.io.*
import java.net.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import config.Config
import config.OS

object Log:
  private var writer: Option[BufferedWriter] = _
  private[logger] var socket: Option[Socket] = _
  cls

  def cls: this.type =
    if initSockets then
      sendMessage("\u001B[H\u001B[2J").closeSockets
    this  
  end cls

  def blank: this.type =
    if initSockets then
      sendMessage("").closeSockets
    this
  end blank

  def line: this.type =
    if initSockets then
      sendMessage(String.valueOf('-').repeat(80)).closeSockets
    this        
  end line

  def time: this.type =
    if initSockets then
      val time = LocalTime.now.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
      sendMessage(time).closeSockets
    this        
  end time

  def send(msg: String, args: Any*): this.type =
    if initSockets then
      sendMessage(String.format(msg, args)).closeSockets
    this        
  end send

  private def sendMessage(msg: String): this.type =
    if writer != null then
      writer.get.write(msg + System.lineSeparator)
      writer.get.flush()
    this  
  end sendMessage

  private def initSockets: Boolean =
    if Config.getOs == OS.WINDOWS then return false
    val port = Config.getLogPort
    if port > 0 then                   
      socket = Some(new Socket("localhost", port))
      val outputStream = socket.get.getOutputStream
      writer = Some(new BufferedWriter(new OutputStreamWriter(outputStream)))
    port > 0
  end initSockets

  private def closeSockets: Unit =
    writer = writer.map(_.close()).map(_ => null).getOrElse(null)
    socket = socket.map(_.close()).map(_ => null).getOrElse(null)
  end closeSockets

end Log

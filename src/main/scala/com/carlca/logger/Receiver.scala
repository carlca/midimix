package com.carlca
package logger

import java.io.*
import java.net.ServerSocket

import scala.util.{Using, Try}

import com.carlca.config.Config
import com.carlca.utils.ConsoleUtils

object Receiver:

	def main(args: Array[String]): Unit = try
		val port         = findFreePort.get
		val serverSocket = new ServerSocket(port)
		outputMessage("Receiver listening on port " + port)
		outputMessage("")
		Config.init("com.carlca.MidiMix")
		Config.setLogPort(port)
		// handle ctrl/c
		Runtime.getRuntime.addShutdownHook(
			new Thread(() =>
				try
					Config.setLogPort(0)
					serverSocket.close
				catch
					case e: IOException =>
						e.printStackTrace)
		)
		while !Thread.interrupted do
			val socket      = serverSocket.accept
			val inputStream = socket.getInputStream
			val reader      = new BufferedReader(new InputStreamReader(inputStream))
			val message     = reader.readLine
			outputMessage(message)
			reader.close
		serverSocket.close
	catch
		case e: IOException =>
			e.printStackTrace
	end main

	private def outputMessage(msg: String): Unit =
		if ConsoleUtils.hasFormattingPlaceholders(msg) then printf(msg)
		else System.out.println(msg)
	end outputMessage

	private def findFreePort: Try[Int] = Using(new ServerSocket(0))(_.getLocalPort)

end Receiver

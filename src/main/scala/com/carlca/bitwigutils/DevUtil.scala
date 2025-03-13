package com.carlca.bitwigutils

import com.bitwig.extension.controller.api.ControllerHost

import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.*

object DevUtil:
  @throws[IOException]
  def dumpBitwigActions(host: ControllerHost, outputFile: Path): Unit =
    val app = host.createApplication()
    val output = ListBuffer[String]()

    for actionCat <- app.getActionCategories() do
      output += actionCat.getName
      for action <- actionCat.getActions() do
        output += "--- ID: " + action.getId + " Name: " + action.getName

    Files.write(outputFile, output.asJava, StandardCharsets.UTF_8)

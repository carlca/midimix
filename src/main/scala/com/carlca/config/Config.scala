package com.carlca
package config

import org.json.*

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

enum OS:
  case WINDOWS, MACOS, LINUX

object Config:
  private val APP_NAME = Some("com.carlca.Logger")
  initFolder

  def getLogPort: Integer =
    getJsonObject.getInt("logPort")

  def setLogPort(logPort: Integer): Unit =
    setJsonObject(getJsonObject.put("logPort", logPort))

  def getOs: OS =
    val osName = System.getProperty("os.name").toLowerCase
    osName match
      case name if name.contains("win") => OS.WINDOWS
      case name if name.contains("mac") => OS.MACOS
      case _                            => OS.LINUX

  private def getJsonObject: JSONObject =
    val path = this.getConfigPath
    if Files.exists(path) then
      val content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8)
      return new JSONObject(content)
    new JSONObject

  private def setJsonObject(jsonObject: JSONObject): Unit =
    Files.write(this.getConfigPath, jsonObject.toString(4).getBytes)

  private def getConfigFolder: String =
    def getPath(prop: String, pathName: String): Path =
      Paths.get(System.getProperty(prop)).resolve(pathName)
    val os = getOs
    val folder = os match
      case OS.LINUX   => getPath("user.home", ".config")
      case OS.MACOS   => getPath("user.home", "Library/Application Support")
      case OS.WINDOWS => getPath("user.home", ".config")
    val name = APP_NAME.getOrElse("default_app")
    folder.resolve(name).toString

  private def getConfigPath =
    val folder = this.getConfigFolder
    Paths.get(folder).resolve("config.json")

  private def initFolder: Unit =
    val path = Paths.get(this.getConfigFolder)
    if !Files.exists(path) then Files.createDirectories(path)
    val configFile = Paths.get(this.getConfigPath.toString)
    if !Files.exists(configFile) then
      createDefaultConfig(configFile)

  private def createDefaultConfig(configFile: Path): Unit =
    val defaultConfig = new JSONObject()
    defaultConfig.put("logPort", 0)
    Files.write(configFile, defaultConfig.toString(4).getBytes(StandardCharsets.UTF_8))

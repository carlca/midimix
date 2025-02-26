package com.carlca
package config

import org.json.*
import org.json.JSONObject

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

enum OS:
  case WINDOWS, MACOS, LINUX

object Config:
  private var appName: Option[String] = None

  def init(appName: String): Unit =
    Config.appName = Some(appName)
    initFolder

  def getLogPort: Integer =
    getJsonObject.getInt("logPort")
  end getLogPort

  def setLogPort(logPort: Integer): Unit =
    setJsonObject(getJsonObject.put("logPort", logPort))
  end setLogPort

  def getOs: OS =
    val osName = System.getProperty("os.name").toLowerCase
    osName match
      case name if name.contains("win") => OS.WINDOWS
      case name if name.contains("mac") => OS.MACOS
      case _                            => OS.LINUX
  end getOs

  private def getJsonObject: JSONObject =
    val path = this.getConfigPath
    if Files.exists(path) then
      val content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8)
      return new JSONObject(content)
    new JSONObject
  end getJsonObject

  private def setJsonObject(jsonObject: JSONObject): Unit =
    Files.write(this.getConfigPath, jsonObject.toString(4).getBytes)
  end setJsonObject

  private def getConfigFolder: String =
    def getPath(prop: String, pathName: String): Path =
      Paths.get(System.getProperties.get(prop).asInstanceOf[String]).resolve(pathName)
    val os = getOs
    val folder = os match
      case OS.LINUX   => getPath("APPDATA", "config")
      case OS.MACOS   => getPath("user.home", "Library/Application Support")
      case OS.WINDOWS => getPath("user.home", ".config")
    folder.resolve(appName.get).toString
  end getConfigFolder

  private def getConfigPath =
    val folder = this.getConfigFolder
    Paths.get(folder).resolve("config.json")
  end getConfigPath

  private def initFolder: Unit =
    val path = Paths.get(this.getConfigFolder)
    if !Files.exists(path) then Files.createDirectories(path)
  end initFolder

end Config

package com.carlca.utils

import java.util

object ArrayUtils:

  def removeElements(list: util.ArrayList[String], item: String): Boolean =
    var found    = false
    val iterator = list.iterator
    while iterator.hasNext do
      val element = iterator.next
      if element.trim.startsWith(item.trim) then
        iterator.remove
        found = true
    found
  end removeElements

  def getStackTrace: util.ArrayList[String] =
    val stackTrace = Thread.currentThread.getStackTrace
    val trace      = new util.ArrayList[String]
    for stackTraceElement <- stackTrace do trace.add(stackTraceElement.getClassName)
    trace
  end getStackTrace
end ArrayUtils

package com.carlca

import config.Config

import logger.Log

object testLog:

  def main(args: Array[String]): Unit =
    Log.cls()
    Log.send("Testing,")
    Log.send("Testing,")
    Log.send("Testing, 1, 2, 3")
  end main

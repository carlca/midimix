package com.carlca

import config.Config

object testGetLogPort:

	def main(args: Array[String]): Unit =
		Config.init("runConfig")
		printf("Test succeeded: %b", Config.getLogPort == 12345)
	end main

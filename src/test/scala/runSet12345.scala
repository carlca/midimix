package com.carlca

import config.Config

object runSet12345:

	def main(args: Array[String]): Unit =
		Config.setLogPort(12345)
	end main

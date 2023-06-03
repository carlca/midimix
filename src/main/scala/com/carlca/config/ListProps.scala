package com.carlca
package config

import java.io.File

object ListProps:

  def main(args: Array[String]): Unit =
    scala.sys.props.foreach((k, v) => println(s"$k: $v"))
    println(File(".").getAbsolutePath)
    println(File(".").getCanonicalPath)
  end main

end ListProps

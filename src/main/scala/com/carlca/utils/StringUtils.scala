package com.carlca.utils

object StringUtils:

  def unadornedClassName(objClass: AnyRef): String =
    var className = objClass.getClass.getName.substring(6)
    // Remove the package name
    val lastDotIndex = className.lastIndexOf('.')
    if lastDotIndex != -1 then className = className.substring(lastDotIndex + 1)
    // Remove any proxy-related suffixes
    val proxyIndex = className.indexOf("Proxy")
    if proxyIndex != -1 then className = className.substring(0, proxyIndex)
    className
  end unadornedClassName

end StringUtils

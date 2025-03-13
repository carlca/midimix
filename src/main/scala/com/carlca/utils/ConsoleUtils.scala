package com.carlca.utils

import java.util.regex.Pattern

object ConsoleUtils:

  def hasFormattingPlaceholders(text: String): Boolean =
    val pattern = Pattern.compile("%[\\w%]")
    val matcher = pattern.matcher(text)
    matcher.find
  end hasFormattingPlaceholders

end ConsoleUtils

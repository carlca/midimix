object Log2:

  def send(msg: String, args: Any*): Log2.type =
    println(String.format(msg, args))
    Log2

  def line: Log2.type =
    println(String.valueOf('-').repeat(80))
    Log2
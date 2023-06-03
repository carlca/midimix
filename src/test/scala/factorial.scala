package com.carlca

object factorial:

  def factorial(n: Int): BigInt =
    if n <= 1 then 1
    else n * factorial(n - 1)

  def main(args: Array[String]): Unit =
    val number = 5
    val result = factorial(number)
    println(s"The factorial of $number is: $result")

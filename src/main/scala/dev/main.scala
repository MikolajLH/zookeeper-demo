package dev


import scala.io.StdIn
import scala.util.Try


@main def main(connString: String, args: String*): Unit =
  val optionMap = args.grouped(2).collect {
    case Seq(key, value) => key -> value
  }.toMap
  //val connectString = s"localhost:$port"
  val sessionTimeout = 3000
  println(s"Connecting with ZooKeeper at $connString")

  val appManager = new AppManager(connString, sessionTimeout, optionMap)

  val err = Try{
    var run = true
    while(run) {
      val input = StdIn.readLine("> ").trim
      if input.nonEmpty then
        val parts = input.split("\\s+").toList
        val cmd = parts.head
        val args = parts.tail
        cmd match
          case "show" =>
            if args.nonEmpty then
              appManager.displayTree(args.head)
            else println("show <node>")
          case "exit" =>
            run = false
          case other =>
            println(s"Unknown command $other")
  }}.toOption
  appManager.close()
  println(err)
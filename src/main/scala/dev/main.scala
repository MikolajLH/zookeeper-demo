

import dev.AppManager
import org.apache.zookeeper.{AddWatchMode, WatchedEvent, Watcher, ZooKeeper}
import org.apache.zookeeper.Watcher.Event
import org.apache.zookeeper.Watcher.Event.EventType

import scala.io.StdIn
import scala.sys.process.*
import scala.util.Try


//case class ZkWatcher() extends Watcher {
//  private var childrenCounter = 0
//  private var pid: Option[Process] = None
//  override def process(event: WatchedEvent): Unit = {
//    println(s"Zookeeper's event: $event")
//    if event.getPath == "/a" then {
//      println("/a event")
//      event.getType match {
//        case EventType.NodeCreated =>
//          if pid.isEmpty then pid = Some(Process("calc.exe").run())
//          println("Node created")
//        case EventType.NodeDeleted =>
//          if pid.isDefined then {
//            println("Destroying process")
//            val pr = pid.get
//            pr.destroy()
//            pid = None
//          }
//          println("Node deleted")
//        case _ =>
//          println("Other node event")
//      }
//    } else if event.getPath.startsWith("/a") then {
//
//      println("/a child event")
//      event.getType match {
//        case EventType.NodeCreated =>
//          println("Node created")
//          childrenCounter = childrenCounter + 1
//        case EventType.NodeDeleted =>
//          println("Node deleted")
//          childrenCounter = childrenCounter - 1
//        case _ =>
//          println("Other node event")
//      }
//      println(s"Children counter: $childrenCounter")
//    } else {
//      println("Other node event")
//    }
//  }
//}

@main def main(): Unit =
  val connectString = "localhost:2181"
  val sessionTimeout = 3000
  println(s"Connecting with ZooKeeper ad $connectString")

//  val watcher = ZkWatcher()
//  val zk = new ZooKeeper(connectString, sessionTimeout, watcher)
//  zk.addWatch("/a", AddWatchMode.PERSISTENT_RECURSIVE)
  
  val appManager = new AppManager(connectString, sessionTimeout, Map("/a" -> "calc.exe"))
  

  val err = Try{
    var run = true
    while(run) {
    val input = StdIn.readLine()
    if input == "exit" then run = false
  }}.toOption
  
//  println(err)
//  var running = true
//  var process: Option[Process] = None
//  while running do
//    val input = StdIn.readLine(">> ").trim.toLowerCase
//    input match
//      case "start" =>
//        if process.isEmpty || !process.exists(_.isAlive) then
//          try
//            val p = Process("notepad.exe").run()
//            process = Some(p)
//            println("Notepad started.")
//          catch
//            case _=>
//              println("Failed to start Notepad")
//        else
//          println("Notepad is already running.")
//
//      case "stop" =>
//        val result = Seq("taskkill", "/IM", "notepad.exe", "/F").!
//        if result == 0 then
//          println("Notepad stopped.")
//        else
//          println("Failed to stop Notepad (maybe it wasn't running?).")
//        process = None
//      case "exit" =>
//        println("Exiting...")
//        process.foreach(p => if p.isAlive then p.destroy())
//        running = false
//      case _ =>
//        println("Oh..")
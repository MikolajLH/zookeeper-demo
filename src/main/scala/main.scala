import org.apache.zookeeper.{AddWatchMode, WatchedEvent, Watcher, ZooKeeper}
import org.apache.zookeeper.Watcher.Event
import org.apache.zookeeper.Watcher.Event.EventType

import scala.io.StdIn
import scala.util.Try


case class ZkWatcher() extends Watcher {
  private var childrenCounter = 0
  override def process(event: WatchedEvent): Unit = {
    println(s"Zookeeper's event: $event")
    if event.getPath == "/a" then {
      println("/a event")
      event.getType match {
        case EventType.NodeCreated =>
          println("Node created")
        case EventType.NodeDeleted =>
          println("Node deleted")
        case _ =>
          println("Other node event")
      }
    } else if event.getPath.startsWith("/a") then {

      println("/a child event")
      event.getType match {
        case EventType.NodeCreated =>
          println("Node created")
          childrenCounter = childrenCounter + 1
        case EventType.NodeDeleted =>
          println("Node deleted")
          childrenCounter = childrenCounter - 1
        case _ =>
          println("Other node event")
      }
      println(s"Children counter: $childrenCounter")
    } else {
      println("Other node event")
    }
  }
}

@main def main(): Unit =
  val connectString = "localhost:2181"
  val sessionTimeout = 3000
  println(s"Connecting with ZooKeeper ad $connectString")

  val watcher = ZkWatcher()
  val zk = new ZooKeeper(connectString, sessionTimeout, watcher)
  zk.addWatch("/a", AddWatchMode.PERSISTENT_RECURSIVE)

  val err = Try{
    var run = true
    while(run) {
    val input = StdIn.readLine()
    if input == "exit" then run = false
  }}.toOption
  zk.close()
  println(err)
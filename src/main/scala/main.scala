import org.apache.zookeeper.{AddWatchMode, WatchedEvent, Watcher, ZooKeeper}
import org.apache.zookeeper.Watcher.Event

import scala.io.StdIn
import scala.util.Try

case class ZkWatcher() extends Watcher {
  override def process(event: WatchedEvent): Unit = {
    println(s"Zookeeper's event: $event")
    if event.getPath == "/a" then {
      println("/a event")
    } else if event.getPath.startsWith("/a") then {
      println("/a child event")
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
package dev

import org.apache.zookeeper.Watcher.Event
import org.apache.zookeeper.{AddWatchMode, WatchedEvent, Watcher, ZooKeeper}
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

import scala.collection.immutable.Seq
import scala.sys.process.*
import scala.jdk.CollectionConverters._

class AppManager(connectString: String, sessionTimeout: Int, nodes: Map[String, String]) extends Watcher:
  private val zk = {
    val zk = new ZooKeeper(connectString, sessionTimeout, this)
    for (k,v) <- nodes do
      zk.addWatch(k, AddWatchMode.PERSISTENT_RECURSIVE)
    zk
  }

  private val processNames = Map(
    "calc.exe" -> "Calculator.exe"
  )

  private var pid: Option[Process] = None

  def close(): Unit = zk.close()

  def displayTree(root: String): Unit =
    if Option(zk.exists(root, false)).isDefined
    then displayNodeAndChildren(root, "")
    else println(s"node $root does not exist")


  private def displayNodeAndChildren(node: String, indent: String): Unit =
    println(s"$indent$node")
    val children = zk.getChildren(node, false).asScala.toList
    for child <- children.sorted do
      displayNodeAndChildren(child, indent + " ")

  override def process(event: WatchedEvent): Unit =
    for (k,v) <- nodes
    do
      if event.getPath == k
      then
        println(s"Node $k event")
        event.getType match
          case Event.EventType.NodeCreated =>
            val p = Process(v).run()
            println(p)
            pid = Some(p)
            println("Node created")
          case Event.EventType.NodeDeleted =>
            if pid.isDefined then
              val name = processNames.getOrElse(v, v)
              //Seq("taskkill", "/PID", pid.get., "/F").!
              Seq("taskkill", "/IM", name, "/F").!
              pid.foreach(_.destroy())
              pid = None

            println("Node deleted")
          case other: Event.EventType =>
            println(s"event $other")
      else if event.getPath.startsWith(k)
      then
        println("Child event")
        val childrenNumber = zk.getAllChildrenNumber(k)
        println(s"Children number: $childrenNumber")

        event.getType match
          case Event.EventType.NodeCreated =>
            println("Node created")
            SwingUtilities.invokeLater(() =>
              JOptionPane.showMessageDialog(null, s"New child node created at ${event.getPath}\n Current children number for $k: $childrenNumber")
            );
          case Event.EventType.NodeDeleted =>
            println("Node deleted")
          case other: Event.EventType =>
            println(s"event $other")


      else
        ()

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
      displayNodeAndChildren(s"$node/$child", indent + " ")

  override def process(event: WatchedEvent): Unit = {
    val nodePathOp = Option(event.getPath)
    for nodePath <- nodePathOp do {
      println(s"Node $nodePath event")
      for (k,v) <- nodes do
        if nodePath == k
        then
          event.getType match
            case Event.EventType.NodeCreated =>
              val p = Process(v).run()
              println(p)
              pid = Some(p)
              println("Node created")
            case Event.EventType.NodeDeleted =>
              if pid.isDefined then
                val name = processNames.getOrElse(v, v)
                Seq("taskkill", "/IM", name, "/F").!
                pid.foreach(_.destroy())
                pid = None
              println(s"Node $nodePath deleted")
            case other: Event.EventType =>
              println(s"event $other")
        else if nodePath.startsWith(k)
        then
          println("Child event")
          event.getType match
            case Event.EventType.NodeCreated =>
              println("Node created")
              val childrenNumber = zk.getAllChildrenNumber(k)
              println(s"Children number: $childrenNumber")
              SwingUtilities.invokeLater(() =>
              JOptionPane.showMessageDialog(null, s"New child node created at ${event.getPath}\n Current children number for $k: $childrenNumber")
              );
            case Event.EventType.NodeDeleted =>
              println(s"Node $nodePath deleted")
            case other: Event.EventType =>
              println(s"event $other")
        else {
          println("Other node event")
          ()
        }
    }
  }

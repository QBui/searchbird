package com.twitter.searchbird

import com.twitter.conversions.time._
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.thrift.ThriftClientFramedCodec
import java.net.InetSocketAddress
import scala.tools.nsc.interpreter._
import scala.tools.nsc.Settings
import com.twitter.finagle.zookeeper.{ZookeeperServerSetCluster, ZkResolver}
import com.twitter.common.zookeeper.ServerSetImpl

object SearchbirdConsoleClient extends App {

  val inst = new ZkInstance
  import inst._

  // Setup serverSet
  inst.start()
  val serverSet = new ServerSetImpl(zookeeperClient, "/services/searchbird")
  val cluster = new ZookeeperServerSetCluster(serverSet)

  val service = ClientBuilder()
    //.hosts(new InetSocketAddress(args(0), args(1).toInt))
    .cluster(cluster)
    .codec(ThriftClientFramedCodec())
    .hostConnectionLimit(1)
    .tcpConnectTimeout(3.seconds)
    .build()

  val client = new SearchbirdService.FinagledClient(service)

  val intLoop = new ILoop()

  Console.println("'client' is bound to your thrift client.")
  intLoop.setPrompt("\nfinagle-client> ")

  intLoop.settings = {
    val s = new Settings(Console.println)
    s.embeddedDefaults[SearchbirdService.FinagledClient]
    s.Yreplsync.value = true
    s
  }

  intLoop.createInterpreter()
  intLoop.in = new JLineReader(new JLineCompletion(intLoop))

  intLoop.intp.beQuietDuring {
    intLoop.intp.interpret("""def exit = println("Type :quit to resume program execution.")""")
    intLoop.intp.bind(NamedParam("client", client))
  }

  intLoop.loop()
  intLoop.closeInterpreter()
}

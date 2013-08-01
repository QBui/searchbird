package com.twitter.searchbird

import com.twitter.logging.Logger
import com.twitter.ostrich.admin.{RuntimeEnvironment, ServiceTracker}
import com.twitter.finagle.zookeeper.{ZkResolver, ZkClientFactory, ZkAnnouncer, ZookeeperServerSetCluster}
import java.net.InetSocketAddress
import com.twitter.common.zookeeper.{ServerSet, ServerSetImpl, ZooKeeperClient}
import com.twitter.finagle.builder.{ServerBuilder}
import com.twitter.finagle.Announcer
import com.twitter.finagle.zipkin.thrift.ZipkinTracer
import com.twitter.finagle.stats.NullStatsReceiver

//import com.twitter.common.application.ShutdownRegistry.ShutdownRegistryImpl
import com.twitter.util.Await
import com.twitter.conversions.time._

object Main {
  private val log = Logger.get(getClass)

  def main(args: Array[String]) {
    
    val inst = new ZkInstance
    import inst._

    val runtime = RuntimeEnvironment(this, args)
    val service = runtime.loadRuntimeConfig[SearchbirdService.ThriftServer]

    // Setup serverSet
    inst.start()
    val serverSet = new ServerSetImpl(zookeeperClient, "/services/searchbird")
    val cluster = new ZookeeperServerSetCluster(serverSet)
    
    // connect to zookeeper server
    //val shutdownRegistry = new ShutdownRegistryImpl
    //val zookeeperServer = new ZookeeperServerImpl
    //val zookeeperClient = zookeeperServer.createClient(ZooKeeperClient.digestCredentials("user","pass"))

    try {
      log.info("Starting SearchbirdService")


      //val processor = new SearchbirdServiceImpl()
     // val serviceAddress = new InetSocketAddress(9999)
     // val server1 = ServerBuilder()
     //   .bindTo(serviceAddress)
     //   .name("ZkSearchBird")
     //   .tracer(ZipkinTracer(scribeHost = "localhost", scribePort = 9410,
     //     NullStatsReceiver, 0.4.toFloat))


      val addr = new InetSocketAddress(service.thriftPort)


      // Manually announce the service endpoint
      val hostPath = "localhost:%d!/foo/bar/baz".format(inst.zookeeperAddress.getPort)

      // val zkTimeout = 100.milliseconds
      // val factory = new ZkClientFactory(zkTimeout)
      val announcer = new ZkAnnouncer()
      //val res = new ZkResolver()

      Await.result(announcer.announce(addr, "%s!0".format(hostPath)))
      //service.tracerFactory()
      cluster.join(addr)
      service.start()
    } catch {
      case e: Exception =>
        log.error(e, "Failed starting SearchbirdService, exiting")
        inst.stop()
        ServiceTracker.shutdown()
        System.exit(1)
    }
  }
}

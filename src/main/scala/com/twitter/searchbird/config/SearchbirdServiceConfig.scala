package com.twitter.searchbird
package config

import com.twitter.finagle.tracing.{NullTracer, Tracer}
import com.twitter.logging.Logger
import com.twitter.logging.config._
import com.twitter.ostrich.admin.{RuntimeEnvironment, ServiceTracker}
import com.twitter.ostrich.admin.config._
import com.twitter.util.Config
import com.twitter.finagle.zipkin.thrift.{RawZipkinTracer, ZipkinTracer}
import com.twitter.finagle.stats.NullStatsReceiver

class SearchbirdServiceConfig extends ServerConfig[SearchbirdService.ThriftServer] {
  var thriftPort: Int = 9999
  //var tracerFactory: Tracer.Factory = NullTracer.factory
  var tracerFactory: Tracer.Factory =
    ZipkinTracer(scribeHost = "localhost", scribePort = 9410,
      // statsReceiver = NullStatsReceiver,
      sampleRate = 1.toFloat)

  def apply(runtime: RuntimeEnvironment) = new SearchbirdServiceImpl(this)
}

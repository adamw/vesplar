package com.softwaremill.vesplar.bolt

import backtype.storm.topology.base.BaseRichBolt
import com.softwaremill.vesplar.config.CassandraConfig
import backtype.storm.task.{TopologyContext, OutputCollector}

abstract class BaseCassandraBolt(cassandraNode: String) extends BaseRichBolt {
  def this(cassandraConfig: CassandraConfig) = this(cassandraConfig.cassandraNode)

  protected var cassandraClient: CassandraClient = _
  protected var _collector: OutputCollector = _

  def prepare(conf: java.util.Map[_, _], context: TopologyContext, collector: OutputCollector) {
    _collector = collector
    cassandraClient = CassandraClient.from(cassandraNode)
  }

  override def cleanup() {
    cassandraClient.shutdown()
    cassandraClient = null
    super.cleanup()
  }
}

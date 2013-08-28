package com.softwaremill.vesplar

import backtype.storm.topology.base.BaseRichBolt
import backtype.storm.task.{TopologyContext, OutputCollector}
import backtype.storm.tuple.{Fields, Values, Tuple}
import backtype.storm.topology.OutputFieldsDeclarer

class SysOutBolt extends BaseRichBolt {
  private var _collector: OutputCollector = _

  def prepare(conf: java.util.Map[_, _], context: TopologyContext, collector: OutputCollector) {
    _collector = collector
  }

  def execute(tuple: Tuple) {
    println("Tweet from @%s: %s".format(tuple.getString(3), tuple.getString(1)))
    //_collector.emit(tuple, new Values(tuple.getString(0) + "!!!"))
    _collector.ack(tuple)
  }

  def declareOutputFields(declarer: OutputFieldsDeclarer) {
    declarer.declare(new Fields("word"))
  }
}

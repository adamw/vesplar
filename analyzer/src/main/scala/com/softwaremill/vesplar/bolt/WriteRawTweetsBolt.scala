package com.softwaremill.vesplar.bolt

import com.softwaremill.vesplar.config.CassandraConfig
import backtype.storm.tuple.Tuple
import backtype.storm.topology.OutputFieldsDeclarer

class WriteRawTweetsBolt(cassandraConfig: CassandraConfig) extends BaseCassandraBolt(cassandraConfig) {
  def execute(tuple: Tuple) {
    cassandraClient.writeTweet(tuple.getString(0), tuple.getString(1), tuple.getString(2), tuple.getString(3))
    _collector.ack(tuple)
  }

  def declareOutputFields(declarer: OutputFieldsDeclarer) {}
}

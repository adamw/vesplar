package com.softwaremill.vesplar.spout

import backtype.storm.topology.base.BaseRichSpout
import backtype.storm.topology.OutputFieldsDeclarer
import java.util
import backtype.storm.task.TopologyContext
import backtype.storm.spout.SpoutOutputCollector
import backtype.storm.tuple.{Values, Fields}
import twitter4j._
import java.util.concurrent.LinkedBlockingQueue
import com.typesafe.scalalogging.slf4j.Logging
import com.softwaremill.vesplar.config.TwitterConfig
import twitter4j.conf.Configuration

class TweetsSpout(twitterConfiguration: Configuration, terms: List[String]) extends BaseRichSpout with Logging with Serializable {
  private var collector: SpoutOutputCollector = _
  private var tweetStream: TwitterStream = _
  private var tweetQueue: LinkedBlockingQueue[Status] = _

  def open(conf: util.Map[_, _], context: TopologyContext, collector: SpoutOutputCollector) {
    this.collector = collector
    this.tweetQueue = new LinkedBlockingQueue[Status](16384)

    val stream = new TwitterStreamFactory(twitterConfiguration).getInstance()
    stream.addListener(new EnqueueingStatusListener(tweetQueue))
    stream.filter(new FilterQuery().track(terms.toArray))
  }

  override def close() {
    if (tweetStream != null) {
      tweetStream.shutdown()
    }
    tweetStream = null

    super.close()
  }

  def nextTuple() {
    tweetQueue.poll() match {
      case null => {
        Thread.sleep(100L)
      }
      case status => {
        collector.emit(new Values(status.getId.toString, status.getText, status.getUser.getId.toString, status.getUser.getName))
      }
    }
  }

  def declareOutputFields(declarer: OutputFieldsDeclarer) {
    declarer.declare(new Fields("tweetId", "tweetText", "userId", "userName"))
  }
}

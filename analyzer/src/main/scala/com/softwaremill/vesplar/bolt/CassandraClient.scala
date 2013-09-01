package com.softwaremill.vesplar.bolt

import com.datastax.driver.core.{BoundStatement, Cluster}
import scala.collection.JavaConversions._
import com.typesafe.scalalogging.slf4j.Logging

class CassandraClient(cluster: Cluster) extends Logging {
  private val KeyspaceName = "vesplar"
  private val TweetsTableName = "tweets"

  private val session = cluster.connect()

  private val insertTweetStmt = session.prepare(
    s"""INSERT INTO $KeyspaceName.$TweetsTableName (tweet_id, tweet_text, user_id, user_name) VALUES (?, ?, ?, ?);""")

  def ensureSchema() {
    val keyspaceCount = session.execute(
      s"""SELECT COUNT(*) FROM system.schema_keyspaces WHERE keyspace_name = '$KeyspaceName';""")
      .one().getLong(0)

    if (keyspaceCount == 0) {
      logger.info("Creating vesplar keyspace")
      session.execute(s"""CREATE KEYSPACE $KeyspaceName WITH REPLICATION = {'class':'SimpleStrategy', 'replication_factor':3};""")
    } else {
      logger.info("Vesplar keyspace already created")
    }

    val tweetsTableCount = session.execute(
      s"""SELECT COUNT(*) FROM system.schema_columnfamilies WHERE keyspace_name='$KeyspaceName' AND columnfamily_name='$TweetsTableName';""")
      .one().getLong(0)

    if (tweetsTableCount == 0) {
      logger.info("Creating tweets table")
      session.execute(s"""CREATE TABLE $KeyspaceName.$TweetsTableName (
        tweet_id text PRIMARY KEY,
        tweet_text text,
        user_id text,
        user_name text
        );
      """)
    } else {
      logger.info("Tweets table already created")
    }
  }

  def writeTweet(tweetId: String, tweetText: String, userId: String, userName: String) {
    val boundStmt = new BoundStatement(insertTweetStmt).bind(tweetId, tweetText, userId, userName)
    session.execute(boundStmt)

    logger.debug(s"Wrote tweet $tweetId")
  }

  def dumpTweets() {
    session.execute(s"""SELECT * FROM $KeyspaceName.$TweetsTableName""").iterator().foreach(println)
  }

  def shutdown() {
    cluster.shutdown()
  }
}

object CassandraClient {
  def from(node: String) = {
    new CassandraClient(Cluster.builder().addContactPoint(node).build())
  }
}

object Test extends App {
  val client = CassandraClient.from("localhost")
  try {
    client.ensureSchema()
    client.writeTweet("1", "text", "10", "adamw")
    client.dumpTweets()
  } finally {
    client.shutdown()
  }
}
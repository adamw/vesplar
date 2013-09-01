package com.softwaremill.vesplar.config

import com.typesafe.config.Config

trait CassandraConfig {
  def rootConfig: Config

  private lazy val cassandraConfig = rootConfig.getConfig("cassandra")

  lazy val cassandraNode = cassandraConfig.getString("node")
}

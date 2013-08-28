package com.softwaremill.vesplar.config

import com.typesafe.config.Config
import twitter4j.conf.ConfigurationBuilder

trait TwitterConfig {
  def rootConfig: Config

  private lazy val oauthTwitterConfig = rootConfig.getConfig("twitter.oauth")

  lazy val twitterOAuthConsumerKey = oauthTwitterConfig.getString("consumerKey")
  lazy val twitterOAuthConsumerSecret = oauthTwitterConfig.getString("consumerSecret")
  lazy val twitterOAuthAccessToken = oauthTwitterConfig.getString("accessToken")
  lazy val twitterOAuthAccessTokenSecret = oauthTwitterConfig.getString("accessTokenSecret")

  def buildTwitter4jConfig = {
    new ConfigurationBuilder()
      .setOAuthConsumerKey(twitterOAuthConsumerKey)
      .setOAuthConsumerSecret(twitterOAuthConsumerSecret)
      .setOAuthAccessToken(twitterOAuthAccessToken)
      .setOAuthAccessTokenSecret(twitterOAuthAccessTokenSecret)
      .build()
  }
}

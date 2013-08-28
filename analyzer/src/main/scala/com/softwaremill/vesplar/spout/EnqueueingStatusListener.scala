package com.softwaremill.vesplar.spout

import twitter4j.{Status, StatusDeletionNotice, StallWarning, StatusListener}
import com.typesafe.scalalogging.slf4j.Logging
import java.util

class EnqueueingStatusListener(tweetQueue: util.Queue[Status]) extends StatusListener with Logging {
  def onStallWarning(warning: StallWarning) {
    logger.warn(s"Stall warning: $warning")
  }

  def onException(ex: Exception) {
    logger.error("Stream exception", ex)
  }

  def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {
    logger.info(s"Status deletion notice: $statusDeletionNotice")
  }

  def onScrubGeo(userId: Long, upToStatusId: Long) {
    logger.info(s"Scrub geo: $userId, $upToStatusId")
  }

  def onStatus(status: Status) {
    if (!tweetQueue.offer(status)) {
      logger.warn(s"Cannot enqueue status $status")
    }
  }

  def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {
    logger.info(s"Track limitation notice: $numberOfLimitedStatuses")
  }
}

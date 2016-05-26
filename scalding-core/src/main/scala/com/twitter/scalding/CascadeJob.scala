package com.twitter.scalding

import cascading.cascade.CascadeConnector
import cascading.cascade.Cascade

abstract class CascadeJob(args: Args) extends Job(args) {

  def jobs: Seq[Job]

  override def run(): Boolean = {
    val flows = jobs.map { _.buildFlow }
    val cascade = new CascadeConnector().connect(flows: _*)
    preProcessCascade(cascade)
    cascade.complete()
    postProcessCascade(cascade)
    val statsData = cascade.getCascadeStats

    handleStats(statsData)
    statsData.isSuccessful
  }

  override def validate(): Unit = {
    jobs.foreach { _.validate }
  }

  /*
   * Good for printing a dot file, setting the flow skip strategy, etc
   */
  def preProcessCascade(cascade: Cascade) = {}

  /*
   * Good for checking the cascade stats
   */
  def postProcessCascade(cascade: Cascade) = {}

}

import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object BuildSettings {
  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization  := "com.softwaremill",
    version       := "0.0.1-SNAPSHOT",
    scalaVersion  := "2.10.2",
    resolvers     += "clojars" at "http://clojars.org/repo",
    homepage      := Some(new java.net.URL("http://www.softwaremill.com")),
    licenses      := ("Apache2", new java.net.URL("http://www.apache.org/licenses/LICENSE-2.0.txt")) :: Nil
  )
}

object Dependencies {
  // Shouldn't go to the fat-jar
  val storm         = "storm"                     % "storm"                 % "0.8.2" % "provided"

  val twitter4j     = "org.twitter4j"             % "twitter4j-stream"      % "3.0.3"

  val jodaConvert   = "org.joda"                  % "joda-convert"          % "1.2"
  val config        = "com.typesafe"              % "config"                % "1.0.0"
  val scalalogging  = "com.typesafe"              %% "scalalogging-slf4j"   % "1.0.1"
}

object VesplarBuild extends Build {
  import Dependencies._
  import BuildSettings._

  lazy val root: Project = Project(
    "root",
    file("."),
    settings = buildSettings
  ) aggregate(analyzer)

  lazy val analyzer: Project = Project(
    "analyzer",
    file("analyzer"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= Seq(storm, twitter4j, config, scalalogging, jodaConvert),
      mainClass in assembly := Some("org.elasticmq.server.Main"))
      ++ assemblySettings
  )
}


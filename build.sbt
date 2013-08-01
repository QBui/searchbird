import com.twitter.sbt._
import com.twitter.scalatest._

seq((
  Project.defaultSettings ++
    StandardProject.newSettings ++
    SubversionPublisher.newSettings ++
    CompileThriftScrooge.newSettings ++
    ScalaTestMixins.testSettings
): _*)

organization := "com.twitter"

name := "searchbird"

version := "1.0.0-SNAPSHOT"


libraryDependencies ++= Seq(
  "org.scala-lang" % "jline"                 % "2.9.1",
  "com.twitter"    % "scrooge"               % "3.0.1",
  "com.twitter"    % "scrooge-runtime_2.9.2" % "3.0.1",
  "com.twitter"    % "finagle-core"          % "6.5.2",
  "com.twitter"    % "finagle-thrift"        % "6.5.2",
  "com.twitter"    % "finagle-serversets"    % "6.5.2",
  "com.twitter"    % "finagle-ostrich4"      % "6.5.2",
  "com.twitter"    % "finagle-zipkin"        % "6.4.1",
  "org.scalatest" %% "scalatest"             % "1.7.1" % "test",
  "com.twitter"   %% "scalatest-mixins"      % "1.1.0" % "test"
)

mainClass in (Compile, run) := Some("com.twitter.searchbird.Main")

mainClass in (Compile, packageBin) := Some("com.twitter.searchbird.Main")

CompileThriftScrooge.scroogeVersion := "3.0.1"

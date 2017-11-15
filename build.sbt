name := "bitemporal"

parallelExecution in Test := false

organization := "com.github.1123"

version := "0.1"

scalaVersion := "2.12.4"

EclipseKeys.withSource := true

//Define dependencies. These ones are only required for Test and Integration Test scopes.
libraryDependencies ++= Seq(
    "org.scalatest"   %% "scalatest"  % "3.0.4"   % "test,it",
    "org.scalacheck"  %% "scalacheck"   % "1.13.5" % "test,it",
    "joda-time" % "joda-time" % "2.0",
    "org.joda" % "joda-convert" % "1.2",
    "junit" % "junit" % "4.8.1" % "test"
)

libraryDependencies += "org.mongodb" % "mongo-java-driver" % "2.12.4"

libraryDependencies += "com.cedarsoftware" % "json-io" % "2.2.29"

libraryDependencies += "org.elasticsearch" % "elasticsearch" % "1.4.0"

// For Settings/Task reference, see http://www.scala-sbt.org/release/sxr/sbt/Keys.scala.html

// Compiler settings. Use scalac -X for other options and their description.
// See Here for more info http://www.scala-lang.org/files/archive/nightly/docs/manual/html/scalac.html 
scalacOptions ++= List("-feature","-deprecation", "-unchecked", "-Xlint")

// ScalaTest settings.
// Ignore tests tagged as @Slow (they should be picked only by integration test)
testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-l", "org.scalatest.tags.Slow", "-u","target/junit-xml-reports", "-oD", "-eS")

//Style Check section 
org.scalastyle.sbt.ScalastylePlugin.Settings
 
org.scalastyle.sbt.PluginKeys.config <<= baseDirectory { _ / "src/main/config" / "scalastyle-config.xml" }


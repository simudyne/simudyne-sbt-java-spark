name := "simudyne-sbt-java"

version := "0.1"

scalaVersion := "2.11.12"
logLevel := Level.Info

resolvers += "Artifactory Realm" at "https://simudyne.jfrog.io/simudyne/releases"
credentials += Credentials(file(".credentials"))

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-encoding", "UTF-8")
scalacOptions ++= Seq("-target:jvm-1.8", "-deprecation", "-feature", "-unchecked")

lazy val simudyneVersion = "2.1.0"
lazy val sparkVersion = "2.2.1"
libraryDependencies ++= Seq(
  "simudyne" %% "simudyne-nexus-server" % simudyneVersion,
  "simudyne" %% "simudyne-core" % simudyneVersion,
  "simudyne" %% "simudyne-core-abm" % simudyneVersion,
  "simudyne" %% "simudyne-core-graph-spark" % simudyneVersion,
  "simudyne" %% "simudyne-core-runner-spark" % simudyneVersion
)

lazy val commonSettings = Seq(
  test in assembly := {},
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs@_*) => MergeStrategy.discard
    // dependencies included in spark
    case PathList("org", "slf4j", xs@_*) => MergeStrategy.discard
    case PathList("com", "fasterxml", "jackson", "core", xs@_*) => MergeStrategy.discard
    case PathList("com", "twitter", xs@_*) => MergeStrategy.discard
    case PathList("org", "apache", "commons", xs@_*) => MergeStrategy.discard
    case PathList("org", "json4s", xs@_*) => MergeStrategy.discard
    case PathList("org", "xerial", xs@_*) => MergeStrategy.discard
    case PathList("org", "scala-lang", xs@_*) => MergeStrategy.discard
      
    //akka configuration files
    case PathList("reference.conf") => MergeStrategy.concat
    case _ => MergeStrategy.first
  },
  assemblyJarName in assembly := "simudyne-fatjar.jar",
  assemblyShadeRules in assembly := Seq(
    ShadeRule.rename("org.json4s.**" -> "shaded.json4s.@1").inAll
  )
)

lazy val root = (project in file("."))
  .settings(commonSettings)
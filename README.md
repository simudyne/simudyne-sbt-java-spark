# Simudyne SBT Java Skeleton

This repository serves as a simple working example of using Simudyne from Java using the SBT build tool.

You need to have SBT installed, or provided through an IDE such as Eclipse or IntelliJ. Because Simudyne jars are
served from an authenticated artifact repository, you will need to provide information to SBT on where this repository
is located and your credentials.

These settings are located in `.credentials` file.

### Running the project
* Update .credentials with your simudyne credentials
* Open a terminal at the project directory, and run `sbt run`

## Running the project distributed with Spark

- Install Spark

- Start Spark standalone master server : `./sbin/start-master.sh`

- Check the spark Master URL at localhost:8080/ (make sure the console is running on a different port),
you can set the console host and port in your `Main` class with :
```java
Server.setHostName("0.0.0.0");
Server.setPort(8081);
```

- Start one or several slaves : `./sbin/start-slave.sh <sparkMasterURL>`

- Add the following dependency in your `build.sbt` file in `libraryDependencies`: 
```sbtshell
"simudyne" %% "simudyne-core-abm-spark" % simudyneVersion
```

- Add the following lines in the `setup()` method of your model to register your agents, messages and setup your backend :
```java
grid.registerAgentTypes(Cell.class);
grid.registerMessageTypes(Messages.Start.class, Messages.Neighbour.class);

grid.getConfig().setBackend(new SparkAgentSystemBackend().setMasterURL("<sparkMasterURL>"));
```
Default Spark Master URL is set to `local[*]`

- Remove `@Variable` annotation of your AgentSystem : for now agent reporting is not supported in distributed mode

- Add the SBT assembly plugin by adding the following line in `project/assembly.sbt` :
```sbtshell
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.6")
```

- Add the following configuration to your `build.sbt`, it will be used to build your project and all its dependencies into a single Jar file, named `simudyne-fatjar.jar` here
```sbtshell
lazy val commonSettings = Seq(
  test in assembly := {},
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs@_*) => MergeStrategy.discard
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
```

- Build your fatJar file with `sbt assembly`, it will be in `target/scala-2.11`

- Submit the fatJar using the url from last step: `spark-submit --class Main --master <sparkMasterURL> --deploy-mode client simudyne-fatjar.jar`
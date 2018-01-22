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

- Set the Spark master URL in the `setup()` method of your model 

- Build your fatJar file with `sbt assembly`, it will be in `target/scala-2.11`

- Submit the fatJar using the url from last step: `spark-submit --class Main --master <sparkMasterURL> --deploy-mode client simudyne-fatjar.jar`
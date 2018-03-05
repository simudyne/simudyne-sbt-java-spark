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
you can set the console host and port in your `simudyneSDK.properties` file in the `NEXUS-SERVER` section.

- Start one or several slaves : `./sbin/start-slave.sh <sparkMasterURL>`

- Build your fatJar file with `sbt assembly`, it will be in `target/scala-2.11`

- Add `core-abm.backend-implementation=simudyne.core.graph.spark.SparkGraphBackend` to SimuydneSDK.properties file

- Use the SimudyneSDK.properties file to set the master URL and other settings

- Upload your fatJar file and simudyneSDK.properties file to your master node.

- SSH into your master node and then submit the fatJar using the url from last step:
```text
spark2-submit --class Main --master <sparkMasterURL>  --deploy-mode client --num-executors 30 --executor-cores 5 --executor-memory 30G --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC" --files simudyneSDK.properties name-of-the-fat-jar.jar
```
- You should set `--num-executors`,  `--executor-cores`,  `--executor-memory` parameters according your own cluster resources.
Useful resource : [http://blog.cloudera.com/blog/2015/03/how-to-tune-your-apache-spark-jobs-part-2/](http://blog.cloudera.com/blog/2015/03/how-to-tune-your-apache-spark-jobs-part-2/)


## Running multiple runs distributed with spark

- Add `core-runner.runner-backend = simudyne.core.runner.spark.SparkRunnerBackend` to SimuydneSDK.properties file

- Run project on Spark as above

- Use console multirun setting to run model multiple times
# Spark-Process-Video-Data
This project provide a framework to process video data using spark, and the main program is written by scala. This program is compiled and ran on ubuntu 13.10 or ubuntu 14.04.

## Compile the project
1. `sbt clean assembly`, then it will generate `target/scala-2.11/my-project-assembly-1.0.jar`

## Run the project
1.  to run spark client model, do `./package-run.sh`  
2.  to run spark cluster model, do `./package-run-cluster.sh`

## Notice
1. custom_spark_lib/hadoopCustomInputFormat.jar, the file is compiled by [jinputFormat.sh](Hadoop-Process-Video-Data/blob/master/Hadoop_streaming_process_video_source_code/jinputFormat.sh)  
2. You should put `libInputFormatSo.so` into Hadoop's `java.library.path`, the Corresponding default path is `$HADOOP_HOME/lib/native`.  

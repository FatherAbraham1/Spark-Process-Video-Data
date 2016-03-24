#!/bin/bash

# cluster model

if [ -n "$1" ] && [ $1 -eq 0 ]; then
 sbt clean package
fi

hadoop fs -rm -r hdfs://HBCluster-1:9000/output

yourAppClass=com.lasclocker.java.SparkGopProcess
inPath=hdfs://HBCluster-1:9000/LPR
outPath=hdfs://HBCluster-1:9000/output
minPartitionNum=4 #视频文件被切成partition的个数
sparkURL=spark://10.37.0.107:7077
hdfsFile=hdfs://10.37.0.107:9000/user/root
ldLib=/opt/hadoop/lib #添加动态库libInputFormatSo.so

spark-submit \
 --class ${yourAppClass} \
 --master ${sparkURL} \
 --driver-library-path $ldLib \
 --deploy-mode cluster \
 $hdfsFile/my-project-assembly-1.0.jar $inPath $outPath $minPartitionNum

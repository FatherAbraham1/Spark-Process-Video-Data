#!/bin/bash

# Client model

if [ -n "$1" ] && [ $1 -eq 0 ]; then
 sbt clean package
fi

hadoop fs -rm -r hdfs://HBCluster-1:9000/output

yourAppClass=com.lasclocker.java.SparkGopProcess
inPath=hdfs://HBCluster-1:9000/LPR #此目录下有car3.mp4文件
outPath=hdfs://HBCluster-1:9000/output #最终车牌图片生成的目录
minPartitionNum=4 #视频文件被切成partition的个数
sparkURL=spark://10.37.0.107:7077
ldLib=/opt/hadoop/lib #添加动态库libInputFormatSo.so

spark-submit \
 --class ${yourAppClass} \
 --master ${sparkURL} \
 --driver-library-path $ldLib \
 target/scala-2.11/my-project-assembly-1.0.jar $inPath $outPath $minPartitionNum

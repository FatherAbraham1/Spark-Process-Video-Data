#!/bin/bash

#批量注释
:<< eof
解析HDFS上的含有车牌的sequencefile
input 表示HDFS上sequencefile的路径；
output 表示本地目录路径
eof




input=hdfs://10.37.0.107:9000/output
output=/tmp/image
hadoop jar ImageToBin_local.jar imageChange.ImageToBin_local $input $output

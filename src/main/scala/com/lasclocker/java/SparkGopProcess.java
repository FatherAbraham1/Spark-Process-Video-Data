package com.lasclocker.java;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import scala.Tuple2;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;

import org.apache.hadoop.streaming.ContentInputFormat;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请参考word文档第十三部分第61条 Spark LPR 介绍.
 */
public class SparkGopProcess {
    private static int minPartitionNum = 2; //默认的视频分块的个数, 会根据输入参数args[2]来确定.
    private static BytesWritable tmpKey = new BytesWritable();
    private static BytesWritable byteTmp = new BytesWritable();
    private static Map<String, byte[]> jniResult = new ConcurrentHashMap<String, byte[]>(); //此处一定要用ConcurrentHashMap, 不然后面的并发会出现error.
    private static Map<String, byte[]> startMap = new ConcurrentHashMap<String, byte[]>();

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("error input output Path.");
            System.out.print("args 0 : input path; args 1 : output path; args 2 : minPartitionNum;\n");
            System.exit(1);
        }
        minPartitionNum = Integer.parseInt(args[2]);
        SparkConf sparkConf = new SparkConf().setAppName("Java SparkGopProcess");
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);

        Configuration conf = new Configuration();
        JobConf jobconf = new JobConf(conf, SparkGopProcess.class);
        FileInputFormat.addInputPath(jobconf, new Path(args[0])); //设置文件的输入路径.

        //由HDFS上的文件, 利用自定义的输入格式,由hadoopRDD接口,来构造RDD.
        JavaPairRDD<NullWritable, BytesWritable> gopRDD = jsc.hadoopRDD(jobconf, ContentInputFormat.class, NullWritable.class, BytesWritable.class, minPartitionNum);

        //利用flatMapToPair函数,输入一个GOP,输出一组车牌图片.
        JavaPairRDD<BytesWritable, BytesWritable> plates = gopRDD.flatMapToPair(new PairFlatMapFunction<Tuple2<NullWritable, BytesWritable>, BytesWritable, BytesWritable>() {
            @Override
            public Iterable<Tuple2<BytesWritable, BytesWritable>> call(Tuple2<NullWritable, BytesWritable> nullWritableBytesWritableTuple2) throws Exception {
                BytesWritable gopInfo = nullWritableBytesWritableTuple2._2();
                byte[] gopInfoArray = gopInfo.getBytes();
                jniResult = getJniResult(gopInfoArray); //此处调用外部的JNI函数,注意此处的JNI函数是自己伪造的,只是一个demo,只是验证Spark能进行视频处理.
                List<Tuple2<BytesWritable, BytesWritable>> result = new ArrayList<Tuple2<BytesWritable, BytesWritable>>();
                for (Map.Entry<String, byte[]> entry : jniResult.entrySet()) {
                    byteTmp.set(entry.getValue(), 0, entry.getValue().length);
                    tmpKey.set(entry.getKey().getBytes(), 0, entry.getKey().getBytes().length);
                    Tuple2<BytesWritable, BytesWritable> tu2 = new Tuple2<BytesWritable, BytesWritable>(tmpKey, byteTmp);
                    result.add(tu2);
                }
                return result; //构造一个新的RDD
            }
        });

        //利用saveAsHadoopFile把图片结果保存到args[1]路径下.
        plates.saveAsHadoopFile(args[1], BytesWritable.class, BytesWritable.class, SequenceFileOutputFormat.class);
    }

    //模拟车牌识别系统程序, 只是模拟其输出输入结果.
    public static Map<String, byte[]> getJniResult(byte[] gopInfoArray) {
        startMap.clear();
        byte[] gopInfoArray_2 = new byte[2*gopInfoArray.length];
        System.arraycopy(gopInfoArray, 0, gopInfoArray_2, 0, gopInfoArray.length);
        System.arraycopy(gopInfoArray, 0, gopInfoArray_2, gopInfoArray.length, gopInfoArray.length);
        startMap.put(String.valueOf(Math.random() * 10000), gopInfoArray);
        startMap.put(String.valueOf(Math.random() * 10000), gopInfoArray_2);
        return  startMap;
    }
}


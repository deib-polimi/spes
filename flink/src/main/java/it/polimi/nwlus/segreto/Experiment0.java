package it.polimi.nwlus.segreto;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.concurrent.TimeUnit;

/**
 * Time-based Window Experiment 0
 *
 * It uses the minimal possible configuration of the timestamp extractor
 * <p/>
 * Input:
 * InStream(Time, Val) = {(10,10),(11,20),(12,30),(13,40),(14,50),(15,60),(16,70), ...}
 * <p/>
 * Query:
 * Continuously compute the average value of the tuples
 * in the input stream using a time-based tumbling window of 3 seconds.
 *
 *
 */
public class Experiment0 implements Experiment {
    public void main(StreamExecutionEnvironment env, String ip, int port) throws Exception {

        DataStreamSource<String> socketStream = env.socketTextStream(ip, port);


        DataStream<Tuple2<Integer, Integer>> input = socketStream.flatMap(new FlatMapFunction<String, Tuple2<Integer, Integer>>() {
            @Override
            public void flatMap(String s, Collector<Tuple2<Integer, Integer>> out) throws Exception {
                out.collect(Utils.parseTuple(s));
            }
        });


        input
                .assignTimestamps(Utils.getAscendingExtractor())
                .timeWindowAll(Time.of(3, TimeUnit.SECONDS))
                .apply(new AllWindowFunction<Tuple2<Integer, Integer>, String, TimeWindow>() {
                    @Override
                    public void apply(
                            TimeWindow window,
                            Iterable<Tuple2<Integer, Integer>> values,
                            Collector<String> out) throws Exception {
                        String res = Utils.windowToString(window, values);
                        out.collect(res);
                    }
                })
                .print();
    }



    @Override
    public String getName() {
        return "Esperimento SEGRETO 0";
    }
}
package org.vox.study.storm;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by cielleech on 16/8/22.
 */
public class WordCounter extends BaseRichBolt {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6400983097928642193L;
	private OutputCollector collector;
    private Map<String, AtomicInteger> counterMap;

    @Override
    public void prepare(@SuppressWarnings("rawtypes") Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        counterMap = new HashMap<>();
    }

    @Override
    public void execute(Tuple input) {
        String word = input.getString(0);
        int count = input.getInteger(1);
        System.out.println("RECV[splitter -> counter] " + word + ":" + count);
        AtomicInteger ai = counterMap.get(word);
        if (ai == null) {
            ai = new AtomicInteger();
            counterMap.put(word, ai);
        }

        ai.addAndGet(count);
        collector.ack(input);
        System.out.println("CHECK statistics map" + this.counterMap);
    }

    @Override
    public void cleanup() {
        System.out.println("The final result:");
        Iterator<Map.Entry<String, AtomicInteger>> iter = this.counterMap.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry<String, AtomicInteger> entry = iter.next();
            System.out.println(entry.getKey() + "\t:\t" + entry.getValue().get());
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word", "count"));
    }
}

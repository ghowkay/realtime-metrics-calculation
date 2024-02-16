package co.numina.traveler.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.streams.kstream.Grouped;

import co.numina.traveler.streams.config.ElasticsearchConfig;
import co.numina.traveler.streams.libraries.Elasticsearch;
import co.numina.traveler.streams.model.CountMetric;
import co.numina.traveler.streams.model.Movement;

import java.io.IOException;
import java.time.Duration;


import java.util.Properties;
import java.util.UUID;

public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);
    

    static String appId = "numina_tracker_app";
    static String kafkaInputTopic = "movements";
    static String brokerServers = System.getenv("KAFKA_HOST");
    static String esIndex = "traveler_count_metrics";
    
    static ObjectMapper mapper = new ObjectMapper();

   

    public static void main(final String[] args) throws Exception {

        Elasticsearch elasticsearch = new Elasticsearch( new ElasticsearchConfig().esClient(), new ElasticsearchConfig().bulkProcessor());

        //create index and mapping
        elasticsearch.createIndex(esIndex);
        elasticsearch.createMapping(esIndex);

        // configure kafka stream properties
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, appId);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, brokerServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);

        StreamsBuilder builder = new StreamsBuilder();

        // connect to movements input topic
        KStream<String, String> inputStream = builder.stream(kafkaInputTopic);

  
         //{"position":["4.769070843612555","34.44355380192485"],"timestamp":"2020-07-23T19:54:20.362454","sensor_id":64,"traveler_id":"cb3cc3d7-35e0-4956-9bca-d9368342d337","traveler_type":"pedestrian"}
        final KTable<Windowed<String>, Long> counts = inputStream.filter((key, value) -> value != null)
                .selectKey((key, value) -> {
                    Movement movement = new Movement();
                   
                    try {
                        movement = mapper.readValue(value, Movement.class);
                      //  System.out.println(toJsonString(movement));
                    } catch (JsonParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (JsonMappingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
 
       return getGroupByCondition(movement);
     })
     .groupByKey(Grouped.with(Serdes.String(), Serdes.String()))
     .windowedBy(TimeWindows.of(Duration.ofMinutes(1)))  // 1 Minute in ms
     .count();



     counts.toStream()
     .foreach((key, value) -> {
         
        CountMetric countMetric = new CountMetric();
        countMetric.setPath(key.key());
        countMetric.setSensorId(Integer.parseInt(key.key().split("/")[1]));
        countMetric.setTravelerType(key.key().split("/")[2]);
        countMetric.setTimestamp(key.window().start());
        countMetric.setCount(value);

       //bulk insert into elasticsearch
        elasticsearch.bulkIndexDocument(esIndex, "count",UUID.randomUUID().toString()  , toJsonString(countMetric));



        
    
     });


        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }




    private static String getGroupByCondition(Movement movement) {
        return "/"+movement.getSensorId()+"/"+movement.getTravelerType();
 }
  
    public static String toJsonString(Object response) {

        String val = "";

        try {
            val = mapper.writeValueAsString(response);
        } catch (Exception e) {

        e.printStackTrace();

        }

        return val;
    }




}
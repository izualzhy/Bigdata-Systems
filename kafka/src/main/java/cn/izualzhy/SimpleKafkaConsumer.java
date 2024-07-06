package cn.izualzhy;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class SimpleKafkaConsumer {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(SimpleKafkaConsumer.class);

    private static void listTopics(KafkaConsumer<String, String> consumer) {
        Map<String, List<PartitionInfo>> topicInfoMap = consumer.listTopics();

        for (Map.Entry<String, List<PartitionInfo>> topicInfo : topicInfoMap.entrySet()) {
            String topic = topicInfo.getKey();
            List<PartitionInfo> partitionInfoList = topicInfo.getValue();

            System.out.println("topic : " + topic + "\t partitionInfo:");
            for (PartitionInfo info : partitionInfoList) {
                System.out.println("\t" + info);
            }
        }
    }

    private static void listPartitions(KafkaConsumer<String, String> consumer, String fixedTopic) {
        List<PartitionInfo> partitionInfoList = consumer.partitionsFor(fixedTopic);
        for (PartitionInfo partitionInfo : partitionInfoList) {
            System.out.println("topic : " + fixedTopic + "\t partitionInfo:");
            System.out.println("\t" + partitionInfo);
        }
    }
    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", args[0]);  // Kafka 服务器地址
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("group.id", args[1]);  // 消费者群组ID
        properties.put("auto.offset.reset", "earliest");

        String topic = args[2];

        // 创建消费者
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

//        consume(args, consumer);
//        listTopics(consumer);
        for (int i = 0; i< 10; ++i) {
            logger.info("i : {} ======================================================================", i);
            System.out.println("======================================================================");
            try {
                listPartitions(consumer, topic);
            } catch (Exception e) {
                logger.info("listPartitions exception", e);
                throw e;
            }
        }
    }

    private static void consume(String[] args, KafkaConsumer<String, String> consumer) {
        // 订阅主题
        consumer.subscribe(Arrays.asList(args[2]));  // 可以订阅多个主题

        try {
            while (true) {
                // 轮询获取数据
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                    TimeUnit.MINUTES.sleep(10);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            consumer.close();  // 退出前关闭消费者
        }
    }
}

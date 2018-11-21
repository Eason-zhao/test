package hadoop.kafkaOperate;

import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.producer.Producer;
import kafka.serializer.StringEncoder;
import kafka.server.ConfigType;
import kafka.utils.ZkUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.security.JaasUtils;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.zookeeper.ZKUtil;

import javax.security.auth.login.Configuration;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IDEA
 * author:ZhaoYiShun
 * Date:2018/11/20
 * Time:15:44
 */
public class OperateKafka {
    public final static String URL="192.168.2.16:2181";
    public final static String NAME="test_topic656";

    public static void main(String[] args) {
        OperateKafka ok=new OperateKafka();
        ok.queryTopic();

    }
    //创建主题
    public void createTopic(){
        ZkUtils zkUtil=ZkUtils.apply(URL,30000,30000,JaasUtils.isZkSecurityEnabled());
        //创建一个单分区副本名为T1的topic
        AdminUtils.createTopic(zkUtil,NAME,1,1,new Properties(),RackAwareMode.Enforced$.MODULE$);
        zkUtil.close();
        System.out.println("创建成功");
    }
    //删除主题（未彻底删除）
    public  void delTopic(){
        ZkUtils zkUtils=ZkUtils.apply(URL,30000,30000,JaasUtils.isZkSecurityEnabled());
        AdminUtils.deleteTopic(zkUtils,NAME);
        zkUtils.close();
        System.out.println("删除成功");
    }
    //修改主题
    public void editTopic(){
        ZkUtils zkUtils=ZkUtils.apply(URL,30000,30000,JaasUtils.isZkSecurityEnabled());
        Properties props=AdminUtils.fetchEntityConfig(zkUtils,ConfigType.Topic(),NAME);
        //增加Topic级别属性
        props.put("min.cleanable.dirty.ratio","0.3");
        //删除级别
        props.remove("max.message.bytes");
        //修改Topic的‘test’属性
        AdminUtils.changeTopicConfig(zkUtils,NAME,props);
        zkUtils.close();
    }
    //读取主题
    public void queryTopic(){
        ZkUtils zkUtils=ZkUtils.apply(URL,30000,30000,JaasUtils.isZkSecurityEnabled());
        // 获取topic 'test'的topic属性属性
        Properties props=AdminUtils.fetchEntityConfig(zkUtils,ConfigType.Topic(),NAME);
        //查询Topic的级别属性
        Iterator it=props.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry entry= (Map.Entry) it.next();
            Object key=entry.getKey();
            Object value=entry.getValue();
            System.out.println("key:"+key+"="+"value:"+value);
        }
        zkUtils.close();
    }
}

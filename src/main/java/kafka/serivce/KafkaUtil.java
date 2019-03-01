package kafka.serivce;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.I0Itec.zkclient.ZkClient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import clojure.string__init;
import kafka.javaapi.OffsetRequest;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.bean.Borker;
import kafka.bean.PartationInfo;
import kafka.bean.TopicMessage;
import kafka.cluster.Broker;
import kafka.common.TopicAndPartition;
import kafka.javaapi.OffsetResponse;
import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.TopicMetadata;
import kafka.javaapi.TopicMetadataRequest;
import kafka.javaapi.TopicMetadataResponse;
import kafka.javaapi.consumer.SimpleConsumer;

 
import kafka.utils.ZKStringSerializer$;
import scala.collection.generic.BitOperations.Int;

/**
 * KAFKA 工具类
 * @author gongp
 * 
 * 1.获取topic列表
 * 2.获取消费者偏移量
 *
 *
 *
 */


public class KafkaUtil {
	
	private static final String KAFKA_TOPIC_PATH ="/brokers/topics";
	private static final String KAFKA_BROKER_PATH ="/brokers/ids";
	
	
	public static ZkClient zkClient = null;
	
	private static List<String > zkList= new ArrayList<>();
	
	
	static {
		String zkStr="node3:2181";
		zkClient = new ZkClient(zkStr, 20000, 70000,ZKStringSerializer$.MODULE$);
		System.out.println("connet to "+zkStr +" success");
	}

	public static void main(String[] args) {
		List<String > topicList = getTopics(zkList);
		System.out.println("1.当前共有一下topic：");
		for (String topic : topicList) {
			System.out.println(topic);
		}
		Map<Integer, Borker> hostMap = new HashMap<>();
		System.out.println("2.broker主机状况");
		getBrokers(hostMap);
		System.out.println("3.查看topic中 partion的分布，及最大消息量");
		Map<String, TopicMessage> map = new HashMap<>();
		insertTopicMessage(topicList,map,hostMap);
		Set<Entry<String, TopicMessage>> entrySet = map.entrySet();
		for (Entry<String, TopicMessage> entry : entrySet) {
			String topicName = entry.getKey();
			TopicMessage topicMessage = entry.getValue();
			int partionCount = topicMessage.getPartionCount();
			long currentMsg = topicMessage.getCurrentMsg();
			List<PartationInfo> partationInfos = topicMessage.getPartationInfos();
			System.out.println("topicName is : "+topicName + " partition count is : "+ partionCount + " msg size is :"+ currentMsg);
			for (PartationInfo info : partationInfos) {
//				System.out.println("parttion detail is :" + info);
			}
			
		}
		//查看消费者组情况
		System.out.println("4.查看消费者情况");
		insertConsumerDetail();
	}
		

	private static long getLogSize(String topic,String node,int port,int partition) {
		SimpleConsumer simpleConsumer = new SimpleConsumer(node, port, 10000, 64*1024, "client_"+node);
		TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partition);
		Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();
		
		requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(kafka.api.OffsetRequest.LatestTime(), 1));
		OffsetRequest request = new OffsetRequest(requestInfo, kafka.api.OffsetRequest.CurrentVersion(), simpleConsumer.clientId());
		long[] offsets = simpleConsumer.getOffsetsBefore(request).offsets(topic, partition);
//		for (long l : offsets) {
//			System.out.println(l);
//		}
		if(offsets.length>0) {
			return offsets[0];
		}
		return -1L;
	}


	private static void insertConsumerDetail() {
		String consumerPath="/consumers";
		List<String> groups = zkClient.getChildren(consumerPath);
		//消费组
		for (String group : groups) {
			String groupPath=consumerPath+"/"+group+"/offsets";
			List<String> groupTopics = zkClient.getChildren(groupPath);
			//topic
			for (String groupTopic : groupTopics) {
				String groupTopicPath =groupPath+"/"+groupTopic;
				List<String> partitions = zkClient.getChildren(groupTopicPath);
				//分区数
				for (String partition : partitions) {
					String groupTopicPartitionPath = groupTopicPath+"/"+partition;
					
					Long data = Long.parseLong(zkClient.readData(groupTopicPartitionPath).toString());
					System.out.println(groupTopicPartitionPath+ "偏移量为："+data);
				}
			}
			
		}
		
	}


	private static void insertTopicMessage(List<String> topicList, Map<String, TopicMessage> map, Map<Integer, Borker> hostMap) {
		for (String topic : topicList) {
			TopicMessage topicMessage = new TopicMessage();
			topicMessage.setTopic(topic);
			
			String partitionPath=KAFKA_TOPIC_PATH+"/"+topic+"/partitions";
			List<String> partitionid = zkClient.getChildren(partitionPath);
			//分区个数
			topicMessage.setPartionCount(partitionid.size());
			List<PartationInfo> partations = new ArrayList<>();
			
			long currentMessage = 0L;
			
			for (String id : partitionid) {
				PartationInfo partationInfo = new PartationInfo();
				String partitionDetail = partitionPath+"/"+id+"/state";
				Object readData = zkClient.readData(partitionDetail);
				JSONObject jsonObject = JSONObject.parseObject(readData.toString());
				partationInfo.setTopic(topic);
				int leader = jsonObject.getIntValue("leader");
				
				partationInfo.setLeaderID(leader);
				partationInfo.setLeaderEpoch(jsonObject.getIntValue("leader_epoch"));
				partationInfo.setControllerEpoch(jsonObject.getIntValue("controller_epoch"));
				partationInfo.setIsrs(jsonObject.getString("isr"));
				partations.add(partationInfo);
				Borker borker = hostMap.get(leader);
				long logSize = getLogSize(topic, borker.getHost(), borker.getPort(), Integer.parseInt(id));
				//当前log值
				partationInfo.setLogSize(logSize);
				currentMessage=currentMessage+logSize;
			}
			topicMessage.setCurrentMsg(currentMessage);
			topicMessage.setPartationInfos(partations);
			map.put(topic,topicMessage);
		}
	}

	private static void getBrokers(Map<Integer,Borker> map) {
		List<Borker> brokers =new ArrayList<>();
		List<String> children = zkClient.getChildren(KAFKA_BROKER_PATH);
		for (String child : children) {
			Borker borker = new Borker();
			int id = Integer.parseInt(child);
			borker.setId(id);
			String tmpPath = KAFKA_BROKER_PATH+"/"+child;
			Object data = zkClient.readData(tmpPath);
			JSONObject jsonObject = JSON.parseObject(data.toString());
			String endpoints = jsonObject.getString("endpoints");
			String host =jsonObject.getString("host");
			borker.setEndpoints(endpoints);
			borker.setHost(host);
			borker.setPort(jsonObject.getIntValue("port"));
			borker.setJmx_port(jsonObject.getIntValue("jmx_port"));
			borker.setStart_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(jsonObject.getLong("timestamp"))));
			brokers.add(borker);
			map.put(id, borker);
		}
	}

	private static List<String> getTopics(List<String> zkList2) {
		List<String> children = zkClient.getChildren(KAFKA_TOPIC_PATH);
		return children;
	}

	
	
	
}

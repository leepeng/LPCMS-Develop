package cn.com.leepeng.plateform.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

public class UserDao {
	public static void main(String[] args) {
		String[] virtualBox = { "WebSocket", "JSONP", "Redis", "MongoDB", "Cassandra", "RabbitMQ", "ActiveMQ", "Lucene",
				"Solr", "ElasticSearch", "Hadoop", "HDFS", "HIVE", "Kafka", "PIG", "Mahout", "HBase", "Spark", "Guava",
				"Protobuf", "Avro", "Thrift", "Motan", "Docker", "DynamoDB", "Scala", "Groovy", "SpringBoot" };

		// 保存学员默认技术分配信息
		Map<String, Object> employeeMap = new HashMap<String, Object>();
		employeeMap.put("吕鹏飞", "ElasticSearch&Redis");
		employeeMap.put("丁虎", "Redis&SpringBoot");
		employeeMap.put("梁秀斗", "Hadoop&HDFS");
		employeeMap.put("李文鹏", "Docker&Kafka");
		employeeMap.put("苗恒飞", "Lucene&Solr");
		employeeMap.put("佘昊", "Solr&Redis");
		employeeMap.put("杜世阳", "ActiveMQ&Hadoop");
		employeeMap.put("刘翩", "SpringBoot&ActiveMQ");
		employeeMap.put("史建智", "Docker&Lucene");
		employeeMap.put("王帅", "Cassandra&Spark");
		employeeMap.put("张昌昌", "SpringBoot&MongoDB");
		employeeMap.put("王腾飞", "SpringBoot&Spark");
		employeeMap.put("杨小平", "WebSocket&RabbitMQ");

		// 统计每门技术的所选的人
		Map<String, String> techMap = new HashMap<String, String>();

		// 学员实际的课题Map
		Map<String, String> relaTaskMap = new HashMap<String, String>();

		// 保存实际学员Map的key ，数组的方式
		Set<String> keySet = employeeMap.keySet();
		String[] relaMapKeys = new String[employeeMap.size()];
		int num = 0;
		for (String str : keySet) {
			relaMapKeys[num] = str;
			num++;
		}

		// 初始化relaTaskMap 数据，value初始化为空串
		for (int i = 0; i < employeeMap.size(); i++) {
			relaTaskMap.put(relaMapKeys[i], "");
		}

		// 保存没人选的技术
		List<String> noPeopleChooseList = new ArrayList<String>();
		Map<String, String> noChooseMap = new HashMap<String, String>();
		// 保存随机过后已经满足两个的名单
		Map<String, String> finalTaskMap = new HashMap<String, String>();

		// 检索每个学员当前所选的技术
		int count = 0;
		for (int j = 0; j < virtualBox.length; j++) {
			String tech = virtualBox[j];
			// 遍历学员默认选择的技术
			for (Map.Entry<String, Object> entry : employeeMap.entrySet()) {
				String obj = (String) entry.getValue();
				String[] technologys = obj.split("&");
				for (int i = 0; i < technologys.length; i++) {
					// 知识库与学员默认选择的技术进行匹配
					if (tech.equals(technologys[i])) {
						count++;
					}
				}

			}

			// count:0 保存到list中
			if (count == 0) {
				noPeopleChooseList.add(tech);
				noChooseMap.put(tech, tech);
			}

			// count:1 保存到relaTaskMap中
			if (count == 1) {
				for (Map.Entry<String, Object> entry : employeeMap.entrySet()) {
					String obj = (String) entry.getValue();
					String[] technologys = obj.split("&");
					for (int i = 0; i < technologys.length; i++) {
						if (tech.equals(technologys[i])) {
							if ("".equals(relaTaskMap.get(entry.getKey()))) {
								relaTaskMap.put(entry.getKey(), tech);
							} else {
								String value = relaTaskMap.get(entry.getKey());
								relaTaskMap.put(entry.getKey(), value + "&" + tech);
							}
						}
					}
				}
			}

			// count>1 分别将这些选择相同技术的人员各自保存到一起
			String name = "";
			if (count > 1) {
				// 检索哪些人同时学了这门技术
				for (Map.Entry<String, Object> entry : employeeMap.entrySet()) {
					String obj = (String) entry.getValue();
					String[] technologys = obj.split("&");
					for (int i = 0; i < technologys.length; i++) {
						if (tech.equals(technologys[i])) {
							name = name + entry.getKey() + ",";
						}
					}
				}
				techMap.put(tech, name);
			}
			count = 0;
		}

		for (Map.Entry<String, String> entry : relaTaskMap.entrySet()) {
			String tech = entry.getValue();
			String[] techs = tech.split("&");
			if (techs.length == 2) {
				finalTaskMap.put(entry.getKey(), tech);
			}
		}

		Random random = new Random();// 获取随机数
		// ===处理被选多次的随机分配技术===
		for (Map.Entry<String, String> entry : techMap.entrySet()) {
			String str = entry.getValue();
			String[] names = str.split(",");
			int result = random.nextInt(names.length);
			// 获取随机姓名
			String name = names[result];
			String val = relaTaskMap.get(name);
			if ("".equals(val)) {
				relaTaskMap.put(name, entry.getKey());
			} else {
				relaTaskMap.put(name, val + "&" + entry.getKey());
				finalTaskMap.put(name, val + "&" + entry.getKey());
				relaTaskMap.remove(name);
			}
		}

		for (Map.Entry<String, String> entry : finalTaskMap.entrySet()) {
			String name = entry.getKey();
			relaTaskMap.remove(name);
		}
		// 将未被选的技术随机分配给0技术或只有一个技术的人员
		for (Map.Entry<String, String> entry : relaTaskMap.entrySet()) {
			String name = entry.getKey();
			String relaTech = entry.getValue();
			int random1 = 0;
			int random2 = 0;

			// 获取随机的技术，然后分配给员工
			if ("".equals(relaTech)) {
				if (noPeopleChooseList.size() > 1) {
					random1 = random.nextInt(noPeopleChooseList.size());
					random2 = random.nextInt(noPeopleChooseList.size());
					if (random1 == random2) {
						if (random1 == noPeopleChooseList.size() - 1) {
							random2 = random1 - 1;
						} else {
							random2 = random1 + 1;
						}
					}
				}
				String tech1 = noPeopleChooseList.get(random1);
				String tech2 = noPeopleChooseList.get(random2);
				relaTaskMap.put(name, tech1 + "&" + tech2);
				finalTaskMap.put(name, tech1 + "&" + tech2);
				noPeopleChooseList.remove(tech1);
				noPeopleChooseList.remove(tech2);
			} else {
				random1 = random.nextInt(noPeopleChooseList.size());
				String tech1 = noPeopleChooseList.get(random1);
				relaTaskMap.put(name, relaTech + "&" + tech1);
				finalTaskMap.put(name, relaTech + "&" + tech1);
				noPeopleChooseList.remove(tech1);
			}
		}

		for (Map.Entry<String, String> entry : finalTaskMap.entrySet()) {
			System.out.println("技术员：" + entry.getKey() + "--》所选技术： " + entry.getValue());
		}
		System.out.print("未选择的技术有：");
		for (int i = 0; i < noPeopleChooseList.size(); i++) {
			System.out.print(" " + noPeopleChooseList.get(i) + ", ");
		}
	}
}

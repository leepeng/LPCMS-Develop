/*
 * Copyright (c) 2007, 2017, ZVING and/or its affiliates. All rights reserved.
 * ZVING PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.com.leepeng.plateform.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

/**
 * @author LEEPENG
 * @email lp@zving.com
 * @date 11st Jun,2017
 * 
 */
public class Test {
	// @formatter:off
	// 如果一项技术只有一个学员选择，则直接为该学员指定该技术
	// 如果一项技术有多个学员选择，则在选择了该项技术的学员中随机指定一位学习该技术
	// 如果一个学员被指定的技术不足两项，则在未被指定的技术中随机指定一项或两项给该学员，以凑足两项但不能多于两项。 每个学员被指定的技术不能重复
	// 需要输出最终的技术指定清单
	// 需要输出未被指定给学员的技术清单。
	// @formatter:on
	private static final Random random = new Random();
	// 初始化技术
	public static final String[] techArr = new String[] { "VirtualBox", "Vagrant", "WebSocket", "JSONP", "Redis",
			"MongoDB", "Cassandra", "RabbitMQ", "ActiveMQ", "Kafka", "Lucene", "Solr", "ElasticSearch", "Hadoop",
			"HDFS", "HIVE", "PIG", "Mahout", "HBase", "Spark", "Guava", "Protobuf", "Avro", "Thrift", "Motan", "Docker",
			"DynamoDB", "Scala", "Groovy", "SpringBoot" };

	/**
	 * 读取default-tech.data文件，初始化学员选择的默认技术
	 * 
	 * @return 默认学员选择的技术
	 */
	private static Map<String, String> initStudentSelectedTech() {
		Map<String, String> defaultTechMap = null;
		BufferedReader bufferedReader = null;
		try {
			defaultTechMap = new HashMap<>();
			bufferedReader = new BufferedReader(new FileReader(new File("D:" + File.separator + "default-tech.data")));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				String[] dft = line.split(" ");
				if (dft != null && dft.length >= 3) {
					defaultTechMap.put(dft[0], dft[1] + "$" + dft[2]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return defaultTechMap;
	}

	public static void main(String[] args) {
		// 初始化默认同学选择的技术
		Map<String, String> initStudentSelectedTech = initStudentSelectedTech();
		// 保存每门技术的所选的人
		Map<String, String> techMap = new HashMap<String, String>();
		// 保存没有人选择的技术名单
		List<String> noChooseTechList = new LinkedList<String>();
		// 保存随机分配技术后经满足条件的Map
		Map<String, String> lastTechMap = new LinkedHashMap<String, String>();
		// 保存学员实际的技术
		Map<String, String> reallyTech = new HashMap<String, String>();
		Set<String> defAllTechSet = initStudentSelectedTech.keySet();
		String[] reallyTemp = new String[initStudentSelectedTech.size()];
		// 初始化
		initReallyTech(initStudentSelectedTech, reallyTech, defAllTechSet, reallyTemp);
		// 处理基础技术数据，遍历默认同学所选择的技术与初始化技术比较
		dealBasicTech(initStudentSelectedTech, techMap, noChooseTechList, lastTechMap, reallyTech);
		// 开始随机处理分配
		dealwithAssignedTechnology(noChooseTechList, reallyTech, lastTechMap);
		// 打印最终清单
		System.out.println("最终的技术指定清单:");
		for (Map.Entry<String, String> tech : lastTechMap.entrySet()) {
			String[] techSp = tech.getValue().split("\\$");
			System.out.println(tech.getKey() + ":【" + techSp[0] + "," + techSp[1] + "】");
		}
		System.out.print("未被指定给学员的技术清单：【");
		for (int j = 0; j < noChooseTechList.size(); j++) {
			if (j != noChooseTechList.size() - 1) {
				System.out.print(noChooseTechList.get(j) + ",");
			} else {
				System.out.print(noChooseTechList.get(j));
			}
		}
		System.out.print("】");
	}

	/**
	 * 开始处理分配
	 * 
	 * @param noChooseTechList
	 * @param reallyTech
	 * @param lastTechMap
	 */
	private static void dealwithAssignedTechnology(List<String> noChooseTechList, Map<String, String> reallyTech,
			Map<String, String> lastTechMap) {
		for (Map.Entry<String, String> tech : lastTechMap.entrySet()) {
			String str = tech.getValue();
			String[] names = str.split(",");
			int len = getRandom(names.length);
			String name = names[len];
			String val = reallyTech.get(name);
			if ("".equals(val)) {
				reallyTech.put(name, tech.getKey());
			} else {
				lastTechMap.put(name, val + "$" + tech.getKey());
				reallyTech.put(name, val + "$" + tech.getKey());
				reallyTech.remove(name);
			}
		}
		for (Map.Entry<String, String> entry : lastTechMap.entrySet()) {
			String name = entry.getKey();
			reallyTech.remove(name);
		}
		for (Map.Entry<String, String> tc : reallyTech.entrySet()) {
			int rd1 = 0, rd2 = 0;
			String t1 = null, t2 = null;
			String name = tc.getKey();
			String relTech = tc.getValue();
			if ("".equals(relTech)) {
				if (noChooseTechList.size() > 1) {
					rd1 = getRandom(noChooseTechList.size());
					rd2 = getRandom(noChooseTechList.size());
					if (rd1 == rd2) {
						rd2 = (rd1 == noChooseTechList.size() - 1) ? (rd2 = rd1 - 1) : (rd2 = rd1 + 1);
					}
				}
				t1 = noChooseTechList.get(rd1);
				t2 = noChooseTechList.get(rd2);
				reallyTech.put(name, t1 + "$" + t2);
				lastTechMap.put(name, t1 + "$" + t2);
				noChooseTechList.remove(t1);
				noChooseTechList.remove(t2);
			} else {
				rd1 = getRandom(noChooseTechList.size());
				String tTemp = noChooseTechList.get(rd1);
				reallyTech.put(name, relTech + "$" + tTemp);
				lastTechMap.put(name, relTech + "$" + tTemp);
				noChooseTechList.remove(tTemp);
			}
		}

	}

	/**
	 * 获取随机值
	 * 
	 * @param num
	 * @return
	 */
	public static int getRandom(int num) {
		return random.nextInt(num);
	}

	private static void dealBasicTech(Map<String, String> initStudentSelectedTech, Map<String, String> techMap,
			List<String> noChooseTechList, Map<String, String> lastTechMap, Map<String, String> reallyTech) {
		// 计数器，累计使用
		int selectTechCount = 0;
		for (String tech : techArr) {
			for (Map.Entry<String, String> defTech : initStudentSelectedTech.entrySet()) {
				String[] tc = defTech.getValue().split("$");
				for (String tcTemp : tc) {
					if (tcTemp.equals(tech)) {
						selectTechCount++;
					}
				}
			}
			switch (selectTechCount) {
			case 0:
				noChooseTechList.add(tech);
			case 1:
				for (Map.Entry<String, String> techEntry : initStudentSelectedTech.entrySet()) {
					String obj = techEntry.getValue();
					String[] technologys = obj.split("$");
					for (String technology : technologys) {
						if (tech.equals(technology)) {
							if ("".equals(reallyTech.get(techEntry.getKey()))) {
								reallyTech.put(techEntry.getKey(), tech);
							} else {
								String value = reallyTech.get(techEntry.getKey());
								reallyTech.put(techEntry.getKey(), value + "$" + tech);
							}
						}
					}
				}
			}
			if (1 < selectTechCount) {
				String name = "";
				for (Entry<String, String> defTech : initStudentSelectedTech.entrySet()) {
					String tc = defTech.getValue();
					String[] techs = tc.split("$");
					for (int j = 0; j < techs.length; j++) {
						if (tech.equals(techs[j])) {
							name += defTech.getKey() + ",";
						}
					}
				}
				techMap.put(tech, name);
			}
			selectTechCount = 0;
		}
		for (Map.Entry<String, String> tcl : reallyTech.entrySet()) {
			String tech = tcl.getValue();
			String[] techs = tech.split("$");
			if (techs.length == 2) {
				lastTechMap.put(tcl.getKey(), tech);
			}
		}
	}

	/**
	 * 初始化数据
	 * 
	 * @param initStudentSelectedTech
	 * @param reallyTech
	 * @param defAllTechSet
	 * @param reallyTemp
	 */
	private static void initReallyTech(Map<String, String> initStudentSelectedTech, Map<String, String> reallyTech,
			Set<String> defAllTechSet, String[] reallyTemp) {
		int i = 0;
		for (String str : defAllTechSet) {
			reallyTemp[i] = str;
			i++;
		}
		for (int j = 0; j < initStudentSelectedTech.size(); j++) {
			reallyTech.put(reallyTemp[j], "");
		}
	}

}

package com.github.xiilei.easyim.core;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

/**
 * 全局配置管理
 * @author xl
 *
 */
public class Config {
                    
                    public static final Logger logger = LogManager.getLogger(Config.class);

	private static final String PROPERTIES_FILE = "easyim.xml";
	
//	private static Properties properties;
	private static XMLConfig properties;
	/**
	 * 加载配置文件
	 * @param webPath 文件加载路径
	 */
	public static void load(String webPath){
		properties = new XMLConfig();
		try {
			properties.load(Config.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
			properties.setProperty("webPath", webPath+File.separator);
		} catch (Exception e) {
			e.printStackTrace();
			//这里的错误处理(Log尚未初始化....)
			System.out.println("无法从CLASSPATH中加载配置文件:"+PROPERTIES_FILE);
		}
	}
	
	/**
	 * 配置参数值为null,返回默认
	 * @param name 参数名
	 * @param valDefault 参数值
	 * @return 参数值/默认值
	 */
	public static String getRroperty(String name,String valDefault){
		String val = properties.getProperty(name);
		return (val==null) ? valDefault : val;
	}
	
	/**
	 * 根据name获取配置参数
	 * @param name 参数名
	 * @return 参数值
	 */
	public static String getProperty(String name){
		String value = properties.getProperty(name);
		if (value == null) {
			throw new IllegalArgumentException("未知 属性: " + name);
		}
		return value;
	}
	/**
	 * 返回long类型配置参数
	 * @param name 参数名
	 * @param i 默认值
	 * @return 正常-返回参数值,异常-返回默认值
	 */
	public static long getLongProperty(String name,Long i){
		String value = properties.getProperty(name);
		
		//如果为null,直接返回,避免try..catch
		if(value == null){
			return i;
		}
		
		try {
			return Long.parseLong(value);
		} catch (Exception e) {
			logger.warn("Config:无法将"+name+"指定参数值转为Long");
			return i;
		}
	}
	
	/**
	 * 返回long类型配置参数
	 * @param name 参数名
	 * @return 参数值
	 */
	public static Long getLongProperty(String name){
		String value = getProperty(name);
		try {
			return Long.parseLong(value);
		} catch (Throwable t) {
			throw new IllegalArgumentException("非法 属性 value: " + name + " val=" + value);
		}
	}
	/**
	 * 是否存在指定参数
	 * @param name 参数名
	 * @return true/false
	 */
	public static boolean hasProperty(String name) {
		return properties.containsKey(name);
	}
	
}

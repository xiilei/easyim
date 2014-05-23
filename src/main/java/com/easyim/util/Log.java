package com.easyim.util;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.easyim.core.Config;


/**
 * 日志记录类
 * 封装Log4j,可以在此类中更换日志记录
 * @author xl
 *
 */
public class Log {
 
	private static Logger logger=Logger.getLogger("easyim");
	
	/**
	 * 初始化,日志
	 * @param path
	 */
	public static void init(){
		PropertyConfigurator.configure(Config.getProperty("webPath")+Config.getProperty("logfile"));
	}
	
	public static Logger getLogger(){
		return logger;
	}
	/**
	 * 信息
	 * @param message 信息
	 */
	public static void info(Object message){
		logger.info(message);
	}
	/**
	 * 调试
	 * @param message 信息
	 */
	public static void debug(Object message){
		logger.debug(message);
	}
	
	/**
	 * 警告
	 * @param message 信息
	 */
	public static void warn(Object message) {
		logger.warn(message);
	}

	/**
	 * 警告
	 * @param message 信息
	 * @param throwable 异常
	 */
	public static void warn(Object message, Throwable throwable) {
		logger.warn(message,throwable);
	}

	/**
	 * 错误
	 * @param message 信息
	 */
	public static void error(Object message) {
		logger.error(message);
	}

	/**
	 * 错误
	 * @param message 信息
	 * @param throwable 异常
	 */
	public static void error(Object message, Throwable throwable) {
		logger.error(message, throwable);
	}

	/**
	 * 致命错误
	 * @param message 错误信息
	 */
	public static void fatal(Object message) {
		logger.fatal(message);
	}

	/**
	 * 致命错误
	 * @param message 信息
	 * @param throwable 异常
	 */
	public static  void fatal(Object message, Throwable throwable) {
		logger.fatal(message, throwable);
	}

	/**
	 * 设置Level
	 * @param level Level
	 */
	public static  void setLevel(Level level) {
		logger.setLevel(level);
	}
	
	
}

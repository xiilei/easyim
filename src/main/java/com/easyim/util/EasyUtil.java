package com.easyim.util;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import com.easyim.core.Config;
import com.easyim.core.Protocol;

public class EasyUtil {
	
	private static SimpleDateFormat dateFormat = null;
	
	private static Random rand =  new Random();
	
	static{
		dateFormat =new SimpleDateFormat(Config.getRroperty(Protocol.DATEFORMAT.toString(), "HH:mm:ss") );
	}
	
	/**
	 * 使用默认格式,格式化时间
	 * @return 时间字符串
	 */
	public static String dateFormat(){
		return dateFormat.format(new Date());
	}
	
	/**
	 * 指定格式,格式化时间 
	 * @param format 时间字符串
	 * @return
	 */
	public static String dateFormat(String format){
		return new SimpleDateFormat(format).format(new Date());
	}
	
	/**
	 * uuid
	 */
	public static String uuid(){
		return UUID.randomUUID().toString(); 
	}
	
	/**
	 * currentTimeMillis
	 */
	public static long now(){
		return System.currentTimeMillis();
	}
	
	public static int randId(){
		return rand.nextInt(10000);
	}
	
	public static String getNewString(String oldStr){
		if(oldStr == null){
			return null;
		}
		try {
			return new String(oldStr.getBytes("ISO-8859-1"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			//e.printStackTrace();
			Log.warn("EasyUtil,不支持的字符集");
		}
		return null;
	}
	
	/**
	 * 无缓存
	 * @param aResponse
	 */
	public static void setNoCacheHeaders(HttpServletResponse aResponse) {
		// Set to expire far in the past.
		aResponse.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");

		// Set standard HTTP/1.1 no-cache headers.
		aResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");

		// Set IE extended HTTP/1.1 no-cache headers (use addHeader).
		aResponse.addHeader("Cache-Control", "post-check=0, pre-check=0");

		// Set standard HTTP/1.0 no-cache header.
		aResponse.setHeader("Pragma", "no-cache");

	}
}

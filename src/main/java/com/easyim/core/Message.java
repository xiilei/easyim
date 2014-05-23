package com.easyim.core;

import java.lang.reflect.Field;

import com.easyim.util.EasyUtil;
import com.easyim.util.Log;

public class Message {
	/**
	 * event 消息类型
	 */
	private String event;
	/**
	 * 发送方ID
	 */
	private String sid;
	/**
	 * 接收方ID
	 */
	private String gid;
	/**
	 * 消息内容
	 */
	private String text;
	/**
	 * 发送时间
	 */
	private String stime;
	
	/**
	 * 发送方用户名
	 */
	private String sname;
	
	
	protected Message(){
		
	}
	
	/**
	 * 创建一个发送给所有在线用户的系统消息
	 * @param eventTye 消息类型
	 * @param text 消息内容7
	 * @return 消息对象
	 */
	public static Message createSys(Protocol eventTye,String text){
		return create(eventTye.toString(), "system", Protocol.ALL.toString(), text);
	}
	
	/**
	 * 创建一个发送给所有在线用户的消息
	 * @param sid 发送者ID
	 * @return 消息对象
	 */
	public static Message create(String sid,String text){
		return create(sid,Protocol.ALL.toString(),text);
	}
	/**
	 * 创建一个 消息对象
	 * @param sid 发送者ID
	 * @param gid 接收者ID
	 * @return 消息对象
	 */
	public static Message create(String sid,String gid,String text){
		return create(Protocol.EVENT_GETLIST.toString(), sid, gid, text); 
	}
	
	public static Message create(String event,String sid,String gid,String text){
		Message m = new Message();
		m.setEvent(event);
		m.setSid(sid);
		m.setGid(gid);
		m.setText(text);
		m.setStime(EasyUtil.dateFormat());
		return m;
	}
	
	//-------------getter/setter----------------
	
	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getStime() {
		return stime;
	}

	protected void setStime(String stime) {
		this.stime = stime;
	}
	
	public String getSname() {
		return sname;
	}

	public void setSname(String sname) {
		this.sname = sname;
	}
	/**
	 * 重写toString方法,使用反射转为JSON字符串
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String toString(){
		StringBuilder su = new StringBuilder();
		Class<Message> clazz =(Class<Message>) this.getClass();
		Field[]  fields  = clazz.getDeclaredFields();
		
		Object name = null;
		
		String fieldName = "";
		
		su.append("{");
		/**
		 * 如果是text字段,将不为其添加  引号
		 */
		for (int i =0; i< fields.length;i++) {
			fields[i].setAccessible(true);
			fieldName =  fields[i].getName();
			su.append("\"");
			
			su.append(fieldName);
			
			su.append(fieldName.equals("text") ? "\":" : "\":\"");
			try {
				name = fields[i].get(this);
				su.append((name == null ? "" : name.toString()));
			} catch (IllegalArgumentException e) {
				Log.warn("Message to JSON:不正确的字段:"+fields[i].getName());
			} catch (IllegalAccessException e) {
				Log.warn("Message to JSON:不可访问:"+fields[i].getName());
			}
			if (i <fields.length-1) {
				su.append(fieldName.equals("text") ? "," : "\",");
			}
		}
		su.append("\"}");
		
		return su.toString();
	}
	
	/**
	 * 待实现
	 * @return xml
	 */
	public String toXMLString(){
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

}
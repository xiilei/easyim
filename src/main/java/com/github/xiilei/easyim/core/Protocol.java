package com.github.xiilei.easyim.core;

/**
 * easyim protocol
 * 如果枚举值与其名不同,可以带参
 * @author xl
 *
 */
public enum Protocol {
	//---------------配置-----------
	/**
	 *session_timeout_mins 配置会话失效时间(分钟)
	 */
	 SESSION_TIMEOUT_MINS,
	 
	/**
	 *timer_intercal_mins 配置检查
	 */
	 TIMER_INTERVAL_MINS,
	 
	 /**
	  * maxwaittime_millis 无新消息等待时间
	  */
	 MAXWAITTIME_MILLIS,
	 
	 /**
	  * dateformat,消息时间格式
	  */
	 DATEFORMAT,
	 
	 /**
	  * queue_capacity,消息队列容量
	  */
	 QUEUE_CAPACITY,
	 
	//--------------传递参数(目前为固定,不可更改)---------
	 
	 /**
	  * event 请求类型
	  */
	 MSG_EVENT("event"),
	 /**
	  * sid,发送者id
	  */
	 MSG_SID("sid"),
	 /**
	  * gid,接收id
	  */
	 MSG_GID("gid"),
	 /**
	  * text,消息内容
	  */
	 MSG_TEXT("text"),
	 /**
	  * sname,发送者用户名
	  */
	 MAG_SNAME("sname"),
	 
	//--------------消息类型-----------
	/**
	 * join消息,表明用户上线
	 */
	EVENT_JOIN("join"),
	
	/**
	 * getlist消息,请求消息列表
	 */
	EVENT_GETLIST("getlist"),
	
	/**
	 * putmsg,消息,添加消息到列表
	 */
	EVENT_PUTMSG("putmsg"),
	
	/**
	 * eait,用户下线
	 */
	EVENT_EXIT("exit"),
	
	//----------接收者---------- 
	/**
	 * all 发给所有 
	 */
	 ALL;
	 
	 
	Protocol() {}
	
	String type = null;
	Protocol(String type){
		this.type=type;
	}
		
	/**
	 * 重写toString()方法,
	 * type==null，返回小写字符串<p>
	 * type!=null,返回type;
	 */
	@Override
	public String toString(){
		//出现与枚举名不同时
		if(type != null){
			return type;
		}
		return this.name().toLowerCase();
	}
	
}

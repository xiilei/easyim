package com.easyim.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.easyim.util.EasyUtil;

/**
 * 会话核心类,代表一个用户连接
 * @author xl
 *
 */
public class Session {
	
	private Controller controller;
	private Connection connection;
	private ClientAdapter client;
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	private long LEASE_TIME_MILLIS =Config.getLongProperty(Protocol.SESSION_TIMEOUT_MINS.toString())* 1000;
	//存活时间
	private volatile long timeToLive = LEASE_TIME_MILLIS; 
	//Session ID
	private String id;
	
	//IP
	private String address = "unknown";
	
	/**
	 * 用户名
	 */
	private String uname = null;
	
	protected Session(){
		
	}
	
	/**
	 * 创建一个Session
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @return 新的Session
	 */
	public static Session create(HttpServletRequest request,HttpServletResponse response){
		Session session = new Session(); 
		//记录request/response
		session.request=request;
		session.response=response;
		
		//产生唯一的uuid
		session.id=EasyUtil.uuid();
		
		session.connection=Connection.create(session);
		session.controller=Controller.create(session);
		session.client=ClientAdapter.create();
		
		return session;
	}
	
	//以下两个方法必须一起用
	public static Session create(){
		return create(null,null);
	}
	
	public void setSession(HttpServletRequest request,HttpServletResponse response){
		this.request=request;
		this.response=response;
		
		this.client.setResponse(response);
	}
	/**
	 * Session ID
	 * @return id
	 */
	public String getId(){
		return id;
	}
	
	/**
	 * 是否存活
	 * @return true/false
	 */
	public boolean isExpired() {
		return timeToLive <= 0;
	}
	
	/**
	 * 更新Session时间
	 */
	public void update() {
		timeToLive = LEASE_TIME_MILLIS;
	}
	
	/**
	 * 减少时间
	 * @param mills
	 */
	public void less(long mills) {
		timeToLive -= mills;
	}
	
	/**
	 * 添加到SessionManager,并发送系统消息,告知用户上线
	 */
	public void add(){
		getConnection().tipOnline(Protocol.EVENT_JOIN);
		SessionManager.getInstance().addSession(this);
	}
	
	/**
	 * 从SessionManager中移除,并发系统消息,告知用户离线
	 */
	public void remove(){
		SessionManager.getInstance().removeSession(this);
		//更新在线信息..
		getConnection().tipOnline(Protocol.EVENT_EXIT);
		//this.getConnection().stop();
	}
	
	/**
	 * 获取此session信息字符串
	 * @return  { id : session id ,name : uname}
	 */
	public String getInfoString(){
		StringBuffer su = new StringBuffer();
		
		su.append("{\"id\":\"");
		su.append(getId());
		su.append("\",\"name\":\"");
		su.append(getUname());
		su.append("\"}");
		
		return su.toString();
	}
	
	//------一系列的getter/setter-------------
	
	public HttpServletRequest getRequest(){
		return request;
	}
	
	public HttpServletResponse getResponse(){
		return response;
	}
	
	public Connection getConnection(){
		return connection;
	}
	
	public Controller getController(){
		return controller;
	}

	public ClientAdapter getClient(){
		return client;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getUname() {
		if(uname == null){
			uname = "游客" + EasyUtil.randId();
		}
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}
}

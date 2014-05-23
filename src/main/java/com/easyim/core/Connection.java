package com.easyim.core;

import java.util.Iterator;
import java.util.List;

import com.easyim.util.Log;

/**
 * 保持与client的连接
 * @author xl
 *
 */
public class Connection {
	
	/**
	 * 消息队列容量
	 */
	private final int  QUEUE_CAPACITY = (int)Config.getLongProperty(Protocol.QUEUE_CAPACITY.toString(), 10L);
	
	/**
	 * 无消息最长等待时间
	 */
	private MessageQueue mqueue = new MessageQueue(QUEUE_CAPACITY);
	
	private Session session;
	
	@Deprecated
	private boolean active = true;
	
	protected Connection(){
		
	}
	
	public static Connection create(Session session){
		Connection connection = new Connection();
		
		connection.session = session;
		
	
		return connection;
	}
	
	/**
	 * 将保持长时间的连接
	 */
	public void keep(){
//		Message message = null;
		
		ClientAdapter clientAdapter = session.getClient();
		session.update();
				
		clientAdapter.start();
		
//		message = mqueue.poll();
		
//		clientAdapter.push(message);
		
		List<Message> list= mqueue.pollAll();
		
		clientAdapter.push(list);
		
		clientAdapter.close();
		
		
	}
	
	public void sendFirst(){
		ClientAdapter clientAdapter = session.getClient();
		clientAdapter.start();
		clientAdapter.pushFirst(session.getId(),session.getUname(),getOnlineString());
		clientAdapter.close();
	}
	
	/**
	 * 向会话消息队列添加消息 call by Dispatcher
	 */
	public void onMessage(Message message){
		
		if (!mqueue.add(message)) {
			Log.warn("Connectionm,onMessage:添加超时");
		}else {
			Log.debug("("+message.getStime()+")添加消息:sid:"+message.getSid()+",gid:"+message.getGid()+",text:"+message.getText()+",sname:"+message.getSname());
		}
		
	}
	
	/**
	 * 发送系统消息,告知用户上线/离线
	 */
	public void tipOnline(Protocol type){
		try {
			Message message = Message.createSys(type,session.getInfoString());
			Dispatcher.getInstance().broadcast(message);
		} catch (Exception e) {
			Log.warn("Connection:,tipOnline,event:"+type.toString()+",发送失败");
		}
	}
	
	/**
	 * 获取在线用户信息
	 * @return JsonString
	 */
	protected String getOnlineString(){
		StringBuilder su = new StringBuilder();
		Session aSession = null;
		su.append("[");
		Iterator<Session> iterator = SessionManager.getInstance().getSessions().iterator();
		while(iterator.hasNext()){
			aSession = iterator.next();
			su.append(aSession.getInfoString());
			if (iterator.hasNext()) {
				su.append(",");
			}
		}
		su.append("]");
		return su.toString();
	}
	
	/**
	 * 停止循环 call by session.remove(); 
	 */
	@Deprecated
	public void stop(){
		active=false;
	}
	
	
	
}

package com.github.xiilei.easyim.core;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

/**
 * 
 * @author xl
 *
 */
public class Dispatcher {
                    public static final Logger logger = LogManager.getLogger(Dispatcher.class);
	
	private static Dispatcher instance;
	
	private static ApplyBroadcast method;
	
	protected Dispatcher(){
		
	}
	
	public static Dispatcher getInstance(){
		if (instance == null) {
			instance = new Dispatcher();
			instance.method = new ApplyBroadcast();
		}
		return instance;
	}
	
	/**
	 * 向所有 有效session添加消息 
	 */
	public synchronized void broadcast(Message message) {
		
		method.setMessage(message);
		SessionManager.getInstance().apply(method);
		
	}
	
	/**
	 * 
	 */
	public synchronized void multicast(Message message) {
	
	}
	
	/**
	 * 向指定session添加消息
	 */
	public synchronized void unicast(Message message, String gid) {
		
		Session to = SessionManager.getInstance().getSession(gid);
		if(to ==null){
			logger.warn("Dispatcher,指定接收session不存在,或已失效");
			//离线消息处理...
		}else{
			to.getConnection().onMessage(message);
		}
		
		
	}
	
	/**
	 * 
	 * @author xl
	 *
	 */
	private static class ApplyBroadcast implements SessionManager.ApplyMethod{
		private Message message;
		
		public ApplyBroadcast(){
		}
		
		public void setMessage(Message message){
			this.message=message;
		}
		
		@Override
		public void invoke(Session session) {
			if(!session.getId().equals(message.getSid())){
				session.getConnection().onMessage(message);
			}
		}
		
	}
}

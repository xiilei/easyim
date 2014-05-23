package com.easyim.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.easyim.util.Log;

/**
 * 阻塞消息队列(仅是对LinkedBlockingQueue的简单封装)
 * @author xl
 * 
 */
public class MessageQueue {
	
	/**
	 * 无新消息等待时间
	 */
	private final long MAXWAITTIME_MILLIS = Config.getLongProperty(Protocol.MAXWAITTIME_MILLIS.toString());
	
	
	private LinkedBlockingQueue<Message> queue ;

	public MessageQueue(){
		queue = new LinkedBlockingQueue<Message>();
	}
	
	public MessageQueue(int capacity){
		queue = new LinkedBlockingQueue<Message>(capacity);
	}
	
	public boolean add(Message m){
		boolean res = false;
		try {
			res=queue.offer(m, MAXWAITTIME_MILLIS, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Log.warn("MessageQueue,add异常:",e);			
			//e.printStackTrace();
		}
		return res;
	}
	
	public Message poll(){
		Message m =null;
		try {
			m = queue.poll(MAXWAITTIME_MILLIS, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Log.warn("MessageQueue,poll异常:", e);
			//e.printStackTrace();
		}
		return m;
	}
	
	/**
	 * 获取并清空消息队列中 所有消息
	 * @return List
	 */
	public List<Message> pollAll(){
		List<Message>  list= new ArrayList<Message>();
		list.add(poll());
		queue.drainTo(list);
		return list;
	}
	
	
	public boolean isEmpty(){
		return queue.isEmpty();
	}
	
	public int size(){
		return queue.size();
	}
}
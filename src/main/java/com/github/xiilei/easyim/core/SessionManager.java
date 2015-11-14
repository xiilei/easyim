package com.github.xiilei.easyim.core;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import com.github.xiilei.easyim.util.EasyUtil;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;


/**
 * Session 管理类(单例)
 * @author xl
 */
public class SessionManager {
    
    public static final Logger logger = LogManager.getLogger(SessionManager.class);
	
	/**
	 * 缓存实例
	 */
	private static SessionManager instance = null;
	
	/**
	 * 存储session,Map<Session Id,Session>
	 */
	private ConcurrentHashMap<String, Session>  sessions = new ConcurrentHashMap<String, Session>();
	
	/**
	 * 计划任务,用于指定时间检查session是否有效
	 */
	private Timer timer;
	private final long TIMER_INTERVAL_MILLIS = Config.getLongProperty(Protocol.TIMER_INTERVAL_MINS.toString())*1000;
	/**
	 * SessionManager同步监视锁
	 */
	private final Object lock = new Object();
	
	
	/**
	 * 缓存sessions,防止检查时,无任何add/remove操作仍然遍历sessions
	 */
	private Session[] sessionCache = new Session[0];
	
	/**
	 * 是否重新缓存(自动设置)
	 */
	private volatile boolean sessionCacheDirty = false;
	
	/**
	 * 
	 */
	protected SessionManager(){
		
	}
	
	/**
	 * 获得SessionManager实例
	 * @return
	 */
	public static SessionManager getInstance(){
		if(instance==null){
			instance = new SessionManager();
		}
		return instance;
	}
	
	public Collection<Session> getSessions(){
		return sessions.values();
	}
	
	/**
	 * 添加Session
	 */
	public void addSession(Session session){
		sessions.put(session.getId(), session);
		sessionCacheDirty = true;
		logger.info("SessionManager add Session:"+session.getId()+",IP:"+session.getAddress());
	}
	
	/**
	 * 获得Session
	 * @param id Session Id
	 * @return Session
	 */
	public Session getSession(String id){
		return sessions.get(id);
	}
	
	/**
	 * 指定id对应Session是否存在
	 * @param id 指定Session Id
	 * @return true/false
	 */
	public boolean hasSession(String id){
		return sessions.containsKey(id);
	}
	
	/**
	 * 移除并返回Session
	 * @param session Session
	 */
	public void removeSession(Session session){
		Session nsession = sessions.remove(session.getId());
		if(nsession!=null){
			logger.info("SessionManager remove Session:"+nsession.getId()+",IP:"+nsession.getAddress());
		}
		sessionCacheDirty = true;
		
	}
	/**
	 * sessions 长度
	 * @return 长度
	 */
	public int getSize(){
		return sessions.size();
	}
	
	/**
	 * 遍历执行Seesion并调用ApplyMethod invoke执行
	 * >>添加缓存支持
	 * @param method
	 */
	public void apply(ApplyMethod method){
//			Iterator<Session> iterator = sessions.values().iterator();
//			Session session = null;
//			while (iterator.hasNext()) {
//				session = iterator.next();
//				try {
//					method.invoke(session);
//				} catch (Exception e) {
//					Log.warn("SessionManager apply invoke 方法执行出错:"+e);
//				}
//			}
			//更新缓存
			if(sessionCacheDirty){
				//TODO (easyim)sessionCache的只增不减...
				sessionCache = sessions.values().toArray(sessionCache);
				//设置状态为fasle,防止再次更新
				sessionCacheDirty=false;
			}
			
			//需要synchronized吗?
			
			//遍历,传递给 method对象的invoke方法执行(必须从0开始遍历)
			for (int i = 0; i < sessionCache.length; i++) {
				//为 null,退出循环
				if(sessionCache[i]==null){
					break;
				}
				try {
					method.invoke(sessionCache[i]);
				} catch (Exception e) {
					logger.warn("SessionManager apply invoke 方法执行出错:",e);
				}
			}
	}
	
	public void start(){
		if (timer!=null) {
			stop();
		}
		timer = new Timer(false);
		timer.schedule(new CheckTimerTask(), TIMER_INTERVAL_MILLIS, TIMER_INTERVAL_MILLIS);
		logger.info("CheckTimerTask started; interval=" + TIMER_INTERVAL_MILLIS + "ms");
		
	}
	
	public void stop(){
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		sessions.clear();
		logger.info("CheckTimerTask stopped");
	}
	
	/**
	 * apply 调用方法接口
	 * @author xl
	 *
	 */
	public static interface ApplyMethod{

		public void invoke(Session session);
	}
	
	/**
	 * 检查Session是否有效
	 * @author xl
	 *
	 */
	private class CheckTimerTask extends TimerTask implements ApplyMethod{
		
		//private final long MAXWAITTIME_MILLIS = Config.getLongProperty(Protocol.MAXWAITTIME_MILLIS.toString());
		
		private long lastRun = EasyUtil.now();
		private long delta;
		
		@Override
		public void run() {
			long now = EasyUtil.now();
			//按理 delta>=TIMER_INTERVAL_MILLIS
			delta = now - lastRun;
			lastRun=now;
			getInstance().apply(this);
			if(logger.isDebugEnabled()){
				logger.debug("CheckTimerTask,时间:"+EasyUtil.dateFormat()+", sessions size:"+sessions.size()+" ,cache size:"+sessionCache.length);
			}
		}

		@Override
		public void invoke(Session session) {
			session.less(delta);
			if(session.isExpired()){
				logger.info("CheckTimerTask: remove Session");
				session.remove();
			}
		}
		
	}
	
}

package com.easyim.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.easyim.util.EasyUtil;
import com.easyim.util.Log;

public class Controller {
	
	private Session session;
	
	private HttpServletRequest request;
	
	private Message message;
	
	protected Controller(){
	}
	
	/**
	 * 创建 Controller ,Call by Session
	 * @param session Session
	 * @return Controller 对象
	 */
	public static Controller create(Session session){
		Controller controller = new Controller();
		controller.session=session;
				
		return controller;
	}
	
	/**
	 * 根据event,处理请求
	 */
	public void doRequest(){
		request = session.getRequest();
		
		//更新Message
		updateMessage();
		
		//更新,防止过期
		session.update();
		
		String event = request.getParameter(Protocol.MSG_EVENT.toString());
		if(event.equals(Protocol.EVENT_GETLIST.toString())){
			doGetList();
		}else if (event.equals(Protocol.EVENT_PUTMSG.toString())){
			doPutMsg();
		}else if (event.equals(Protocol.EVENT_EXIT.toString())){
			doExit();
		}else if (event.equals(Protocol.EVENT_JOIN.toString())){
			doJoin();
		}else {
			Log.warn("Controller,非法的event:"+event);
			try {
				session.getResponse().sendError(HttpServletResponse.SC_BAD_REQUEST, "unkonw event"+event);
			} catch (IOException e) {
				Log.warn("Controller,输出流异常:",e);
				//e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 */
	protected void doJoin(){
		String sname = request.getParameter(Protocol.MAG_SNAME.toString());
		if(sname!=null){
			
			try {
				session.setUname(URLDecoder.decode(EasyUtil.getNewString(sname),"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				//e.printStackTrace();
				Log.info("Controller,不支持的字符集");
			}
		}
		session.add();
		session.getConnection().sendFirst();
	}
	
	/**
	 * 添加消息
	 */
	protected void doPutMsg(){
		String gid = message.getGid();
		message.setSname(session.getUname());
		try {
			if(gid.equals(Protocol.ALL.toString()) || gid == null){
				Dispatcher.getInstance().broadcast(message);
			}else {
				Dispatcher.getInstance().unicast(message, gid);
			}
		} catch (Exception e) {
			Log.warn("Controller,添加消息失败sid:"+message.getSid(),e);
		}
		
	}
	
	/**
	 * 获取消息
	 */
	protected void doGetList(){
		session.getConnection().keep();
	}
	
	/**
	 * 
	 */
	protected void doExit(){
		session.remove();
	}
	
	/**
	 * 更新消息,不存在则创建
	 */
	protected void updateMessage(){
		
		String sid = request.getParameter(Protocol.MSG_SID.toString());
		
		String gid = request.getParameter(Protocol.MSG_GID.toString());
		String text = request.getParameter(Protocol.MSG_TEXT.toString());
		
		text = EasyUtil.getNewString(text);
		if(message != null){
			
			message.setSid(sid);
			message.setGid(gid);
			message.setText("\""+(text== null ? "" : text)+"\"");
			message.setStime(EasyUtil.dateFormat());
			
		}else {
			message = Message.create(sid, gid, text);
		}
	}
}

package com.github.xiilei.easyim.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletResponse;

import com.github.xiilei.easyim.util.EasyUtil;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
/**
 * 将消息输出到客户端
 * @author xl
 *
 */
public class ClientAdapter {
                    
    public static final Logger logger = LogManager.getLogger(ClientAdapter.class);

	private HttpServletResponse response;
	
	private PrintWriter out;
	
	
	protected ClientAdapter(){
	}
	
	public static ClientAdapter create(HttpServletResponse response){
		ClientAdapter client = new ClientAdapter();
		client.response=response;
		
		return client;
	}
	
	//以下两个方法必须一起用
	public static ClientAdapter create(){
		return create(null);
	}
	
	public void setResponse(HttpServletResponse response){
		this.response=response;
	}

	/**
	 * 输出准备
	 */
	public void start(){
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("text/html;charset=UTF-8");
		//无缓存
		EasyUtil.setNoCacheHeaders(response);
		try {
			out = response.getWriter();
		} catch (IOException e) {
			logger.warn("ClientAdaper,start,输出流异常");
			//e.printStackTrace();
		}
		
		out.flush();
	}
	
	/**
	 * 将消息输出到客户端
	 * @param message
	 */
	public void push(Message message){
		if (message != null) {
			out.print(p(message.toString()));
		}else {
			out.println(p("\"\""));
		}
		out.flush();
	}
	
	/**
	 * 将一组消息输出到客户端
	 * @param list 消息集合
	 */
	public void push(List<Message> list){
		StringBuilder su = new StringBuilder();
		ListIterator<Message> iterator = list.listIterator();
		Message message = null;
		su.append("[");
		while (iterator.hasNext()) {
			if((message=iterator.next())!=null){
				su.append(message.toString());
				if(iterator.hasNext()){
					su.append(",");
				}
			}
		}
		su.append("]");
		out.println(p(su.toString()));
	}
	
	/**
	 * 输出自定义json
	 * @param json
	 */
	public void push(String json){
		out.println(p(json));
		out.flush();
	}
	
	/**
	 * 用户第一次请求,返回用户id/名,在线信息
	 * @param id
	 * @param uname
	 */
	public void pushFirst(String id,String uname,String online){
		out.println(p("{\"id\":\""+id+"\",\"uname\":\""+uname+"\",\"online\":"+online+"}"));
		out.flush();
	}
	
	public void close(){
		if(out!=null){
			out.close();
			out = null;
		}
	}
	
	public String p(String data){
		return "{\"data\":"+data+"}";
	}
}

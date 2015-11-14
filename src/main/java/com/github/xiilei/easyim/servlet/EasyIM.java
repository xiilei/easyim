package com.github.xiilei.easyim.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.xiilei.easyim.core.Protocol;
import com.github.xiilei.easyim.core.Session;
import com.github.xiilei.easyim.core.SessionManager;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;


@WebServlet("/im")
public class EasyIM extends HttpServlet {
    
    public static final Logger logger = LogManager.getLogger(EasyIM.class);

	/**
	 * Constructor of the object.
	 */
	public EasyIM() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); 
	}
	
	//处理  GET/POST 请求
	public void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//PrintWriter out = response.getWriter();
		String event = request.getParameter(Protocol.MSG_EVENT.toString());
		if(event == null){
			logger.warn("im,接收到错误的请求,无event 参数");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No event specified");
			return;
		}
		
		String sid = request.getParameter(Protocol.MSG_SID.toString());
		
		Session session = null;
		
		//新的用户,创建新的Session,并添加到SessionManager
		if(event.equals(Protocol.EVENT_JOIN.toString())){
			
			//创建Session
			session = Session.create();
			//设置IP
			session.setAddress(request.getRemoteAddr());
			
		}else {
			
			if (sid == null ) {
				logger.warn("im,接收到错误的请求,缺少 sid 参数");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Join No sid specified");
				return;
			}
			
			session = SessionManager.getInstance().getSession(sid);
			
			if(session == null){
				logger.warn("im,Session不存在或者已经失效  id:"+sid);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid or expired id:"+sid);
				return;
			}
		}
		
		//更新request,reponse
		session.setSession(request, response);
		
		session.getController().doRequest();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}

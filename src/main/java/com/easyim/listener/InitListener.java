package com.easyim.listener;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.easyim.core.Config;
import com.easyim.core.SessionManager;
import com.easyim.util.Log;

/**
 * easyim 启动加载监听
 * @author xl
 */
@WebListener
public class InitListener implements ServletContextListener{
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
    	
    	String webPath = sce.getServletContext().getRealPath("/") +"WEB-INF";
    	//从classpath下加载配置文件
    	Config.load(webPath);
    	
    	SessionManager.getInstance().start(); 
    	
    	Log.info("easyim 启动,webPath:"+webPath);
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    	
    	SessionManager.getInstance().stop();
    	
    	Log.info("easyim 停止");
    	
    
    }

}

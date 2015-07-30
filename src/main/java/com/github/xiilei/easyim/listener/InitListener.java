package com.github.xiilei.easyim.listener;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.github.xiilei.easyim.core.Config;
import com.github.xiilei.easyim.core.SessionManager;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
/**
 * easyim 启动加载监听
 * @author xl
 */
@WebListener
public class InitListener implements ServletContextListener{
    
    public static final Logger logger = LogManager.getLogger(InitListener.class);
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
    	
    	String webPath = sce.getServletContext().getRealPath("/") +"WEB-INF";
    	//从classpath下加载配置文件
    	Config.load(webPath);
    	
    	SessionManager.getInstance().start(); 
    	
    	logger.info("easyim 启动,webPath:"+webPath);
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    	
    	SessionManager.getInstance().stop();
    	
    	logger.info("easyim 停止");
    	
    
    }

}

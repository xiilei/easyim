package com.easyim.core;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.VisitorSupport;
import org.dom4j.io.SAXReader;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.easyim.util.Log;

public class XMLConfig {

	private HashMap<String, String> properties = new HashMap<String, String>();
	
	
	public XMLConfig(){}
	
	public XMLConfig(InputStream in) throws DocumentException{
		this.load(in);
	}
	
	/**
	 * 加载配置文件并解析
	 * @param path
	 * @throws DocumentException
	 */
	public void load(InputStream in) throws DocumentException{
		SAXReader reader = new SAXReader();
		
		EasyVisitor easy = new EasyVisitor();
		Document doc;
		doc = reader.read(in);
		doc.accept(easy);
		reader.setErrorHandler(new ErrorHandler() {
			
			@Override
			public void warning(SAXParseException exception) throws SAXException {
				Log.warn("配置文件解析出错:行"+exception.getLineNumber()+","+exception.getMessage());
			}
			
			@Override
			public void fatalError(SAXParseException exception) throws SAXException {
				Log.fatal("配置文件解析出错:行"+exception.getLineNumber()+","+exception.getMessage());
			}
			
			@Override
			public void error(SAXParseException exception) throws SAXException {
				Log.error("配置文件解析出错:行"+exception.getLineNumber()+","+exception.getMessage());
			}
		});
	}
//	public void print(){
//		Iterator<String> iterator = properties.keySet().iterator();
//		while(iterator.hasNext()){
//			String key = iterator.next();
//			String value = properties.get(key);
//			
//			System.out.println(key+"------->"+value);
//		}
//	}
	/**
	 * 设置属性配置
	 * @param key 属性名
	 * @param value 属性值
	 */
	public void setProperty(String key,String value){
		properties.put(key, value);
	}
	
	/**
	 * 获取属性配置
	 * @param key 属性名
	 * @return 属性值
	 */ 
	public String getProperty(String key){
		return properties.get(key);
	}
	
	public boolean containsKey(String key){
		return properties.containsKey(key);
	}
	
	/**
	 * 使用visitor 方式简单解析 xml配置文件
	 * @author xl
	 *
	 */
	private class EasyVisitor extends VisitorSupport{
		
		//保存正在处理的节点
		private String currentELement;
		
		@Override
		public void visit(Element node){
				
			if(node.getName().equals("property")){
				currentELement = node.getText();
			}else{
				currentELement = null;
			}
				
		}
		@Override
		public void visit(Attribute attr){
			
			if(currentELement != null && attr.getName().equals("name")){
				properties.put(attr.getValue(), currentELement);
			}
			
		}
		
	}
	
	
}

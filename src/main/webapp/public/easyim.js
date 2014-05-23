(function(window){
var Easy=function(){	
},	
/**
 * DOM加载完成执行ready方法
 */
DOMContentLoaded = function() {
	if ( document.addEventListener ) {
		document.removeEventListener( "DOMContentLoaded", DOMContentLoaded, false );
		Easy.ready();
	} else if ( document.readyState === "complete" ) {
		document.detachEvent( "onreadystatechange", DOMContentLoaded );
		Easy.ready();
	}
};
//添加事件

if(document.addEventListener){

    document.addEventListener('DOMContentLoaded',DOMContentLoaded,false);

}else if(document.attachEvent){

    document.attachEvent('onreadystatechange',DOMContentLoaded);

}


Easy.fn=Easy.prototype={
	constructor: Easy
};

/**
 * 扩展方法
 */
Easy.extend=Easy.fn.extend=function(){
	var target,
		i=0,
		name,
		src,
		copy,
		option;
	//参数多于两个添加到第一个
	if(arguments.length<2){
		target = this;
		i=0;
	}else{
		target = arguments[0];
		i=1;
	}
	for(;i<arguments.length;i++){
		if(!!(option=arguments[i])){
			for(name in option){
				src = target[name];
				copy = option[name];
				if(!!copy && src == copy){
					continue;
				}
				
				target[name] = copy;
			}
		}
	}
};

Easy.extend({
	readyQueue:[],
	
	addReady:function(){
		Easy.each(arguments,function(i,v){
			if(Easy.type(v)=="function"){
				
				Easy.readyQueue.push(v);
			}
		});
	},
	
	ready:function(){
		Easy.each(Easy.readyQueue,function(i,v){
			if(Easy.type(v)=="function") v();
		});
	}
	
});	

/**
 *工具方法
 */
Easy.extend({
	each:function(obj,callback){
		var name,
			i = 0,
			length = obj.length,
			isObj = length === undefined || Object.prototype.toString.call(obj) === "[object Function]";
		if ( isObj ) {
			for ( name in obj ) {
				if ( callback.call( obj[ name ], name, obj[ name ] ) === false ) {
						break;
				}
			}
		} else {
			for ( ; i < length; ) {
				if ( callback.call( obj[ i ], i, obj[ i++ ] ) === false ) {
					break;
				}
			}
		}
		
	},
	class2type:{},
	type:function(o){
		return o==null ? String(o) : Easy.class2type[Object.prototype.toString.call(o)] || 'object';
	},
	trim:function(str){
		return str==null ? "" : (str+"").replace(/^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g,"");
	},
	isEmpty:function(unknow){
		return !unknow || (/^\s*$/.test(unknow));
	}
});

Easy.each("Boolean Number String Function Array Date RegExp Object".split(" "), function(i, name) {
    Easy.class2type[ "[object " + name + "]" ] = name.toLowerCase();
});
/**
 * 时间格式化
 */
Easy.extend({
	format:function(pattern,source){
	    if ('string' != typeof pattern) {
	        return source.toString();
	    }

	    function replacer(patternPart, result) {
	        pattern = pattern.replace(patternPart, result);
	    }

	    var pad     = Easy.pad,
	        year    = source.getFullYear(),
	        month   = source.getMonth() + 1,
	        date2   = source.getDate(),
	        hours   = source.getHours(),
	        minutes = source.getMinutes(),
	        seconds = source.getSeconds();

	        replacer(/yyyy/g, pad(year, 4));
	        replacer(/yy/g, pad(parseInt(year.toString().slice(2), 10), 2));
	        replacer(/MM/g, pad(month, 2));
	        replacer(/M/g, month);
	        replacer(/dd/g, pad(date2, 2));
	        replacer(/d/g, date2);

	        replacer(/HH/g, pad(hours, 2));
	        replacer(/H/g, hours);
	        replacer(/hh/g, pad(hours % 12, 2));
	        replacer(/h/g, hours % 12);
	        replacer(/mm/g, pad(minutes, 2));
	        replacer(/m/g, minutes);
	        replacer(/ss/g, pad(seconds, 2));
	        replacer(/s/g, seconds);

	 

	    return pattern;

	},

	pad:function(source,length){    

	       var pre = "", negative = (source < 0), string = String(Math.abs(source));   
	        if (string.length < length) {
	            pre = (new Array(length - string.length + 1)).join('0');
	        }    
	        return (negative ?  "-" : "") + pre + string;
	}
});
/**
 * DOM 事件注册与移除 (依赖于event.js)
 */
Easy.extend({
	on:window.addEvent,
	off:window.removeEvent
});
/**
 * 创建XMLHttpRequest,只判断一次
 */
Easy.createXHR =window.XMLHttpRequest ?
            function(){return new XMLHttpRequest()}:
            function(){return new ActiveXObject("Microsoft.XMLHTTP")};//ie6     

/**
 * ajax
 */
Easy.extend({
	/**
	 ajax请求方法
	 options{
		url:'',
		method:get/post,
		data:{key:value},
		succeed:function(){},
		failed:function(){}
	 }
	 */
	request:function(options){
		var xhr =Easy.createXHR(),data,isPost;
		
		options.method = (options.method || 'GET');
		data = !!options.data ? Easy.params(options.data) : '';
		
		
		xhr.onreadystatechange=function(){
			if(xhr.readyState===4){
				if(xhr.status==200){
					if(Easy.type(options.succeed) == 'function'){
						options.succeed(xhr.responseText,xhr.responseXML);
					}else{

					}
				}else{
					if(xhr.status>=400){
						if(Easy.type(options.failed) == 'function'){
							options.failed(xhr);
						}
					}
				}
			}
		};
				
		isPost = /POST/i.test(options.method);
		
		if(!isPost){
			options.url+='?'+data;
		}
		
		//准备发送请求		
		xhr.open(options.method,options.url,true);
		
		xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
		
		//发送请求
		if(isPost){
			xhr.setRequestHeader("Content-Type","application/x-www-form-urlencoded");			
			xhr.send(data);
		}else{
			xhr.send(null);
		}

	},
	
	/**
	 * 参数处理
	 */
	params:function(data){
		var arr=[];
		for(var i in data){
			if(data[i]!=undefined)
			arr.push(encodeURIComponent(i)+"="+encodeURIComponent(data[i]));
		}
		return arr.join("&");
	},
	/**
	 * 解析JSON
	 */
	parseJson:function(data){
		if ( window.JSON && window.JSON.parse ) {
            return window.JSON.parse( data );
		}
		return ( new Function( 'return ' + data ) )();
	},
	/**
	 * 解析XML (jQuery)
	 */
	parseXML:function(data){
		var xml, tmp;
        if ( !data || typeof data !== "string" ) {
            return null;
        }
        try {
            if ( window.DOMParser ) { // Standard
                tmp = new DOMParser();
                xml = tmp.parseFromString( data , "text/xml" );
            } else { // IE
                xml = new ActiveXObject( "Microsoft.XMLDOM" );
                xml.async = "false";
                xml.loadXML( data );
            }
        } catch( e ) {
            xml = undefined;
        }
        if ( !xml || !xml.documentElement) {
            throw new Error( "Invalid XML: " + data );
        }
        return xml;
	}
	
});

Easy.extend({
	
	getByClass:function(className,context,node){
	    context=context || window.document;node=node||"*";
	    if(context.getElementsByClassName){//优先考虑FF自带方法
	        return context.getElementsByClassName(className);
	    }
	    var nodes=context.getElementsByTagName(node),
	            ocn=[];        
	    for(var i=0;i<nodes.length;i++){    
	        //检测元素是否有该className(class可能有多个值)
	        if(new RegExp("\\b"+className+"\\b").test(nodes[i].className))
	        {ocn.push(nodes[i]);}
	    }
	    return ocn;
	}
	
});

/**
 * className的相关操作
 */

Easy.extend({
	/**
	 * 添加class
	 */
	addClass:function(node){
		for(var i=1;i<arguments.length;i++)
	    {
	            if(!Easy.hasClass(node,arguments[i])){
	                node.className+=" "+arguments[i];
	            }
	    }
	    return node;

	},
	/**
	 * 删除class
	 */
	removeClass:function(node,cn){
		var carr=node.className.split(/\s+/);    
		for(var i=0;i<carr.length;i++){//也可用正则替换
		      if(carr[i]==cn)carr.splice(i,1);        
		}
	    node.className=carr.join(" ");
	    return node;

	},
	/**
	 * 检测是否存在 
	 */
	hasClass:function(node,cn){
		return new RegExp("\\b"+cn+"\\b").test(node.className);
	},
	/**
	 *转className为驼峰式命名 
	 */
	camelize:function(){
		return s.replace(/-[a-z]/gi,function (c) {
	        return c.charAt(1).toUpperCase();
	    });
	}
	
});


/**
 * DOM 操作
 */
Easy.fn.extend({
	domCache:{},
	
	get:function(domid){
		if(!this.domCache[domid]){
			this.domCache[domid]=document.getElementById(domid);
		}
		return this.domCache[domid];
	},
	
	addHtml:function(domid,html){
		var dom = this.get(domid);
		dom.innerHTML += html;
		dom.scrollTop = dom.scrollHeight;
		
	},
	
	ons:function(className,eventType,fn,context,node){
		var nodes = Easy.getByClass(className,context,node);
		Easy.each(nodes,function(i,v){
			
			Easy.on(v,eventType,fn);
			
		});
	}
});
/**
 * 获取url参数
 */
Easy.extend({
	escapeReg :function (str) {

	    return str.replace(new RegExp("([.*+?^=!:\x24{}()|[\\]\/\\\\])", "g"), '\\\x241');

	},

	getQueryValue :function (url, key) {
	    var reg = new RegExp("(^|&|\\?|#)" + Easy.escapeReg(key) + "=([^&#]*)(&|\x24|#)","");
	    var match = url.match(reg);
	    if (match) {
	        return match[2];
	    }
	    return null;
	}
	
});

//--------------EasyIM--Controller--------------



Easy.fn.extend({
	
	/**
	 * 配置
	 */
	conf:{
		//request 配置
		URL:'im',
		METHOD:'GET',
		
		//event 类型
		E_JOIN:'join',
		E_GETLIST:'getlist',
		E_PUTMSG:'putmsg',
		E_EXIT:'exit',
		
		ALL:'all',
		
		/**
		 * 消息显示html
		 */
		M_ITEM_HTML:'<div class="easyim-im-item">'
			+'<div><span class="{0}">{1} {2}</span></div>'
			+'<p>{3}</p></div>',
	    M_ITEM_SELF_CLASS:'easyim-item-self',
	    M_ITEM_OTHER_CLASS:'easyim-item-other',
	    
	    ONL_ITEM_HTML:'<div class="easyim-online-item" id="{0}">{1} [在线]</div>',
	    ONL_ITEM_CLASS:'easyim-online-item',
	    ONL_ITEM_OVERCLASS:'easyim-online-item-over',
	    
	    //DOM ID
    	D_MSG:'easyim-im-msg',
    	D_INPUT:'easyim-input',
    	D_SEND:'easyim-send',
    	D_USER:'easyim-user',
    	
    	D_ONLINELIST:'easyim-online'
    	//上线插入点
        //D_ONLINELIST_FITSR:'easyim-online-first'   
	   
	},
	/**
	 * 用户名
	 */
	cache:{
		id:'',
		uname:''
	},
	
	init:function(name){
		var me = this,data;
		
		function add(){
			var input = me.get(me.conf.D_INPUT);
			if(!Easy.isEmpty(input.value)){
				me.putMsg(input.value);
			}
			input.value="";
		}
		//回车,点击发送消息
		Easy.on(me.get(me.conf.D_SEND),'click',add);
		Easy.on(window.document,'keydown',function(e){
			if(e.keyCode==13){
				add();
			}
		});
		
		
		
		Easy.on(window,'beforeunload',function(e){
			
//			Easy.request({
//				url:'test',
//				
//				succeed:function(obj){
//				}
//				
//			});
			var live_message='现在离开此页,将导致消息无法准确获取';
			e.returnValue=live_message;
			return live_message;
		});
		
		me.send({event:me.conf.E_JOIN,sname:name},function(obj){
			data = obj.data || false;
			if(!!data){
				me.cache.id = data.id;
				me.cache.uname = data.uname;
				
				if(data.online){
					
					me.addOnlineItems(data.online);
					
					/**
					 * 注册鼠标经过/点击事件,注册给onlineItem父对象实现live方法
					 */
					
					Easy.on(me.get(me.conf.D_ONLINELIST),'mouseover',function(e){
						if(Easy.hasClass(e.target,me.conf.ONL_ITEM_CLASS)){
							Easy.addClass(e.target,me.conf.ONL_ITEM_OVERCLASS);
						}
					});
					
					Easy.on(me.get(me.conf.D_ONLINELIST),'mouseout',function(e){
						if(Easy.hasClass(e.target,me.conf.ONL_ITEM_CLASS)){
							Easy.removeClass(e.target,me.conf.ONL_ITEM_OVERCLASS);
						}
					},window.document,'div');
					
					Easy.on(me.get(me.conf.D_ONLINELIST),'click',function(e){
						if(Easy.hasClass(e.target,me.conf.ONL_ITEM_CLASS)){
							//console.log(this.id);
						}
					},document,'div');
					
				}
				
				me.get(me.conf.D_USER).innerHTML=me.cache.uname;
				//开始获取消息
				me.getList();
				
			}else{
				//异常...
			}
			
		});
		
		
	},
	
	/**
	 * 发送消息
	 * @param data 参数
	 * @param fn  成功回调
	 */
	send:function(data,fn){
		var me = this;
		
//		$.get('im',data,function(data){
//			fn.call(me,Easy.parseJson(data));
//		});
		
		Easy.request({
			
			url:me.conf.URL,
			
			method:me.conf.METHOD,
			
			data:data,
			
			succeed:function(text){
				var obj = null;
				try{
					obj=Easy.parseJson(text);
				}catch (e) {
					//alert(e);
				}
				
				fn.call(me,obj);
				
			},
			
			failed:function(xhr){
				//失败处理...
			}
			
			
		});
			
	},
	
	putMsg:function(text,gid){
		var me = this;
		
		me.addItemSelf(text);
		me.send({
			
			event:me.conf.E_PUTMSG,
			
			text:text,
			
			sid:me.cache.id,
			
			gid: gid || me.conf.ALL
			
		},function(obj){});
		
	},
	/**
	 * 获取消息列表
	 */
	getList:function(){
		var me = this;
		
		me.send({
			
			event:me.conf.E_GETLIST,
			
			sid:me.cache.id
			
			
		},function(obj){
			me.addItemOther(obj);
			me.getList();
		});
		
	},
	/**
	 * 
	 * @param obj
	 */
	addItemOther:function(obj){
		var me= this;
		var data = obj.data || false;
		if(!!data ){
			if(Easy.type(data) == "array"){
				//如果数据是数组
				Easy.each(data,function(i,v){
					me.eventItem(v);
				});
				
			}else{
				me.eventItem(v);
			}
		}
	},
	
	/**
	 * 根据event类型处理消息
	 * @param v
	 */
	eventItem:function(v){
		var me =this;
		if(v.event == me.conf.E_JOIN){
			me.addOnlineItems(v.text);
		}else if(v.event == me.conf.E_EXIT){
			me.removeOnlieItem(v.text.id);
		}else{
			me.addItem(me.conf.M_ITEM_OTHER_CLASS,v.sname, v.stime ,v.text);
		}
	},
	
	addItemSelf:function(text){
		var me = this;
		me.addItem(me.conf.M_ITEM_SELF_CLASS, me.cache.uname, Easy.format('HH:mm:ss',new Date()), text);
	},
	
	/**
	 * 将消息替换模板,并添加
	 * @param className
	 * @param name
	 * @param time
	 * @param text
	 */
	addItem:function(className,name,time,text){
		
		var args = arguments,me=this;
		var html;
		html = me.conf.M_ITEM_HTML.replace(/\{(\d)\}/g,function(m,c){
			return args[c];
		});
		
		me.addHtml(me.conf.D_MSG,html);
	},
	
	addOnlineItems:function(data){
		var html=[],me=this;
		if(Easy.type(data) == "array"){
			Easy.each(data,function(i,v){
				html.push(me.getOnlineItem(v.id,v.name));
			});
		}else{
			html.push(me.getOnlineItem(data.id,data.name));
		}
		me.addHtml(me.conf.D_ONLINELIST,html.join(""));
	},
	/**
	 * 初始化添加在线列表
	 * @param id
	 * @param name
	 * @returns
	 */
	getOnlineItem:function(id,name){
		var args = arguments,me=this;
		var html;
		if(me.get(id)){
			return '';
		}
		html=me.conf.ONL_ITEM_HTML.replace(/\{(\d)\}/g,function(m,c){
			return args[c];
		});
		
		return  html;
	},
	
	//添加在线
	insertOnlineItem:function(id,name){
	},
	
	//移除在线
	removeOnlieItem:function(id){
		var me = this;
		me.get(id).parentNode.removeChild(me.get(id));
	}
	
});

Easy.addReady(function(){
	
	var easy = new Easy();
	
	var name = Easy.getQueryValue(location.href,'name');
	if( name!= null ){
		easy.init(name);
	}else{
		easy.init();
	}
		
	
	
});


//console.dir(Easy);
})(window);
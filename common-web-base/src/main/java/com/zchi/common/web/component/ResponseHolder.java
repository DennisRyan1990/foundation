package com.zchi.common.web.component;

import org.springframework.stereotype.Component;

/**
 * @comment 返回实体
 */
@Component
public class ResponseHolder {

	private static  ThreadLocal<Object> model = new ThreadLocal<>();

	/**
	 * 清除返回对象
	 */
	public static void clean() {
		model.remove();
	}

	public static Object get(){
		return model.get();
	}

	public static void set(Object obj){
		model.set(obj);
	}
}

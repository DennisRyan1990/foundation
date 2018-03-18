package com.zchi.common.aop;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Map;

public class LoggingAspect {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private String requestPath = null; // 请求地址
	private String userName = null; // 用户名
	private Map<?, ?> inputParamMap = null; // 传入参数
	private Object result; // 存放输出结果
	private long startTimeMillis = 0; // 开始时间
	private long endTimeMillis = 0; // 结束时间

	private ObjectMapper objectMapper;

	public LoggingAspect() {
		objectMapper = new ObjectMapper();
	}

	@Before("execution(* com.htjc..controller..*.*(..))")
	public void doBeforeInServiceLayer(JoinPoint joinPoint) {
		startTimeMillis = System.currentTimeMillis(); // 记录方法开始执行的时间
	}

	@After("execution(* com.htjc..controller..*.*(..))")
	public void doAfterInServiceLayer(JoinPoint joinPoint) {
		endTimeMillis = System.currentTimeMillis(); // 记录方法执行完成的时间
		this.printOptLog();
	}

	@Around("execution(* com.htjc..controller..*.*(..))")
	public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
		try {
			/**
			 * 1.获取request信息 2.根据request获取session 3.从session中取出登录用户信息
			 */
			RequestAttributes ra = RequestContextHolder.getRequestAttributes();
			ServletRequestAttributes sra = (ServletRequestAttributes) ra;
			HttpServletRequest request = sra.getRequest();
			StringBuilder sb = new StringBuilder();
			sb.append(printHeading("HttpServletRequest"));
			sb.append(printValue("HTTP Method", request.getMethod()));
			sb.append(printValue("Request URI", request.getRequestURI()));
			sb.append(printValue("Parameters", getParamsMultiValueMap(request)));
			sb.append(printValue("Headers", getRequestHeaders(request)));
			logger.info(sb.toString());
		} catch(Exception e) {}
		

		// 执行完方法的返回值：调用proceed()方法，就会触发切入点方法执行
		result = pjp.proceed();// result的值就是被拦截方法的返回值
		return result;
	}

	private void printOptLog() {
		StringWriter sw = new StringWriter();
		String optTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTimeMillis);
		StringBuilder sb = new StringBuilder();
		sb.append(printHeading("Response"));
		sb.append(printValue("op_time", (endTimeMillis - startTimeMillis) + "ms "));
		try {
			objectMapper.writeValue(sw, this.result);
		} catch (JsonGenerationException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		} catch (JsonMappingException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		} catch (IOException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
		sb.append(printValue("Body In Json", sw.toString()));
		logger.info(sb.toString());
	}

	protected final HttpHeaders getRequestHeaders(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		Enumeration<?> names = request.getHeaderNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			Enumeration<String> values = request.getHeaders(name);
			while (values.hasMoreElements()) {
				headers.add(name, values.nextElement());
			}
		}
		return headers;
	}

	protected final MultiValueMap<String, String> getParamsMultiValueMap(HttpServletRequest request) {
		Map<String, String[]> params = request.getParameterMap();
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<String, String>();
		for (String name : params.keySet()) {
			if (params.get(name) != null) {
				for (String value : params.get(name)) {
					multiValueMap.add(name, value);
				}
			}
		}
		return multiValueMap;
	}

	public String printHeading(String heading) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append(String.format("%20s:\n", heading));
		return sb.toString();
	}

	public String printValue(String label, Object value) {
		if (value != null && value.getClass().isArray()) {
			value = CollectionUtils.arrayToList(value);
		}
		return String.format("%20s = %s\n", label, value);
	}
}

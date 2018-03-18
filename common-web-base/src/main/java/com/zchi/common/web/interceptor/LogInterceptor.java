package com.zchi.common.web.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zchi.common.utils.CommonUtil;
import com.zchi.common.web.component.ResponseHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;

/**
 * <h3>For Web</h3>
 * 通用日志记录拦截器
 * Created by zchi on 16/5/11.
 */
public class LogInterceptor extends HandlerInterceptorAdapter {
    protected Logger logger = LogManager.getLogger(getClass());
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        if (StringUtils.isNotBlank(requestURI)) {
            long beginTime = System.currentTimeMillis();
            request.setAttribute("beginTime", beginTime);
            Map paramMap = request.getParameterMap();
            Iterator it = paramMap.keySet().iterator();
            StringBuffer sb = new StringBuffer("[" + request.getRequestURI() + "]");
            while (it.hasNext()) {
                Object key = it.next();
                String value = request.getParameter(key.toString());
                if (value != null && value.length() > 1000) {
                    sb.append("[");
                    sb.append(key).append("=").append("...");
                    sb.append("]");
                } else {
                    sb.append("[");
                    sb.append(key).append("=").append(escape(value));
                    sb.append("]");
                }
            }
            String userAgent = request.getHeader("user-agent");
            sb.append("[agent=").append(escape(userAgent)).append("]");
            String realIP = CommonUtil.getIpAddr(request);
            sb.append("[ip=").append(escape(realIP)).append("]");
            sb.append("[session=").append(request.getSession().getId()).append("]");
            logger.info(sb.toString());
        }
        return true;
    }

    @Override public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
        Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        if (StringUtils.isNotBlank(requestURI)) {
            long beginTime = (Long) request.getAttribute("beginTime");
            long timeConsume = System.currentTimeMillis() - beginTime;
            StringBuilder sb = new StringBuilder("[" + request.getRequestURI() + "]");
            sb.append("[").append(request.getMethod()).append("]");
            if (handler != null) {
                sb.append("[timeconsume=")
                    .append(timeConsume).append("][session=").append(request.getSession().getId())
                    .append("]");
                Object result = ResponseHolder.get();
                ResponseHolder.clean();
                if (result != null) {
                    String responseJson = objectMapper.writeValueAsString(result);
                    sb.append("[return=").append(
                        StringUtils.substring(responseJson, 0, 1000))
                        .append("]");
                }
            } else {
                sb.append("[timeconsume=" + timeConsume).append("]");
            }
            logger.info(sb.toString());

        }
    }

    /**
     * 将参数值中的“[]”进行转义
     *
     * @param value
     * @return
     */
    public static String escape(String value) {
        if (StringUtils.isNotBlank(value)) {
            return value.replace("[", "\\[").replace("]", "\\]");
        }
        return "";
    }
}

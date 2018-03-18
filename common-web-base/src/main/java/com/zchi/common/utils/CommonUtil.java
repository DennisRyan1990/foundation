package com.zchi.common.utils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zchi on 16/5/11.
 */
public class CommonUtil {

    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static Integer getTotalPage(Integer limit, Integer total) {
        int totalPage = 0;
        if (limit == -1) {
            totalPage = 1;
        } else if (limit < -1 || limit == 0) {
            return 0;
        } else {
            if (total <= limit) {
                if (total > 0) {
                    totalPage = 1;
                }
            } else {
                int tmp = total % limit;
                if (tmp == 0) {
                    totalPage = total / limit;
                } else {
                    totalPage = (total - tmp) / limit + 1;
                }
            }
        }
        return totalPage;
    }

    public static Integer getOffset(Integer current, Integer limit) {
        if (current <= 0 || limit == -1) {
            current = 1;
        }
        return (current - 1) * limit;
    }

    public static String getGUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static StringBuilderPlus str4Sign(Object req, List<String> exclude) {
        Map<String, String> paramMap = CommonUtil.object2Map(req, true);
        return str4Sign(paramMap, exclude);
    }

    public static StringBuilderPlus str4Sign(Map<String, String> paramMap, List<String> exclude) {
        TreeMap<String, String> sortedMap = new TreeMap<String, String>();
        sortedMap.putAll(paramMap);
        StringBuilderPlus str4Sign = new StringBuilderPlus();
        for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
            if (exclude == null || (exclude != null && !exclude.contains(entry.getKey()))) {
                if (str4Sign.getStringBuilder().length() > 0) {
                    str4Sign.append("&");
                }
                str4Sign.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        return str4Sign;

    }

    /**
     * object 转 map
     *
     * @param obj
     * @param containSuper 是否包含父类
     * @return
     */
    public static Map<String, String> object2Map(Object obj, boolean containSuper) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            Class<?> clazz = obj.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                String key = field.getName();
                Method method =
                    obj.getClass().getDeclaredMethod("get" + capitalise(field.getName()));
                Object value = method.invoke(obj);
                if (value != null) {
                    if (method.getReturnType().getSimpleName().equals("Date")) {
                        map.put(key, date2Str((Date) value));
                    } else {
                        map.put(key, String.valueOf(value));
                    }
                }
            }
            if (containSuper) {
                Class<?> superClass = clazz.getSuperclass();
                if (!"Object".equals(superClass.getSimpleName())) {
                    fields = superClass.getDeclaredFields();
                    for (Field field : fields) {
                        String key = field.getName();
                        Method method =
                            superClass.getDeclaredMethod("get" + capitalise(field.getName()));
                        Object value = method.invoke(obj);
                        if (value != null) {
                            if (method.getReturnType().getSimpleName().equals("Date")) {
                                map.put(key, date2Str((Date) value));
                            } else {
                                map.put(key, String.valueOf(value));
                            }
                        }
                    }
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static String capitalise(String str) {
        return str.substring(0, 1).toUpperCase() + str.replaceFirst("\\w", "");
    }
    public static String date2Str(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date);
    }
}


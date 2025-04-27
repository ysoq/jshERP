package com.jsh.erp.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.lang.reflect.Field;
import com.fasterxml.jackson.annotation.JsonFormat;

public class ResponseJsonUtil {
    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    static {
        FORMAT.setTimeZone(TimeZone.getTimeZone("GMT+8"));
    }

    /**
     * 响应过滤器
     */
    public static final class ResponseFilter extends ExtJsonUtils.ExtFilter implements ValueFilter {
        @Override
        public Object process(Object object, String name, Object value) {
            if (name.equals("createTime") || name.equals("modifyTime")||name.equals("updateTime")) {
                return value;
            } else if (value instanceof Date) {
                // 获取字段的JsonFormat注解
                JsonFormat jsonFormat = getJsonFormatAnnotation(object, name);
                if (jsonFormat != null && !jsonFormat.pattern().isEmpty()) {
                    try {
                        String pattern = jsonFormat.pattern();
                        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
                        // 处理时区
                        if (!jsonFormat.timezone().isEmpty()) {
                            formatter.setTimeZone(TimeZone.getTimeZone(jsonFormat.timezone()));
                        }
                        return formatter.format((Date) value);
                    } catch (IllegalArgumentException e) {
                        // 格式或时区无效时回退到默认格式
                        return FORMAT.format(value);
                    }
                } else {
                    // 无注解或格式无效时使用默认格式化
                    return FORMAT.format(value);
                }
            } else {
                return value;
            }
        }
    }

    // 辅助方法：递归查找字段的JsonFormat注解
    private static JsonFormat getJsonFormatAnnotation(Object object, String name) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(name);
                return field.getAnnotation(JsonFormat.class);
            } catch (NoSuchFieldException e) {
                // 继续查找父类
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    /**
     *
     * @param responseCode
     * @return
     */
    public static String backJson4HttpApi(ResponseCode responseCode) {
        if (responseCode != null) {
            String result = JSON.toJSONString(responseCode, new ResponseFilter(),
                    SerializerFeature.DisableCircularReferenceDetect,
                    SerializerFeature.WriteNonStringKeyAsString);
            result = result.replaceFirst("\"data\":\\{", "");
            return result.substring(0, result.length() - 1);
        }
        return null;
    }

    /**
     * 验证失败的json串
     * @param code
     * @return
     */
    public static String backJson4VerifyFailure(int code) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("message", "未通过验证");
        return JSON.toJSONString(new ResponseCode(code, map), new ResponseFilter(),
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteNonStringKeyAsString);
    }

    /**
     * 成功的json串
     * @param responseCode
     * @return
     */
    public static String backJson(ResponseCode responseCode) {
        if (responseCode != null) {
            return JSON.toJSONString(responseCode, new ResponseFilter(),
                    SerializerFeature.DisableCircularReferenceDetect,
                    SerializerFeature.WriteNonStringKeyAsString);
        }
        return null;
    }

    public static String returnJson(Map<String, Object> map, String message, int code) {
        map.put("message", message);
        return backJson(new ResponseCode(code, map));
    }



    public static <T> String backJson(IPage<T> list, String message, int code) {
        Map<String, Object> objectMap = new HashMap<String, Object>();
        objectMap.put("rows", list.getRecords());
        objectMap.put("total", list.getTotal());
        return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
    }

}

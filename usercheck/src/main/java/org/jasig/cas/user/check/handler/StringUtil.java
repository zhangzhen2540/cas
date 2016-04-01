package org.jasig.cas.user.check.handler;

/**
 * Created by csf on 2015/5/16.
 *
 * @author csf
 */
public class StringUtil {

    /**
     * 过滤空串
     *
     * @param str 字符串
     * @return 返回结果
     */
    public static String filterNull(String str) {
        if (str == null) {
            return "";
        } else {
            return str.trim();
        }
    }

    /**
     * 两个字符串是否相等
     *
     * @param source 原串
     * @param target 目标串
     * @return 结果
     */
    public static boolean stringEquals(String source, String target) {
        return isEmpty(source) && isEmpty(target) || !(isEmpty(source) || isEmpty(target)) && source.equals(target);
    }

    public static boolean isEmpty(String str) {
        return filterNull(str).equals("");
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}

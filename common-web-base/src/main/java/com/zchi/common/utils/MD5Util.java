package com.zchi.common.utils;
import java.security.MessageDigest;

/**
 * Created by zchi on 2015/2/2 16:20.
 */
public class MD5Util {
	public final static String MD5(String s) {
		char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public static void main(String[] args) {
		System.out.println(MD5Util.MD5("cc7255521"));
		System.out.println("28707838fcfed3f0bea9a11791933002".equals(MD5Util.MD5("cc7255521")));
	}
}

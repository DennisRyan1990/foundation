package com.zchi.common.utils;

import org.apache.commons.codec.net.BCodec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class UAuthUtils {
  
	private static String key = "8w.zch$i.com@$#->1base1<-@$#3w.zch$i.com";

	/**
	 * 使用配置文件中的秘钥,对编码为utf-8的字符串进行des加密
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String desEncode2Hex(String str) throws Exception
	{
	  return desEncode2Hex(str, key, "utf-8") ;
	}
	
	/**
	 * 使用配置文件中的秘钥,对目标字符串进行des解密,结果为utf-8的字符串
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String desDecodeFromHex(String str) throws Exception
	{
//	  TODO  推荐用这个方法, 但是大部分代码使用的是decodeUAuth, 所以在两个入口都进行旧版加密的处理
    if (str.startsWith("=?"))
    {
      BCodec codec = new BCodec("UTF-8");
      return codec.decode(str);
    }
	  
	  return desDecodeFromHex(str, key, "utf-8") ;
	}
	
	/**
	 * 使用指定的key,对指定charset的目标字符串进行des加密, 结果返回为hex字符串 
	 * @param str 需要加密的字符串
	 * @param key 秘钥
	 * @param charsetName 字符集
	 * @return hex字符串
	 * @throws Exception
	 */
	public static String desEncode2Hex(String str, String key, String charsetName) throws Exception
	{
    Cipher cipher = Cipher.getInstance("DES");
    SecretKey secretKey = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(key.getBytes())) ;
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    byte[] encrypted = cipher.doFinal(str.getBytes(charsetName)) ;
    
    return byte2hex(encrypted) ;
	}
	
	/**
	 * 使用指定的key,对的目标字符串进行des解密, 结果返回为指定charset的原文字符串
	 * @param str
	 * @param key
	 * @param charsetName
	 * @return
	 * @throws Exception
	 */
	public static String desDecodeFromHex(String str, String key, String charsetName) throws Exception 
	{
    Cipher cipher = Cipher.getInstance("DES");
    SecretKey secretKey = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(key.getBytes())) ;
    cipher.init(Cipher.DECRYPT_MODE, secretKey);
    
    byte[] decodedString = cipher.doFinal(hex2byte(str)) ;
    
    return new String(decodedString, charsetName);
	}
	
	public static String calculateUAuth(String uid) throws Exception
	{
	  return desEncode2Hex(uid) ;
  }

  public static String decodeUAuth(String uauth) throws Exception 
  {
    if (uauth.startsWith("=?"))
    {
      BCodec codec = new BCodec("UTF-8");
      return codec.decode(uauth);
    }
    
    return desDecodeFromHex(uauth) ;
  }
  
  


  public static String byte2hex(byte bytes[]){
     StringBuffer retString = new StringBuffer();
     for (int i = 0; i < bytes.length; ++i)
     {
       retString.append(Integer.toHexString(0x0100 + (bytes[i] & 0x00FF)).substring(1).toUpperCase());
     }
     return retString.toString();
   }

  public static byte[] hex2byte(String hex) {
     byte[] bts = new byte[hex.length() / 2];
     for (int i = 0; i < bts.length; i++) {
        bts[i] = (byte) Integer.parseInt(hex.substring(2*i, 2*i+2), 16);
     }
     return bts;
   }
  
  public static void main(String[] args) throws NoSuchPaddingException, Exception {
	System.out.println(UAuthUtils.calculateUAuth("comhtjc"));
     // System.out.println(UAuthUtils.decodeUAuth("8CC7F8F25C9698C31B4EA735CD9BC5EB"));
     // System.out.println(UAuthUtils.decodeUAuth("553B053EB1804950"));



      //pushService.push(map);
  }
}

package com.zchi.common.utils;

/**
 * Created by zchi on 2016-07-06 14:21
 * Description:
 */
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证各类数据格式
 *
 * @author wanzhuo
 * @date 2015年11月13日 下午9:01:07
 */
public final class ValidatorUtils {

    /**
     * 正则表达式：验证用户名
     */
    public static final String REGEX_USERNAME = "^[a-zA-Z]\\w{5,17}$";

    /**
     * 正则表达式：验证密码
     */
    public static final String REGEX_PASSWORD = "^[a-zA-Z0-9]{6,16}$";

    /**
     * 正则表达式：验证手机号
     */
    public static final String REGEX_MOBILE = "^(1[3-9])\\d{9}$";

    /**
     * 正则表达式：验证邮箱
     */
    public static final String REGEX_EMAIL =
        "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    /**
     * 正则表达式：验证汉字
     */
    public static final String REGEX_CHINESE = "^[\u4e00-\u9fa5],{0,}$";

    /**
     * 正则表达式：验证URL
     */
    public static final String REGEX_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";

    /**
     * 正则表达式：验证IP地址
     */
    public static final String REGEX_IP_ADDR = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";

    /**
     * 正则表达式：验证QQ地址
     */
    public static final String REGEX_QQ = "^[1-9]\\d{4,9}$";

    /**
     * 正则表达式：验证Double数值
     */
    public static final String REGEX_DOUBBLE_VALUE = "^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$";

    /**
     * 正则表达式:验证日期yyyy-MM-dd
     */
    public static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    /**
     * 校验金额数值
     *
     * @param doubleValue
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isDoubleValue(String doubleValue) {
        return Pattern.matches(REGEX_DOUBBLE_VALUE, doubleValue);
    }

    /**
     * 校验用户名
     *
     * @param username
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUsername(String username) {
        return Pattern.matches(REGEX_USERNAME, username);
    }

    /**
     * 校验密码
     *
     * @param password
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPassword(String password) {
        return Pattern.matches(REGEX_PASSWORD, password);
    }

    /**
     * 校验手机号
     *
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMobile(String mobile) {
        return Pattern.matches(REGEX_MOBILE, mobile);
    }

    /**
     * 校验邮箱
     *
     * @param email
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isEmail(String email) {
        return Pattern.matches(REGEX_EMAIL, email);
    }

    /**
     * 校验汉字
     *
     * @param chinese
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isChinese(String chinese) {
        return Pattern.matches(REGEX_CHINESE, chinese);
    }

    /**
     * 校验URL
     *
     * @param url
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUrl(String url) {
        return Pattern.matches(REGEX_URL, url);
    }

    /**
     * 校验IP地址
     *
     * @param ipAddr
     * @return
     */
    public static boolean isIPAddr(String ipAddr) {
        return Pattern.matches(REGEX_IP_ADDR, ipAddr);
    }

    public static boolean isQQ(String qq) {
        return Pattern.matches(REGEX_QQ, qq);
    }

    /*********************************** 身份证验证开始 ****************************************/
    /**
     * 身份证号码验证 1、号码的结构 公民身份号码是特征组合码，由十七位数字本体码和一位校验码组成。排列顺序从左至右依次为：六位数字地址码，
     * 八位数字出生日期码，三位数字顺序码和一位数字校验码。 2、地址码(前六位数）
     * 表示编码对象常住户口所在县(市、旗、区)的行政区划代码，按GB/T2260的规定执行。 3、出生日期码（第七位至十四位）
     * 表示编码对象出生的年、月、日，按GB/T7408的规定执行，年、月、日代码之间不用分隔符。 4、顺序码（第十五位至十七位）
     * 表示在同一地址码所标识的区域范围内，对同年、同月、同日出生的人编定的顺序号， 顺序码的奇数分配给男性，偶数分配给女性。 5、校验码（第十八位数）
     * （1）十七位数字本体码加权求和公式 S = Sum(Ai * Wi), i = 0, ... , 16 ，先对前17位数字的权求和
     * Ai:表示第i位置上的身份证号码数字值 Wi:表示第i位置上的加权因子 Wi: 7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2
     * （2）计算模 Y = mod(S, 11) （3）通过模得到对应的校验码 Y: 0 1 2 3 4 5 6 7 8 9 10 校验码: 1 0 X 9 8 7 6 5 4 3 2
     */

    private static final String errorInfo = "身份证无效，不是合法的身份证号码";// 记录错误信息
    private static final String[] ValCodeArr =
        {"1", "0", "x", "9", "8", "7", "6", "5", "4", "3", "2"};
    private static final Integer[] Wi = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    public static String isIdCard(String idCard) {
        String Ai = "";
        if (isValidLength(idCard)) {
            Ai = idCard.length() == 18 ? idCard.substring(0, 17) : idCard;
            if (isNumeric(Ai)) {
                int TotalmulAiWi = 0;
                for (int i = 0; i < 17; i++) {
                    TotalmulAiWi =
                        TotalmulAiWi + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Wi[i];
                }
                int modValue = TotalmulAiWi % 11;
                String strVerifyCode = ValCodeArr[modValue];
                Ai = Ai + strVerifyCode;
                if ((idCard.length() == 18 && Ai.equals(idCard.toLowerCase()))
                    || idCard.length() == 15) {
                    if (isValidDate(idCard, Ai) && isValidAreaCode(idCard, Ai)) {
                        return "";
                    }
                }
            }
        }
        return errorInfo;
    }

    public static String isClerkIdCard(String idCard, Integer age) {
        String Ai = "";
        if (isValidLength(idCard)) {
            Ai = idCard.length() == 18 ? idCard.substring(0, 17) : idCard;
            if (isNumeric(Ai)) {
                int TotalmulAiWi = 0;
                for (int i = 0; i < 17; i++) {
                    TotalmulAiWi =
                        TotalmulAiWi + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Wi[i];
                }
                int modValue = TotalmulAiWi % 11;
                String strVerifyCode = ValCodeArr[modValue];
                Ai = Ai + strVerifyCode;
                if ((idCard.length() == 18 && Ai.equals(idCard.toLowerCase()))
                    || idCard.length() == 15) {
                    if (isValidAreaCode(idCard, Ai) && isValidAge(age, Ai)) {
                        return "";
                    }
                }
            }
        }
        return errorInfo;
    }


    // ================ 号码的长度 15位或18位 ================
    private static Boolean isValidLength(String idCard) {
        return !(idCard.length() != 15 && idCard.length() != 18);
    }

    private static Boolean isValidAge(Integer age, String Ai) {
        Boolean flag = Boolean.FALSE;
        String strYear = Ai.substring(6, 10);// 年份
        String strMonth = Ai.substring(10, 12);// 月份
        String strDay = Ai.substring(12, 14);// 月份
        try {
            LocalDate birthDate = LocalDate
                .of(Integer.parseInt(strYear), Integer.parseInt(strMonth),
                    Integer.parseInt(strDay));
            if (birthDate.plusYears(age).isBefore(LocalDate.now())) {
                flag = true;
            }
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    // ================ 出生年月是否有效 ================
    private static Boolean isValidDate(String idCard, String Ai) {
        String strYear = Ai.substring(6, 10);// 年份
        String strMonth = Ai.substring(10, 12);// 月份
        String strDay = Ai.substring(12, 14);// 月份
        if (isDate(strYear + "-" + strMonth + "-" + strDay) == false) {
            return false;
        } else {
            GregorianCalendar gc = new GregorianCalendar();
            SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
            try {
                if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150 ||
                    (gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay)
                        .getTime()) < 0) {
                    return false;
                } else {
                    Calendar cal = Calendar.getInstance();
                    int yearNow = cal.get(Calendar.YEAR);
                    int monthNow = cal.get(Calendar.MONTH) + 1;
                    int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
                    int age = yearNow - Integer.parseInt(strYear);
                    if ((monthNow == Integer.parseInt(strMonth) && dayOfMonthNow < Integer
                        .parseInt(strDay)) || monthNow < Integer.parseInt(strMonth)) {
                        age--;
                    }
                    if (age > 55 || age < 18) {
                        return false;
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        }

        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
            return false;
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            return false;
        }
        return true;
    }

    // ================ 地区码是否有效 ================
    private static Boolean isValidAreaCode(String idCard, String Ai) {
        Hashtable<String, String> h = GetAreaCode();
        return h.get(Ai.substring(0, 2)) != null;
    }

    /**
     * 功能：判断字符串是否为数字
     *
     * @param str
     * @return
     */
    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 功能：判断字符串是否为日期格式
     *
     * @param strDate
     * @return
     */
    private static boolean isDate(String strDate) {
        Pattern pattern = Pattern.compile(
            "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        Matcher m = pattern.matcher(strDate);
        return m.matches();
    }

    /**
     * 功能：设置地区编码
     *
     * @return Hashtable 对象
     */
    public static Hashtable<String, String> GetAreaCode() {
        Hashtable<String, String> hashtable = new Hashtable<String, String>();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");
        return hashtable;
    }

    /**
     * 参数验证
     *
     * @param date       验证的日期字符串
     * @param isOptional 参数是否可选(若可选,如果date为null返回true)
     */
    public static boolean validateDate(String date, boolean isOptional) {
        if (date == null) {
            return isOptional;
        }
        return DATE_PATTERN.matcher(date).matches();
    }

    public static void main(String[] args) {
        String idcard = ValidatorUtils.isIdCard("320321200001270213");
        String clerkcard = ValidatorUtils.isClerkIdCard("320321200001270213", 16);
        System.out.println(idcard + ";" + clerkcard);
    }
}

package com.macrolab.myAcct.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.security.CodeSource;
import java.security.MessageDigest;
import java.security.ProtectionDomain;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**
 * @author hzhlu
 * @version 1.0.20120427 add 驼峰式字符串格式化
 */
public class MyTools {

    /**
     * Logger for this class
     */
    public static final String FACE_SUCCESS = "\\(^o^)/YES! ";
    public static final String FACE_FAILED = "{@o@}!!Err! ";
    public static final String FACE_WARN = ")@_@(..Warn ";
    public static final String FACE_WAITTING = "[O_O]..Wait ";

    public static byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(buffer);
            oos.writeObject(obj);
            oos.close();
            return buffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("error writing to byte-array!");
        }
    }

    public static List<String> str2List(String values, String split) {
        String[] temp = values.split(split); // 交易使用的优惠券
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < temp.length; i++) {
            list.add(temp[i]);
        }
        return list;
    }

//	public static void makeDir(String workDir) {
//		File dir = new File(workDir);
//		boolean isDirCreated = dir.mkdirs();
//		if (isDirCreated)
//			logger.trace("工作目录不存在! 创建：" + dir);
//		else
//			logger.trace("工作目录已经存在! ：" + dir);
//	}

    public static Object deserialize(byte[] bytes) throws ClassNotFoundException {
        try {
            ByteArrayInputStream input = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(input);
            return ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("error reading from byte-array!");
        }
    }

    public static Object serializedCopy(Object obj) {
        try {
            return deserialize(serialize(obj));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("this shouldn't happen");
        }
    }

    /**
     * 计算两个日期相差天数。 用第一个日期减去第二个。如果前一个日期小于后一个日期，则返回负数
     *
     * @param checkDate 待比较日期
     * @param baseDate  基准期数
     * @return 两个日期相差天数
     */
    public static long diffDays(Date checkDate, Date baseDate) {
        return (checkDate.getTime() - baseDate.getTime()) / (24 * 60 * 60 * 1000);
    }

    /**
     * @param mode       Calendar.MONTH, Calendar.HOUR, Calendar.DATE, Calendar.YEAR,
     *                   Calendar.WEEK_OF_YEAR
     * @param start      起始值 0为当前系统时间，<0 表示历史时间，>0表示未来时间
     * @param randomSeed
     * @return
     */
    public static Date getRandomDate(int mode, int start, int randomSeed) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(mode, start + new Random().nextInt(randomSeed));
        return calendar.getTime();
    }

    /**
     * 把一个字节数组的串格式化成十六进制形式. 格式化后的样式如下<br>
     * <code>
     * -------   0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15
     * 00000H  30 31 30 30 A2 38 44 81 88 C1 80 30 00 00 00 00 ; 0100�8D����0
     * 00016H  00 00 00 01 33 31 30 30 30 30 30 31 30 39 31 39 ; 310000010919
     * 00032H  30 32 32 36 37 30 30 33 34 36 31 32 35 39 35 39 ; 0226700346125959
     * 00048H  31 32 31 37 30 30 30 30 30 31 32 39 32 30 38 32 ; 1217000001292082
     * 00064H  32 32 32 32 37 30 33 30 38 32 32 32 32 32 37 30 ; 2222703082222270
     * </code>
     *
     * @param b 需要格式化的字节数组
     * @return 格式化后的串，其内容如上。可以直接输出。
     */
    public static String formatBytes2Hex(byte[] b) {
        String caption_str = "-------   0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15";
        String result_str = caption_str;
        byte[] chdata = new byte[19]; // 只保存十六进制串后面的字符串 (" : " 就占了三个字节，后面为16个字节)
        for (int i = 0; i < chdata.length; i++) {
            chdata[i] = ' '; // 初始化字符为空格
        }
        for (int i = 0; i < b.length; i++) {
            String hex_of_one_byte = Integer.toHexString((int) b[i]).toUpperCase();
            if (i % 16 == 0) {
                try {
                    String temp = new String(chdata, "GBK");
                    // temp = temp.replaceAll("\n", " ");
                    for (int j = 0; j < 32; j++) {
                        temp = temp.replace((char) j, '~');
                    }
                    // for (int j = 127; j < 256; j++) {
                    // temp = temp.replace((char)j, '!');
                    // }
                    // result_str = result_str + temp + "\n ";
                    result_str = result_str + temp + "\n ";
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Arrays.fill(chdata, (byte) 0x00);
                System.arraycopy(" ; ".getBytes(), 0, chdata, 0, 3);
                for (int j = 0; j < 5 - String.valueOf(i).length(); j++)
                    result_str = result_str + "0";
                result_str = result_str + i + ": ";

            }
            if (hex_of_one_byte.length() >= 2)
                result_str = result_str + " " + hex_of_one_byte.substring(hex_of_one_byte.length() - 2);
            else
                result_str = result_str + " 0" + hex_of_one_byte.substring(hex_of_one_byte.length() - 1);
            System.arraycopy(b, i, chdata, 3 + (i % 16), 1);
        }
        for (int j = 0; j < (16 - (b.length % 16)) % 16; j++)
            result_str = result_str + "   ";
        result_str = result_str + new String(chdata);

        result_str = result_str + "\n" + caption_str + "\n";

        // 剔除特殊字符
        for (int j = 127; j < 256; j++) {
            result_str = result_str.replaceAll(new String(new byte[]{(byte) j}), "?");
        }

        return result_str;
    }

    public static String long2timeStr(long time) {
        long mSec = time % 1000;
        time /= 1000;
        long year = time / (365 * 24 * 3600);
        time = time % (365 * 24 * 3600);
        long month = time / (30 * 24 * 3600);
        time = time % (30 * 24 * 3600);
        long day = time / (24 * 3600);
        time = time % (24 * 3600);
        long hour = time / 3600;
        time = time % 3600;
        long min = time / 60;
        time = time % 60;
        long sec = time;

        String result = String.format("%d年%d月%d日  %2d时%2d分%2d秒%3d毫秒", year, month, day, hour, min, sec, mSec);
        result = String.format("%2d时%2d分%2d秒%3d毫秒", hour, min, sec, mSec);
        return result;
    }

    /**
     * To hex string.
     *
     * @param value the value
     * @return the string
     */
    public static String toHexString(byte[] value) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        int j = value.length;
        char str[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte byte0 = value[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }

    /**
     * 将数字转换为字符串，左侧补零
     *
     * @return
     */
    public static String lpad0(int num, int len) {
        String result = Integer.toString(num);
        int length = len - result.length();
        for (int i = 0; i < length; i++) {
            result = "0" + result;
        }
        return result;
    }

    public static String lpad0(long num, int len) {
        String result = Long.toString(num);
        int length = len - result.length();
        for (int i = 0; i < length; i++) {
            result = "0" + result;
        }
        return result;
    }

    /**
     * 格式化数字 0 一位数字，这一位缺失显示为0。用来补零
     * <p>
     * # 一位数字, 这一位缺失就不显示
     * <p>
     * . 小数点，不用多说了吧
     * <p>
     * , 千位分隔符
     * <p>
     * E 科学计数法
     * <p>
     * % 百分比
     *
     * @param pattern
     * @param num
     * @return
     */
    public static String numFormat(String pattern, Object num) {
        DecimalFormat df = new DecimalFormat();
        df.applyPattern(pattern);
        return df.format(num).toString();
    }

    /**
     * 字符串左侧补空格
     *
     * @param str
     * @param len
     * @return
     */
    public static String lpadBlank(String str, int len) {
        String result = str;
        int length = len - result.length();
        for (int i = 0; i < length; i++) {
            result = " " + result;
        }
        return result;
    }

    /**
     * 字符串右侧补空格
     *
     * @param str
     * @param len
     * @return
     */
    public static String rpadBlank(String str, int len) {
        String result = str;
        int length = len - result.length();
        for (int i = 0; i < length; i++) {
            result = result + " ";
        }
        return result;
    }

    /**
     * 字符串右侧补字符串
     *
     * @param str
     * @param len
     * @param c
     * @return
     */
    public static String rpadChar(String str, int len, char c) {
        String result = str;
        int length = len - result.length();
        for (int i = 0; i < length; i++) {
            result = result + c;
        }
        return result;
    }

    /**
     * 字符串左侧补字符串
     *
     * @param str
     * @param len
     * @param c
     * @return
     */
    public static String lpadChar(String str, int len, char c) {
        String result = str;
        int length = len - result.length();
        for (int i = 0; i < length; i++) {
            result = c + result;
        }
        return result;
    }

    /**
     * 字符串左侧补'0'
     *
     * @param str
     * @param len
     * @return
     */
    public static String lpadZero(String str, int len) {
        String result = str;
        int length = len - result.length();
        for (int i = 0; i < length; i++) {
            result = "0" + result;
        }
        return result;
    }

    /**
     * C网sim卡apdu读取的原始值，推算到IMSI
     *
     * <code>
     * 460036011046950	004E02515B175C806701
     * 460031234567890	000C00155F565C806701
     * </code> APDU-C 换算IMSI卡号
     *
     * @param apduC
     * @return IMSI
     */
    public static String apduC2IMSI(String apduC) {
        String result = "";
        String val;
        byte[] code = hex2bytes(apduC);

        // seg 4
        byte[] imsiSeg4 = new byte[2];
        imsiSeg4[0] = code[9];
        imsiSeg4[1] = code[8];
        val = lpad0(Integer.parseInt(bytes2hex(imsiSeg4), 16), 3);
        for (int i = 0; i < val.length(); i++) {
            result += (Integer.parseInt(val.substring(i, i + 1)) + 1) % 10;
        }

        // seg 3
        byte[] imsiSeg3 = new byte[1];
        imsiSeg3[0] = code[6];
        val = lpad0(Integer.parseInt(bytes2hex(imsiSeg3), 16), 2);
        for (int i = 0; i < val.length(); i++) {
            result += (Integer.parseInt(val.substring(i, i + 1)) + 1) % 10;
        }

        // seg 1
        byte[] imsiSeg1 = new byte[2];
        imsiSeg1[0] = code[2];
        imsiSeg1[1] = code[1];
        val = lpad0(Integer.parseInt(bytes2hex(imsiSeg1), 16), 3);
        for (int i = 0; i < val.length(); i++) {
            result += (Integer.parseInt(val.substring(i, i + 1)) + 1) % 10;
        }

        // seg 2
        byte[] imsiSeg2 = new byte[3];
        imsiSeg2[0] = code[5];
        imsiSeg2[1] = code[4];
        imsiSeg2[2] = code[3];
        String val2 = lpadZero(Long.toBinaryString(Long.parseLong(bytes2hex(imsiSeg2), 16)), 24);
        String sval;
        // seg 2 - sub seg1
        sval = val2.substring(0, 10);
        val = lpad0(Integer.parseInt(sval, 2), 3);
        for (int i = 0; i < val.length(); i++) {
            result += (Integer.parseInt(val.substring(i, i + 1)) + 1) % 10;
        }

        // seg 2 - sub seg2
        sval = val2.substring(10, 14);
        result += Integer.parseInt(sval, 2);

        // seg 2 - sub seg3
        sval = val2.substring(14, 24);
        val = lpad0(Integer.parseInt(sval, 2), 3);
        for (int i = 0; i < val.length(); i++) {
            result += (Integer.parseInt(val.substring(i, i + 1)) + 1) % 10;
        }

        return result;
    }

    /**
     * 循环左移字符串
     *
     * @param str
     * @param step
     * @return
     */
    public static String leftShift(String str, int step) {
        if (str == null) {
            return "";
        }
        int len = str.length();
        if (len == 0) {
            return "";
        }
        int cnt = step % len;
        String result = str.substring(cnt, len) + str.substring(0, cnt);
        return result;
    }

    /**
     * 循环左移字符串
     *
     * @param str
     * @param step
     * @return
     */
    public static String rightShift(String str, int step) {
        if (str == null) {
            return "";
        }
        int len = str.length();
        if (len == 0) {
            return "";
        }
        int cnt = step % len;
        String result = str.substring(len - cnt, len) + str.substring(0, len - cnt);
        return result;
    }

    public static byte[] hex2bytes(String str) {
        if (null == str || 0 != str.length() % 2) {
            return null;
        }
        int len = str.length() / 2;
        byte[] buffer = new byte[len];
        for (int i = 0; i < len; i++) {
            buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
        }
        return buffer;
    }

    public static String bytes2hex(byte[] data) {
        if (null == data) {
            return null;
        }
        int len = data.length;
        StringBuilder strBuilder = new StringBuilder();

        for (int i = 0; i < len; i++) {
            if ((data[i] & 0xFF) < 16) {
                strBuilder.append('0');
            }
            strBuilder.append(java.lang.Integer.toHexString(data[i] & 0xFF));
        }
        return strBuilder.toString().toUpperCase();
    }

    /**
     * G网sim卡apdu读取的原始值，推算到IMSI
     *
     * <code>
     * 460000303123323	084906003030213332
     * 460011950200011	084906001159200011
     * </code> APDU-G 换算IMSI卡号
     *
     * @param apduG
     * @return
     */
    public static String apduG2IMSI(String apduG) {
        String result = "";
        for (int i = 0; i < 18; i = i + 2) {
            result += apduG.substring(i + 1, i + 2) + apduG.subSequence(i, i + 1);
        }
        return result.substring(3);
    }

    /**
     * 字符串右侧补'0'
     *
     * @param str
     * @param len
     * @return
     */
    public static String rpadZero(String str, int len) {
        String result = str;
        int length = len - result.length();
        for (int i = 0; i < length; i++) {
            result = result + "0";
        }
        return result;
    }


    protected static java.util.Random r = new java.util.Random();

    /**
     * Date to String.
     *
     * @param date
     * @return yyyy-MM-dd HH:mm:ss
     */
    static public String date2String(Date date) {
        if (date == null) {
            return "null";
        } else {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(date);
        }
    }

    static public String date2String(long date) {
        return date2String(new Date(date));
    }

    /**
     * Date to string.
     *
     * @param date
     * @param pattern yyyy-MM-dd HH:mm:ss, yyyy-MM-dd ...
     * @return date string
     */
    public static String date2String(Date date, String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(date);
    }

//	/**
//	 * String to date.
//	 *
//	 * @param dStr
//	 *            sample: 2001-12-2 21:12:39
//	 * @return date
//	 */
//	public static Date str2Date(String dStr) {
//		String[] parsePatterns = new String[] { "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss,SSS", "yyyy-MM-dd",
//				"yyyyMMdd" };
//		Date result = null;
//		try {
//			result = DateUtils.parseDate(dStr, parsePatterns);
//		} catch (ParseException e) {
//			logger.error("String2Date(String) Error! " + e.getMessage());
//		}
//		return result;
//	}

//	/**
//	 * 从Web URL上获取图片
//	 *
//	 * @param imgUrl
//	 * @param os
//	 * @return 图片大小 ，单位K
//	 * @throws IOException
//	 */
//	public static int getURLImg(String imgUrl, OutputStream os) throws IOException {
//		logger.debug("download image from:" + imgUrl);
//		URL url = new URL(imgUrl);
//		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
//
//		// File savefile = new File(savePath);
//		// FileOutputStream localFileOutputStream = null;
//		// localFileOutputStream = new FileOutputStream(savefile);
//		byte[] arrayOfByte = new byte[1024];
//		int i = 0;
//		InputStream inputStream = urlConn.getInputStream();
//		int size = 0;
//		while ((i = inputStream.read(arrayOfByte)) != -1) {
//			os.write(arrayOfByte, 0, i);
//			size++;
//		}
//		inputStream.close();
//		os.close();
//		urlConn.disconnect();
//		return size;
//	}

    /**
     * Gets the number password.
     *
     * @param length 生成随机数字的长度
     * @return String number
     */
    public static String getNumberPassword(int length) {
        // 删除了容易在屏幕上看混淆的字母和数字的所有字符用作密码字符空间
        char[] goodChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        if (length < 4)
            length = 4;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            sb.append(goodChar[r.nextInt(goodChar.length)]);
        }
        return sb.toString();
    }

    /**
     * Gets the code. 生成密码字符串,密码字符串的内容由goodChar数组中的字符构成.返回指
     * <p>
     * 定长度的密码
     *
     * @param length 密码长度,最短返回4位
     * @return String
     */
    public static String getStringPassword(int length) {
        // 删除了容易在屏幕上看混淆的字母和数字的所有字符用作密码字符空间
        char[] goodChar = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u',
                'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R',
                'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8', '9', '!', '@', '#', '$',
                '%', '^', '&', '*', '+', '-', '='};
        if (length < 4)
            length = 4;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            sb.append(goodChar[r.nextInt(goodChar.length)]);
        }
        return sb.toString();
    }

    /**
     * Random. [0-maxVal]
     *
     * @param maxVal 最大数
     * @return int [0-maxVal]
     */
    public static int random(int maxVal) {
        return r.nextInt(maxVal);
    }

    /**
     * 解析参数行中的值对,将结果放入到map中(值对中的名称转入到map时作了大写转换)
     *
     * <li>值对样例: " price=150.23 rate=0.80 limit=100 " <li>Map结果: { PRICE=150.23
     * , RATE=0.80 , LIMIT=100 }
     *
     * @param params the params
     * @return the hash map
     */
    public static HashMap<String, String> parseArgs(String params) {
        HashMap<String, String> result = new HashMap<String, String>();
        String[] x = Pattern.compile(" ").split(params);
        Pattern p = Pattern.compile("(\\w+)=(\\S+)");
        for (int i = 0; i < x.length; i++) {
            Matcher matcher = p.matcher(x[i]);
            if (matcher.find()) {
                result.put(matcher.group(1).toUpperCase(), matcher.group

                        (2));
            }
        }
        return result;
    }

    /**
     * Adds the day.
     *
     * @param nday 基准日期
     * @param nday 增加天数,可以是负数
     * @return date
     */
    public static Date addDay(int nday) {
        return addDay(new Date(), nday);
    }

    public static Date addDay(Date date, int nday) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, nday);
        return calendar.getTime();
    }

    /**
     * Adds the month.
     *
     * @param nMonth 基准日期
     * @param nMonth 增加月数,可以是负数
     * @return date
     */
    public static Date addMonth(int nMonth) {
        return addMonth(new Date(), nMonth);
    }

    public static Date addMonth(Date date, int nMonth) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, nMonth);
        return calendar.getTime();
    }

    /**
     * Adds the week.
     *
     * @param nWeek 基准日期
     * @param nWeek 增加周数,可以是负数
     * @return date
     */
    public static Date addWeek(int nWeek) {
        return addWeek(new Date(), nWeek);
    }

    public static Date addWeek(Date date, int nWeek) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.WEEK_OF_YEAR, nWeek);
        return calendar.getTime();
    }

    /**
     * 获取时间差.
     *
     * @param t1 the t1
     * @param t2 the t2
     * @return t2-t1的时间差字符串. 如 02:05:45.006
     */
    public static String timeDiff(Date t1, Date t2) {
        String result;
        NumberFormat form = NumberFormat.getInstance();
        form.setGroupingUsed(false);
        form.setMinimumFractionDigits(0);
        form.setMaximumFractionDigits(0);
        Calendar calendar = new GregorianCalendar();
        long d1 = t1.getTime();
        long d2 = t2.getTime();

        // 时间相减后还要去处时区间的时差8小时
        long diff = d2 - d1 - calendar.getTimeZone().getOffset(d2);

        // if (logger.isDebugEnabled()) {
        // logger.debug("timeDiff(Date, Date) - \n t1="
        // + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS",
        // Locale.getDefault())
        // .format(t1)
        // + ", d1="
        // + d1
        // + "\n t2="
        // + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS",
        // Locale.getDefault())
        // .format(t2) + ", d2=" + d2 + "\n d2-d1=" + diff);
        // }
        calendar.setTime(new Date(diff));

        form.setMinimumIntegerDigits(3);
        String ms = form.format(calendar.get(Calendar.MILLISECOND));
        form.setMinimumIntegerDigits(2);
        String sec = form.format(calendar.get(Calendar.SECOND));
        String min = form.format(calendar.get(Calendar.MINUTE));
        String hour = form.format(calendar.get(Calendar.HOUR_OF_DAY));
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR) - 1970;

        // if (logger.isDebugEnabled()) {
        // logger.debug("timeDiff(Date, Date) - \n" + " year=" + year +
        // "\nmonth=" + month
        // + "\n day=" + day + "\n hour=" + hour + "\n min=" + min + "\n s=" +
        // sec
        // + "\n ms=" + ms);
        // }

        result = hour + ":" + min + ":" + sec + "." + ms;
        form.setMinimumIntegerDigits(2);
        if ((day + month + year) > 1) {
            result = form.format((day - 1)) + " " + result;
            if ((month + year) > 0) {
                result = form.format((month)) + "-" + result;
            }
            if (year > 0) {
                form.setMinimumIntegerDigits(4);
                result = form.format((year)) + "-" + result;
            }
        }
        return result;
    }

    /**
     * Month day count.返回指定的月份有多少天
     *
     * @param date the d
     * @return the int
     */
    public static int monthDayCount(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        Date d1 = calendar.getTime();

        // 时间相减后还要去处时区间的时差8小时
        long diff = d1.getTime() - date.getTime() - calendar.getTimeZone().getOffset

                (d1.getTime());
        calendar.setTime(new Date(diff));
        System.out.println(calendar);

        int day = calendar.get(Calendar.DAY_OF_YEAR) - 1;
        return day;
    }


    /**
     * 将double型压缩为36进制的字符串
     * <p>
     * 限制条件:
     * <li>最多2位小数
     * <li>最多16位整数部分
     *
     * @param id 如: 62527623.78
     * @return byte[]
     */
    public static String zipDouble(double id) {
        int radix = 36;
        long tempID = Math.round(id * 100);
        String result = Long.toString(tempID, radix);
        return result;
    }

    /**
     * 还原由zipDouble生成的double型数据
     *
     * @param num String 如: 169biqcvny
     * @return byte[]
     */
    public static double unZipDouble(String num) {
        double result;
        int radix = 36;
        long tempL = Long.parseLong(num, radix);
        String tempS = Long.toString(tempL);
        if (Math.abs(tempL) > 9) {
            tempS = tempS.substring(0, tempS.length() - 2) + "." + tempS.substring(tempS.length() - 2, tempS.length());
        } else {
            // 当原始数据在[-0.09,0.09]之间时,转化需要单独处理
            if (tempL < 0) {
                tempS = "-0.0" + Math.abs(tempL);
            } else {
                tempS = "0.0" + tempL;
            }
        }
        result = Double.parseDouble(tempS);
        return result;
    }

    public static long unZipLong(String num) {

        return 0;
    }

    public static byte[] zipLong(long num) {
        // 1. 将数字串补齐成3的倍数，不足位补0
        String numHex = Long.toHexString(num);
        int len = numHex.length();
        // System.out.println("number [hex]： " + numHex + " length:" + len);
        int cnt = 0;
        boolean oddHexLength = true; // 16进制的字符串长度为奇数
        if (len % 2 == 0) {
            cnt = len / 2;
            oddHexLength = false; // 16进制的字符串长度为偶数
        } else {
            cnt = len / 2 + 1;
            oddHexLength = true; // 16进制的字符串长度为奇数
        }
        byte result[] = new byte[cnt];
        // System.out.println("cnt =" + cnt);

        int startPos = 0, endPos = 0;
        for (int i = 0; i < cnt; i++) {
            System.out.print("[" + i + "]:");
            endPos = startPos + 2;
            if (endPos >= len & oddHexLength)
                endPos = startPos + 1;

            String t = numHex.substring(startPos, endPos);
            startPos = (i + 1) * 2;
            System.out.println(t);

            Integer a = Integer.parseInt(t, 16);
            result[i] = a.byteValue();
        }
        return result;
    }

    // /**
    // * 获取当前程序执行的位置，类，方法，文件名，行号
    // *
    // * @param msg
    // * 添加到追踪信息中的自定义信息
    // * @param deep
    // * 调用深度。 如：0表示traceInfo自己，1表示调用traceInfo的函数
    // * @return
    // */

    // public static int TRACEINFO_PARENT = 3; // 调用 traceInfo的父程序
    //
    // public static int TRACEINFO_GRANDPARENT = 4;// 调用 traceInfo的祖父程序

    // public static String traceInfo(String msg, int deep) {
    // StackTraceElement ste[] = (new Throwable()).getStackTrace();
    // String result = ste[deep].toString() + " - " + msg;
    // System.out.println(result);
    // return result;
    // }
    //
    // public static String traceInfo(String msg) {
    // return traceInfo(msg, 2);
    // }

    /**
     * 获取被调用函数的方法名称
     *
     * @return
     */
    public static String traceCallPath() {
        return traceCallPath(2);
    }

    /**
     * 获取被调用父函数的方法名称
     *
     * @return
     */
    public static String traceCallPath_L2() {
        return traceCallPath(3);
    }

    /**
     * 获取被调用祖父函数的方法名称
     *
     * @return
     */
    public static String traceCallPath_L3() {
        return traceCallPath(4);
    }

    /**
     * 根据调用深度，获取被调用函数的方法名称
     *
     * @return
     */
    private static String traceCallPath(int callDepth) {
        StackTraceElement ste[] = (new Throwable()).getStackTrace();
        String caller = " 【CallPath: ";
        for (int i = callDepth; i > 2; i--) {
            caller += String.format("◇%s() (%s:%d) → ", ste[i].getMethodName(), ste[i].getFileName(),
                    ste[i].getLineNumber());
        }
        caller += String.format("◆%s() (%s:%d)】", ste[2].getMethodName(), ste[2].getFileName(), ste[2].getLineNumber());
        return caller;
    }

    /**
     * Encrypto AES. AES算法加密数据
     *
     * @param key     the key 的长度必须是16,24,32个字符长度
     * @param content the content
     * @return the string 加密结果
     */
    public static byte[] encryptAES(byte[] content, String key) {
        byte[] ctext = null;
        // 判断key的长度必须是16,24,32个字符长度
        if ((key.getBytes().length != 16) && (key.getBytes().length != 24) && (key.getBytes().length != 32)) {
            System.err.println("加密密钥必须是16,24,32位字符.");
            return null;
        }
        try {
            // 生成密匙，可用多种方法来保存密匙加密：
            SecretKey skey = new SecretKeySpec(key.getBytes(), "AES");
            // 创建密码器
            Cipher cp = Cipher.getInstance("AES");
            // 初始化
            cp.init(Cipher.ENCRYPT_MODE, skey);
            byte[] ptext = content;
            // 加密处理
            ctext = cp.doFinal(ptext);
        } catch (Exception e) {
            System.err.println("encryptoAES 异常! ErrMsg: " + e.getMessage());
            return null;
        }

        return ctext;
    }

    /**
     * EDE DES
     *
     * @param input
     * @param key   2*8=16字节 3*8=24字节
     * @return
     */
    public static byte[] encrypt3DES(byte[] input, byte[] key) {
        if (key.length != 16 && key.length != 24) {
            System.err.println("3DES 密钥必须是16或24字节! key length = " + key.length);
            return null;
        }

        byte[] key1 = new byte[8];
        byte[] key2 = new byte[8];
        byte[] key3 = new byte[8];
        System.arraycopy(key, 0, key1, 0, 8);
        System.arraycopy(key, 8, key2, 0, 8);
        if (key.length == 16) {
            key3 = key1;
        } else {
            System.arraycopy(key, 16, key3, 0, 8);
        }

        byte[] code1 = encryptDES(input, key1);
        byte[] code2 = decryptDES(code1, key2);
        byte[] code3 = encryptDES(code2, key3);
        return code3;
    }

    /**
     * EDE DES
     *
     * @param input
     * @param key   3*8=24字节
     * @return
     */
    public static byte[] decrypt3DES(byte[] input, byte[] key) {
        if (key.length != 16 && key.length != 24) {
            System.err.println("3DES 密钥必须是16或24字节! key length = " + key.length);
            return null;
        }

        byte[] key1 = new byte[8];
        byte[] key2 = new byte[8];
        byte[] key3 = new byte[8];
        System.arraycopy(key, 0, key1, 0, 8);
        System.arraycopy(key, 8, key2, 0, 8);
        if (key.length == 16) {
            key3 = key1;
        } else {
            System.arraycopy(key, 16, key3, 0, 8);
        }
        byte[] code1 = decryptDES(input, key1);
        byte[] code2 = encryptDES(code1, key2);
        byte[] code3 = decryptDES(code2, key3);
        return code3;
    }

    /**
     * 异或校验
     *
     * @param keyOne
     * @param keyTwo
     * @return
     */
    public static byte[] xor(byte[] keyOne, byte[] keyTwo) {
        int length = 0;
        if (keyOne.length > keyTwo.length) {
            length = keyOne.length;
        } else {
            length = keyTwo.length;
        }
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = (byte) (keyOne[i] ^ keyTwo[i]);
        }
        return result;
    }

    /**
     * Encrypto DES. DES算法加密数据
     *
     * @param key     长度8字节
     * @param content the content
     * @return 加密结果
     */
    public static byte[] encryptDES(byte[] content, byte[] key) {
        byte[] ctext = null;
        // 判断key的长度必须是16,24,32个字符长度
        if (key.length != 8) {
            // logger.error("DES 密钥必须是8字节! key length = " + key.length);
            return null;
        }
        try {
            // 生成密匙，可用多种方法来保存密匙加密：
            SecretKey skey = new SecretKeySpec(key, "DES");
            // 创建密码器
            Cipher cp = Cipher.getInstance("DES/ECB/NoPadding");
            // Cipher cp = Cipher.getInstance("DES");

            // 初始化
            cp.init(Cipher.ENCRYPT_MODE, skey);
            byte[] ptext = content;
            // 加密处理
            ctext = cp.doFinal(ptext);
        } catch (Exception e) {
            // logger.error("encryptoDES 异常! ErrMsg: " + e.getMessage(), e);
            return null;
        }
        return ctext;
    }

    /**
     * Decrypto DES.
     *
     * @param key   长度8字节
     * @param input the content
     * @return the byte[]
     */
    public static byte[] decryptDES(byte[] input, byte[] key) {
        // 判断key的长度必须是8个字节
        if (key.length != 8) {
            // logger.error("DES 密钥必须是8字节! key length = " + key.length);
            return null;
        }
        // 从Base64编码中恢复原始数据
        SecretKey skey = new SecretKeySpec(key, "DES");
        byte[] ptext = null;
        try {
            Cipher cp = Cipher.getInstance("DES/ECB/NoPadding");
            // Cipher cp = Cipher.getInstance("DES");
            cp.init(Cipher.DECRYPT_MODE, skey);
            ptext = cp.doFinal(input);
        } catch (Exception e) {
            // logger.error("decryptDES 解码错误,请核对您的密钥!  ErrMsg: " +
            // e.getMessage());
            return null;
        }
        return ptext;
    }

    /**
     * DES或3Des算法加密数据
     *
     * @param content
     * @param key
     * @param algorithm  DES:"DES/ECB/NoPadding"
     * @param mode       "DES"，"DESede"
     * @param cipherMode 加密： Cipher.ENCRYPT_MODE；解密 Cipher.DECRYPT_MODE
     * @return
     */
    public static byte[] des(byte[] content, byte[] key, String algorithm, String mode, int cipherMode) {
        byte[] enc;
        // 判断key的长度必须是16,24,32个字符长度
        if (key.length % 8 != 0 && key.length / 8 < 1) {
            System.err.println("DES 密钥必须是8字节的倍数! key length = " + key.length);
            return null;
        }
        try {

            Cipher c = Cipher.getInstance(algorithm);
            // 格式化密钥
            SecretKeySpec k = new SecretKeySpec(key, mode);
            // 根据密钥，对Cipher对象进行初始化,ENCRYPT_MODE表示加密模式
            c.init(Cipher.ENCRYPT_MODE, k);
            // 加密，结果保存进enc
            enc = c.doFinal(content);
            return enc;

            // 生成密匙，可用多种方法来保存密匙加密：
            // SecretKey skey = new SecretKeySpec(key, mode);
            // // SecretKey skey = new SecretKeySpec(key, "DES");
            //
            // // 创建密码器
            // Cipher cp = Cipher.getInstance(algorithm);
            // // Cipher cp = Cipher.getInstance("DES/ECB/NoPadding");
            // // Cipher cp = Cipher.getInstance("DES");
            //
            // // 初始化
            // cp.init(Cipher.ENCRYPT_MODE, skey);
            // byte[] ptext = content;
            // // 加密处理
            // ctext = cp.doFinal(ptext);
        } catch (Exception e) {
            System.err.println("DES计算异常! ErrMsg: " + e.getMessage() );
            return null;
        }
        // return enc;
    }


    /**
     * Decrypto AES.
     *
     * @param key   the key 的长度必须是16,24,32个字符长度
     * @param input the content
     * @return the byte[]
     */
    public static byte[] decryptAES(byte[] input, String key) {
        // 判断key的长度必须是16,24,32个字符长度
        if ((key.getBytes().length != 16) && (key.getBytes().length != 24) && (key.getBytes().length != 32)) {
            System.err.println("密钥长度有误: " + key);
            return null;
        }
        // 从Base64编码中恢复原始数据
        SecretKey skey = new SecretKeySpec(key.getBytes(), "AES");
        byte[] ptext = null;
        try {
            Cipher cp = Cipher.getInstance("AES");
            cp.init(Cipher.DECRYPT_MODE, skey);
            ptext = cp.doFinal(input);
        } catch (Exception e) {
            System.err.println("decryptAES 解码错误,请核对您的密钥!  ErrMsg: " + e.getMessage());
            return null;
        }
        return ptext;
    }

    /**
     * MD5.
     *
     * @param s the s
     * @return the string
     */
    public static String md5(String s) {
        try {
            return md5(s.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            System.err.println("calc MD5 error for string:" + s);
            return null;
        }
    }

    public static String md5(byte[] s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] strTemp = s;
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
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
            return null;
        }
    }


    public static void sleep(long sleepMS) {
        try {
            Thread.sleep(sleepMS);// 延时n毫秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 以分为单位的整数转换为以元为单位的小数（ nnn.nn）元
     *
     * @param n
     * @return
     */
    public static String long2money(Long n) {
        if (n == null) {
            return "0";
        }
        String result = n / 100 + "." + lpad0(n % 100, 2);
        return result;
    }

    /**
     * 以某数量级返回金额
     *
     * @param n
     * @param level 显示级别， 如：万元级，此值为4，千元级此值为3
     * @return
     */
    public static String long2money(long n, int level) {
        long power = Math.round(Math.pow(10, 2 + level));
        String result = n / power + "";
        // String result = (n / power) + "." + lpad0(n % power, 2);
        return result;
    }



    /**
     * 驼峰式字符串格式化，_a → A ----: a_b_cDEfgh ===> aBCdefgh =FU=> ABCdefgh
     *
     * @param srcName
     * @param firstUpper 首字母大写
     * @return
     */
    public static String buildCamelName(String srcName, boolean firstUpper) {
        String[] nameSegs = srcName.toLowerCase().split("_");
        String name = "";
        for (int i = 0; i < nameSegs.length; i++) {
            String nameSeg = nameSegs[i];
            // System.out.println("nameSegs[" + i + "] = " + nameSeg);
            if (firstUpper) {
                firstUpper = false;
                nameSeg = (nameSeg.length() > 1) ? (nameSeg.substring(0, 1).toUpperCase() + nameSeg.substring(1))
                        : nameSeg.toUpperCase();
            } else {
                if (i == 0) {
                    // 首单词字母小写
                    nameSeg = nameSeg.toLowerCase();
                } else {

                    nameSeg = (nameSeg.length() > 1) ? (nameSeg.substring(0, 1).toUpperCase() + nameSeg.substring(1))
                            : nameSeg.toUpperCase();
                }
            }

            // 追加名字分段到最终结果
            name += nameSeg;
        }
        return name;
    }

    /**
     * 用于查看函数的调用路径 从第[4]层开始，追溯三层调用路径
     * <p>
     * 适用于 CallJsonService.call(String serviceID, Map<String, String> params)
     *
     * @return
     */
    public static String callPath() {
        return callPath(4, 3);
    }

    /**
     * 用于查看函数的调用路径
     *
     * @param startDepth 开始层 0,1,2,...
     * @param deepth     追溯层深度调用路径
     * @return
     */
    public static String callPath(int startDepth, int deepth) {
        StackTraceElement ste[] = (new Throwable()).getStackTrace();
        String callPath = "";
        int endDepth = (ste.length - startDepth) > deepth ? deepth + startDepth : ste.length;
        for (int i = startDepth; i < endDepth; i++) {
            String path = "【" + ste[i].getFileName() + ":" + ste[i].getLineNumber() + " => " + ste[i].getMethodName()
                    + "()】\n";
            callPath += path;
        }
        return callPath;
    }


}

package utils;

import java.text.DecimalFormat;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-13
 * Time: 下午4:13
 */
public class NumericalUtils {

    private static final DecimalFormat df = (DecimalFormat)DecimalFormat.getInstance();
    public static String formatDecimal(String pattern, double number)
    {
        df.applyPattern(pattern);
        return df.format(number);
    }
    /**
     * turn a string into integer, return Integer.MIN_VALUE if exception happens
     * @param str
     * @return
     */
    public static int toInteger(String str)
    {
        if(StringUtils.isStringNullOrEmpty(str))
        {
            return Integer.MIN_VALUE;
        }
        try{
            int value = Integer.parseInt(str);
            return value;
        }catch (Exception e)
        {
            return Integer.MIN_VALUE;
        }
    }

    /**
     * turn a string into long, return Long.MIN_VALUE if exception happens
     * @param str
     * @return
     */
    public static long toLong(String str)
    {
        if(StringUtils.isStringNullOrEmpty(str))
        {
            return Long.MIN_VALUE;
        }
        try{
            long value = Long.parseLong(str);
            return value;
        }catch (Exception e)
        {
            return Long.MIN_VALUE;
        }
    }

    /**
     * turn a string into double, return Double.MIN_VALUE if exception happens
     * @param str
     * @return
     */
    public static Double toDouble(String str)
    {
        if(StringUtils.isStringNullOrEmpty(str))
        {
            return Double.MIN_VALUE;
        }
        try{
            double value = Double.parseDouble(str);
            return value;
        }catch (Exception e)
        {
            return Double.MIN_VALUE;
        }
    }

    public static boolean isTrue(String str)
    {
        return "1".equals(str) ? true : false;
    }
}

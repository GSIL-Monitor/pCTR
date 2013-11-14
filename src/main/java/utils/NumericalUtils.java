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
     * turn a string into integer, return Integer.MIN_VALUE if exception happened
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


}

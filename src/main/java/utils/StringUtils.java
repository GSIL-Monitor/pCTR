package utils;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-14
 * Time: 上午10:45
 */
public class StringUtils {
    public static boolean isStringNullOrEmpty(String str)
    {
        return str == null || str.length() < 1;
    }

    /**
     * split a given string with a given char c, use it carefully
     * @param str  string
     * @param c  split char
     * @return String array
     */
    public static String[] splitStr(String str, char c)
    {
        if(str.length() == 1)
        {
            if(str.charAt(0) == c)
            {
                return new String[0];
            }
            else
            {
                return new String[]{str};
            }
        }

        String[] tmpContents = new String[str.length()/2];
        int startIndex = 0;
        int index = 0;
        while(startIndex < str.length())
        {
            int endIndex = str.indexOf(c, startIndex);
            if(endIndex > 0)
            {
                if(endIndex > startIndex)
                {
                    tmpContents[index] = str.substring(startIndex, endIndex);
                    index++;
                }
                startIndex = endIndex+1;
            }
            else
            {
                tmpContents[index] = str.substring(startIndex);
                index++;
                break;
            }
        }

        String[] contents = new String[index];
        System.arraycopy(tmpContents, 0, contents, 0, index);

        return contents;
    }
}

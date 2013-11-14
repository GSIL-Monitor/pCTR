package commons.framework.sample;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-13
 * Time: 下午4:11
 */
public enum SampleType {
    TRAIN,
    VALIDATE,
    TEST;

    public static boolean isSampleType(String inputType)
    {
        SampleType[] types = SampleType.values();
        for(int i = 0; i < types.length; i++)
        {
            if(types[i].name().equals(inputType))
            {
                return true;
            }
        }
        return false;
    }
}

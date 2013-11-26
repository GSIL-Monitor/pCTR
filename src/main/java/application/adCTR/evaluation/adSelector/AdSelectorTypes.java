package application.adCTR.evaluation.adSelector;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-26
 * Time: 上午11:28
 */
public enum AdSelectorTypes {
    SIMPLE_AD_SELECTOR;

    public static boolean isAdSelectorType(String typeName)
    {
        AdSelectorTypes[] types = AdSelectorTypes.values();
        for(int i = 0; i < types.length; i++)
        {
            if(types[i].name().equals(typeName))
            {
                return true;
            }
        }
        return false;
    }

    public static AdSelectorTypes toAdSelectorTypes(String typeName)
    {
        String name = typeName.toUpperCase();
        if(isAdSelectorType(name))
        {
            return AdSelectorTypes.valueOf(name);
        }
        return null;
    }
}

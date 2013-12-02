package application.adCTR.evaluation.adSelectionEvaluator;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-28
 * Time: 上午10:00
 */
public enum MetricTypes {
    CTR,
    INDEX;

    public static boolean isValidMetricType(String metricType)
    {
        String type = metricType.toUpperCase();
        MetricTypes[] types = MetricTypes.values();
        for(int i = 0; i < types.length; i++)
        {
            if(type.equals(types[i].name()))
            {
                return true;
            }
        }
        return false;
    }

    public static MetricTypes toMetricType(String metricTypeName)
    {
        if(isValidMetricType(metricTypeName))
        {
            return MetricTypes.valueOf(metricTypeName.toUpperCase());
        }
        return null;
    }
}

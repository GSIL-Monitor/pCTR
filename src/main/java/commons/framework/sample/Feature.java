package commons.framework.sample;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-13
 * Time: 上午11:31
 */
public class Feature {
    private int featureId;
    private String featureName;
    private String featureValue;

    public Feature(int featureId, String featureName, String featureValue)
    {
        this.featureId = featureId;
        this.featureName = featureName;
        this.featureValue = featureValue;
    }

    public int getFeatureId() {
        return featureId;
    }

    public String getFeatureName() {
        return featureName;
    }

    public String getFeatureValue() {
        return featureValue;
    }
}

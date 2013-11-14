package commons.framework.sample;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-13
 * Time: 下午3:34
 */
public class Sample {
    private SampleType sampleType;
    private ArrayList<Feature> featureArrayList;
    private long sampleID;
    private double targetValue;
    private double predictValue;

    public Sample(SampleType sampleType, long sampleID, double targetValue)
    {
        this.sampleID = sampleID;
        this.sampleType = sampleType;
        this.targetValue = targetValue;
        this.featureArrayList = new ArrayList<Feature>(1024);
    }

    public void addFeatures(ArrayList<Feature> newFeatures)
    {
        this.featureArrayList.addAll(newFeatures);
    }

    public void setPredictValue(double predictValue) {
        this.predictValue = predictValue;
    }

    public SampleType getSampleType() {
        return sampleType;
    }

    public long getSampleID() {
        return sampleID;
    }

    public double getTargetValue() {
        return targetValue;
    }

    public double getPredictValue() {
        return predictValue;
    }

    public String toLibLinearTypeString()
    {
        StringBuilder sbd = new StringBuilder();
        sbd.append((int)targetValue);
        for(int i = 0, len = featureArrayList.size(); i < len; i++)
        {
            Feature f = featureArrayList.get(i);
            sbd.append(" ").append(f.getFeatureId()).append(":").append(f.getFeatureValue());
        }
        return sbd.toString();
    }

}

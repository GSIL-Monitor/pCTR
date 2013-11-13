package commons.framework.sample;

import commons.framework.data.DataInstance;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-13
 * Time: 下午3:35
 */
public abstract class SampleMaker {
    protected final int defaultArrayListSize = 256;
    protected ArrayList<IFeatureHandler> featureHandlerArrayList = new ArrayList<IFeatureHandler>(defaultArrayListSize);

    protected void registerFeatureHandler(IFeatureHandler featureHandler)
    {
        this.featureHandlerArrayList.add(featureHandler);
    }

    protected void fillSample(Sample sample, DataInstance dataInstance)
    {
        for(int i = 0, len = featureHandlerArrayList.size(); i < len; i++)
        {
            sample.addFeatures(featureHandlerArrayList.get(i).ExtractFeature(dataInstance));
        }
    }
}

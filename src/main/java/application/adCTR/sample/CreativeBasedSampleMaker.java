package application.adCTR.sample;

import application.adCTR.data.CTRDataInstance;
import commons.framework.sample.IFeatureHandler;
import commons.framework.sample.Sample;
import commons.framework.sample.SampleMaker;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-28
 * Time: 上午9:17
 */
public class CreativeBasedSampleMaker extends SampleMaker {
    private static CreativeBasedSampleMaker instance = null;
    private CreativeBasedSampleMaker(){}

    public static CreativeBasedSampleMaker getInstance()
    {
        if(instance == null)
        {
            instance = new CreativeBasedSampleMaker();
        }
        return instance;
    }

    //business part
    public int registerFeatureHandler(IFeatureHandler handler, int initFeatureId)
    {
        int featureId = handler.initFeatureHandler(initFeatureId);
        super.registerFeatureHandler(handler);
        return featureId;
    }

    public void fillSample(Sample sample, CTRDataInstance ctrDataInstance)
    {
        super.fillSample(sample,ctrDataInstance);
    }
}

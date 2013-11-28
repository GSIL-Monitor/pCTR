package application.adCTR.sample;

import application.adCTR.data.CTRDataInstance;
import commons.framework.sample.IFeatureHandler;
import commons.framework.sample.Sample;
import commons.framework.sample.SampleMaker;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-28
 * Time: 上午9:15
 */
public class CreativeIrrelevantSampleMaker extends SampleMaker {
    private static CreativeIrrelevantSampleMaker instance = null;
    private CreativeIrrelevantSampleMaker(){}

    public static CreativeIrrelevantSampleMaker getInstance()
    {
        if(instance == null)
        {
            instance = new CreativeIrrelevantSampleMaker();
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

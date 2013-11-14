package application.adCTR.sample;

import application.adCTR.data.CTRDataInstance;
import commons.framework.sample.IFeatureHandler;
import commons.framework.sample.Sample;
import commons.framework.sample.SampleMaker;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-13
 * Time: 下午4:53
 */
public class CTRSampleMaker extends SampleMaker {
    private static CTRSampleMaker instance = null;
    private CTRSampleMaker(){}

    public static CTRSampleMaker getInstance()
    {
        if(instance == null)
        {
            instance = new CTRSampleMaker();
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

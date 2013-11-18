package application.adCTR.sample.handlers;

import application.adCTR.data.CTRDataInstance;
import commons.framework.data.DataInstance;
import commons.framework.sample.Feature;
import commons.framework.sample.IFeatureHandler;
import utils.FileUtils;
import utils.NumericalUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-18
 * Time: 上午10:24
 */
public class CreativeChannelHandler implements IFeatureHandler {
    private final String creativeChannelFeatureConf = "featureHandlerConf/creativeCategoryClickRate.conf";
    private int maxFeatureId = 0;
    private Feature unseenFeature = null;
    private HashMap<String,Feature> creativeChannelClickRateMap = null;
    private final int numOfFields = 4;
    @Override
    public int initFeatureHandler(int initFeatureId) {
        creativeChannelClickRateMap = new HashMap<String, Feature>();
        int cntOfCreativeChannelCombo = 0;
        double sumOfAvgClickRate = 0;
        try{
            InputStream is = FileUtils.getStreamFromFile(creativeChannelFeatureConf);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while((line = bufferedReader.readLine()) != null)
            {
                String[] contents = line.split("\t");
                if(contents.length != numOfFields)
                {
                    continue;
                }
                String creativeChannelCombo = contents[0];
                double clickRate =  NumericalUtils.toDouble(contents[3]);
                if(!creativeChannelClickRateMap.containsKey(creativeChannelCombo) && !Double.isNaN(clickRate))
                {
                    cntOfCreativeChannelCombo++;
                    sumOfAvgClickRate += clickRate;
                    int localId = 1;//for real value feature, we use local id = 1;
                    Feature feature = new Feature(localId + initFeatureId, "creativeChannelClickRate", contents[3]);
                    creativeChannelClickRateMap.put(creativeChannelCombo, feature);
                }
            }
            bufferedReader.close();
            is.close();
        }catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("init creative channel feature fail");
        }finally
        {
            /**
             * deal with unseen feature
             * for unseen feature, we set its click rate as average of all
             * its feature local id is the same as everyone else
             */
            maxFeatureId = initFeatureId + 1;
            unseenFeature = new Feature(maxFeatureId,"creativeChannelClickRate",String.valueOf(sumOfAvgClickRate/(cntOfCreativeChannelCombo+0.0000001)));
        }
        return maxFeatureId;
    }

    @Override
    public ArrayList<Feature> ExtractFeature(DataInstance dataInstance) {
        ArrayList<Feature> featureArrayList = new ArrayList<Feature>();
        if(dataInstance instanceof CTRDataInstance)
        {
            String creativeChannelCombo = String.format("%d_%s", ((CTRDataInstance) dataInstance).getCreativeId(), ((CTRDataInstance) dataInstance).getCategory());
            if(creativeChannelClickRateMap.containsKey(creativeChannelCombo))
            {
                featureArrayList.add(creativeChannelClickRateMap.get(creativeChannelCombo));
            }
            else {
                featureArrayList.add(unseenFeature);
            }
        }
        return featureArrayList;
    }
}

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
 * Time: 上午9:39
 */
public class CreativeIDHandler implements IFeatureHandler {
    private final String creativeIDFeatureConf = "featureHandlerConf/creativeClickRate.conf";
    private int maxFeatureId = 0;
    private Feature unseenFeature = null;
    private HashMap<Integer,Feature> creativeClickRateMap = null;
    private final int numOfFields = 4;
    @Override
    public int initFeatureHandler(int initFeatureId) {
        creativeClickRateMap = new HashMap<Integer, Feature>();
        int cntOfCreativeID = 0;
        double sumOfAvgClickRate = 0;
        try{
            InputStream is = FileUtils.getStreamFromFile(creativeIDFeatureConf);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while((line = bufferedReader.readLine()) != null)
            {
                String[] contents = line.split("\t");
                if(contents.length != numOfFields)
                {
                    continue;
                }
                Integer creativeID = NumericalUtils.toInteger(contents[0]);
                double clickRate =  NumericalUtils.toDouble(contents[3]);
                if(!creativeClickRateMap.containsKey(creativeID) && !Double.isNaN(clickRate))
                {
                    cntOfCreativeID++;
                    sumOfAvgClickRate += clickRate;
                    int localId = 1;//for real value feature, we use local id = 1;
                    Feature feature = new Feature(localId + initFeatureId, "creativeClickRate", contents[3]);
                    creativeClickRateMap.put(creativeID, feature);
                }
            }
            bufferedReader.close();
            is.close();
        }catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("init creative id feature fail");
        }finally
        {
            /**
             * deal with unseen feature
             * for unseen feature, we set its click rate as average of all
             * its feature local id is the same as everyone else
             */
            maxFeatureId = initFeatureId + 1;
            unseenFeature = new Feature(maxFeatureId,"creativeClickRate",String.valueOf(sumOfAvgClickRate/(cntOfCreativeID+0.0000001)));
        }
        return maxFeatureId;
    }

    @Override
    public ArrayList<Feature> ExtractFeature(DataInstance dataInstance) {
        ArrayList<Feature> featureArrayList = new ArrayList<Feature>();
        if(dataInstance instanceof CTRDataInstance)
        {
            int creativeId = ((CTRDataInstance) dataInstance).getCreativeId();
            if(creativeClickRateMap.containsKey(creativeId))
            {
                featureArrayList.add(creativeClickRateMap.get(creativeId));
            }
            else {
                featureArrayList.add(unseenFeature);
            }
        }
        return featureArrayList;
    }
}

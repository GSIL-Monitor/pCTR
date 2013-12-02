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
    private final String creativeIDFeatureConf = "featureHandlerConf/creativeID.conf";
    private int maxFeatureId = 0;
    private Feature unseenFeature = null;
    private HashMap<Integer,Feature> creativeIdMap = null;
    private final int numOfFields = 2;
    @Override
    public int initFeatureHandler(int initFeatureId) {
        creativeIdMap = new HashMap<Integer, Feature>();
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
                Integer localId = NumericalUtils.toInteger(contents[1]);
                if(!creativeIdMap.containsKey(creativeID) && localId != Integer.MIN_VALUE)
                {
                    Feature feature = new Feature(localId + initFeatureId, "creativeID", "1");
                    creativeIdMap.put(creativeID, feature);
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
            maxFeatureId = initFeatureId + creativeIdMap.size() + 1;
            unseenFeature = new Feature(maxFeatureId, "creativeID", "1");
        }
        return maxFeatureId;
    }

    @Override
    public ArrayList<Feature> ExtractFeature(DataInstance dataInstance) {
        ArrayList<Feature> featureArrayList = new ArrayList<Feature>();
        if(dataInstance instanceof CTRDataInstance)
        {
            int creativeId = ((CTRDataInstance) dataInstance).getCreativeId();
            if(creativeIdMap.containsKey(creativeId))
            {
                featureArrayList.add(creativeIdMap.get(creativeId));
            }
            else {
                featureArrayList.add(unseenFeature);
            }
        }
        return featureArrayList;
    }
}

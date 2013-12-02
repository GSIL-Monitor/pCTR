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
    private final String creativeChannelFeatureConf = "featureHandlerConf/creativeCategoryID.conf";
    private int maxFeatureId = 0;
    private Feature unseenFeature = null;
    private HashMap<String,Feature> creativeChannelIDMap = null;
    private final int numOfFields = 2;
    @Override
    public int initFeatureHandler(int initFeatureId) {
        creativeChannelIDMap = new HashMap<String, Feature>();
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
                int localId = NumericalUtils.toInteger(contents[1]);
                if(!creativeChannelIDMap.containsKey(creativeChannelCombo) && localId != Integer.MIN_VALUE)
                {
                    Feature feature = new Feature(localId + initFeatureId, "creativeChannelID", "1");
                    creativeChannelIDMap.put(creativeChannelCombo, feature);
                }
            }
            bufferedReader.close();
            is.close();
        }catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("init creative channel id feature fail");
        }finally
        {
            /**
             * deal with unseen feature
             * for unseen feature, we set its click rate as average of all
             * its feature local id is the same as everyone else
             */
            maxFeatureId = initFeatureId + creativeChannelIDMap.size() + 1;
            unseenFeature = new Feature(maxFeatureId,"creativeChannelID","1");
        }
        return maxFeatureId;
    }

    @Override
    public ArrayList<Feature> ExtractFeature(DataInstance dataInstance) {
        ArrayList<Feature> featureArrayList = new ArrayList<Feature>();
        if(dataInstance instanceof CTRDataInstance)
        {
            String creativeChannelCombo = String.format("%d_%s", ((CTRDataInstance) dataInstance).getCreativeId(), ((CTRDataInstance) dataInstance).getCategory());
            if(creativeChannelIDMap.containsKey(creativeChannelCombo))
            {
                featureArrayList.add(creativeChannelIDMap.get(creativeChannelCombo));
            }
            else {
                featureArrayList.add(unseenFeature);
            }
        }
        return featureArrayList;
    }
}

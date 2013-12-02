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
 * Time: 上午9:29
 */
public class CastIDHandler implements IFeatureHandler{
    private final String castIDFeatureConf = "featureHandlerConf/castID.conf";
    private int maxFeatureId = 0;
    private Feature unseenFeature = null;
    private HashMap<Integer,Feature> castIDMap = null;
    private final int numOfFields = 2;
    @Override
    public int initFeatureHandler(int initFeatureId) {
        castIDMap = new HashMap<Integer, Feature>();
        try{
            InputStream is = FileUtils.getStreamFromFile(castIDFeatureConf);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while((line = bufferedReader.readLine()) != null)
            {
                String[] contents = line.split("\t");
                if(contents.length != numOfFields)
                {
                    continue;
                }
                Integer castID = NumericalUtils.toInteger(contents[0]);
                Integer localId = NumericalUtils.toInteger(contents[1]);
                if(!castIDMap.containsKey(castID) && localId != Integer.MIN_VALUE)
                {
                    Feature feature = new Feature(localId + initFeatureId, "castID", "1");
                    castIDMap.put(castID, feature);
                }
            }
            bufferedReader.close();
            is.close();
        }catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("init cast id feature fail");
        }finally
        {
            /**
             * deal with unseen feature
             * for unseen feature, we set its click rate as average of all
             * its feature local id is the same as everyone else
             */
            maxFeatureId = initFeatureId + castIDMap.size() + 1;
            unseenFeature = new Feature(maxFeatureId,"castID", "1");
        }
        return maxFeatureId;
    }

    @Override
    public ArrayList<Feature> ExtractFeature(DataInstance dataInstance) {
        ArrayList<Feature> featureArrayList = new ArrayList<Feature>();
        if(dataInstance instanceof CTRDataInstance)
        {
            int castId = ((CTRDataInstance) dataInstance).getCastId();
            if(castIDMap.containsKey(castId))
            {
                featureArrayList.add(castIDMap.get(castId));
            }
            else {
                featureArrayList.add(unseenFeature);
            }
        }
        return featureArrayList;
    }
}



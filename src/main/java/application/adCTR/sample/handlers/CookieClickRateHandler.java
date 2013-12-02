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
 * Date: 13-11-28
 * Time: 下午2:12
 */
public class CookieClickRateHandler implements IFeatureHandler {
    private final String cookieClickRateFeatureConf = "featureHandlerConf/cookieClickRate.conf";
    private int maxFeatureId = 0;
    private Feature unseenFeature = null;
    private HashMap<String,Feature> cookieClickRateMap = null;
    private final int numOfFields = 4;
    @Override
    public int initFeatureHandler(int initFeatureId) {
        cookieClickRateMap = new HashMap<String, Feature>();
        try{
            InputStream is = FileUtils.getStreamFromFile(cookieClickRateFeatureConf);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while((line = bufferedReader.readLine()) != null)
            {
                String[] contents = line.split("\t");
                if(contents.length != numOfFields)
                {
                    continue;
                }
                String cookie = contents[0];
                double clickRate =  NumericalUtils.toDouble(contents[3]);
                if(!cookieClickRateMap.containsKey(cookie) && !Double.isNaN(clickRate))
                {
                    int localId = 1;//for real value feature, we use local id = 1;
                    Feature feature = new Feature(localId + initFeatureId, "cookieClickRate", contents[3]);
                    cookieClickRateMap.put(cookie, feature);
                }
            }
            bufferedReader.close();
            is.close();
        }catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("init cookie click rate feature fail");
        }finally
        {
            /**
             * deal with unseen feature
             * for unseen feature, we set its click rate as average of all
             * its feature local id is the same as everyone else
             */
            maxFeatureId = initFeatureId + 1 + 1;
            unseenFeature = new Feature(maxFeatureId,"cookieClickRate","1");
        }
        return maxFeatureId;
    }

    @Override
    public ArrayList<Feature> ExtractFeature(DataInstance dataInstance) {
        ArrayList<Feature> featureArrayList = new ArrayList<Feature>();
        if(dataInstance instanceof CTRDataInstance)
        {
            String cookie = ((CTRDataInstance) dataInstance).getCookie();
            if(cookieClickRateMap.containsKey(cookie))
            {
                featureArrayList.add(cookieClickRateMap.get(cookie));
            }
            else {
                featureArrayList.add(unseenFeature);
            }
        }
        return featureArrayList;
    }
}

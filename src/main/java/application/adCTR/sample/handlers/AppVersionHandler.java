package application.adCTR.sample.handlers;

import application.adCTR.data.CTRDataInstance;
import commons.framework.data.DataInstance;
import commons.framework.sample.Feature;
import commons.framework.sample.IFeatureHandler;
import utils.FileUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-20
 * Time: 下午3:01
 */
public class AppVersionHandler implements IFeatureHandler {
    private final String appVersionFeatureConf = "featureHandlerConf/appVersion.conf";
    private int maxFeatureId = 0;
    private Feature unseenFeature = null;
    private HashMap<String,Feature> appVersionFeatureHashMap = null;
    private final int numOfFields = 2;
    @Override
    public int initFeatureHandler(int initFeatureId) {
        appVersionFeatureHashMap = new HashMap<String, Feature>();
        try{
            InputStream is = FileUtils.getStreamFromFile(appVersionFeatureConf);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while((line = bufferedReader.readLine()) != null)
            {
                String[] contents = line.split("\t");
                if(contents.length != numOfFields)
                {
                    continue;
                }
                String appVersion = contents[0];
                if(!appVersionFeatureHashMap.containsKey(appVersion))
                {
                    int localId = Integer.parseInt(contents[1]);
                    Feature feature = new Feature(localId+initFeatureId,"appVersion","1");
                    appVersionFeatureHashMap.put(appVersion, feature);
                }
            }
            bufferedReader.close();
            is.close();
        }catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("init app version feature fail");
        }finally
        {
            //deal with unseen feature
            maxFeatureId = initFeatureId + appVersionFeatureHashMap.size() + 1;
            unseenFeature = new Feature(maxFeatureId,"appVersion","1");
        }
        return maxFeatureId;
    }

    @Override
    public ArrayList<Feature> ExtractFeature(DataInstance dataInstance) {
        ArrayList<Feature> featureArrayList = new ArrayList<Feature>();
        if(dataInstance instanceof CTRDataInstance)
        {
            String appVersion = ((CTRDataInstance) dataInstance).getAppVersion();
            if(appVersionFeatureHashMap.containsKey(appVersion))
            {
                featureArrayList.add(appVersionFeatureHashMap.get(appVersion));
            }
            else
            {
                featureArrayList.add(unseenFeature);
            }
        }
        return featureArrayList;
    }
}

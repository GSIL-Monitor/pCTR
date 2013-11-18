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
 * Time: 下午2:35
 */
public class ClientTypeHandler implements IFeatureHandler {
    private final String clientTypeFeatureConf = "featureHandlerConf/clientType.conf";
    private int maxFeatureId = 0;
    private Feature unseenFeature = null;
    private HashMap<String,Feature> clientTypeFeatureHashMap = null;
    private final int numOfFields = 2;
    @Override
    public int initFeatureHandler(int initFeatureId) {
        clientTypeFeatureHashMap = new HashMap<String, Feature>();
        try{
            InputStream is = FileUtils.getStreamFromFile(clientTypeFeatureConf);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while((line = bufferedReader.readLine()) != null)
            {
                String[] contents = line.split("\t");
                if(contents.length != numOfFields)
                {
                    continue;
                }
                String clientType = contents[0];
                if(!clientTypeFeatureHashMap.containsKey(clientType))
                {
                    int localId = Integer.parseInt(contents[1]);
                    Feature feature = new Feature(localId+initFeatureId,"clientType","1");
                    clientTypeFeatureHashMap.put(clientType, feature);
                }
            }

            bufferedReader.close();
            is.close();
        }catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("init os client feature fail");
        }finally
        {
            //deal with unseen feature
            maxFeatureId = initFeatureId + clientTypeFeatureHashMap.size() + 1;
            unseenFeature = new Feature(maxFeatureId,"clientType","1");
        }
        return maxFeatureId;
    }

    @Override
    public ArrayList<Feature> ExtractFeature(DataInstance dataInstance) {
        ArrayList<Feature> featureArrayList = new ArrayList<Feature>();
        if(dataInstance instanceof CTRDataInstance)
        {
            String clientType = ((CTRDataInstance) dataInstance).getClientType();
            if(clientTypeFeatureHashMap.containsKey(clientType))
            {
                featureArrayList.add(clientTypeFeatureHashMap.get(clientType));
            }
            else
            {
                featureArrayList.add(unseenFeature);
            }
        }
        return featureArrayList;
    }
}

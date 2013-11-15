package application.adCTR.sample.handlers;

import application.adCTR.data.CTRDataInstance;
import commons.framework.data.DataInstance;
import commons.framework.sample.Feature;
import commons.framework.sample.IFeatureHandler;
import utils.FileUtils;
import utils.NumericalUtils;
import utils.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-13
 * Time: 下午4:56
 */
public class DeviceTypeHandler implements IFeatureHandler{
    private final String deviceFeatureConf = "featureHandlerConf/deviceType.conf";
    private int maxFeatureId = 0;
    private Feature unseenFeature = null;
    private HashMap<Integer,Feature> deviceTypeFeatureHashMap = null;
    private final int numOfFields = 2;
    @Override
    public int initFeatureHandler(int initFeatureId) {
        deviceTypeFeatureHashMap = new HashMap<Integer, Feature>();
        try{
            InputStream is = FileUtils.getStreamFromFile(deviceFeatureConf);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while((line = bufferedReader.readLine()) != null)
            {
                String[] contents = line.split("\t");
                if(contents.length != numOfFields)
                {
                    continue;
                }
                int deviceType = NumericalUtils.toInteger(contents[0]);
                if(!deviceTypeFeatureHashMap.containsKey(deviceType))
                {
                    int localId = Integer.parseInt(contents[1]);
                    Feature feature = new Feature(localId+initFeatureId,"deviceType","1");
                    deviceTypeFeatureHashMap.put(deviceType, feature);
                }
            }
            bufferedReader.close();
            is.close();
        }catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("init device type feature fail");
        }finally
        {
            //deal with unseen feature
            maxFeatureId = initFeatureId + deviceTypeFeatureHashMap.size() + 1;
            unseenFeature = new Feature(maxFeatureId,"deviceType","1");
        }
        return maxFeatureId;
    }

    @Override
    public ArrayList<Feature> ExtractFeature(DataInstance dataInstance) {
        ArrayList<Feature> featureArrayList = new ArrayList<Feature>();
        if(dataInstance instanceof CTRDataInstance)
        {
            int deviceType = ((CTRDataInstance) dataInstance).getDeviceType();
            if(deviceTypeFeatureHashMap.containsKey(deviceType))
            {
                featureArrayList.add(deviceTypeFeatureHashMap.get(deviceType));
            }
            else
            {
                featureArrayList.add(unseenFeature);
            }
        }
        return featureArrayList;
    }
}

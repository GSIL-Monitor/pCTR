package application.adCTR.sample.handlers;

import application.adCTR.data.CTRDataInstance;
import commons.framework.data.DataInstance;
import commons.framework.sample.Feature;
import commons.framework.sample.IFeatureHandler;
import utils.FileUtils;
import utils.NumericalUtils;
import utils.StringUtils;

import java.io.BufferedReader;
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
    private int initFeatureId = 1;
    private int maxFeatureId = 0;
    private Feature unseenFeature = null;
    private HashMap<Integer,Feature> deviceTypeFeatureHashMap = null;

    @Override
    public int initFeatureHandler(int initFeatureId) {
        this.initFeatureId = initFeatureId;
        deviceTypeFeatureHashMap = new HashMap<Integer, Feature>();
        try{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(FileUtils.getStreamFromFile(deviceFeatureConf)));
            String line = null;
            while((line = bufferedReader.readLine()) != null)
            {
                String[] contents = StringUtils.splitStr(line,'\t');
                int deviceType = NumericalUtils.toInteger(contents[0]);
                if(!deviceTypeFeatureHashMap.containsKey(deviceType))
                {
                    int localId = Integer.parseInt(contents[1]);
                    Feature feature = new Feature(localId+initFeatureId,"deviceType","1");
                    deviceTypeFeatureHashMap.put(deviceType, feature);
                }
            }
        }catch (Exception e)
        {
            System.out.println("init device type feature fail");
        }finally
        {
            maxFeatureId = initFeatureId + deviceTypeFeatureHashMap.size();
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

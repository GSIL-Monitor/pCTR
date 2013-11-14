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
 * Date: 13-11-14
 * Time: 上午10:36
 */
public class OsTypeHandler implements IFeatureHandler{
    private final String osFeatureConf = "featureHandlerConf/osType.conf";
    private int initFeatureId = 1;
    private int maxFeatureId = 0;
    private Feature unseenFeature = null;
    private HashMap<Integer,Feature> osTypeFeatureHashMap = null;
    @Override
    public int initFeatureHandler(int initFeatureId) {
        this.initFeatureId = initFeatureId;
        osTypeFeatureHashMap = new HashMap<Integer, Feature>();
        try{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(FileUtils.getStreamFromFile(osFeatureConf)));
            String line = null;
            while((line = bufferedReader.readLine()) != null)
            {
                String[] contents = StringUtils.splitStr(line,'\t');
                int osType = NumericalUtils.toInteger(contents[0]);
                if(!osTypeFeatureHashMap.containsKey(osType))
                {
                    int localId = Integer.parseInt(contents[1]);
                    Feature feature = new Feature(localId+initFeatureId,"osType","1");
                    osTypeFeatureHashMap.put(osType, feature);
                }
            }
        }catch (Exception e)
        {
            System.out.println("init os type feature fail");
        }finally
        {
            maxFeatureId = initFeatureId + osTypeFeatureHashMap.size();
            unseenFeature = new Feature(maxFeatureId,"osType","1");
        }
        return maxFeatureId;
    }

    @Override
    public ArrayList<Feature> ExtractFeature(DataInstance dataInstance) {
        ArrayList<Feature> featureArrayList = new ArrayList<Feature>();
        if(dataInstance instanceof CTRDataInstance)
        {
            int osType = ((CTRDataInstance) dataInstance).getOsType();
            if(osTypeFeatureHashMap.containsKey(osType))
            {
                featureArrayList.add(osTypeFeatureHashMap.get(osType));
            }
            else
            {
                featureArrayList.add(unseenFeature);
            }
        }
        return featureArrayList;
    }
}

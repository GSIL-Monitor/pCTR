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
 * Date: 13-11-20
 * Time: 上午10:19
 */
public class IpHandler implements IFeatureHandler {
    private final String iPFeatureConf = "featureHandlerConf/clickedIp.conf";
    private int maxFeatureId = 0;
    private Feature unseenFeature = null;
    private HashMap<String,Feature> clickedIpFeatureHashMap = null;
    private final int numOfFields = 2;
    @Override
    public int initFeatureHandler(int initFeatureId) {
        clickedIpFeatureHashMap = new HashMap<String, Feature>();
        try{
            InputStream is = FileUtils.getStreamFromFile(iPFeatureConf);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while((line = bufferedReader.readLine()) != null)
            {
                String[] contents = line.split("\t");
                if(contents.length != numOfFields)
                {
                    continue;
                }
                String ip = contents[0];
                if(!clickedIpFeatureHashMap.containsKey(ip))
                {
                    int localId = 1;//it is more like to be a real value feature
                    Feature feature = new Feature(localId+initFeatureId,"clickedIp","1");
                    clickedIpFeatureHashMap.put(ip, feature);
                }
            }

            bufferedReader.close();
            is.close();
        }catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("init ip feature fail");
        }finally
        {
            //deal with unseen feature
            maxFeatureId = initFeatureId + 1;
            unseenFeature = new Feature(maxFeatureId,"ip","0");
        }
        return maxFeatureId;
    }

    @Override
    public ArrayList<Feature> ExtractFeature(DataInstance dataInstance) {
        ArrayList<Feature> featureArrayList = new ArrayList<Feature>();
        if(dataInstance instanceof CTRDataInstance)
        {
            String ip = ((CTRDataInstance) dataInstance).getIp();
            if(clickedIpFeatureHashMap.containsKey(ip))
            {
                featureArrayList.add(clickedIpFeatureHashMap.get(ip));
            }
            else
            {
                featureArrayList.add(unseenFeature);
            }
        }
        return featureArrayList;
    }
}

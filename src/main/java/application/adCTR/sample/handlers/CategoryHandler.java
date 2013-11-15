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
 * Date: 13-11-15
 * Time: 下午4:39
 */
public class CategoryHandler  implements IFeatureHandler {

    private final String categoryFeatureConf = "featureHandlerConf/category.conf";
    private int maxFeatureId = 0;
    private Feature unseenFeature = null;
    private HashMap<String,Feature> categoryFeatureHashMap = null;
    private final int numOfFields = 2;
    @Override
    public int initFeatureHandler(int initFeatureId) {
        categoryFeatureHashMap = new HashMap<String, Feature>();
        try{
            InputStream is = FileUtils.getStreamFromFile(categoryFeatureConf);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while((line = bufferedReader.readLine()) != null)
            {
                String[] contents = line.split("\t");
                if(contents.length != numOfFields)
                {
                    continue;
                }
                String category = contents[0];
                if(!categoryFeatureHashMap.containsKey(category))
                {
                    int localId = Integer.parseInt(contents[1]);
                    Feature feature = new Feature(localId+initFeatureId,"category","1");
                    categoryFeatureHashMap.put(category, feature);
                }
            }
            bufferedReader.close();
            is.close();
        }catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("init category feature fail");
        }finally
        {
            //deal with unseen feature
            unseenFeature = new Feature(maxFeatureId,"category","1");
            maxFeatureId = initFeatureId + categoryFeatureHashMap.size() + 1;
        }
        return maxFeatureId;
    }

    @Override
    public ArrayList<Feature> ExtractFeature(DataInstance dataInstance) {
        ArrayList<Feature> featureArrayList = new ArrayList<Feature>();
        if(dataInstance instanceof CTRDataInstance)
        {
            String category = ((CTRDataInstance) dataInstance).getCategory();
            if(categoryFeatureHashMap.containsKey(category))
            {
                featureArrayList.add(categoryFeatureHashMap.get(category));
            }
            else
            {
                featureArrayList.add(unseenFeature);
            }
        }
        return featureArrayList;
    }
}

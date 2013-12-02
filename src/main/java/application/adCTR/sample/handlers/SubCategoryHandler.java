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
 * Time: 下午6:07
 */
public class SubCategoryHandler implements IFeatureHandler {
    private final String subCategoryFeatureConf = "featureHandlerConf/subCategory.conf";
    private int maxFeatureId = 0;
    private Feature unseenFeature = null;
    private HashMap<String,Feature> subCategoryFeatureHashMap = null;
    private final int numOfFields = 2;
    @Override
    public int initFeatureHandler(int initFeatureId) {
        subCategoryFeatureHashMap = new HashMap<String, Feature>();
        try{
            InputStream is = FileUtils.getStreamFromFile(subCategoryFeatureConf);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while((line = bufferedReader.readLine()) != null)
            {
                String[] contents = line.split("\t");
                if(contents.length != numOfFields)
                {
                    continue;
                }
                String subCategory = contents[0];
                int localId = NumericalUtils.toInteger(contents[1]);
                if(!subCategoryFeatureHashMap.containsKey(subCategory) && localId != Integer.MIN_VALUE)
                {
                    Feature feature = new Feature(localId+initFeatureId,"subCategory","1");
                    subCategoryFeatureHashMap.put(subCategory, feature);
                }
            }
            bufferedReader.close();
            is.close();
        }catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("init subCategory feature fail");
        }finally
        {
            //deal with unseen feature
            maxFeatureId = initFeatureId + subCategoryFeatureHashMap.size() + 1;
            unseenFeature = new Feature(maxFeatureId,"subCategory","1");
        }
        return maxFeatureId;
    }

    @Override
    public ArrayList<Feature> ExtractFeature(DataInstance dataInstance) {
        ArrayList<Feature> featureArrayList = new ArrayList<Feature>();
        if(dataInstance instanceof CTRDataInstance)
        {
            String subCategory = ((CTRDataInstance) dataInstance).getSubCategory();
            if(subCategoryFeatureHashMap.containsKey(subCategory))
            {
                featureArrayList.add(subCategoryFeatureHashMap.get(subCategory));
            }
            else
            {
                featureArrayList.add(unseenFeature);
            }
        }
        return featureArrayList;
    }
}

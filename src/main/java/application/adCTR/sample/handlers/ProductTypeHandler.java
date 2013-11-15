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
 * Time: 下午4:43
 */
public class ProductTypeHandler implements IFeatureHandler {
    private final String productTypeFeatureConf = "featureHandlerConf/productType.conf";
    private int maxFeatureId = 0;
    private Feature unseenFeature = null;
    private HashMap<Integer,Feature> productTypeFeatureHashMap = null;
    private final int numOfFields = 2;
    @Override
    public int initFeatureHandler(int initFeatureId) {
        productTypeFeatureHashMap = new HashMap<Integer, Feature>();
        try{
            InputStream is = FileUtils.getStreamFromFile(productTypeFeatureConf);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while((line = bufferedReader.readLine()) != null)
            {
                String[] contents = line.split("\t");
                if(contents.length != numOfFields)
                {
                    continue;
                }
                int productType = NumericalUtils.toInteger(contents[0]);
                if(!productTypeFeatureHashMap.containsKey(productType))
                {
                    int localId = Integer.parseInt(contents[1]);
                    Feature feature = new Feature(localId+initFeatureId,"productType","1");
                    productTypeFeatureHashMap.put(productType, feature);
                }
            }
            bufferedReader.close();
            is.close();
        }catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("init product type feature fail");
        }finally
        {
            //deal with unseen feature
            unseenFeature = new Feature(maxFeatureId,"productType","1");
            maxFeatureId = initFeatureId + productTypeFeatureHashMap.size() + 1;
        }
        return maxFeatureId;
    }

    @Override
    public ArrayList<Feature> ExtractFeature(DataInstance dataInstance) {
        ArrayList<Feature> featureArrayList = new ArrayList<Feature>();
        if(dataInstance instanceof CTRDataInstance)
        {

            int productType = ((CTRDataInstance) dataInstance).getProductType();
            if(productTypeFeatureHashMap.containsKey(productType))
            {
                featureArrayList.add(productTypeFeatureHashMap.get(productType));
            }
            else
            {
                featureArrayList.add(unseenFeature);
            }
        }
        return featureArrayList;
    }
}

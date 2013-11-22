package application.adCTR.sample.handlers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import utils.FileUtils;
import application.adCTR.data.CTRDataInstance;
import commons.framework.data.DataInstance;
import commons.framework.sample.Feature;
import commons.framework.sample.IFeatureHandler;

public class IsLongHandler implements IFeatureHandler{
	   private final String isLongFeatureConf = "featureHandlerConf/isLong.conf";
	    private int maxFeatureId = 0;
	    private Feature unseenFeature = null;
	    private HashMap<String,Feature> isLongFeatureHashMap = null;
	    private final int numOfFields = 4;
	    @Override
	    public int initFeatureHandler(int initFeatureId) {
	    	isLongFeatureHashMap = new HashMap<String, Feature>();
	        int cntOfProgramIDCombo = 0;
	        double sumOfAvgClickRate = 0;
	        try{
	            InputStream is = FileUtils.getStreamFromFile(isLongFeatureConf);
	            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
	            String line = null;
	            while((line = bufferedReader.readLine()) != null)
	            {
	                String[] contents = line.split("\t");
	                if(contents.length != numOfFields)
	                {
	                    continue;
	                }
	                String isLong = contents[0];
	                double clickRate = Double.parseDouble(contents[3]);
	                if(!isLongFeatureHashMap.containsKey(isLong) && !Double.isNaN(clickRate))
	                {
	                	cntOfProgramIDCombo++;
	                	sumOfAvgClickRate += clickRate; 
	                    int localId = 1;
	                    Feature feature = new Feature(localId+initFeatureId,"isLongClickRate",contents[3]);
	                    isLongFeatureHashMap.put(isLong, feature);
	                }
	            }
	            bufferedReader.close();
	            is.close();
	        }catch (Exception e)
	        {
	            e.printStackTrace();
	            System.out.println("init isLong feature fail");
	        }finally
	        {
	            //deal with unseen feature
	            maxFeatureId = initFeatureId + 1;
	            unseenFeature = new Feature(maxFeatureId,"isLongClickRate","0");
	        }
	        return maxFeatureId;
	    }

	    @Override
	    public ArrayList<Feature> ExtractFeature(DataInstance dataInstance) {
	        ArrayList<Feature> featureArrayList = new ArrayList<Feature>();
	        if(dataInstance instanceof CTRDataInstance)
	        {
	            String isLong = ((CTRDataInstance) dataInstance).isLong()?"1":"0";
	            if(isLongFeatureHashMap.containsKey(isLong))
	            {
	                featureArrayList.add(isLongFeatureHashMap.get(isLong));
	            }
	            else
	            {
	                featureArrayList.add(unseenFeature);
	            }
	        }
	        return featureArrayList;
	    }
}

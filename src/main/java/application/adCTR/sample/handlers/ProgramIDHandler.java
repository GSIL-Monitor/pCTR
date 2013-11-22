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

public class ProgramIDHandler implements IFeatureHandler {
	   private final String programIdFeatureConf = "featureHandlerConf/programId.conf";
	    private int maxFeatureId = 0;
	    private Feature unseenFeature = null;
	    private HashMap<String,Feature> programIdFeatureHashMap = null;
	    private final int numOfFields = 4;
	    @Override
	    public int initFeatureHandler(int initFeatureId) {
	    	programIdFeatureHashMap = new HashMap<String, Feature>();
	        int cntOfProgramIDCombo = 0;
	        double sumOfAvgClickRate = 0;
	        try{
	            InputStream is = FileUtils.getStreamFromFile(programIdFeatureConf);
	            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
	            String line = null;
	            while((line = bufferedReader.readLine()) != null)
	            {
	                String[] contents = line.split("\t");
	                if(contents.length != numOfFields)
	                {
	                    continue;
	                }
	                String programId = contents[0];
	                double clickRate = Double.parseDouble(contents[3]);
	                if(!programIdFeatureHashMap.containsKey(programId) && !Double.isNaN(clickRate))
	                {
	                	cntOfProgramIDCombo++;
	                	sumOfAvgClickRate += clickRate; 
	                    int localId = 1;
	                    Feature feature = new Feature(localId+initFeatureId,"programIdClickRate",contents[3]);
	                    programIdFeatureHashMap.put(programId, feature);
	                }
	            }
	            bufferedReader.close();
	            is.close();
	        }catch (Exception e)
	        {
	            e.printStackTrace();
	            System.out.println("init programId feature fail");
	        }finally
	        {
	            //deal with unseen feature
	            maxFeatureId = initFeatureId + 1;
	            unseenFeature = new Feature(maxFeatureId,"programIdClickRate","0");
	        }
	        return maxFeatureId;
	    }

	    @Override
	    public ArrayList<Feature> ExtractFeature(DataInstance dataInstance) {
	        ArrayList<Feature> featureArrayList = new ArrayList<Feature>();
	        if(dataInstance instanceof CTRDataInstance)
	        {
	            String programId = ((CTRDataInstance) dataInstance).getProgramId();
	            if(programIdFeatureHashMap.containsKey(programId))
	            {
	                featureArrayList.add(programIdFeatureHashMap.get(programId));
	            }
	            else
	            {
	                featureArrayList.add(unseenFeature);
	            }
	        }
	        return featureArrayList;
	    }
}

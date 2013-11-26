package application.adCTR.evaluation.adSelector;

import application.adCTR.evaluation.creative.Creative;
import utils.FileUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-26
 * Time: 上午10:38
 */
public class SimpleAdSelector implements IAdSelector{
    private ArrayList<Double> weightList;
    private static SimpleAdSelector instance;
    private SimpleAdSelector(){
    }

    public static SimpleAdSelector getInstance()
    {
        if(instance == null)
        {
            instance = new SimpleAdSelector();
        }
        return instance;
    }

    private double logisticFunction(double value)
    {
        return 1/(1 + Math.exp(0 - value));
    }

    @Override
    public ArrayList<Creative> selectAdFromPool(HashSet<Creative> creativePool) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long loadModel(String modelFile) {
        this.weightList = new ArrayList<Double>();
        try{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(FileUtils.getStreamFromFile(modelFile)));
            String line;
            boolean tag = false;
            while((line = bufferedReader.readLine()) != null)
            {
                if(line.trim().equals("w"))
                {
                    tag = true;
                    continue;
                }
                if(!tag)
                {
                    continue;
                }
                double value = Double.parseDouble(line.trim());
                this.weightList.add(value);
            }
            bufferedReader.close();
        }catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("Loading weight file wrong");
            return -1;
        }
        return weightList.size();
    }
}



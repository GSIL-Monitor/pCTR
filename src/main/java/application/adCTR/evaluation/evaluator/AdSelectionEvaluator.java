package application.adCTR.evaluation.evaluator;

import application.adCTR.evaluation.creative.CreativeInventory;
import utils.FileUtils;
import utils.NumericalUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-20
 * Time: 上午9:42
 */
public class AdSelectionEvaluator {
    //creative inventory
    CreativeInventory inventory = CreativeInventory.getInstance();

    private ArrayList<Double> weightList;
    private int weightSize = 0;
    private double threshold = 0.5;

    private long pos_pos; //positive sample with positive prediction
    private long pos_neg; //positive sample with negative prediction
    private long neg_pos; //negative sample with positive prediction
    private long neg_neg; //negative sample with negative prediction

    public void setThreshold(double threshold)
    {
        this.threshold = threshold;
    }

    public void evaluateUsingLR(String testFile)
    {
        this.clearMetric();
        try{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(FileUtils.getStreamFromFile(testFile)));
            String line;
            long cnt = 0;
            while( (line = bufferedReader.readLine()) != null)
            {
                cnt++;
                if(cnt % 1000000 == 0)
                {
                    System.out.println("Evaluation " + cnt + " lines");
                }
                String[] values = line.split(" ");
                int target = NumericalUtils.toInteger(values[0]);
                if(target == Integer.MIN_VALUE)
                {
                    continue;
                }
                double value = logisticFunction(getPrediction(values));
                int prediction = value >= threshold ? 1 : 0;
                updateMetric(target, prediction);
            }
            bufferedReader.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        printMetric();
    }

    public void evaluateUsingSVM(String testFile)
    {
        this.clearMetric();
    }

    private void printMetric()
    {
        long testCallback = pos_pos + neg_pos;
        long testClick = pos_pos;
        double testCtr = (double)testClick/(double)testCallback;

        long baseCallback = pos_pos + pos_neg + neg_pos + neg_neg;
        long baseClick = pos_pos + pos_neg;
        double baseCtr = (double)baseClick/(double)baseCallback;


        System.out.println("Threshold = " + this.threshold);
        System.out.printf("Base: click = %d, impression = %d, ctr = %2f\n", baseClick, baseCallback, baseCtr);
        System.out.printf("Test: click = %d, impression = %d, ctr = %2f\n", testClick, testCallback, testCtr);
        System.out.printf("Ctr improve = %s \n", NumericalUtils.formatDecimal("##.##%",(testCtr/baseCtr - 1)));
        System.out.printf("Recall = %s\n", NumericalUtils.formatDecimal("##.##%", ((double)testClick/(double)baseClick)));
        System.out.println("========================================================");
    }

    private void updateMetric(int target, int prediction)
    {
        if(target == 1)
        {
            if(prediction == 1)
            {
                pos_pos++;
            }
            else
            {
                pos_neg++;
            }
        }
        else
        {
            if(prediction == 1)
            {
                neg_pos++;
            }
            else
            {
                neg_neg++;
            }
        }
    }

    private double logisticFunction(double value)
    {
        return 1/(1 + Math.exp(0 - value));
    }

    private double getPrediction(String[] features)
    {
        double value = 0;
        for(int i = 1; i < features.length; i++)
        {
            String[] pairs = features[i].split(":");
            if(pairs.length != 2)
            {
                continue;
            }
            int index = NumericalUtils.toInteger(pairs[0]);
            double featureWeight = NumericalUtils.toDouble(pairs[1]);
            if(index == Integer.MIN_VALUE || Double.isNaN(featureWeight))
            {
                continue;
            }
            if(index >= weightSize)
            {
                continue;
            }
            value += weightList.get(index - 1) * featureWeight;
        }
        return value;
    }

    private void clearMetric()
    {
        this.pos_pos = 0;
        this.pos_neg = 0;
        this.neg_pos = 0;
        this.neg_neg = 0;
    }

    public void setupAdDataBase(String date)
    {
        int numOfCreative = inventory.loadCreativeFromDB();
        if(numOfCreative < 0)
        {
            System.out.println("Loading Creative from Database Fail");
            System.exit(-1);
        }
        System.out.println("Loading " + numOfCreative + " pieces of creative from database");
    }

    public void loadWeightArrayList(String weightFile)
    {
        this.weightList = new ArrayList<Double>();
        try{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(FileUtils.getStreamFromFile(weightFile)));
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
            System.exit(-1);
        }
        weightSize = weightList.size();
        System.out.println("weight loaded, there are " + weightSize + " features");
    }

    public static void main(String[] args)
    {
        int numOfArgs = 3;
        if(args.length != numOfArgs)
        {
            System.out.println("Not Enough Parameters");
            System.out.println("Parameters: $typeOfTraining, $weightFile, $testFile");
            System.exit(-1);
        }
        String type = args[0].toUpperCase();
        String weightFile = args[1];
        String testFile = args[2];

        if(!LiblinearTypes.isLiblinearTypes(type))
        {
            System.out.println("unsupported training type");
            System.out.println("only logistic and svm are supported");
            System.exit(-1);
        }

        LiblinearTypes trainingType = LiblinearTypes.valueOf(type);
        AdSelectionEvaluator evaluator = new AdSelectionEvaluator();
        evaluator.loadWeightArrayList(weightFile);
        if(trainingType == LiblinearTypes.LOGISTIC)
        {
            for(int i = 2; i < 14; i++)
            {
                evaluator.setThreshold(0.05 + i * 0.05);
                evaluator.evaluateUsingLR(testFile);
            }
        }
        if(trainingType == LiblinearTypes.SVM)
        {
            evaluator.evaluateUsingSVM(testFile);
        }
    }
}

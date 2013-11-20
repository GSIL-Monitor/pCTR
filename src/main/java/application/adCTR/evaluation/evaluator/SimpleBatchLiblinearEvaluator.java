package application.adCTR.evaluation.evaluator;

import utils.FileUtils;
import utils.NumericalUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-20
 * Time: 下午1:01
 */
public class SimpleBatchLiblinearEvaluator {
    private ArrayList<Double> weightList;
    private int weightSize = 0;
    private final int numOfBatch = 20;
    private final double base = 0.4;
    private final double stepSize = 0.01;

    private long[] pos_pos; //positive sample with positive prediction
    private long[] pos_neg; //positive sample with negative prediction
    private long[] neg_pos; //negative sample with positive prediction
    private long[] neg_neg; //negative sample with negative prediction

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
                double tmpValue = getPrediction(values);
                double value = logisticFunction(tmpValue);
                batchUpdateMetric(target, value);
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
                double value = getPrediction(values);
                batchUpdateMetric(target, value);
            }
            bufferedReader.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        printMetric();
    }

    private void printMetric()
    {
        System.out.println("Here comes the result");
        System.out.println("========================================================");
        for(int i = 0; i < numOfBatch; i++)
        {
            long testCallback = pos_pos[i] + neg_pos[i];
            long testClick = pos_pos[i];
            double testCtr = (double)testClick/(double)testCallback;

            long baseCallback = pos_pos[i] + pos_neg[i] + neg_pos[i] + neg_neg[i];
            long baseClick = pos_pos[i] + pos_neg[i];
            double baseCtr = (double)baseClick/(double)baseCallback;


            System.out.printf("Threshold = %2f\n", (i * stepSize + base));
            System.out.printf("Base: click = %d, impression = %d, ctr = %2f\n", baseClick, baseCallback, baseCtr);
            System.out.printf("Test: click = %d, impression = %d, ctr = %2f\n", testClick, testCallback, testCtr);
            double improve = testCtr/baseCtr - 1;
            System.out.printf("Ctr improve = %s \n", NumericalUtils.formatDecimal("##.##%", improve));
            double recall = (double)testClick/(double)baseClick;
            System.out.printf("Recall = %s\n", NumericalUtils.formatDecimal("##.##%", recall));
            double score = improve * recall * 10000;
            System.out.printf("Score = %s\n", NumericalUtils.formatDecimal("##.##", score));
            System.out.println("========================================================");
        }
    }

    private void batchUpdateMetric(int target, double value)
    {
        for(int i = 0; i < numOfBatch; i++)
        {
            double threshold = stepSize * i + base;
            int prediction = value > threshold ? 1 : 0;
            updateMetric(target, prediction, i);
        }
    }

    private void updateMetric(int target, int prediction, int index)
    {
        if(target == 1)
        {
            if(prediction == 1)
            {
                pos_pos[index]++;
            }
            else
            {
                pos_neg[index]++;
            }
        }
        else
        {
            if(prediction == 1)
            {
                neg_pos[index]++;
            }
            else
            {
                neg_neg[index]++;
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
            if(index > weightSize)
            {
                continue;
            }
            value += weightList.get(index - 1) * featureWeight;
        }
        return value;
    }

    private void clearMetric()
    {
        this.pos_pos = new long[numOfBatch];
        this.pos_neg = new long[numOfBatch];
        this.neg_pos = new long[numOfBatch];
        this.neg_neg = new long[numOfBatch];
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
        SimpleBatchLiblinearEvaluator evaluator = new SimpleBatchLiblinearEvaluator();
        if(trainingType == LiblinearTypes.LOGISTIC)
        {
            evaluator.loadWeightArrayList(weightFile);
            evaluator.evaluateUsingLR(testFile);
        }
        if(trainingType == LiblinearTypes.SVM)
        {
            evaluator.loadWeightArrayList(weightFile);
            evaluator.evaluateUsingSVM(testFile);
        }
    }
}

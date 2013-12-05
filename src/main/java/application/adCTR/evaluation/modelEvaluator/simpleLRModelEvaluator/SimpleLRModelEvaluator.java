package application.adCTR.evaluation.modelEvaluator.simpleLRModelEvaluator;

import application.adCTR.evaluation.modelEvaluator.AbstractModelEvaluator;
import commons.framework.sample.Feature;
import commons.framework.sample.Sample;
import utils.FileUtils;
import utils.NumericalUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-12-2
 * Time: 上午9:14
 */
public class SimpleLRModelEvaluator extends AbstractModelEvaluator {
    private static SimpleLRModelEvaluator evaluator = null;
    private SimpleLRModelEvaluator(){}

    public static SimpleLRModelEvaluator getEvaluator()
    {
        if(evaluator == null)
        {
            evaluator = new SimpleLRModelEvaluator();
        }
        return evaluator;
    }

    //business part
    private int numOfBatch;
    private double base;
    private double stepSize;

    private ArrayList<Double> weightList;
    private SimpleLRModelSampleDecorator decorator;
    private long weightSize;

    private long[] pos_pos; //positive sample with positive prediction
    private long[] pos_neg; //positive sample with negative prediction
    private long[] neg_pos; //negative sample with positive prediction
    private long[] neg_neg; //negative sample with negative prediction

    @Override
    public void init(String sampleSourceFile, String modelFile) {
        //set up parameters
        this.config = new SimpleLRModelEvaluatorConfig();
        config.loadConfig();
        setParameters();
        System.out.println("evaluation parameters set succeed");
        System.out.println("=======================================================");

        //set up model
        long numOfFeatures = loadModel(modelFile);
        if(numOfFeatures > 0)
        {
            System.out.println("model loaded, there are " + numOfFeatures + " features");
            System.out.println("=======================================================");
        }
        else
        {
            System.out.println("loading model file " + modelFile + " fail");
            System.exit(-1);
        }

        //set up decorator
        this.setupSampleDecorator(sampleSourceFile);
        System.out.println("sample decorator setup succeed");
        System.out.println("=======================================================");
    }

    private void setParameters()
    {
        SimpleLRModelEvaluatorConfig simpleConfig = (SimpleLRModelEvaluatorConfig) config;
        numOfBatch = simpleConfig.getNumOfBatch();
        base = simpleConfig.getBase();
        stepSize = simpleConfig.getStepSize();

        this.pos_pos = new long[numOfBatch];
        this.pos_neg = new long[numOfBatch];
        this.neg_pos = new long[numOfBatch];
        this.neg_neg = new long[numOfBatch];
    }

    private void setupSampleDecorator(String sampleSourceFile)
    {
        decorator = new SimpleLRModelSampleDecorator(sampleSourceFile);
    }

    @Override
    public long evaluation() {
        System.out.println("Evaluation started");
        long index = 0;
        Sample sample;
        while((sample = decorator.getSample()) != null)
        {
            index++;
            if(index % 1000000 == 0)
            {
                System.out.println("Evaluation process " + index + " lines");
            }

            double prediction = calcScore(sample.getFeatureArrayList());
            double target = sample.getTargetValue();

            batchUpdateMetric(target, prediction);
        }

        return index;
    }

    private void batchUpdateMetric(double target, double value)
    {
        for(int i = 0; i < numOfBatch; i++)
        {
            double threshold = stepSize * i + base;
            double prediction = value > threshold ? 1 : 0;
            updateMetric(target, prediction, i);
        }
    }

    private void updateMetric(double target, double prediction, int index)
    {
        if(target > 0)
        {
            if(prediction > 0)
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
            if(prediction > 0)
            {
                neg_pos[index]++;
            }
            else
            {
                neg_neg[index]++;
            }
        }
    }

    @Override
    public void printMetric() {
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

    @Override
    public long loadModel(String modelFile) {
        this.weightList = new ArrayList<Double>();
        try{
            InputStream is = FileUtils.getStreamFromFile(modelFile);
            InputStreamReader isReader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(isReader);
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
            isReader.close();
            is.close();
        }catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("Loading model file wrong");
            System.exit(-1);
        }
        this.weightSize = weightList.size();
        return weightList.size();
    }

    @Override
    public double calcScore(ArrayList<Feature> features) {
        if(features == null || features.size() < 1)
        {
            return 0;
        }

        double score = 0;
        for(int i = 0, len = features.size(); i < len; i++)
        {
            Feature feature = features.get(i);
            int idx = feature.getFeatureId() - 1;
            double value = NumericalUtils.toDouble(feature.getFeatureValue());
            if(Double.isNaN(value) || idx < 0)
            {
                continue;
            }
            score += getWeight(idx) * value;
        }

        return logisticFunction(score);
    }

    private double getWeight(int index)
    {
        return weightSize >= index ? weightList.get(index) : 0;
    }

    private double logisticFunction(double value)
    {
        return 1/(1 + Math.exp(0 - value));
    }

    public void cleanup()
    {
        decorator.close();
        weightList.clear();
    }

    private static void usage()
    {
        System.out.println("usage : $jarFile $modelFile $testFile");
    }

    public static void main(String[] args)
    {
        int numOfParameters = 2;
        if(args.length != numOfParameters)
        {
            usage();
            System.exit(-1);
        }

        String modelFile = args[0];
        String testFile = args[1];

        SimpleLRModelEvaluator simpleLRModelEvaluator = SimpleLRModelEvaluator.getEvaluator();
        simpleLRModelEvaluator.init(testFile, modelFile);

        long start = System.currentTimeMillis();
        long numOfSamples = simpleLRModelEvaluator.evaluation();
        long end = System.currentTimeMillis();

        System.out.println("Evaluate " + numOfSamples + " Samples Takes " + (end-start)/1000 + " seconds");

        simpleLRModelEvaluator.printMetric();
        simpleLRModelEvaluator.cleanup();
    }
}

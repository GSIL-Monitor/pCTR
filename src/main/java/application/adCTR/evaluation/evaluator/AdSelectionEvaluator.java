package application.adCTR.evaluation.evaluator;

import application.adCTR.data.CTRDataInstance;
import application.adCTR.data.CTRDataInstanceMaker;
import application.adCTR.data.CTRDataSource;
import application.adCTR.data.CTRSourceHandler;
import application.adCTR.evaluation.adSelector.AdSelectorTypes;
import application.adCTR.evaluation.adSelector.IAdSelector;
import application.adCTR.evaluation.adSelector.SimpleAdSelector;
import application.adCTR.evaluation.creative.Creative;
import application.adCTR.evaluation.creative.CreativeInventory;
import commons.framework.sample.Sample;
import utils.FileUtils;
import utils.NumericalUtils;
import utils.database.DataBaseManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-20
 * Time: 上午9:42
 */
public class AdSelectionEvaluator {
    //creative inventory
    private CreativeInventory inventory = null;
    private DataBaseManager dataBaseManager = null;
    private String datetime = null;

    //evaluation parameters
    private IAdSelector adSelector;
    private AdSelectionEvaluatorConfig config;
    private int numOfBatch;
    private double base;
    private double stepSize;

    //data instance
    private CTRDataSource dataSource;
    private CTRDataInstanceMaker dataInstanceMaker = null;

    //six possible return
    private long[] noReturn_Click; //clicked but no return from the model
    private long[] noReturn_nonClick; //not clicked and no return from the model
    private long[] hasReturn_Same_Click; //has the same return as data source, which is also clicked
    private long[] hasReturn_Same_nonClick; //has the same return as data source, which not being clicked
    private long[] hasReturn_Diff_Click; //with different return as data source, which is clicked in data source
    private long[] hasReturn_Diff_nonClick; //different return as data source, and not clicked in data source
    private long totalClick;
    private long totalUnClick;

    public AdSelectionEvaluator(String datetime)
    {
        this.datetime = datetime;
    }

    public void initAdSelector(String adSelectorModelFile, String dateSourceFile)
    {
        //parameters
        this.config = AdSelectionEvaluatorConfig.getInstance();
        config.loadConfig();
        this.setThresholdParameters();
        System.out.println("init evaluation parameters succeed");
        System.out.println("======================================================");
        //database
        dataBaseManager = DataBaseManager.getInstance();
        dataBaseManager.init();
        System.out.println("init database manager succeed");
        System.out.println("======================================================");
        //inventory
        inventory = CreativeInventory.getInstance();
        inventory.init(datetime);
        System.out.println("init creative inventory succeed");
        System.out.println("======================================================");
        //data source and instance maker
        this.registerDataSource(dateSourceFile);
        this.initDataInstanceMaker();
        System.out.println("init data source and data instance maker succeed");
        System.out.println("======================================================");
        //ad selector
        long numOfFeatures = this.adSelector.loadModel(adSelectorModelFile);
        if(numOfFeatures < 0)
        {
            System.out.println("loading models from " + adSelectorModelFile + " fail");
            System.exit(-1);
        }
        int numOfHandlers = this.adSelector.registerSampleHandlers();
        if(numOfHandlers < 0)
        {
            System.out.println("init sample handlers fail");
            System.exit(-1);
        }
        System.out.println("init ad selector succeed, there are " + numOfFeatures + " features in total");
        System.out.println("======================================================");
    }

    public long parallelRunEvaluation()
    {
        ExecutorService executorService = Executors.newCachedThreadPool();
        System.out.println("Evaluation started......");
        String line;
        long id = 0;
        while((line = dataSource.readLine()) != null)
        {
            final String innerLine = line;
            id++;
            final long index = id;
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    if(index % 10000000 == 0)
                    {
                        System.out.println("Processing " + index + " Samples");
                    }
                    CTRDataInstance dataInstance = makeDataInstance(innerLine, index);
                    if(!dataInstance.isCorrectlyFilled())//not correctly filled
                    {
                        System.out.println("data source line = " + index + " not correctly set");
                    }
                    else
                    {
                        int originCreativeID = dataInstance.getCreativeId();
                        double targetValue = dataInstance.getTargetValue();
                        updateOriginMetric(targetValue);
                        if(!inventory.isCreativeSubstitutable(originCreativeID))//not substitutable
                        {
                            updateWithOriginCreative(dataInstance.getTargetValue());
                        }
                        else
                        {
                            //creative context
                            String context = String.format("%d_%s", dataInstance.getCityId(), dataInstance.getCategory());
                            Creative[] creativePart1 = inventory.getCreativeSetByContext(context);
                            context = String.format("%d_%s", dataInstance.getCityId(), dataInstance.getSubCategory());
                            Creative[] creativePart2 = inventory.getCreativeSetByContext(context);
                            Creative selectedCreative = null;
                            if(creativePart1.length > 0)
                            {
                                selectedCreative = adSelector.selectAdFromPool(dataInstance, creativePart1).get(0);
                            }
                            if(creativePart2.length > 0)
                            {
                                Creative tmpCreative =  adSelector.selectAdFromPool(dataInstance, creativePart2).get(0);
                                if(tmpCreative.getScore() > selectedCreative.getScore())
                                {
                                    selectedCreative = tmpCreative;
                                }
                            }
                            if(selectedCreative == null)
                            {
                                updateWithOriginCreative(dataInstance.getTargetValue());
                            }
                            else
                            {
                                batchUpdateMetric(selectedCreative, originCreativeID, targetValue);
                            }
                        }
                    }
                }
            };
            executorService.execute(run);
        }
        executorService.shutdown();
        return id;
    }

    public long runEvaluation()
    {
        System.out.println("Evaluation started......");
        String line;
        long id = 1;
        while((line = dataSource.readLine()) != null)
        {
            if(id % 100000 == 0)
            {
                System.out.println("Processing " + id + " lines");
            }
            id++;
            CTRDataInstance dataInstance = makeDataInstance(line, id);
            if(!dataInstance.isCorrectlyFilled())//not correctly filled
            {
                continue;
            }
            int originCreativeID = dataInstance.getCreativeId();
            double targetValue = dataInstance.getTargetValue();
            updateOriginMetric(targetValue);
            if(!inventory.isCreativeSubstitutable(originCreativeID))//not substitutable
            {
                updateWithOriginCreative(dataInstance.getTargetValue());
                continue;
            }

            //creative context
            String context = String.format("%d_%s", dataInstance.getCityId(), dataInstance.getCategory());
            Creative[] creativePart1 = inventory.getCreativeSetByContext(context);
            context = String.format("%d_%s", dataInstance.getCityId(), dataInstance.getSubCategory());
            Creative[] creativePart2 = inventory.getCreativeSetByContext(context);
            Creative selectedCreative = null;
            if(creativePart1.length > 0)
            {
                selectedCreative = adSelector.selectAdFromPool(dataInstance, creativePart1).get(0);
            }
            if(creativePart2.length > 0)
            {
                Creative tmpCreative =  adSelector.selectAdFromPool(dataInstance, creativePart2).get(0);
                if(tmpCreative.getScore() > selectedCreative.getScore())
                {
                    selectedCreative = tmpCreative;
                }
            }
            if(selectedCreative == null)
            {
                updateWithOriginCreative(dataInstance.getTargetValue());
            }
            else
            {
                batchUpdateMetric(selectedCreative, originCreativeID, targetValue);
            }
        }

        System.out.println("Evaluation done......");
        System.out.println("======================================================");
        return id;
    }

    public void printMetric()
    {
        for(int i = 0; i < numOfBatch; i++)
        {
            double threshold = base + i * stepSize;
            System.out.println("Metric for threshold = " + threshold);
            //base information
            System.out.println("call back same click = " + hasReturn_Same_Click[i]);
            System.out.println("call back same nonClick = " + hasReturn_Same_nonClick[i]);
            System.out.println("call back diff click = " + hasReturn_Diff_Click[i]);
            System.out.println("call back diff nonClick = " + hasReturn_Diff_nonClick[i]);
            System.out.println("non call back click = " + noReturn_Click[i]);
            System.out.println("non call back nonClick = " + noReturn_nonClick[i]);
            //recall
            long totalCallBack = hasReturn_Same_Click[i] + hasReturn_Same_nonClick[i] + hasReturn_Diff_Click[i] + hasReturn_Diff_nonClick[i];
            long totalNonCallBack = noReturn_Click[i] + noReturn_nonClick[i];
            long totalInstance = totalCallBack + totalNonCallBack;
            double recall = (double)totalCallBack/(double)totalInstance;
            System.out.printf("callback = %d, throw = %d, total = %d, recall = %s\n",
                    totalCallBack, totalNonCallBack, totalInstance, NumericalUtils.formatDecimal("##.##%",recall));
            //right parts
            long callBackClick = hasReturn_Same_Click[i];
            long totalSameCallBack = hasReturn_Same_Click[i] + hasReturn_Same_nonClick[i];
            double clickRate = (double)callBackClick/(double)totalSameCallBack;
            double baseClickRate = (double)totalClick/(double)(totalClick + totalUnClick);
            System.out.printf("callback click = %d, test ctr = %s, base ctr = %s\n", callBackClick, NumericalUtils.formatDecimal("##.##%", clickRate),
                    NumericalUtils.formatDecimal("##.##%", baseClickRate));
            //waste rate, which is the number of click we throw over non-callback and total click * 10000
            long wasteClick = noReturn_Click[i];
            double wasteRatio = (double)wasteClick /(double)totalClick;
            double wasteRate = (double)wasteClick / (double)totalNonCallBack;
            double wasteScore = wasteRatio * wasteRate * 10000;
            System.out.printf("waste click = %d, waste ratio = %s, waste rate = %s, waste score = %s\n",
                    wasteClick, NumericalUtils.formatDecimal("##.##%", wasteRatio),
                    NumericalUtils.formatDecimal("##.##%", wasteRate), NumericalUtils.formatDecimal("##.##", wasteScore));
            System.out.println("======================================================");
        }
    }

    private void updateOriginMetric(double targetValue)
    {
        if(targetValue > 0)
        {
            totalClick++;
            return;
        }
        totalUnClick++;
    }

    private void updateWithOriginCreative(double targetValue)
    {
        if(targetValue > 0)
        {
            for(int i = 0; i < numOfBatch; i++)
            {
                hasReturn_Same_Click[i]++;
            }
        }
        else
        {
            for(int i = 0; i < numOfBatch; i++)
            {
                hasReturn_Same_nonClick[i]++;
            }
        }
    }

    private void batchUpdateMetric(Creative returnCreative, int originCreativeID, double originTargetValue)
    {
        double score = returnCreative.getScore();
        int retCreativeID = returnCreative.getCreativeId();

        for(int i = 0; i < numOfBatch; i++)
        {
            double threshold = base + i * stepSize;
            if(threshold > score)//no return for this threshold
            {
                updateNoReturnMetric(i, originTargetValue);
            }
            else
            {
                if(retCreativeID == originCreativeID)//get the same return
                {
                    updateSameReturnMetric(i, originTargetValue);
                }
                else
                {
                    updateDiffReturnMetric(i, originTargetValue);
                }
            }
        }
    }

    private void updateDiffReturnMetric(int index, double targetValue)
    {
        if(targetValue > 0)//clicked in data source
        {
            hasReturn_Diff_Click[index]++;
        }
        else
        {
            hasReturn_Diff_nonClick[index]++;
        }
    }

    private void updateSameReturnMetric(int index, double targetValue)
    {
        if(targetValue > 0)//clicked in data source
        {
            hasReturn_Same_Click[index]++;
        }
        else
        {
            hasReturn_Same_nonClick[index]++;
        }
    }

    private void updateNoReturnMetric(int index, double targetValue)
    {
        if(targetValue > 0)//clicked in the data source
        {
            noReturn_Click[index]++;
        }
        else
        {
            noReturn_nonClick[index]++;
        }
    }

    private CTRDataInstance makeDataInstance(String sourceContent, long id)
    {
        CTRDataInstance dataInstance = new CTRDataInstance(id);
        dataInstanceMaker.fillDataInstance(sourceContent,dataInstance);
        return dataInstance;
    }

    private void registerDataSource(String dataSourceFile)
    {
        this.dataSource = CTRDataSource.getInstance();
        dataSource.setupSource(dataSourceFile);
    }

    private void initDataInstanceMaker()
    {
        dataInstanceMaker = CTRDataInstanceMaker.getInstance();
        CTRSourceHandler ctrSourceHandler = new CTRSourceHandler();
        dataInstanceMaker.registerSourceHandler(ctrSourceHandler);
    }

    private void setThresholdParameters()
    {
        this.numOfBatch = config.getNumOfBatch();
        this.base = config.getBase();
        this.stepSize = config.getStepSize();

        initMetric();

        setAdSelector(config.getAdSelectorType());
    }

    private void initMetric()
    {
        this.noReturn_Click = new long[numOfBatch];
        this.noReturn_nonClick = new long[numOfBatch];
        this.hasReturn_Same_Click = new long[numOfBatch];
        this.hasReturn_Same_nonClick = new long[numOfBatch];
        this.hasReturn_Diff_Click = new long[numOfBatch];
        this.hasReturn_Diff_nonClick = new long[numOfBatch];
    }

    private void setAdSelector(AdSelectorTypes adSelectorType)
    {
        switch (adSelectorType){
            case SIMPLE_AD_SELECTOR:
                this.adSelector = SimpleAdSelector.getInstance();
                break;
            default:
                System.out.println("wrong type of adSelector");
                System.exit(-1);
        }
    }

    public static void main(String[] args)
    {
        int numOfParameters = 3;
        if(args.length < numOfParameters)
        {
            System.out.println("not enough parameters");
            System.out.println("parameters: $datetime, $modelFile, $testFile");
            System.exit(-1);
        }
        String datetime = args[0];
        String modelFile = args[1];
        String testFile = args[2];
        AdSelectionEvaluator evaluator = new AdSelectionEvaluator(datetime);
        evaluator.initAdSelector(modelFile, testFile);
        long start = System.currentTimeMillis();
        long numOfSamples = evaluator.runEvaluation();
//        long numOfSamples = evaluator.parallelRunEvaluation();
        long end = System.currentTimeMillis();
        System.out.println("Evaluate " + numOfSamples + " data instance takes " + (end - start) + " mills");
        evaluator.printMetric();
    }
}

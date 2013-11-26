package application.adCTR.evaluation.evaluator;

import application.adCTR.evaluation.adSelector.AdSelectorTypes;
import application.adCTR.evaluation.adSelector.IAdSelector;
import application.adCTR.evaluation.adSelector.SimpleAdSelector;
import application.adCTR.evaluation.creative.CreativeInventory;
import utils.FileUtils;
import utils.NumericalUtils;
import utils.database.DataBaseManager;

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
    private CreativeInventory inventory = null;
    private DataBaseManager dataBaseManager = null;
    private String datetime = null;

    //evaluation parameters
    private IAdSelector adSelector;
    private AdSelectionEvaluatorConfig config;
    private int numOfBatch;
    private double base;
    private double stepSize;

    private long pos_pos; //positive sample with positive prediction
    private long pos_neg; //positive sample with negative prediction
    private long neg_pos; //negative sample with positive prediction
    private long neg_neg; //negative sample with negative prediction

    public AdSelectionEvaluator(String datetime)
    {
        this.datetime = datetime;
    }

    public void initAdSelector(String adSelectorModelFile)
    {
        dataBaseManager = DataBaseManager.getInstance();
        dataBaseManager.init();
        System.out.println("init database manager succeed");
        System.out.println("======================================================");

        inventory = CreativeInventory.getInstance();
        inventory.init(datetime);
        System.out.println("init creative inventory succeed");
        System.out.println("======================================================");

        this.config = AdSelectionEvaluatorConfig.getInstance();
        this.setThresholdParameters();
        System.out.println("init evaluation parameters succeed");
        System.out.println("======================================================");

        long numOfFeatures = this.adSelector.loadModel(adSelectorModelFile);
        if(numOfFeatures < 0)
        {
            System.out.println("loading models from " + adSelectorModelFile + " fail");
            System.exit(-1);
        }
        System.out.println("init ad selector succeed, there are " + numOfFeatures + " features in total");
        System.out.println("======================================================");
    }

    private void setThresholdParameters()
    {
        this.numOfBatch = config.getNumOfBatch();
        this.base = config.getBase();
        this.stepSize = config.getStepSize();
        setAdSelector(config.getAdSelectorType());
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

    public void checkCreative()
    {
        inventory.printCreative();
    }

    public static void main(String[] args)
    {
        String datetime = "2013-11-16";
        String modelFile = "";
        System.out.println("prepare evaluation for date : " + datetime);
        AdSelectionEvaluator evaluator = new AdSelectionEvaluator(datetime);

        evaluator.initAdSelector(modelFile);
        evaluator.checkCreative();
        System.out.println("init done");
    }
}

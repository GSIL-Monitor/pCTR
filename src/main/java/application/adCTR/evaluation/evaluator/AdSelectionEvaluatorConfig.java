package application.adCTR.evaluation.evaluator;

import application.adCTR.evaluation.adSelector.AdSelectorTypes;
import utils.FileUtils;

import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-26
 * Time: 上午11:10
 */
public class AdSelectionEvaluatorConfig {
    private final String configFile = "conf/AdSelectionEvaluatorConfig.properties";
    private static AdSelectionEvaluatorConfig instance = null;
    private AdSelectionEvaluatorConfig(){}

    public static AdSelectionEvaluatorConfig getInstance()
    {
        if(instance == null)
        {
            instance = new AdSelectionEvaluatorConfig();
        }
        return instance;
    }

    private int numOfBatch;
    private double base;
    private double stepSize;
    private AdSelectorTypes adSelectorType;

    public void loadConfig()
    {
        try{
            Properties properties = new Properties();
            properties.load(FileUtils.getStreamFromFile(configFile));

            this.numOfBatch = Integer.parseInt(properties.getProperty("numOfBatch"));
            this.base = Double.parseDouble(properties.getProperty("base"));
            this.stepSize = Double.parseDouble(properties.getProperty("stepSize"));
            this.adSelectorType = AdSelectorTypes.toAdSelectorTypes(properties.getProperty("adSelectorType"));

            System.out.println("ad selector config loaded");
        }catch (Exception e)
        {
            setDefaultParameters();
            System.out.println("load config for simple ad selector fail, use default values instead");
        }
    }

    private void setDefaultParameters()
    {
        this.numOfBatch = 20;
        this.base = 0.4;
        this.stepSize = 0.01;
        this.adSelectorType = AdSelectorTypes.SIMPLE_AD_SELECTOR;
    }

    public int getNumOfBatch() {
        return numOfBatch;
    }

    public double getBase() {
        return base;
    }

    public double getStepSize() {
        return stepSize;
    }

    public AdSelectorTypes getAdSelectorType()
    {
        return this.adSelectorType;
    }
}

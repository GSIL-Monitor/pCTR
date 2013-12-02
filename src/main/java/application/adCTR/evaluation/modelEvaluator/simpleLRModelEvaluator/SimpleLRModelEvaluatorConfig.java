package application.adCTR.evaluation.modelEvaluator.simpleLRModelEvaluator;

import application.adCTR.evaluation.modelEvaluator.AbstractModelEvaluatorConfig;
import utils.FileUtils;
import utils.NumericalUtils;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-12-2
 * Time: 上午9:21
 */
public class SimpleLRModelEvaluatorConfig extends AbstractModelEvaluatorConfig {
    private final String configFile = "conf/modelEvaluators/SimpleModelEvaluatorConfig.properties";

    private int numOfBatch;
    private double base;
    private double stepSize;

    public int getNumOfBatch() {
        return numOfBatch;
    }

    public double getBase() {
        return base;
    }

    public double getStepSize() {
        return stepSize;
    }

    @Override
    public void loadConfig() {
        try{
            InputStream is = FileUtils.getStreamFromFile(configFile);
            Properties properties = new Properties();
            properties.load(is);

            numOfBatch = Integer.parseInt(properties.getProperty("numOfBatch"));
            base = Double.parseDouble(properties.getProperty("base"));
            stepSize = Double.parseDouble(properties.getProperty("stepSize"));

            is.close();
        }catch (Exception e)
        {
            System.out.println("loading config [" + configFile + "] fail, use default values instead");
            e.printStackTrace();
            setDefaultParameter();
        }
    }

    @Override
    protected void setDefaultParameter() {
        this.numOfBatch = 20;
        this.base = 0.2;
        this.stepSize = 0.04;
    }
}

package application.adCTR.evaluation.modelEvaluator;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-12-2
 * Time: 上午9:08
 */
public abstract class AbstractModelEvaluator implements IModelEvaluator {
    protected AbstractModelEvaluatorConfig config;
    public abstract void init(String sampleSourceFile, String modelFile);
    public abstract long evaluation();
    public abstract void printMetric();
}

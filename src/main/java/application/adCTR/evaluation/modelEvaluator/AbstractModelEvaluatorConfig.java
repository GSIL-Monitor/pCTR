package application.adCTR.evaluation.modelEvaluator;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-12-2
 * Time: 上午9:10
 */
public abstract class AbstractModelEvaluatorConfig {
    public abstract void loadConfig();
    protected abstract void setDefaultParameter();
}

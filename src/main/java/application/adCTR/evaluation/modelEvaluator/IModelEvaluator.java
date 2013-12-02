package application.adCTR.evaluation.modelEvaluator;

import commons.framework.sample.Feature;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-12-2
 * Time: 上午9:04
 */
public interface IModelEvaluator {
    public abstract long loadModel(String modelFile);
    public abstract double calcScore(ArrayList<Feature> features);
}

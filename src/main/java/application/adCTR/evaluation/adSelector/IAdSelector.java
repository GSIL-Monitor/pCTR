package application.adCTR.evaluation.adSelector;

import application.adCTR.evaluation.creative.Creative;
import commons.framework.data.DataInstance;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-26
 * Time: 上午11:21
 */
public interface IAdSelector {
    public abstract ArrayList<Creative> selectAdFromPool(DataInstance dataInstance, Creative[] creativePool);
    public abstract long loadModel(String modelFile);
    public abstract int registerSampleHandlers();
}

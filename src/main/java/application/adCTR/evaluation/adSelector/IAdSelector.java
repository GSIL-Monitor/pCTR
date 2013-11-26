package application.adCTR.evaluation.adSelector;

import application.adCTR.evaluation.creative.Creative;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-26
 * Time: 上午11:21
 */
public interface IAdSelector {
    public abstract ArrayList<Creative> selectAdFromPool(HashSet<Creative> creativePool);
    public abstract long loadModel(String modelFile);
}

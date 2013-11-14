package commons.framework.sample;

import commons.framework.data.DataInstance;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-13
 * Time: 上午11:29
 */
public interface IFeatureHandler {
    public abstract int initFeatureHandler(int initFeatureId);
    public abstract ArrayList<Feature> ExtractFeature(DataInstance dataInstance);
}

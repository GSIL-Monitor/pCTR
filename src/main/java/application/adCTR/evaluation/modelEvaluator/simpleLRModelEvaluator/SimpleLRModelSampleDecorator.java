package application.adCTR.evaluation.modelEvaluator.simpleLRModelEvaluator;

import commons.framework.sample.Feature;
import commons.framework.sample.Sample;
import commons.framework.sample.SampleType;
import utils.NumericalUtils;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-12-2
 * Time: 上午9:52
 */
public class SimpleLRModelSampleDecorator {
    private SimpleLRModelSampleSource dataSource;

    public SimpleLRModelSampleDecorator(String sampleSourceFile)
    {
        dataSource = new SimpleLRModelSampleSource(sampleSourceFile);
    }

    public Sample getSample()
    {
        String line = dataSource.readLine();
        if(line == null)
        {
            return null;
        }
        String[] parts = line.split(" ");
        Sample sample = new Sample(SampleType.TEST, dataSource.getNumOfLines(), Double.parseDouble(parts[0]));
        ArrayList<Feature> featureArrayList = new ArrayList<Feature>();
        for(int i = 1; i < parts.length; i++)
        {
            String[] contents = parts[i].split(":");
            if(contents.length != 2)
            {
                continue;
            }
            int id = NumericalUtils.toInteger(contents[0]);
            if(id != Integer.MIN_VALUE)
            {
                Feature feature = new Feature(id, "", contents[1]);
                featureArrayList.add(feature);
            }
        }

        sample.addFeatures(featureArrayList);
        return sample;
    }

    public void close()
    {
        this.dataSource.close();
    }
}

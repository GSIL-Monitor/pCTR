package application.adCTR.control;

import application.adCTR.data.CTRDataInstance;
import application.adCTR.data.CTRDataInstanceMaker;
import application.adCTR.data.CTRDataSource;
import application.adCTR.data.CTRSourceHandler;
import application.adCTR.sample.CTRSampleMaker;
import application.adCTR.sample.DeviceTypeHandler;
import commons.framework.sample.Sample;
import commons.framework.sample.SampleType;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-13
 * Time: 下午5:02
 */
public class CTRSampleControl {
    private CTRDataSource dataSource = null;
    private CTRDataInstanceMaker dataInstanceMaker = null;
    private CTRSampleMaker sampleMaker = null;

    public CTRSampleControl()
    {
        this.dataSource = CTRDataSource.getInstance();
        this.dataInstanceMaker = CTRDataInstanceMaker.getInstance();
        this.sampleMaker = CTRSampleMaker.getInstance();
    }

    private void initSourceHandlers()
    {
        CTRSourceHandler ctrSourceHandler = new CTRSourceHandler();

        dataInstanceMaker.registerSourceHandler(ctrSourceHandler);
    }

    private void initSampleHandlers()
    {
        DeviceTypeHandler deviceTypeHandler = new DeviceTypeHandler();

        sampleMaker.registerFeatureHandler(deviceTypeHandler);
    }

    private void setupDataSource(String infile)
    {
        dataSource.setupSource(infile);
    }

    private Sample makeSample(String sourceContent, long id)
    {
        CTRDataInstance dataInstance = new CTRDataInstance(id);
        dataInstanceMaker.fillDataInstance(sourceContent,dataInstance);
        if(dataInstance.isCorrectlyFilled())
        {
            Sample sample = new Sample(SampleType.TRAIN,id);
            sampleMaker.fillSample(sample,dataInstance);
        }
        return null;
    }

    public void generateSamplesFromFile(String infile)
    {
        //preparation
        this.setupDataSource(infile);
        this.initSourceHandlers();
        this.initSampleHandlers();
        //start
        String line = null;
        long id = 1;
        while((line = dataSource.readLine()) != null)
        {
            Sample sample = makeSample(line, id);
            id++;

            //TODO find a way to output the samples
        }

    }
}

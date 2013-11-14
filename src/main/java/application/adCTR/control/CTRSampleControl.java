package application.adCTR.control;

import application.adCTR.data.CTRDataInstance;
import application.adCTR.data.CTRDataInstanceMaker;
import application.adCTR.data.CTRDataSource;
import application.adCTR.data.CTRSourceHandler;
import application.adCTR.sample.CTRSampleMaker;
import application.adCTR.sample.handlers.DeviceTypeHandler;
import application.adCTR.sample.handlers.OsTypeHandler;
import commons.framework.sample.Sample;
import commons.framework.sample.SampleType;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

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
        int initFeatureId = 0;//because all localId starts from 1, so we set init all featureId as 1 here
        /**
         * start to add all kinds of feature handlers
         */
        //device handler
        DeviceTypeHandler deviceTypeHandler = new DeviceTypeHandler();
        initFeatureId = sampleMaker.registerFeatureHandler(deviceTypeHandler, initFeatureId);
        //os handler
        OsTypeHandler osTypeHandler = new OsTypeHandler();
        initFeatureId = sampleMaker.registerFeatureHandler(osTypeHandler, initFeatureId);
        //more feature handlers here
    }

    private void setupDataSource(String infile)
    {
        dataSource.setupSource(infile);
    }

    private Sample makeSample(String sourceContent, long id, SampleType sampleType)
    {
        CTRDataInstance dataInstance = new CTRDataInstance(id);
        dataInstanceMaker.fillDataInstance(sourceContent,dataInstance);
        if(dataInstance.isCorrectlyFilled())
        {
            Sample sample = new Sample(sampleType, id, dataInstance.getTargetValue());
            sampleMaker.fillSample(sample,dataInstance);
            return sample;
        }
        return null;
    }

    private long generateSamplesFromSource(BufferedWriter bufferedWriter, SampleType sampleType)
    {
        String line;
        long id = 1;
        while((line = dataSource.readLine()) != null)
        {
            Sample sample = makeSample(line, id, sampleType);
            id++;

            try {
                bufferedWriter.write(sample.toLibLinearTypeString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return id;
    }

    private long generateSamplesFromFile(String infile, String outfile, SampleType sampleType)
    {
        //prepare for input
        this.setupDataSource(infile);
        this.initSourceHandlers();
        this.initSampleHandlers();
        //prepare for output
        BufferedWriter bufferedWriter = getWriterFromFile(outfile);
        if(bufferedWriter == null)
        {
            System.out.println("creating BufferedWriter from " + outfile + " fail");
            System.exit(-1);
        }
        //start
        long numOfSamples = this.generateSamplesFromSource(bufferedWriter,sampleType);
        //clean up
        dataSource.close();
        try{
            bufferedWriter.flush();
            bufferedWriter.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return numOfSamples;
    }

    private BufferedWriter getWriterFromFile(String fileName)
    {
        try{
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
            return bufferedWriter;
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public long run(String infile, String inputType, String outFile)
    {
        if(!SampleType.isSampleType(inputType))
        {
            System.out.println("unrecognized sample type as " + inputType);
            System.exit(-1);
        }

        SampleType sampleType = SampleType.valueOf(inputType);
        long numOfSamples = this.generateSamplesFromFile(infile,outFile,sampleType);
        return numOfSamples;
    }

    public static void main(String[] args)
    {
        int numberOfParameters = 3;
        if(args.length < numberOfParameters)
        {
            System.out.println("need more parameters");
            System.out.println("usage: run program with parameters $inputFile $sampleType $outputFile");
            System.exit(-1);
        }

        String infile = args[0];
        String sampleType = args[1];
        String outFile = args[2];

        long start = System.currentTimeMillis();
        CTRSampleControl control = new CTRSampleControl();
        long numberOfSamples = control.run(infile, sampleType, outFile);
        long end = System.currentTimeMillis();

        System.out.printf("There are total %l samples, takes %l seconds\n", numberOfSamples, (end-start)/1000);
    }
}

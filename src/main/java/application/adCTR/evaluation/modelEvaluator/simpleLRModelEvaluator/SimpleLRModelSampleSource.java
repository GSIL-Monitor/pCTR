package application.adCTR.evaluation.modelEvaluator.simpleLRModelEvaluator;

import commons.framework.data.DataSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-12-2
 * Time: 上午9:49
 */
public class SimpleLRModelSampleSource extends DataSource {
    private long numOfLines = 0;
    public SimpleLRModelSampleSource(String sampleSourceFile)
    {
        super.registerInputStream(sampleSourceFile);
        this.sourceName = sampleSourceFile;
        bufferedReader = new BufferedReader(new InputStreamReader(this.inputStream));
    }

    private BufferedReader bufferedReader = null;

    public long getNumOfLines()
    {
        return numOfLines;
    }

    @Override
    public String readLine() {
        try
        {
            if(bufferedReader != null)
            {
                numOfLines++;
                return bufferedReader.readLine();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    public void close() {
        if(this.bufferedReader != null)
        {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(inputStream != null)
        {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

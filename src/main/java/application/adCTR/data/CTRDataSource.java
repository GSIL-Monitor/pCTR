package application.adCTR.data;

import commons.framework.data.DataSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-13
 * Time: 下午3:29
 */
public class CTRDataSource extends DataSource {
    private static CTRDataSource instance = null;
    private CTRDataSource(){}

    public static CTRDataSource getInstance()
    {
        if(instance == null)
        {
            instance = new CTRDataSource();
        }
        return instance;
    }
    //business part
    private BufferedReader bufferedReader = null;
    public void setupSource(String fileName)
    {
        super.registerInputStream(fileName);
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    @Override
    public String readLine() {
        try
        {
            if(bufferedReader != null)
            {
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

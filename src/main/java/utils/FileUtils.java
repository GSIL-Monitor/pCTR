package utils;

import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-13
 * Time: 下午2:49
 */
public class FileUtils {
    /**
     * read file(from jar files or not) to input stream
     * there are two ways of loading config files,you can choose any one you like.
     * 1.ClassLoader.getResourceAsStream("some/package/your.config")
     * 2.Class.getResourceAsStream("/some/package/your.config")
     */
    public static InputStream getStreamFromFile(String fileName)
    {
        InputStream resourceAsStream = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream(fileName);
        if (resourceAsStream == null) {
            resourceAsStream = Thread.currentThread().getClass().getResourceAsStream(
                    "/".concat(fileName));
            if (resourceAsStream == null) {
                throw new RuntimeException("read file " + fileName + " error");
            }
        }
        return resourceAsStream;
    }
}

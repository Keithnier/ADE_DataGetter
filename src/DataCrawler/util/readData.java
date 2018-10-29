package DataCrawler.util;


import java.io.IOException;

public interface readData {
    String myreadline(String filepath);
    void close()throws IOException;
}

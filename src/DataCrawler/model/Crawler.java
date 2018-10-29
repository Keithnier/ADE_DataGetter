package DataCrawler.model;

import java.util.Map;

/**
 * Crawler接口，便于统一管理
 */
public interface Crawler {
    Map<String, Object> getDataByLocation(int west_long, int south_lat, int east_long, int north_lat, Map<String, Object> others) throws Exception;
    String getCrawlerName();

    Map<String, Object> getDataByKeyword(String keyword, Map<String, Object> others) throws Exception;
}

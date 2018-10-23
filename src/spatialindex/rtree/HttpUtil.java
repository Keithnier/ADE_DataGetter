package spatialindex.rtree;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HttpUtil {

    private static String doGet(String url, Map<String, String> params) throws IOException {
        String urlWithParam = formUrlString(url, params);
        HttpGet httpGet = new HttpGet(urlWithParam);
        HttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = httpClient.execute(httpGet);
        if(response.getStatusLine().getStatusCode() != 200) throw new IOException();

        return EntityUtils.toString(response.getEntity());
    }



    private static String formUrlString(String url, Map<String, String> params) throws UnsupportedEncodingException {
        StringBuffer buffer = new StringBuffer();
        for(String key: params.keySet()){
            buffer.append(key)
                    .append("=")
                    .append(URLEncoder.encode(params.get(key),"UTF-8"))
                    .append("&");
        }

        String urlWithParams = url;
        if(buffer.length() > 1) {
            urlWithParams = urlWithParams +"?"+buffer.substring(0,buffer.length()-1);
        }
        return urlWithParams;
    }

    static Location getLocationByName(String cityName) throws Exception {
        Map<String, String> params = new HashMap<>();

        params.put("address", cityName);
        params.put("key", "AIzaSyApWz4M414iV8QLZYo9EKR6g_Bez73tHQ8");
        params.put("language","en");

        String json = HttpUtil.doGet("https://maps.google.cn/maps/api/geocode/json", params);
//        System.out.println(json);
        JSONObject jsonObject = new JSONObject(json);
        String status = jsonObject.getString("status");
        if (!status.equals("OK")) throw new Exception("Http error");

        JSONArray results = jsonObject.getJSONArray("results");

        if (results.length() < 1) throw new Exception("no result match");

        String name = results.getJSONObject(0).getString("formatted_address");
        JSONObject geometry = results.getJSONObject(0).getJSONObject("geometry");

        double lat = geometry.getJSONObject("location").getDouble("lat");
        double lng = geometry.getJSONObject("location").getDouble("lng");

        return new Location(lng,lat);
    }

    public static void main(String[] args) throws Exception {
        HttpUtil.getLocationByName("多个和");

//        System.out.println("[City]: " + cityModel.getName());
//        System.out.println("[Lat, Lng]: " + cityModel.getLat() + "," + cityModel.getLng());
//        System.out.println("[Region]: " + cityModel.getBoundsSTR());
    }
}

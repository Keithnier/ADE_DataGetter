package Flickr;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.github.scribejava.apis.FlickrApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth10aService;
import model.OAuth;
import model.RestParam;
import util.ConfigurationFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class FlickrOAuth implements OAuth{

    private static OAuth1AccessToken flickrAccessToken = null;
    private static OAuth10aService flickrService = null;
    private static RestParam REST_PARAM = null;

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        String URL = "https://api.flickr.com/services/rest";
        initFlickrAccessToken(ConfigurationFactory.getRestParam("paramfile/flickrparam.txt").get(0));
        Map<String, String> params = new HashMap<>();
        params.put("method", "flickr.photos.search");
        params.put("min_upload_date", "1510479048&");
        params.put("bbox", "-180,-90,180,90");
        params.put("extras", "geo,original_format");
        params.put("format", "json");
        params.put("nojsoncallback", "1");

        Response response = getFlickrResponse(URL, params);
        System.out.println(response.getBody());
    }

    /**
     *
     * @param restParam  用户信息，通过ConfigurationFactory可以获取
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IOException
     */
    public static void initFlickrAccessToken(RestParam restParam) throws InterruptedException, ExecutionException, IOException {
        flickrService = new ServiceBuilder(restParam.getConsumerKey())
                .apiSecret(restParam.getConsumerSecret())
                .callback("oob")
                .build(FlickrApi.instance());
        // 获取 Request Token

        REST_PARAM = restParam;
        final OAuth1RequestToken requestToken = flickrService.getRequestToken();
        String verify_url = flickrService.getAuthorizationUrl(requestToken);
        System.out.println(verify_url);
        String oauthVerifier = getFlickrVerify(verify_url, restParam.getUser(), restParam.getPassword());
        final OAuth1AccessToken accessToken = flickrService.getAccessToken(requestToken, oauthVerifier);
        flickrAccessToken = accessToken;
    }

    /**
     *
     * @param token_url 获取verify key 的httpURL
     * @param user      注册Flickr的用户名
     * @param password  密码
     * @return
     * @throws IOException
     */
    private static String getFlickrVerify(String token_url, String user, String password) throws IOException {

        final WebClient webClient = new WebClient();
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        final HtmlPage htmlPage = webClient.getPage(token_url);//获取授权界面
//        System.out.println(htmlPage.asXml());


        final HtmlForm htmlForm = htmlPage.getForms().get(0);//找到页面中提交用户信息的form
        final DomElement submit = htmlPage.getElementById("login-signin");//找到提交按钮
        final HtmlTextInput input_name = htmlForm.getInputByName("username");//找到用户账号输入框
        input_name.setText(user);//填写账号
        final HtmlPage htmlPage1 = submit.click();//提交信息，得到verify码所在界面



        final HtmlForm htmlForm1 = htmlPage1.getForms().get(0);
        final DomElement submit1 = htmlPage1.getElementById("login-signin");
        final HtmlPasswordInput input_password = htmlForm1.getInputByName("password");//找到密码输入框
        input_password.setText(password);//填写密码



        final HtmlPage htmlPage2 = submit1.click();
        final HtmlForm htmlForm2 = htmlPage2.getForms().get(0);
        /*System.out.println(htmlForm2.asXml());*/
        final DomElement submit2 = htmlForm2.getLastElementChild().getFirstElementChild();

        final HtmlPage verifyPage = submit2.click();
        /*String result = verifyPage.getElementById("Main").getLastElementChild().asText();*/
        System.out.println(verifyPage.asXml());



        String result="772-979-748";
        return result;
    }

    /**
     *
     * @param url       要访问的url地址
     * @param params    需要的参数
     * @return
     * @throws UnsupportedEncodingException
     */
    private static OAuthRequest getSignedFlickrRequest(String url, Map<String, String> params) throws UnsupportedEncodingException {

        //组合URL
        String urlWithParam = formUrlString(url, params);
        final OAuthRequest request = new OAuthRequest(Verb.GET, urlWithParam);

        flickrService.signRequest(flickrAccessToken, request);

        return request;
    }

    /**
     * 将参数进行编码，并和URL合并
     * @param url 基础URL
     * @param params 需要进行编码的参数Map key值为参数名，value值为参数值
     * @return
     * @throws UnsupportedEncodingException
     */
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

    /**
     * 获取请求的响应
     * @param url        Rest Api 对应的基本URL地址
     * @param params     访问该Api带的参数，key值为参数名，value值为参数值
     * @return Response  该请求的响应
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static Response getFlickrResponse(String url, Map<String, String> params) throws IOException, InterruptedException, ExecutionException {
        OAuthRequest request = getSignedFlickrRequest(url, params);

        Response response = flickrService.execute(request);

        return response;
    }

    /**
     * 初始化Token
     * @return
     */
    public static boolean isTokenInit() {
        if(flickrAccessToken == null) return false;
        return true;
    }

    @Override
    public void RefreshAccessToken() throws InterruptedException, ExecutionException, IOException {
        if(REST_PARAM != null) {
            initFlickrAccessToken(REST_PARAM);
        }
    }
}

package Tumblr;

import model.OAuth;
import model.RestParam;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import util.ConfigurationFactory;

import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Tumblr OAuth验证模块
 */
public class TumblrOAuth implements OAuth{
    private static String api_key = null;
    private static RestParam REST_PARAM = null;

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {

        //twitter rest param file path
        final String paramFilePath = "paramfile/tumblrparam.txt";

        initTumblrAccessToken(ConfigurationFactory.getRestParam(paramFilePath).get(0));

        Map<String, String> param = new HashMap<>();
        param.put("tag", "lol");
        HttpResponse response = getTumblrResponse("https://api.tumblr.com/v2/tagged", param);
        HttpEntity entity = response.getEntity();
        InputStream stream = entity.getContent();
        InputStreamReader streamReader = new InputStreamReader(stream);
        BufferedReader bufferedReader = new BufferedReader(streamReader);
        System.out.println(bufferedReader.readLine());

    }

    public static void initTumblrAccessToken(RestParam restParam) throws InterruptedException, ExecutionException, IOException {
//        tumblrService = new ServiceBuilder(restParam.getConsumerKey())
//                .apiSecret(restParam.getConsumerSecret())
//                .callback("http://www.tumblr.com/connect/login_success.html")//http://www.tumblr.com/connect/login_success.html
//                .build(TumblrApi.instance());
//        // 获取 Request Token
//
//        final OAuth1RequestToken requestToken = tumblrService.getRequestToken();
//        String verify_url = tumblrService.getAuthorizationUrl(requestToken);
//        System.out.print(verify_url);
//        String oauthVerifier = getTumblrVerify(verify_url, restParam.getUser(), restParam.getPassword());
//        final OAuth1AccessToken accessToken = tumblrService.getAccessToken(requestToken, oauthVerifier);
//        tumblrAccessToken = accessToken;

        REST_PARAM = restParam;
        api_key = restParam.getConsumerKey();
    }

    /**
     *
     * @param token_url 获取verify key 的httpURL
     * @param user      注册Flickr的用户名
     * @param password  密码
     * @return
     * @throws IOException
     */
    private static String getTumblrVerify(String token_url, String user, String password) throws IOException {

//        final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_45);
//        webClient.getOptions().setCssEnabled(false);
//        webClient.getOptions().setJavaScriptEnabled(true);
//        final HtmlPage htmlPage = webClient.getPage(token_url);//获取授权界面
//        webClient.getOptions().setThrowExceptionOnScriptError(false);
//        // 5 设置超时
//        webClient.getOptions().setTimeout(50000);
//        //6 设置忽略证书
//        //webClient.getOptions().setUseInsecureSSL(true);
//        //7 设置Ajax
//        //webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//        //8设置cookie
//        webClient.getCookieManager().setCookiesEnabled(true);
//        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//
//        final HtmlForm htmlForm = htmlPage.getForms().get(0);
//        final DomElement submit_list = htmlPage.getElementById("signup_forms_submit");//找到提交按钮
//        final DomElement submit = submit_list.getLastElementChild();
//        System.out.println(submit.toString());
//        final HtmlEmailInput input_email = htmlForm.getInputByName("user[email]");//找到密码输入框
//        final HtmlPasswordInput input_password = htmlForm.getInputByName("user[password]");//找到密码输入框
//        input_password.setText(password);//填写密码
//        input_email.setText(user);
//
//        final HtmlPage htmlPage1 = submit.click();
//        webClient.waitForBackgroundJavaScript(10000);
//
//        System.out.println(htmlPage1.asXml());
////        System.out.println("form size:" + htmlPage2.getForms().size());
//        final HtmlForm htmlForm1 = htmlPage1.getForms().get(1);
////        System.out.println(htmlPage2.asXml());
//        final DomElement submit1 = htmlForm1.getLastElementChild().getFirstElementChild();
//
//        final HtmlPage verifyPage = submit1.click();
//        String result = verifyPage.getElementById("Main").getLastElementChild().asText();
//
//
//        return result;
        return "";
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
    public static HttpResponse getTumblrResponse(String url, Map<String, String> params) throws IOException, InterruptedException, ExecutionException {
        //组合URL
        params.put("api_key", api_key);
        String urlWithParam = formUrlString(url, params);
        System.out.println(urlWithParam);
        HttpGet httpGet = new HttpGet(urlWithParam);
        HttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = httpClient.execute(httpGet);


        return response;
    }

    /**
     * 初始化Token
     * @return
     */
    public static boolean isTokenInit() {
        if(api_key == null) return false;
        return true;
    }

    @Override
    public void RefreshAccessToken() throws InterruptedException, ExecutionException, IOException {
        if(REST_PARAM != null) {
            initTumblrAccessToken(REST_PARAM);
        }
    }
 }

package DataCrawler.Twitter;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.httpclient.jdk.JDKHttpClientConfig;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth10aService;
import DataCrawler.model.OAuth;
import DataCrawler.model.RestParam;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class TwitterOAuth implements OAuth{
//    private static final String PROTECTED_RESOURCE_URL = "https://stream.twitter.com/1.1/statuses/filter.json?locations=-180%2c-90%2c180%2c90";

    private static OAuth1AccessToken twitterAccessToken = null;
    private static OAuth10aService twitterService = null;
    private static RestParam REST_PARAM = null;
    public static void main(String... args) throws IOException, InterruptedException, ExecutionException {
//        final OAuth10aService service = new ServiceBuilder("zusqzDFXKLRUs3okAlaqumYG9")
//                .apiSecret("qAlMilk1edTgkEjRT2kH9cbXeAnFEfQL6CYS2r04Szg5NKqlYN")
//                .callback("oob")
//                .build(TwitterApi.instance());
//        final Scanner in = new Scanner(System.in);
//
//        System.out.println("=== Twitter's Twitter.TwitterOAuth Workflow ===");
//        System.out.println();
//
//        // Obtain the Request Token
//        System.out.println("Fetching the Request Token...");
//        final OAuth1RequestToken requestToken = service.getRequestToken();
//        System.out.println("Got the Request Token!");
//        System.out.println();
//
//        //System.out.println("Now go and authorize ScribeJava here:");
//        System.out.println(service.getAuthorizationUrl(requestToken));
//        //System.out.println("And paste the verifier here");
//        String verify_url = service.getAuthorizationUrl(requestToken);
////        System.out.print(">>");
////        final String oauthVerifier = in.nextLine();
////        System.out.println();
//
//
//
//        // Trade the Request Token and Verfier for the Access Token
//        // System.out.println("Trading the Request Token for an Access Token...");
//        String oauthVerifier = getVerify(verify_url);
//        final OAuth1AccessToken accessToken = service.getAccessToken(requestToken, oauthVerifier);
//        System.out.println("Got the Access Token!");
//        System.out.println("(if your curious it looks like this: " + accessToken
//                + ", 'rawResponse'='" + accessToken.getRawResponse() + "')");
//        System.out.println();
//
//        // Now let's go and ask for a protected resource!
//        System.out.println("Now we're going to access a protected resource...");
//        final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
//        service.signRequest(accessToken, request);
//        final Response response = service.execute(request);
//        System.out.println("Got it! Lets see what we found...");
//        System.out.println();
//
//        InputStreamReader reader = new InputStreamReader(response.getStream());
//        BufferedReader reader1 = new BufferedReader(reader);
//        System.out.println(reader1.readLine());
//
//        System.out.println();
//        System.out.println("That's it man! Go and build something awesome with ScribeJava! :)");

//        String url = "https://stream.twitter.com/1.1/statuses/filter.json";
//        Map<String, String> map = new HashMap<>();
//        map.put("locations", "-180,-90,180,90");
//
//        final boolean b = initTwitterAccessToken((ConfigurationFactory.getTwitterRestParam("paramfile/twitterrestparam.txt").get(0)));
    }

    /**
     * 获取账户的验证码
     * @param token_url 验证地址
     * @param user      账户
     * @param password  密码
     * @return String   验证码
     * @throws IOException
     */
    private static String getTwitterVerify(String token_url, String user, String password, String phoneNum) throws IOException {

//        System.out.println(token_url);
        final WebClient webClient = new WebClient();
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        final HtmlPage htmlPage = webClient.getPage(token_url);//获取授权界面
//        System.out.println(htmlPage.asXml());
        final HtmlForm htmlForm = htmlPage.getForms().get(0);//找到页面中提交用户信息的form
        final DomElement submit = htmlPage.getElementById("allow");//找到提交按钮
        final HtmlTextInput input_name = htmlForm.getInputByName("session[username_or_email]");//找到用户账号输入框
        final HtmlPasswordInput input_password = htmlForm.getInputByName("session[password]");//找到密码输入框
        input_name.setText(user);//填写账号
        input_password.setText(password);//填写密码
        HtmlPage verifyPage = submit.click();//提交信息，得到verify码所在界面

        System.out.println(verifyPage.asXml());
        DomElement verify = verifyPage.getElementById("oauth_pin");

        //判断是否需要电话验证
        if(verify == null) {
            final HtmlTextInput phoneInput = verifyPage.getForms().get(0).getInputByName("challenge_response");
            phoneInput.setText(phoneNum);
            final DomElement submit_phoneNum = verifyPage.getElementById("email_challenge_submit");
            verifyPage = submit_phoneNum.click();
        }

        String result = verifyPage.getElementById("oauth_pin")
                           .getFirstElementChild()
                           .getLastElementChild()
                           .getFirstElementChild().asText();//找到verify码，此处代码和网页内容有关
        return result;
    }

    /**
     * 初始化并保存twitterToken至 OAuth1Access
     * @param restParam Twitter账户信息模版， 包含使用Twitter Rest Api 需要的信息
     * @return boolean  初始化用户token是否成功
     */
    public static void initTwitterAccessToken(RestParam restParam) throws InterruptedException, ExecutionException, IOException {
        System.out.println("Access Token init start..");

        JDKHttpClientConfig config = new JDKHttpClientConfig();
        config.setConnectTimeout(60000);
        config.setReadTimeout(60000);

        twitterService = new ServiceBuilder(restParam.getConsumerKey())
                .apiSecret(restParam.getConsumerSecret())
                .callback("oob")
                .httpClientConfig(config)
                .build(TwitterApi.instance());

        System.out.println("Twitter Service Builder init.");
        // 获取 Request Token

        REST_PARAM = restParam;
        final OAuth1RequestToken requestToken = twitterService.getRequestToken();

        System.out.println("Request token init.");
        String verify_url = twitterService.getAuthorizationUrl(requestToken);
        System.out.println("Verify Url get.");
        String oauthVerifier = getTwitterVerify(verify_url, restParam.getUser(), restParam.getPassword(), restParam.getPhoneNum());
        System.out.println("Verifier get.");
        final OAuth1AccessToken accessToken = twitterService.getAccessToken(requestToken, oauthVerifier);
        System.out.println("Access Token get.");
        twitterAccessToken = accessToken;
        System.out.println(accessToken);
        System.out.println("Access Token init success.");
    }

    /**
     * 将http访问请求绑定验证信息
     * @param url       Rest Api 对应的基本url地址
     * @param params    访问该Api带的参数，key值为参数名，value值为参数值
     * @return OAuthRequest 包含OAuth验证的Resquest
     * @throws UnsupportedEncodingException
     */
    private static OAuthRequest getSignedTwitterRequest(String url, Map<String, String> params) throws UnsupportedEncodingException {

        //组合URL
        String urlWithParam = formUrlString(url, params);
        final OAuthRequest request = new OAuthRequest(Verb.GET, urlWithParam);
        System.out.println("Request Signed Start." + " || URL: " + urlWithParam);

        twitterService.signRequest(twitterAccessToken, request);

        System.out.println("Request Signed Success." + " || URL: " + urlWithParam);
        return request;
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
    public static Response getTwitterResponse(String url, Map<String, String> params) throws IOException, InterruptedException, ExecutionException {
        OAuthRequest request = getSignedTwitterRequest(url, params);

        System.out.println("Start to get Response....");
        Response response = null;
        try{
            response = twitterService.execute(request);
        } catch (SocketTimeoutException e) {
            System.out.println("Get Response Time out.");
            response = new Response(408, "Request Timeout", new HashMap<String, String>(), "Error");
        }
        System.out.println("Response get success");

        return response;
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
        String urlWithParams = url+"?"+buffer.substring(0,buffer.length()-1);
        return urlWithParams;
    }

    public static boolean isTokenInit(){
        if(twitterAccessToken == null) return false;
        return true;
    }

    @Override
    public void RefreshAccessToken() throws InterruptedException, ExecutionException, IOException {
        if(REST_PARAM != null) {
            initTwitterAccessToken(REST_PARAM);
        }
    }
}

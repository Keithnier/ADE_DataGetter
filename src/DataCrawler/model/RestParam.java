package DataCrawler.model;

public class RestParam {
    private String consumerKey;
    private String consumerSecret;
    private String user;
    private String password;
    private String phoneNum;

    public RestParam(String consumerKey, String consumerSecret, String user, String password, String phoneNum){
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.user = user;
        this.password = password;
        this.phoneNum = phoneNum;
    }


    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String password) {
        this.phoneNum = phoneNum;
    }
}

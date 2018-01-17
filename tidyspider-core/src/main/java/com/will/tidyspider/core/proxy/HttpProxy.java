package com.will.tidyspider.core.proxy;

import org.apache.http.HttpHost;

/**
 * Created by shangru_yu.
 */
public class HttpProxy {
    public HttpProxy(String host, int port) {
        this.proxyHost = new HttpHost(host, port);
        this.userName = null;
        this.passWord = null;
    }

    public HttpProxy(String host, int port, String uname, String pass) {
        this.proxyHost = new HttpHost(host, port);
        this.userName = uname;
        this.passWord = pass;
    }

    public boolean isAuthNeed() {
        return this.userName != null && this.passWord != null;
    }

    public HttpHost getProxyHost() {
        return proxyHost;
    }

    private HttpHost proxyHost;

    public String getUserName() {
        return userName;
    }

    private String userName;

    public String getPassWord() {
        return passWord;
    }

    private String passWord;

    public int getHitTimes() {
        return hitTimes;
    }

    public void setHitTimes(int hitTimes) {
        this.hitTimes = hitTimes;
    }

    private int hitTimes = 0;
}

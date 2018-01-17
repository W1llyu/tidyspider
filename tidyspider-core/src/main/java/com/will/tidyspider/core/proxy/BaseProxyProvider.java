package com.will.tidyspider.core.proxy;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Created by will on 2017/6/15.
 */
public abstract class BaseProxyProvider implements HttpProxyProvider, Runnable{
    protected Logger logger = LoggerFactory.getLogger(getClass());
    protected Map<String, HttpProxy> siteProxyMap = new HashMap<String, HttpProxy>();
    protected List<HttpProxy> proxies;
    protected Thread updateProxyThread;
    protected CountDownLatch latch = new CountDownLatch(10);
    protected int poolSize = 1000;
    protected int index = 0;
    private String sourceUrl;
    private String username;
    private String password;

    public BaseProxyProvider(String sourceUrl, int psize){
        this(sourceUrl, null, null, psize);
    }

    public BaseProxyProvider(String sourceUrl, String username, String password, int psize) {
        this.sourceUrl = sourceUrl;
        this.username = username;
        this.password = password;
        proxies = Collections.synchronizedList(new ArrayList<HttpProxy>(psize));
        this.updateProxyThread = new Thread(this);
        this.updateProxyThread.setDaemon(false);
        this.updateProxyThread.start();
        this.poolSize = psize;

        try {
            logger.debug("waiting for proxy pool ready...");
            latch.await();
            logger.debug("waiting for proxy pool ready...Done");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public HttpProxy getOneProxy() {
        try {
            if (index >= proxies.size()) {
                index = 0;
            }
            HttpProxy pro = proxies.get(index++);
            pro.setHitTimes(pro.getHitTimes() + 1);
            if (pro.getHitTimes() >= 20) {
                this.proxies.remove(index - 1);
            }
            return pro;
        } catch (Exception e) {
            return proxies.get(0);
        }
    }

    @Override
    public void removeOneProxy(HttpProxy proxy) {
        this.proxies.remove(proxy);
    }

    protected boolean isProxyAvailable(String ip, int port) {
        Socket socket = new Socket();
        SocketAddress remote = new InetSocketAddress(ip, port);
        try {
            socket.connect(remote, 1000);
            return true;
        } catch (IOException e) {
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
        return false;
    }

    @Override
    public void run(){
        while (true) {
            if (this.proxies.size() < poolSize) {
                HttpGet hget = new HttpGet(sourceUrl);
                CloseableHttpClient client = HttpClients.custom().build();
                try {
                    CloseableHttpResponse response = client.execute(hget);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        String[] iplists = EntityUtils.toString(response.getEntity()).split("\r\n");
                        for (String ipport : iplists) {
                            try {
                                int location = ipport.indexOf(":");
                                String ip = ipport.substring(0, location);
                                int port = Integer.parseInt(ipport.substring(location + 1));
                                if (isProxyAvailable(ip, port)) {
                                    HttpProxy proxy = username == null ? new HttpProxy(ip, port) : new HttpProxy(ip, port, "wupei", "uayhed4l");
                                    this.proxies.add(proxy);
                                    if (latch.getCount() != 0) {
                                        latch.countDown();
                                        logger.debug("waiting for proxy pool ready..." + latch.getCount());
                                    }
                                }
                            } catch (Exception e){
                                break;
                            }
                        }
                    }
                    response.close();
                } catch (IOException e) {
                } finally {
                    try {
                        client.close();
                    } catch (IOException e) {
                    }
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }
}

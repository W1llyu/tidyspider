package com.will.tidyspider.core.proxy;

/**
 * Created by shangru_yu.
 */
public interface HttpProxyProvider {

    /**
     * ����һ������Ĵ���IP
     * @return
     */
    HttpProxy getOneProxy();

    void removeOneProxy(HttpProxy proxy);
}

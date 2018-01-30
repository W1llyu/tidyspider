package com.will.tidyspider.core;

import com.will.tidyspider.core.collector.Collector;
import com.will.tidyspider.core.config.ServerConfig;
import com.will.tidyspider.core.config.TaskConfig;
import com.will.tidyspider.core.entity.Page;
import com.will.tidyspider.core.entity.Request;
import com.will.tidyspider.core.entity.Results;
import com.will.tidyspider.core.entity.SpiderTask;
import com.will.tidyspider.core.processor.PageProcessor;
import com.will.tidyspider.core.resources.RunTimeConfig;
import com.will.tidyspider.core.resources.ThreadPoolProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by will on 03/01/2018.
 */
public class Spider {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ThreadPoolProxy threadPoolProxy = ThreadPoolProxy.getInstance();

    private ServerConfig serverConfig;

    private TaskConfig taskConfig;

    public Spider() {
        RunTimeConfig runTimeConfig = RunTimeConfig.getInstance();
        this.serverConfig = runTimeConfig.getServerConfig();
        this.taskConfig = runTimeConfig.getTaskConfig();
    }

    public void run() {
        while(true) {
            final Request request = serverConfig.getScheduler().poll();
            if(null == request) {
                try {
                    Thread.sleep(serverConfig.getSleepTime() * 1000);
                } catch (InterruptedException e) {
                    break;
                }
                continue;
            }
            threadPoolProxy.getThreadPoolExecutor().execute(new Runnable() {
                public void run() {
                    logger.info("Callable task [{}] run", request.getUrl());
                    try {
                        processRequest(request);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void once() {
        while(true) {
            final Request request = serverConfig.getScheduler().poll();
            if(null == request) {
                try {
                    Thread.sleep(serverConfig.getSleepTime() * 1000);
                } catch (InterruptedException e) {
                    break;
                }
                continue;
            } else {
                processRequest(request);
                break;
            }
        }
    }

    /**
     * 处理请求
     * @param request
     */
    private void processRequest(Request request) {
        SpiderTask task = taskConfig.fetchSpiderTask(request.getUrl());
        if(task == null) {
            logger.warn("url {} not in task map!", request.getUrl());
            return;
        }
        updateRequest(request, task);
        PageProcessor processor = task.getProcessor();
        if(null == processor) {
            logger.error("Unable to find Class {}", task.getProcessorName());
            return;
        }

        Page page = serverConfig.getDownloader().download(request);
        boolean downloadOk = checkStatusCode(page.getStatusCode(), task);
        if(downloadOk) {
            processPage(page, task.getProcessor());
            processResults(page, task.getName());
        } else {
            processRetry(request, task);
        }
    }

    private void updateRequest(Request request, SpiderTask task) {
        request.setResponseCharset(task.getCharset());
        request.setNeedProxy(task.getNeedProxy());
        request.setTimeOut(task.getTimeOut());
    }

    /**
     * @param statusCode
     * @param task
     * @return
     */
    private boolean checkStatusCode(Integer statusCode, SpiderTask task) {
        for(int code: task.getAcceptStatusCode()) {
            if(code == statusCode)
                return true;
        }
        return false;
    }

    /**
     * 调用processor处理请求response
     * @param page
     * @param pageProcessor
     */
    private void processPage(Page page, PageProcessor pageProcessor) {
        try {
            pageProcessor.process(page);
            addTargetRequests(page.getTargetRequests());
        } catch (Exception e) {
            logger.error("Process request " + page.getRequest().getUrl() + " error " + e + "_" + e.getStackTrace()[0].toString());
            e.printStackTrace();
        }
    }

    /**
     * collector持久化抓取结果
     * @param page
     * @param typeIdentifier
     */
    private void processResults(Page page, String typeIdentifier) {
        Results[] results = page.getResultItems();
        if(results == null || results.length == 0)
            return;
        for (Results result: results) {
            if (result.isSkip() || result.getAll().isEmpty()) continue;
            result.setTypeIdentier(typeIdentifier);
            for(Collector collector: serverConfig.getCollectors())
                collector.process(result);
        }
    }

    /**
     * 请求重试
     * @param request
     * @param task
     */
    private void processRetry(Request request, SpiderTask task) {
        Integer reqRetryTimes = request.getRetryTimes();
        if(task.getRetryTimes() > reqRetryTimes) {
            request.setRetryTimes(reqRetryTimes + 1);
            addRequest(request);
        }
    }

    private void addTargetRequests(List<Request> targetRequests){
        if(0 != targetRequests.size()){
            for(Request request: targetRequests){
                addRequest(request);
            }
        }
    }

    private void addRequest(Request request) {
        serverConfig.getScheduler().push(request);
    }
}

package com.will.tidyspider.core.config.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.will.tidyspider.core.config.TaskConfig;
import com.will.tidyspider.core.entity.SpiderTask;
import com.will.tidyspider.core.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by will on 02/01/2018.
 */
public class DefaultTaskConfig implements TaskConfig{
    private static DefaultTaskConfig instance;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<String, SpiderTaskFetcher> taskFetcherMap;

    public static DefaultTaskConfig getInstance() {
        if(instance == null) {
            synchronized (DefaultServerConfig.class) {
                if(instance == null) instance = new DefaultTaskConfig();
            }
        }
        return instance;
    }

    @Override
    public SpiderTask fetchSpiderTask(String url) {
        for (Map.Entry<String, SpiderTaskFetcher> entry: taskFetcherMap.entrySet()) {
            SpiderTask spiderTask = entry.getValue().fetchSpiderTask(url);
            if(spiderTask != null) return spiderTask;
        }
        return null;
    }

    private DefaultTaskConfig() {
        taskFetcherMap = new HashMap<>();
        loadConfig();
    }

    private void loadConfig() {
        URL url = getClass().getClassLoader().getResource("./tasks");
        File directory = new File(url.getFile());
        File[] files = directory.listFiles();
        assert files != null;
        for (File file: files) {
            if (!file.isFile() ||
                    !file.getName().substring(file.getName().length() - 5, file.getName().length()).equals(".json"))
                continue;
            try {
                SpiderTaskFetcher spiderTaskFetcher = new SpiderTaskFetcher(FileUtil.getParamFromJSONConfig(file));
                if(spiderTaskFetcher.getName() == null || taskFetcherMap.containsKey(spiderTaskFetcher.getName())) {
                    logger.error("task config error");
                    System.exit(1);
                }
                taskFetcherMap.put(spiderTaskFetcher.getName(), spiderTaskFetcher);
            } catch (Exception e) {

            }
        }
    }

    class SpiderTaskFetcher {
        private String name;
        private String globalUserAgent = "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36";
        private String globalCharset = "UTF-8";
        private Integer globalRetryTimes = 5;
        private Integer globalTimeOut = 30000;
        private Boolean globalProxy = false;
        private Integer[] globalAcceptStatusCode = new Integer[]{200};
        private JSONArray taskRules = new JSONArray();

        String getName() {
            return name;
        }

        private SpiderTaskFetcher(JSONObject task) {
            this.name = task.getString("name");
            this.globalUserAgent = task.getString("userAgent");
            this.globalCharset = task.getString("charset");
            this.globalRetryTimes = task.getInteger("retryTimes");
            this.globalTimeOut = task.getInteger("timeOut");
            this.globalProxy = task.getBoolean("proxy");
            this.globalAcceptStatusCode = task.getJSONArray("acceptStatusCode")
                    .toArray(new Integer[task.getJSONArray("acceptStatusCode").size()]);

            if(task.containsKey("rules"))
                this.taskRules = task.getJSONArray("rules");
            else this.taskRules = new JSONArray();
        }

        public SpiderTask fetchSpiderTask(String url) {
            for(Object object: taskRules) {
                JSONObject rule = (JSONObject) object;
                String regex = rule.getString("urlRegex");
                Pattern p = Pattern.compile(regex);
                Matcher matcher = p.matcher(url);
                if (matcher.find()) {
                    SpiderTask spiderTask = new SpiderTask(name);
                    initSpiderTask(spiderTask, rule);
                    return spiderTask;
                }
            }
            return null;
        }

        public void initSpiderTask(SpiderTask spiderTask, JSONObject rule) {
            if(rule.containsKey("userAgent")){
                spiderTask.setUserAgent(rule.getString("userAgent"));
            }else{
                spiderTask.setUserAgent(this.globalUserAgent);
            }
            if(rule.containsKey("charset")){
                spiderTask.setCharset(rule.getString("charset"));
            }else{
                spiderTask.setCharset(this.globalCharset);
            }
            if(rule.containsKey("retryTimes")){
                spiderTask.setRetryTimes(rule.getInteger("retryTimes"));
            }else{
                spiderTask.setRetryTimes(this.globalRetryTimes);
            }
            if(rule.containsKey("timeOut")){
                spiderTask.setTimeOut(rule.getInteger("timeOut"));
            }else{
                spiderTask.setTimeOut(this.globalTimeOut);
            }
            if(rule.containsKey("proxy")){
                spiderTask.setNeedProxy(rule.getBoolean("proxy"));
            }else{
                spiderTask.setNeedProxy(this.globalProxy);
            }
            if(rule.containsKey("acceptStatusCode")){
                spiderTask.setAcceptStatusCode(rule.getJSONArray("acceptStatusCode").toArray(
                        new Integer[rule.getJSONArray("acceptStatusCode").size()]));
            }else{
                spiderTask.setAcceptStatusCode(this.globalAcceptStatusCode);
            }
            if(rule.containsKey("processor")){
                spiderTask.setProcessor(rule.getString("processor"));
            }
        }
    }
}

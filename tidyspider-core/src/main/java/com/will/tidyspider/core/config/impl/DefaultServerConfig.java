package com.will.tidyspider.core.config.impl;

import com.alibaba.fastjson.JSONObject;
import com.will.tidyspider.core.collector.Collector;
import com.will.tidyspider.core.collector.ConsoleCollector;
import com.will.tidyspider.core.config.ServerConfig;
import com.will.tidyspider.core.downloader.Downloader;
import com.will.tidyspider.core.downloader.OKHTTPDownloader;
import com.will.tidyspider.core.scheduler.QueueScheduler;
import com.will.tidyspider.core.scheduler.Scheduler;
import com.will.tidyspider.core.utils.FileUtil;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by will on 02/01/2018.
 * 默认的系统配置，读取
 */
public class DefaultServerConfig implements ServerConfig{
    private static DefaultServerConfig instance = null;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Scheduler scheduler;

    private Downloader downloader;

    private List<Collector> collectors;

    private JSONObject appConfig;

    private JSONObject componentsConfig;

    private JSONObject systemConfig;

    public static DefaultServerConfig getInstance() {
        if(instance == null) {
            synchronized (DefaultServerConfig.class) {
                if(instance == null) instance = new DefaultServerConfig();
            }
        }
        return instance;
    }

    @Override
    public int getConcurrency() {
        return appConfig.containsKey("concurrency") ? appConfig.getInteger("concurrency") : 10;
    }

    @Override
    public int getSleepTime() {
        return appConfig.containsKey("sleep_time") ? appConfig.getInteger("sleep_time") : 5;
    }

    @Override
    public Scheduler getScheduler() {
        if(scheduler == null)
            loadScheduler();
        return scheduler;
    }

    @Override
    public Downloader getDownloader() {
        if(downloader == null)
            loadDownloader();
        return downloader;
    }

    @Override
    public List<Collector> getCollectors() {
        if(collectors == null || collectors.isEmpty())
            loadCollectors();
        return collectors;
    }

    @Override
    public JSONObject getAppConfig() {
        return appConfig;
    }

    @Override
    public JSONObject getComponentsConfig() {
        return componentsConfig;
    }

    @Override
    public JSONObject getSystemConfig() {
        return systemConfig;
    }

    // ================== Private ===================
    private DefaultServerConfig() {
        try {
            loadConfig();
            loadLog4j();
            loadJar();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("load config error");
            System.exit(1);
        }
    }

    private void loadConfig() {
        String configPath = "./conf/elise.json";
        File file = new File(configPath);
        if(file.exists()) {
            JSONObject config = FileUtil.getParamFromJSONConfig(file);
            appConfig = config.getJSONObject("app");
            componentsConfig = config.getJSONObject("components");
            systemConfig = config.getJSONObject("system");
        }
    }

    private void loadLog4j() {
        DOMConfigurator.configure("./conf/log4j.xml");
    }

    private void loadJar() throws NoSuchMethodException, MalformedURLException {
        if(!appConfig.containsKey("jar_path")) return;
        File libPath = new File(appConfig.getString("jar_path"));
        // 获取所有的.jar和.zip文件
        List<File> jarFiles = new ArrayList<File>();
        getAllFiles(libPath, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar") || name.endsWith(".zip");
            }
        }, jarFiles);

        // 从URLClassLoader类中获取类所在文件夹的方法
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        boolean accessible = method.isAccessible();     // 获取方法的访问权限
        try {
            if (!accessible) method.setAccessible(true);     // 设置方法的访问权限
            // 获取系统类加载器
            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            for (File file : jarFiles) {
                URL url = file.toURI().toURL();
                try {
                    method.invoke(classLoader, url);
                    logger.debug("read jar file [name={}] succeed.", file.getName());
                } catch (Exception e) {
                    logger.error("read jar file [name={}] failed.", file.getName());
                    System.exit(1);
                }
            }
        } finally {
            method.setAccessible(accessible);
        }
    }

    private static void getAllFiles(File srcDir, FilenameFilter filter, List<File> list) {
        File[] files = srcDir.listFiles();
        if(files == null) return;
        for (File file : files) {
            if (file.isDirectory())
                getAllFiles(file, filter, list);
            else if (filter.accept(file.getParentFile(), file.getName())) list.add(file);
        }
    }

    private void loadScheduler() {
        try {
            Class claz = Class.forName(componentsConfig.getString("scheduler"));
            scheduler = (Scheduler) claz.newInstance();
        } catch (Exception e) {
            scheduler = new QueueScheduler();
        }
    }

    private void loadDownloader() {
        try {
            Class claz = Class.forName(componentsConfig.getString("downloader"));
            downloader = (Downloader) claz.newInstance();
        } catch (Exception e) {
            downloader = new OKHTTPDownloader();
        }
    }

    private void loadCollectors() {
        collectors = new ArrayList<>();
        for(Object obj: componentsConfig.getJSONArray("collectors")) {
            try {
                String strClass = (String) obj;
                Class claz = Class.forName(strClass);
                collectors.add((Collector) claz.newInstance());
            } catch (Exception ignored) {}
        }
        if(collectors.isEmpty()) collectors.add(new ConsoleCollector());
    }
}

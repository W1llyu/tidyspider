package com.will.tidyspider.core.collector;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.will.tidyspider.core.entity.Results;
import com.will.tidyspider.core.resources.RunTimeConfig;
import com.will.tidyspider.core.utils.FilePersistentBase;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by shangru_yu.
 */
public class JsonFileCollector extends FilePersistentBase implements Collector {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private String path;

    public JsonFileCollector() {
        this.path = RunTimeConfig.getInstance().getServerConfig().getAppConfig().getString("persist_path");
    }

    public JsonFileCollector(String path) {
        this.path = path;
    }

    @Override
    public void process(Results results){
        String path = this.path + PATH_SEPERATOR;
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(
                    getFile(path + DigestUtils.md5Hex(results.getRequest().getUrl()) + ".json")));
            printWriter.write(JSON.toJSONString(results.getAll(), SerializerFeature.PrettyFormat));
            printWriter.close();
        } catch (IOException e) {
            logger.warn("write file error", e);
        }
    }
}

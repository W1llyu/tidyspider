package com.will.tidyspider.core.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by will on 02/01/2018.
 */
public abstract class FileUtil {
    public static JSONObject getParamFromJSONConfig(File file){
        JSONObject config = new JSONObject();
        Scanner scanner = null;
        StringBuilder buffer = new StringBuilder();
        try {
            scanner = new Scanner(file, "utf-8");
            while (scanner.hasNextLine()) {
                buffer.append(scanner.nextLine());
            }
            String json = buffer.toString();
            config = JSON.parseObject(json);

        } catch (FileNotFoundException e) {

        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        return config;
    }
}

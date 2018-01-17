package com.will.tidyspider.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by will on 02/01/2018.
 */
public abstract class StringUtil {
    public static String getFilteredCh(String str, String Regex){
        if(null == str){
            return "";
        }
        String result;
        Pattern pat = Pattern.compile(Regex);
        Matcher mat = pat.matcher(str);
        boolean rs = mat.find();
        if(rs) {
            result = mat.group(1);
        }else{
            result = "";
        }
        return result;
    }

    public static List<String> getFilteredChs(String content, String regex){
        List<String> strs = new ArrayList<String>();
        List<String> tmpStrs = new ArrayList<String>();
        if(null == content){
            return strs;
        }
        Pattern pat = Pattern.compile(regex);
        Matcher mat = pat.matcher(content);
        while(mat.find()){
            String tmpStr = mat.group(1);
            if(!tmpStr.equals("")){
                tmpStrs.add(tmpStr);
            }
        }
        for(String str: tmpStrs){
            if(!strs.contains(str)){
                strs.add(str);
            }
        }
        return strs;
    }

    public static String filterCh(String str){
        if(null == str)
            return str;
        return str.replaceAll("[^\\u4e00-\\u9fa5\\w\\pP\\pS\\s\\n\\r\\t]", "").replaceAll("[\\n]+", "\n");
    }
}

package com.will.tidyspider.core.entity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by shangru_yu.
 */
public class Results {

    private Map<String, Object> fields = new LinkedHashMap<String, Object>();

    private Request request;

    private boolean skip;

    private String typeIdentifier;

    public <T> T get(String key) {
        Object o = fields.get(key);
        if (o == null) {
            return null;
        }
        return (T) fields.get(key);
    }

    public Map<String, Object> getAll() {
        return fields;
    }

    public <T> Results put(String key, T value) {
        fields.put(key, value);
        return this;
    }

    public Request getRequest() {
        return request;
    }

    public Results setRequest(Request request) {
        this.request = request;
        return this;
    }

    public Results setTypeIdentier(String typeIdentier){
        this.typeIdentifier = typeIdentier;
        return this;
    }

    public String getTypeIdentifier(){
        return this.typeIdentifier;
    }

    public boolean isSkip() {
        return skip;
    }

    public Results setSkip(boolean skip) {
        this.skip = skip;
        return this;
    }

    @Override
    public String toString() {
        return "ResultItems{" +
                "fields=" + fields +
                ", request=" + request +
                ", skip=" + skip +
                '}';
    }
}

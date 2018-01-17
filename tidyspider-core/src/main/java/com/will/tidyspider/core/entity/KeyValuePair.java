package com.will.tidyspider.core.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by will on 02/01/2018.
 */
public class KeyValuePair implements Serializable{
    @JSONField(name="key")
    private String key;
    @JSONField(name="value")
    private String value;

    public KeyValuePair(){}

    public KeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        if(this.value == null) {
            return this.key;
        } else {
            int len = this.key.length() + 1 + this.value.length();
            StringBuilder buffer = new StringBuilder(len);
            buffer.append(this.key);
            buffer.append("=");
            buffer.append(this.value);
            return buffer.toString();
        }
    }
}

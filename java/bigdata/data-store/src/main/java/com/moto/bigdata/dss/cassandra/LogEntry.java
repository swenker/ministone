package com.moto.bigdata.dss.cassandra;

import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

/**
 * Created by wenjusun on 3/28/2016.
 */
@Table
public class LogEntry {
    @PrimaryKey
    private int id;

    private String api;
    private int latency;

    public LogEntry(int id, String api, int latency) {
        this.id = id;
        this.api = api;
        this.latency = latency;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public int getLatency() {
        return latency;
    }

    public void setLatency(int latency) {
        this.latency = latency;
    }


}

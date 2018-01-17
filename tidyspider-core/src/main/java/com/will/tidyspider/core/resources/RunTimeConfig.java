package com.will.tidyspider.core.resources;

import com.will.tidyspider.core.config.ServerConfig;
import com.will.tidyspider.core.config.TaskConfig;
import com.will.tidyspider.core.config.impl.SimpleServerConfig;
import com.will.tidyspider.core.config.impl.SimpleTaskConfig;

/**
 * Created by will on 04/01/2018.
 * Singleton
 */
public class RunTimeConfig {
    private static RunTimeConfig instance = new RunTimeConfig();

    private ServerConfig serverConfig;
    private TaskConfig taskConfig;

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public TaskConfig getTaskConfig() {
        return taskConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public void setTaskConfig(TaskConfig taskConfig) {
        this.taskConfig = taskConfig;
    }

    public static RunTimeConfig getInstance() {
        return instance;
    }

    private RunTimeConfig() {
        serverConfig = new SimpleServerConfig();
        taskConfig = new SimpleTaskConfig();
    }
}

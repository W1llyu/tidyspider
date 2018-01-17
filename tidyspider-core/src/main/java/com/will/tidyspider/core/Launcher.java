package com.will.tidyspider.core;

import com.will.tidyspider.core.config.impl.DefaultServerConfig;
import com.will.tidyspider.core.config.impl.DefaultTaskConfig;
import com.will.tidyspider.core.resources.RunTimeConfig;

/**
 * Created by will on 02/01/2018.
 */
public class Launcher {
    public static void main(String[] argvs) {
        RunTimeConfig.getInstance().setServerConfig(DefaultServerConfig.getInstance());
        RunTimeConfig.getInstance().setTaskConfig(DefaultTaskConfig.getInstance());
        new Spider().run();
    }
}

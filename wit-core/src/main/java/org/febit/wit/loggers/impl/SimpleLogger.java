// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.loggers.impl;

import java.io.PrintStream;
import org.febit.wit.Init;
import org.febit.wit.loggers.AbstractLogger;
import org.febit.wit.util.StringUtil;

/**
 *
 * @author zqq90
 */
public final class SimpleLogger extends AbstractLogger {

    //settings
    protected String level = "info";

    private PrintStream out = System.out;
    private String prefix;
    private int levelNum;

    @Init
    public void init() {
        prefix = StringUtil.concat("[", name, "] ");
        switch (level.trim().toLowerCase()) {
            case "error":
                levelNum = LEVEL_ERROR;
                break;
            case "warn":
                levelNum = LEVEL_WARN;
                break;
            case "info":
                levelNum = LEVEL_INFO;
                break;
            case "debug":
                levelNum = LEVEL_DEBUG;
                break;
            case "off":
            default:
                levelNum = Integer.MAX_VALUE;
        }
    }

    @Override
    protected boolean isEnabled(int level) {
        return level >= this.levelNum;
    }

    @Override
    protected void log(int level, String msg) {
        printLog(level, msg, null);
    }

    @Override
    protected void log(int level, String msg, Throwable throwable) {
        printLog(level, msg, throwable);
    }

    private void printLog(int level, String msg, Throwable throwable) {
        if (isEnabled(level)) {
            out.println(prefix.concat(msg != null ? msg : ""));
            if (throwable != null) {
                throwable.printStackTrace(out);
            }
        }
    }
}

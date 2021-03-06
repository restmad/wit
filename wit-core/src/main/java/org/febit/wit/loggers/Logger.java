// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.loggers;

/**
 *
 * @author zqq90
 */
public interface Logger {

    public boolean isDebugEnabled();

    public void debug(String msg);

    public void debug(String format, Object... args);

    public void debug(String msg, Throwable t);

    public boolean isInfoEnabled();

    public void info(String msg);

    public void info(String format, Object... args);

    public void info(String msg, Throwable t);

    public boolean isWarnEnabled();

    public void warn(String msg);

    public void warn(String format, Object... args);

    public void warn(String msg, Throwable t);

    public boolean isErrorEnabled();

    public void error(String msg);

    public void error(String format, Object... args);

    public void error(String msg, Throwable t);
}

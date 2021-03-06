// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.servlet.global;

import javax.servlet.ServletContext;
import org.febit.wit.global.GlobalManager;
import org.febit.wit.global.GlobalRegister;

/**
 *
 * @author zqq90
 * @since 1.4.0
 */
public class GlobalServletRegister implements GlobalRegister {

    protected ServletContext servletContext;
    protected String basePathName = "BASE_PATH";
    protected String servletContextName = "SERVLET_CONTEXT";

    @Override
    public void regist(GlobalManager manager) {
        manager.setConst(this.servletContextName, this.servletContext);
        manager.setConst(this.basePathName, this.servletContext.getContextPath());
    }
}

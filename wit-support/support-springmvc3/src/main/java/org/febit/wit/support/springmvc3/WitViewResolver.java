// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.support.springmvc3;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.febit.wit.servlet.WebEngineManager;
import org.febit.wit.servlet.WebEngineManager.ServletContextProvider;

/**
 *
 * @author zqq90
 */
public class WitViewResolver extends AbstractTemplateViewResolver {

    protected final WebEngineManager engineManager;

    @Override
    protected Class<?> requiredViewClass() {
        return WitView.class;
    }

    public WitViewResolver() {
        setViewClass(requiredViewClass());

        this.engineManager = new WebEngineManager(new ServletContextProvider() {

            @Override
            public ServletContext getServletContext() {
                return WitViewResolver.this.getServletContext();
            }
        });
    }

    public void setConfigPath(String configPath) {
        this.engineManager.setConfigPath(configPath);
    }

    public void resetEngine() {
        this.engineManager.resetEngine();
    }

    @Override
    protected WitView buildView(String viewName) throws Exception {
        WitView view = (WitView) super.buildView(viewName);
        view.setResolver(this);
        return view;
    }

    protected void render(String viewName, Map<String, Object> model, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        this.engineManager.renderTemplate(viewName, model, response);
    }
}

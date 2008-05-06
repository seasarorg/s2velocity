/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.velocity.tools;

import java.io.StringWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.tools.view.ToolboxManager;
import org.apache.velocity.tools.view.servlet.VelocityLayoutServlet;
import org.apache.velocity.tools.view.servlet.VelocityViewServlet;
import org.seasar.framework.container.ComponentNotFoundRuntimeException;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

/**
 * @author <a href="mailto:sato@ouobpo.org">Sato Tadayosi</a>
 * @author TANIGUCHI Hikaru
 * 
 * $Id$
 */
public class S2VelocityViewServlet extends VelocityViewServlet {
    /**
     * VelocityLayoutViewServlet互換のプロパティキー。 このプロパティキーの
     * vmファイル名をvelocity.propertiesに書いておけばよい
     */
    private static final String PROPERTY_ERROR_TEMPLATE = "tools.view.servlet.error.template";

    /** TOOLBOX_KEY servlet初期化パラメータにこの値が指定されている場合はコンテナ委任型になる */
    private static final String TOOLBOX_KEY_CONTAINER_MANAGED = "container";

    protected String errorTemplate;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        errorTemplate = getVelocityProperty(PROPERTY_ERROR_TEMPLATE, null);
    }

    /**
     * Overrides {@link S2VelocityViewServlet} to change toolboxManager
     * implementation from ServletToolboxManager to S2ServletToolboxManager.
     * 
     * Initializes the S2ServletToolboxManager for this servlet's toolbox (if
     * any).
     * 
     * @param config
     *            servlet configuration
     */
    protected void initToolbox(ServletConfig config) throws ServletException {
        /* check the servlet config and context for a toolbox param */
        String file = findInitParameter(config, TOOLBOX_KEY);
        if (file == null)
        {
            // ok, look in the default location
            file = DEFAULT_TOOLBOX_PATH;
            getVelocityEngine().getLog().debug("S2VelocityViewServlet: No toolbox entry in configuration."
                           + " Looking for '" + DEFAULT_TOOLBOX_PATH + "'");
        }
        
        /* if we have a toolbox, get a manager for it */
        if (TOOLBOX_KEY_CONTAINER_MANAGED.equals(file)) {
            getVelocityEngine().getLog().info("S2VelocityViewServlet: trying to init ContainerBasedToolboxManager");
            toolboxManager = initContainerBasedToolboxManager();
        } else {
            toolboxManager = S2ServletToolboxManager.getInstance(getServletContext(), file); 
        }
    }

    /**
     * create and return {@link ToolboxManager} instance.
     * If {@link S2Container} has a component with ToolboxManager.class, get component and returns it. 
     * If not, default {@link ContainerBasedToolboxManager} is instantiated and returns it.
     * 
     * @return appropriate toolbox manager instance. returns null if no s2container found.
     * 
     * @author TANIGUCHI Hikaru
     */
    protected ToolboxManager initContainerBasedToolboxManager() {
    	ToolboxManager toolboxManager;
    	
        S2Container container = SingletonS2ContainerFactory.getContainer();
        if (container != null) {
            try {
                toolboxManager = (ToolboxManager) container.getComponent(ToolboxManager.class);
                getVelocityEngine().getLog().info("S2VelocityViewServlet: Toolbox found in S2Container");
            } catch (ComponentNotFoundRuntimeException e) {
                toolboxManager = new ContainerBasedToolboxManager();
                getVelocityEngine().getLog().info("S2VelocityViewServlet: Toolbox not found in S2Container, using default ContainerBasedToolboxManager.");
            }
        } else {
        	toolboxManager = null;
            getVelocityEngine().getLog().error("S2VelocityViewServlet: no container found but container managed specified.");
        }
        
        return toolboxManager;
    }

    protected void error(HttpServletRequest request, HttpServletResponse response, Exception e)
            throws ServletException {
        if (errorTemplate == null) {
            super.error(request, response, e);
        }

        try {
            // get a velocity context
            Context ctx = createContext(request, response);

            Throwable cause = e;

            // if it's an MIE, i want the real cause and stack trace!
            if (cause instanceof MethodInvocationException)
            {
                // put the invocation exception in the context
                ctx.put(VelocityLayoutServlet.KEY_ERROR_INVOCATION_EXCEPTION, e);
                // get the real cause
                cause = ((MethodInvocationException)e).getWrappedThrowable();
            }

            // add the cause to the context
            ctx.put(VelocityLayoutServlet.KEY_ERROR_CAUSE, cause);

            // grab the cause's stack trace and put it in the context
            StringWriter sw = new StringWriter();
            cause.printStackTrace(new java.io.PrintWriter(sw));
            ctx.put(VelocityLayoutServlet.KEY_ERROR_STACKTRACE, sw.toString());

            // retrieve and render the error template
            Template et = getTemplate(errorTemplate);
            mergeTemplate(et, ctx, response);
        } catch (Exception e2) {
            getVelocityEngine().getLog().error("S2VelocityViewServlet: Error during error template rendering", e2);

        	// then punt the original to a higher authority
            super.error(request, response, e);
        }
    }

}
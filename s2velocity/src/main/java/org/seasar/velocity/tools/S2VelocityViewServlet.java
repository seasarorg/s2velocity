/*
 * Copyright 2004-2005 the Seasar Foundation and the Others.
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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.velocity.tools.view.servlet.VelocityViewServlet;

/**
 * @author <a href="mailto:sato@ouobpo.org">Sato Tadayosi</a>
 * 
 * @version $Id: S2VelocityViewServlet.java,v 1.5 2005/11/16 02:23:18 hanao Exp $
 */
public class S2VelocityViewServlet extends VelocityViewServlet {

    /**
     * Overrides {@link S2VelocityViewServlet} to change toolboxManager
     * implementation from ServletToolboxManager to S2ServletToolboxManager.
     * 
     * Initializes the S2ServletToolboxManager for this servlet's
     * toolbox (if any).
     *
     * @param config servlet configuration
     */
    protected void initToolbox(ServletConfig config) throws ServletException
    {
        /* check the servlet config and context for a toolbox param */
        String file = findInitParameter(config, TOOLBOX_KEY);

        /* if we have a toolbox, get a manager for it */
        if (file != null)
        {
            toolboxManager = 
                S2ServletToolboxManager.getInstance(getServletContext(), file); // NOTE changed here.
        }
        else
        {
            getVelocityEngine().info("VelocityViewServlet: No toolbox entry in configuration.");
        }
    }
}
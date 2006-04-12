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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.digester.RuleSet;
import org.apache.velocity.tools.view.ToolInfo;
import org.apache.velocity.tools.view.XMLToolboxManager;
import org.apache.velocity.tools.view.context.ToolboxContext;
import org.apache.velocity.tools.view.servlet.ServletToolboxManager;

// TODO 委譲バージョンのS2ServletToolboxManagerを作る
public class S2ServletToolboxManager_ extends XMLToolboxManager {

    private static Map            managerWrappersMap = new HashMap();
    private static RuleSet        servletRuleSet     = new S2ServletToolboxRuleSet();

    private ServletToolboxManager fManager;

    protected S2ServletToolboxManager_(ServletToolboxManager manager) {
        fManager = manager;
    }

    public static synchronized S2ServletToolboxManager_ getInstance(
            ServletContext servletContext,
            String toolboxFile) {
        if (!toolboxFile.startsWith("/"))
            toolboxFile = "/" + toolboxFile;
        String pathname = servletContext.getRealPath(toolboxFile);

        S2ServletToolboxManager_ toolboxManager_ = (S2ServletToolboxManager_) managerWrappersMap.get(pathname);
        if (toolboxManager_ == null) {
            toolboxManager_ = new S2ServletToolboxManager_(
                    ServletToolboxManager.getInstance(
                            servletContext,
                            toolboxFile));
            managerWrappersMap.put(pathname, toolboxManager_);
        }

        return toolboxManager_;
    }

    /**
     * NOTE Overrides {@link XMLToolboxManager}.
     */
    protected RuleSet getRuleSet() {
        return servletRuleSet;
    }

    // -------------------- delegate methods to ServletToolboxManager ---------------------

    public void setCreateSession(boolean b) {
        fManager.setCreateSession(b);
    }

    public void setXhtml(Boolean value) {
        fManager.setXhtml(value);
    }

    public void addTool(ToolInfo info) {
        fManager.addTool(info);
    }

    public ToolboxContext getToolboxContext(Object initData) {
        return fManager.getToolboxContext(initData);
    }
}
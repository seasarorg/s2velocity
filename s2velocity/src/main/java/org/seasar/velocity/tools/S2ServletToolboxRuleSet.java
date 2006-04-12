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

import org.apache.velocity.tools.view.servlet.ServletToolboxRuleSet;

/**
 * @author <a href="mailto:sato@ouobpo.org">Sato Tadayosi</a>
 * 
 * @version $Id: S2ServletToolboxRuleSet.java,v 1.4 2005/01/04 05:36:01 sato Exp $
 */
public class S2ServletToolboxRuleSet extends ServletToolboxRuleSet {

    /**
     * Overrides {@link ServletToolboxRuleSet} to use S2ServletToolInfo class
     * instead of ServletToolInfo class.
     */
    protected Class getToolInfoClass() {
        // NOTE to turn off auto-registration from toolbox.xml, just change here.
        //return S2ServletToolInfo.class;
        return AutoRegisteringS2ServletToolInfo.class;
    }
}
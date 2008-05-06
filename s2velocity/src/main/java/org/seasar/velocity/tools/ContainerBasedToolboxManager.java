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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.velocity.tools.view.ToolInfo;
import org.apache.velocity.tools.view.ToolboxManager;
import org.apache.velocity.tools.view.context.ViewContext;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.InstanceDef;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.deployer.InstancePrototypeDef;
import org.seasar.framework.container.deployer.InstanceRequestDef;
import org.seasar.framework.container.deployer.InstanceSessionDef;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

/**
 * (in japanese)
 * S2Containerベースでツールボックス・インスタンスを管理するToolboxManager実装.<br />
 * このToolboxManagerを使う場合は、Velocity-toolsで指定している<b>toolbox.xml</b>が不要となる.<br />
 * 各ツールのインスタンス管理はすべてS2Containerに委任される。requestやsessionなどのインスタンス定義もS2Container依存となるため、
 * 従来のtoolbox.xmlはdiconファイルの同等物を記入することとなる.<br />
 * <br />
 * 
 * @author tanigon
 */
public class ContainerBasedToolboxManager implements ToolboxManager {
	/** toolboxとして扱うコンテナのnamespace (default = "toolbox") */
	private String toolboxNamespace = "toolbox";

	/**
	 * addTool may be called by digester (see ToolboxRuleSet) but This toolbox
	 * manager doesn't handle any XML file with Digester or RuleSet, 
	 * so this method may be never called.
	 */
	public void addTool(ToolInfo toolInfo) {
		throw new UnsupportedOperationException();
	}

	/**
	 * addData may be called by digester (see ToolboxRuleSet) but This toolbox
	 * manager doesn't handle any XML file with Digester or RuleSet, 
	 * so this method may be never called.
	 */
	public void addData(ToolInfo arg0) {
		throw new UnsupportedOperationException();
	}

	public Map getToolbox(Object initData) {
		if (!(initData instanceof ViewContext)) {
			throw new IllegalArgumentException("arg must be ViewContext");
		}

		ViewContext ctx = (ViewContext) initData;
		Map toolbox = new HashMap();

		S2Container toolboxContainer = getToolboxContainer();
		int size = toolboxContainer.getComponentDefSize();
		for (int i = 0; i < size; i++) {
			ComponentDef def = toolboxContainer.getComponentDef(i);
			InstanceDef instanceDef = def.getInstanceDef();

			Object component = null;

			if (instanceDef instanceof InstanceSessionDef) {
				component = assembleSessionTool(initData, ctx, def);
			} else {
				component = def.getComponent();
				if (instanceDef instanceof InstanceRequestDef ||
                        instanceDef instanceof InstancePrototypeDef) {
                    initViewToolComponent(initData, component);
				}
			}
            
			toolbox.put(def.getComponentName(), component);
		}
		
		return toolbox;
	}

    /**
     * initialize component which is (former) ViewTool style.
     * @param initData initData, assumed as ViewContext
     * @param component destination object
     */
    protected void initViewToolComponent(Object initData, Object component) {
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(component.getClass());
        try {
            Method method = beanDesc.getMethod("init", new Class[] { Object.class } );
            if (method != null) {
                method.invoke(component, new Object[] { initData } );
            }
        } catch (Exception e) {
            // this case is normally expected
        }
    }

	protected S2Container getToolboxContainer() {
		return (S2Container) SingletonS2ContainerFactory.getContainer().getComponent(toolboxNamespace);
	}

	protected Object assembleSessionTool(Object initData, ViewContext ctx,
			ComponentDef def) {
		Object component = null;

		HttpSession session = ctx.getRequest().getSession();
		if (session != null) {
			component = session.getAttribute(def.getComponentName());
		}
		if (component == null) {
			component = def.getComponent();
            initViewToolComponent(initData, component);
		}

		return component;
	}

	public String getToolboxNamespace() {
		return toolboxNamespace;
	}

	public void setToolboxNamespace(String toolboxNamespace) {
		this.toolboxNamespace = toolboxNamespace;
	}

}

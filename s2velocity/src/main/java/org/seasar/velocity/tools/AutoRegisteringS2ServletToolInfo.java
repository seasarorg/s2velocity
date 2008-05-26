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

import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.InstanceDef;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.deployer.InstanceDefFactory;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.container.impl.ComponentDefImpl;

/**
 * DICONファイルにコンポーネント登録しなくても、toolbox.xmlから
 * 自動的に登録を行なうToolInfoクラス。
 * 
 * @author <a href="mailto:sato@ouobpo.org">Sato Tadayosi</a>
 * 
 * @version $Id: AutoRegisteringS2ServletToolInfo.java,v 1.4 2004/11/27 07:06:00 sato Exp $
 */
public class AutoRegisteringS2ServletToolInfo extends S2ServletToolInfo {

    protected static final String COMPONENT_NAME_PREFIX = "velocity.toolbox.";

    /**
     * Overrides {@link S2ServletToolInfo}.
     * ツールのインスタンスは、すべてコンテナに管理させる。
     * また、親メソッドと違い、コンポーネント名を元にコンポーネントを取得する。
     * こうすることで、コンポーネント衝突を回避できる。
     */
    protected Object createToolInstance() {
        Object tool = null;

        S2Container container = SingletonS2ContainerFactory.getContainer();
        String compName = COMPONENT_NAME_PREFIX + getKey();
        try { 
            if (!container.hasComponentDef(compName)) {
            	synchronized (AutoRegisteringS2ServletToolInfo.class) {
            		/*
            		 * 未解決コンポーネントへの初回のアクセスが集中した場合に二重登録となってしまうため回避
            		 * hasComponentDef==falseのケースは全体としてみると少ないケースなので1枚中でsynchronizedしている
            		 */
                    if (!container.hasComponentDef(compName)) {
		                // NOTE ツールのコンテナへの登録。toolbox.xmlのスコープを有効にするため、ツールはすべてprototypeとして管理される。
		                ComponentDef compDef = new ComponentDefImpl(clazz, compName);
		
		                /*
		                 * Velocity View がスコープ毎に適切にgetInstanceメソッドを呼び出しているので、
		                 * S2側では、すべてprototypeにしてしまってよいはず。
		                 */
		                compDef.setInstanceDef(InstanceDefFactory.getInstanceDef(InstanceDef.PROTOTYPE_NAME));
		
		                container.register(compDef);
                    }
            	}
            }
            tool = container.getComponent(compName);
        } catch (Exception e) {
            // コンテナへの登録に失敗した場合
            LOG.error("Cannot register tool "
                    + getKey()
                    + " to S2Container!", e);
        }

        return tool;
    }
}
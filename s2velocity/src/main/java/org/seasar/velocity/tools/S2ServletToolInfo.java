/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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

import org.apache.velocity.tools.view.ViewToolInfo;
import org.apache.velocity.tools.view.servlet.ServletToolInfo;
import org.apache.velocity.tools.view.tools.ViewTool;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.util.ClassUtil;

/**
 * @author <a href="mailto:sato@ouobpo.org">Sato Tadayosi</a>
 * 
 * @version $Id: S2ServletToolInfo.java,v 1.4 2004/11/27 06:38:45 sato Exp $
 */
public class S2ServletToolInfo extends ServletToolInfo {

    protected Class   clazz;
    protected boolean initializable = false;

    /**
     * Overrides {@link ViewTool}.
     * NOTE ここでS2コンテナからインスタンスを取り出している。
     */
    public Object getInstance(Object initData) {
        if (clazz == null) {
            LOG.error("Tool " + getKey() + " has no Class definition!");
            return null;
        }

        Object tool = createToolInstance();

        if (initializable) {
            ((ViewTool) tool).init(initData);
        }

        return tool;
    }

    /**
     * ツールのクラスがコンテナに存在すれば、コンテナからインスタンスを取得。
     * 無ければ、通常の方法でインスタンスを作る。
     * NOTE ここを上書きすることで、ツールのコンテナ管理の方法を変更できる。
     */
    protected Object createToolInstance() {
        Object tool = null;
        S2Container container = SingletonS2ContainerFactory.getContainer();
        if (container.hasComponentDef(clazz))
            tool = container.getComponent(clazz);
        else
            tool = ClassUtil.newInstance(clazz);
        return tool;
    }

    /**
     * If an instance of the tool cannot be created from
     * the classname passed to this method, it will throw an exception.
     *
     * @param classname the fully qualified java.lang.Class of the tool
     */
    public void setClassname(String classname) throws Exception {
        this.clazz = getApplicationClass(classname);
        /* create an instance and see if it is initializable */
        if (hasViewToolInterface(this.clazz)) {
            this.initializable = true;
        }
    }

    /**
     * コンストラクタに引数がある場合、S2コンテナを通してインスタンス生成
     * するようにしたいため、Class#newInstance() を安易に使えない。
     * そのため、以下のようにして ViewToolインタフェースの実装有無を確かめる
     * 必要がある。
     */
    private boolean hasViewToolInterface(Class clazz)
            throws InstantiationException, IllegalAccessException {
        if (clazz == Object.class)
            return false;

        for (int i = 0; i < clazz.getInterfaces().length; i++)
            if (clazz.getInterfaces()[i] == ViewTool.class)
                return true;

        return hasViewToolInterface(clazz.getSuperclass());
    }

    /**
     * Overrides {@link ViewToolInfo} to use class loader for
     * S2ServletToolInfo.
     */
    protected Class getApplicationClass(String name)
            throws ClassNotFoundException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = S2ServletToolInfo.class.getClassLoader();
        }
        return loader.loadClass(name);
    }

    /**
     * Overrides {@link ViewToolInfo} to use clazz field here.
     */
    public String getClassname() {
        return clazz.getName();
    }
}
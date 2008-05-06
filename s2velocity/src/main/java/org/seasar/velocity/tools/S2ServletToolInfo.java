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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.tools.view.ToolInfo;
import org.apache.velocity.tools.view.ViewToolInfo;
import org.apache.velocity.tools.view.servlet.ServletToolInfo;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.util.ClassUtil;

/**
 * @author <a href="mailto:sato@ouobpo.org">Sato Tadayosi</a>
 * @author tanigon
 * 
 * @version $Id: S2ServletToolInfo.java,v 1.4 2004/11/27 06:38:45 sato Exp $
 */
public class S2ServletToolInfo extends ServletToolInfo {
    protected static final Log LOG = LogFactory.getLog(S2ServletToolInfo.class);
    
	private String key;
    protected Class   clazz;
    private Method init = null;

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

        // NOTE: this version has no "configure" behavior. user should manage initialization with S2Container&dicon
        
        /* if the tool is initializable... */
        if (init != null)
        {
            try
            {
                // call the init method on the instance
                init.invoke(tool, new Object[]{ initData });
            }
            catch (IllegalAccessException iae)
            {
                LOG.error("Exception when calling init(Object) on "+tool, iae);
            }
            catch (InvocationTargetException ite)
            {
                LOG.error("Exception when calling init(Object) on "+tool, ite);
            }
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

    public void setKey(String key)
    {
        this.key = key;
    }

    /**
     * If an instance of the tool cannot be created from
     * the classname passed to this method, it will throw an exception.
     * 
     * @param classname the fully qualified java.lang.Class of the tool
     */
    public void setClassname(String classname) throws Exception {
        if (classname != null && classname.length() != 0)
        {
	    	this.clazz = getApplicationClass(classname);
	    	
            try
            {
                // try to get an init(Object) method
                this.init = clazz.getMethod("init", new Class[]{ Object.class });
            }
            catch (NoSuchMethodException nsme)
            {
                // ignore
            }
        } else {
        	this.clazz = null;
        }
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

    public String getClassname() {
        return clazz != null ? clazz.getName() : null;
    }
   

    public String getKey()
    {
        return key;
    }
    
}
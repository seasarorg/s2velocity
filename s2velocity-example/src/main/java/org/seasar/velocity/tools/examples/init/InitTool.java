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
package org.seasar.velocity.tools.examples.init;

import java.util.Date;

/**
 * This tool class is only for demonstration of "init(Object)" invocation, 
 * which is standard behavior of Velocity-Tools 1.3.
 * 
 * @author TANIGUCHI Hikaru
 */
public class InitTool {
	private int invocationCount = 0;
	private Date lastInvocation;
	private Object initData;
	
	public void init(Object initData) {
		this.initData = initData;
		invocationCount++;
		lastInvocation = new Date();
	}

	public int getInvocationCount() {
		return invocationCount;
	}

	public Date getLastInvocation() {
		return lastInvocation;
	}

	public Object getInitData() {
		return initData;
	}
}

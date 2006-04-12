/*********************************************************
 * PROJECT: s2velocity-example
 * Created on 2004/11/27, 14:32:13
 * 
 * Copyright (c) 2004 SATO TADAYOSI <sato@mogra.net>,
 * MOGRA DESIGN, Ltd.
 * all rights reserverd.
 * YOU'RE GONNA CARRY THAT WEIGHT...
 * 
 * $Id: CountTool.java,v 1.1 2004/11/27 06:38:45 sato Exp $
 *********************************************************/
package org.seasar.velocity.tools.examples.scope;

public class CountTool {

  private int count = 0;

  public int count() {
    return ++count;
  }
}
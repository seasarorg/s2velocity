package org.seasar.velocity.tools.examples.hello;


public class SampleTool {

  private IHello fHello;

  public SampleTool(IHello hello) {
    fHello = hello;
  }

  public String getString() {
    return fHello.getHello();
  }

}
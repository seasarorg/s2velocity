package org.seasar.velocity.tools.examples.dao;

public class ItemDAOImpl implements IItemDAO {

  public String[] findAllItems() {
    return new String[] { "モデラート・カンタービレ", "インディア・ソング", "愛人", "北の愛人"};
  }
}
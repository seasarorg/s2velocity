package org.seasar.velocity.tools.examples.dao;

public class ItemTool {

  private IItemDAO fItemDAO;

  public ItemTool(IItemDAO itemDAO) {
    fItemDAO = itemDAO;
  }

  public String[] getItems() {
    return fItemDAO.findAllItems();
  }

}
package org.wso2.carbon.order.manager.model;

import java.io.Serializable;

/**
 * Created by tharika on 2/16/17.
 */
public class Item implements Serializable{

  private String id;
  private String name;
  private Double price;

  public Item(String itemId, String itemName, Double itemPrice) {
    this.id = itemId;
    this.name = itemName;
    this.price = itemPrice;
  }

  public Item(){
    //Default Constructor
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Double getPrice() {  return price; }

  public void setPrice(Double price) {
    this.price = price;
  }
}
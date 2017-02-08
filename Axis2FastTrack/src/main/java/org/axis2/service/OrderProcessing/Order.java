package org.axis2.service.OrderProcessing;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Order {

    private String orderId;
    private ArrayList<Item> itemList;
    private Double orderPrice;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public ArrayList<Item> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<Item> itemList) {
        this.itemList = itemList;
    }

    public Double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(Double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public HashMap<String, ArrayList<String>> calculateOrderPrice() {
        ArrayList<String> noSuchProductList = new ArrayList<>();
        ArrayList<String> removeList = new ArrayList<>();
        orderPrice = 0.0;
        Inventory inventoryInstance = Inventory.getInstance();
        HashMap<Product, Integer> inventoryList = inventoryInstance.getProductsInStock();
        for (Item item : itemList) {
            boolean isProduct = false;
            String itemID = item.getItemId();
            Integer itemQuantity = item.getQuantity();
            for (Product prod : inventoryList.keySet()) {
                if (prod.getProductId().equals(item.getItemId())) {
                    if (inventoryInstance.updateInventory(itemQuantity, itemID)) {
                        orderPrice += prod.getUnitPrice() * itemQuantity;
                    } else {
                        removeList.add(itemID);
                    } //remove item from order
                    isProduct = true;
                    break;
                }
            }

            if(!isProduct) {
                noSuchProductList.add(itemID);
            }

        }
        DecimalFormat df = new DecimalFormat("####0.00"); //Rounding off price to two decimal places
        orderPrice = Double.parseDouble(df.format(orderPrice));
        inventoryInstance.printCurrentStockStatus();
        handleOrderItems(removeList);
        handleOrderItems(noSuchProductList);
        HashMap<String, ArrayList<String>> returnList = new HashMap<>();
        returnList.put("noSuchProductList", noSuchProductList);
        returnList.put("notInStockList", removeList);
        return returnList;
    }

    public void handleOrderItems (ArrayList<String> removeList) {

        if (!removeList.isEmpty()) {
            for (String item : removeList) {
                Iterator<Item> iterator = itemList.iterator();
                while (iterator.hasNext()) {
                    Item i = iterator.next();
                    if (i.getItemId().equals(item)) {
                        iterator.remove();
                    }
                }
            }
        }
    }

}

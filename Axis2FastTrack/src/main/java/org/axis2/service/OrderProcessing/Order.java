/*
 *
 *   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 * /
 */

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

    /**
     * This is the method used to calculate the order price
     *
     * @return - a list of items that are not in stock and a list of items that does not really exist in the inventory.
     * This handles the case where the customer inputs an invalid itemID
     */
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

            if (!isProduct) {
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

    /**
     * This method handles the order items to identified the items that are removed from the order
     *
     * @param removeList - items removed from the order
     */
    public void handleOrderItems(ArrayList<String> removeList) {

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

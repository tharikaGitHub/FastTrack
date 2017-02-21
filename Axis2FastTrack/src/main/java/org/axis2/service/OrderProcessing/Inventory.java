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

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Inventory {

    private static final Log log = LogFactory.getLog(Inventory.class);

    private HashMap<Product, Integer> productsInStock;

    private static Inventory instance = new Inventory();

    /**
     * This is the private constructor to initialize the stock. The Singleton Design pattern is used here
     */
    private Inventory() {
        Product prod1 = new Product("MacC_1", "Mac Cheese", 34.89);
        Product prod2 = new Product("PasReg_4", "Pasta Regular", 126.00);
        Product prod3 = new Product("PeaB_31", "Peanut Butter", 54.50);
        Product prod4 = new Product("AppSa_62", "Apple Sauce", 78.45);

        HashMap<Product, Integer> tempStock = new HashMap<>();

        tempStock.put(prod1, 25);
        tempStock.put(prod2, 12);
        tempStock.put(prod3, 14);
        tempStock.put(prod4, 11);

        setProductsInStock(tempStock);

    }

    /**
     * This is the method to return the single instance of the stock/inventory
     *
     * @return - single instance of inventory
     */
    public static Inventory getInstance() {
        return instance;
    }

    public HashMap<Product, Integer> getProductsInStock() {
        return productsInStock;
    }

    public void setProductsInStock(HashMap<Product, Integer> productsInStock) {
        this.productsInStock = productsInStock;
    }

    /**
     * This is the method used to update the inventory
     *
     * @param requestedQuantity - quantity requested in the order
     * @param id                - order id
     * @return - true if successfully updated
     */
    public boolean updateInventory(Integer requestedQuantity, String id) {
        for (Product prod : productsInStock.keySet()) {
            Integer availableQuant = productsInStock.get(prod);
            if (prod.getProductId().equals(id)) {
                if (availableQuant >= requestedQuantity) {
                    productsInStock.put(prod, availableQuant - requestedQuantity);
                    return true;
                } else
                    return false; //product not in stock
            }
        }
        return false; //no such product
    }

    /**
     * This is the method to print the stock status
     */
    public void printCurrentStockStatus() {
        for (Product prod : productsInStock.keySet()) {
            log.info(prod.getProductId() + " --- " + productsInStock.get(prod));
        }
    }
}

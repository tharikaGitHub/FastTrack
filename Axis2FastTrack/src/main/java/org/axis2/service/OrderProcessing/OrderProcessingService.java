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

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderProcessingService {

    private static final Log log = LogFactory.getLog(OrderProcessingService.class);

    private ArrayList<Order> orderList = new ArrayList<>();
    private int ordercount = 0;
    private static final String NAMESPACE = "http://OrderProcessing.service.axis2.org";

    /**
     * This method creates the order placed by the customer
     *
     * @param element - order submitted by the customer
     * @return - place order response to the customer
     */
    public OMElement placeOrder(OMElement element) {
        element.build();
        element.detach();
        ArrayList<Item> itemList = new ArrayList<>();
        OMElement orderItem = element.getFirstElement();
        OMElement itemID = (OMElement) orderItem.getFirstOMChild();
        OMElement quantity = (OMElement) itemID.getNextOMSibling();
        Item item = new Item(itemID.getText(), Integer.parseInt(quantity.getText()));
        itemList.add(item);

        while ((orderItem = (OMElement) orderItem.getNextOMSibling()) != null) {
            itemID = (OMElement) orderItem.getFirstOMChild();
            quantity = (OMElement) itemID.getNextOMSibling();
            item = new Item(itemID.getText(), Integer.parseInt(quantity.getText()));
            itemList.add(item);
        }

        ordercount++;
        String orderId = "order_" + ordercount;

        Order order = new Order();
        order.setOrderId(orderId);
        order.setItemList(itemList);
        HashMap<String, ArrayList<String>> itemsRemovedFromOrder = order.calculateOrderPrice();

        ArrayList<String> notInStockList = itemsRemovedFromOrder.get("notInStockList");
        ArrayList<String> noSuchProductList = itemsRemovedFromOrder.get("noSuchProductList");

        orderList.add(order);

        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = factory.createOMNamespace(NAMESPACE, "ns");
        OMElement method = factory.createOMElement("placeOrderResponse", omNs);
        OMElement id = factory.createOMElement("orderId", omNs);
        id.addChild(factory.createOMText(id, orderId));
        method.addChild(id);

        boolean notInStockListIsEmpty = notInStockList.isEmpty();
        boolean noSuchProductListIsEmpty = noSuchProductList.isEmpty();

        if (!notInStockListIsEmpty || !noSuchProductListIsEmpty) {
            OMElement itemsRemoved = factory.createOMElement("itemsRemovedFromOrder", omNs);
            if (!notInStockListIsEmpty) {
                for (String itemId : notInStockList) {
                    OMElement itemRemoved = factory.createOMElement("item", omNs);
                    itemRemoved.addChild(factory.createOMText(itemRemoved, itemId));
                    itemsRemoved.addChild(itemRemoved);
                }
                OMElement reason = factory.createOMElement("reason", omNs);
                reason.addChild(factory.createOMText(reason, "Items out of stock"));
                itemsRemoved.addChild(reason);
            }

            if (!noSuchProductListIsEmpty) {
                for (String itemId : noSuchProductList) {
                    OMElement itemRemoved = factory.createOMElement("item", omNs);
                    itemRemoved.addChild(factory.createOMText(itemRemoved, itemId));
                    itemsRemoved.addChild(itemRemoved);
                }
                OMElement reason = factory.createOMElement("reason", omNs);
                reason.addChild(factory.createOMText(reason, "No such product"));
                itemsRemoved.addChild(reason);
            }
            method.addChild(itemsRemoved);
        }
        OMElement price = factory.createOMElement("orderPrice", omNs);
        price.addChild(factory.createOMText(price, "$" + order.getOrderPrice()));
        method.addChild(price);

        log.info("Created order with id " + orderId);

        return method;
    }

    /**
     * This is the method used to view the details of the placed order
     *
     * @param element - request sent in by the customer
     * @return - view order details response
     */
    public OMElement viewOrderDetails(OMElement element) {
        element.build();
        element.detach();

        OMElement orderIdRequested = element.getFirstElement();
        String orderId = orderIdRequested.getText();

        ArrayList<Item> items = new ArrayList<>();

        for (Order order : orderList) {
            if (order.getOrderId().equals(orderId)) {
                items = order.getItemList();
            }
        }

        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = factory.createOMNamespace(NAMESPACE, "ns");
        OMElement method = factory.createOMElement("viewOrderDetailsResponse", omNs);

        if (!items.isEmpty()) {
            for (Item i : items) {
                OMElement item = factory.createOMElement("item", omNs);

                OMElement itemId = factory.createOMElement("itemId", omNs);
                itemId.addChild(factory.createOMText(itemId, i.getItemId()));
                item.addChild(itemId);

                OMElement quantity = factory.createOMElement("quantity", omNs);
                quantity.addChild(factory.createOMText(quantity, "" + i.getQuantity()));
                item.addChild(quantity);

                method.addChild(item);
            }
        }

        log.info("Took order details of order with id " + orderId);

        return method;
    }

    /**
     * This method gets the current stock status
     *
     * @param element - request from the customer
     * @return - stock status response
     */
    public OMElement getStockStatus(OMElement element) {
        element.build();
        element.detach();

        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = factory.createOMNamespace(NAMESPACE, "ns");
        OMElement method = factory.createOMElement("getStockStatusResponse", omNs);

        Inventory inventoryInstance = Inventory.getInstance();
        HashMap<Product, Integer> productsInStock = inventoryInstance.getProductsInStock();

        for (Product prod : productsInStock.keySet()) {
            OMElement product = factory.createOMElement("product", omNs);
            OMElement id = factory.createOMElement("productID", omNs);
            id.addChild(factory.createOMText(id, prod.getProductId()));
            OMElement name = factory.createOMElement("productName", omNs);
            name.addChild(factory.createOMText(name, prod.getProductName()));
            OMElement quantity = factory.createOMElement("quantityInStock", omNs);
            quantity.addChild(factory.createOMText(quantity, productsInStock.get(prod).toString()));
            product.addChild(id);
            product.addChild(name);
            product.addChild(quantity);
            method.addChild(product);
        }

        return method;

    }

}

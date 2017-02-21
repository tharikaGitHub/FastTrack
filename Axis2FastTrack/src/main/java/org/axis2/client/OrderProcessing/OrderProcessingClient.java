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

package org.axis2.client.OrderProcessing;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;

public class OrderProcessingClient {

    private static final Log log = LogFactory.getLog(OrderProcessingClient.class);

    private static EndpointReference targetEPR = new EndpointReference(
            "http://localhost:8080/axis2/services/OrderProcessingService");
    private static final String NAMESPACE = "orderprocessingns";

    private OrderProcessingClient() {
    }

    public static void main(String[] args) {
        try {
            Options options = new Options();
            options.setTo(targetEPR);
            options.setTransportInProtocol(Constants.TRANSPORT_HTTP);

            ServiceClient sender;
            sender = new ServiceClient();
            sender.setOptions(options);

            String orderId = createOrder(sender);
            OMElement orderResult = getOrderDetails(sender, orderId);
            OMElement stockStatusResult = getStockStatus(sender);

            System.out.println();
            System.out.println("--------------");
            System.out.println("Order id - " + orderId);
            System.out.println("--------------");
            System.out.println();
            System.out.println("Order Details");
            System.out.println();
            printOrder(orderResult);
            System.out.println();
            System.out.println("____________________________________________________________________________");
            System.out.println();
            System.out.println("Stock status");
            System.out.println();
            printStockStatus(stockStatusResult);

        } catch (AxisFault e) {
            log.error(e);
        }
    }

    /**
     * This method forms the payload for the place order request.
     *
     * @param order - the order with the itemID and the requested quantity
     * @return - payload
     */
    public static OMElement placeOrderPayload(HashMap<String, Integer> order) {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = factory.createOMNamespace(NAMESPACE, "ns");
        OMElement method = factory.createOMElement("placeOrder", omNs);

        for (String key : order.keySet()) {
            OMElement item = factory.createOMElement("item", omNs);

            OMElement itemId = factory.createOMElement("itemId", omNs);
            itemId.addChild(factory.createOMText(itemId, key));
            item.addChild(itemId);

            OMElement quantity = factory.createOMElement("quantity", omNs);
            quantity.addChild(factory.createOMText(quantity, "" + order.get(key)));
            item.addChild(quantity);

            method.addChild(item);
        }

        return method;
    }

    /**
     * This method forms the payload for the view order details request.
     *
     * @param id - order id
     * @return - payload
     */
    public static OMElement viewOrderDetailsPayload(String id) {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = factory.createOMNamespace(NAMESPACE, "ns");

        OMElement method = factory.createOMElement("viewOrderDetails", omNs);
        OMElement orderId = factory.createOMElement("orderId", omNs);
        orderId.addChild(factory.createOMText(orderId, id));
        method.addChild(orderId);
        return method;
    }

    /**
     * This method processes and creates the order
     *
     * @param sender - service client object
     * @return - order id
     */
    public static String createOrder(ServiceClient sender) {
        HashMap<String, Integer> order = new HashMap<>();
        order.put("MacC_1", 4);
        order.put("PasReg_4", 2);
        order.put("AppSa_62", 1);

        String orderId = "";

        OMElement orderPayload = placeOrderPayload(order);
        OMElement result;
        try {
            result = sender.sendReceive(orderPayload);
            orderId = result.getFirstElement().getText();
            OMElement orderElem = result.getFirstElement();
            OMElement removedItemList;

            String qnameItemsRemoved = "itemsRemovedFromOrder";
            String qnameItem = "item";
            String qnameReason = "reason";
            String qnamePrice = "orderPrice";
            System.out.println();
            System.out.println("Order ID: " + orderId);
            do {
                if (orderElem.getQName().getLocalPart().equals(qnameItemsRemoved)) {
                    System.out.println("Items removed from order : ");
                    System.out.println("--------------------------");
                    removedItemList = ((OMElement) orderElem.getFirstOMChild());
                    do {
                        String removedItem = removedItemList.getText();
                        System.out.println("- " + removedItem);
                    } while (!(removedItemList = ((OMElement) removedItemList.getNextOMSibling())).getQName()
                            .getLocalPart().equals(qnameReason));

                    if (removedItemList.getQName().getLocalPart().equals(qnameReason)) {
                        System.out.println("Reason for removal - " + removedItemList.getText());
                    }

                    if ((removedItemList.getNextOMSibling()) != null) {
                        while (((removedItemList = (OMElement) removedItemList.getNextOMSibling()).getQName()
                                .getLocalPart().equals(qnameItem))) {
                            String removedItem = removedItemList.getText();
                            System.out.println("- " + removedItem);
                        }
                        if (removedItemList.getQName().getLocalPart().equals(qnameReason)) {
                            System.out.println("Reason for removal - " + removedItemList.getText());
                        }
                    }

                }
                if (orderElem.getQName().getLocalPart().equals(qnamePrice)) {
                    System.out.println("Total Price of Order : " + orderElem.getText());
                }
            } while ((orderElem = (OMElement) orderElem.getNextOMSibling()) != null);
        } catch (AxisFault e) {
            log.error(e);
        }
        return orderId;
    }

    /**
     * This method gets the order details
     *
     * @param sender  - the service client
     * @param orderId - order id
     * @return - result of order from the server
     */
    public static OMElement getOrderDetails(ServiceClient sender, String orderId) {
        OMElement orderDetails = viewOrderDetailsPayload(orderId);
        OMElement orderResult;
        try {
            orderResult = sender.sendReceive(orderDetails);
        } catch (AxisFault e) {
            OMFactory factory = OMAbstractFactory.getOMFactory();
            OMNamespace omNs = factory.createOMNamespace(NAMESPACE, "ns");
            orderResult = factory.createOMElement("error", omNs);
            orderResult.addChild(factory.createOMText(orderResult, "error"));
            log.error(e);
        }

        return orderResult;
    }

    /**
     * This method prints the order details from the response of the server
     *
     * @param element - response from the server
     */
    public static void printOrder(OMElement element) {
        element.build();

        if ((element.getFirstElement()) != null) {

            element.detach();

            OMElement orderItem = element.getFirstElement();
            OMElement item;
            OMElement quantity;

            do {
                item = (OMElement) orderItem.getFirstOMChild();
                quantity = (OMElement) item.getNextOMSibling();
                System.out.println("Item ID : " + item.getText() + " ---- " + "Quantity ordered : " + Integer
                        .parseInt(quantity.getText()));
            } while ((orderItem = (OMElement) orderItem.getNextOMSibling()) != null);
        } else {
            System.out.println("The requested order does not exist");
        }

    }

    /**
     * This method creates the payload for requesting the stock status.
     *
     * @return - payload
     */
    public static OMElement stockStatusRequestPayload() {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = factory.createOMNamespace(NAMESPACE, "ns");

        OMElement method = factory.createOMElement("getStockStatus", omNs);
        return method;
    }

    /**
     * This method gets the stock status
     *
     * @param sender - the service client
     * @return - response from the server
     */
    public static OMElement getStockStatus(ServiceClient sender) {
        OMElement stockStatusRequest = stockStatusRequestPayload();
        OMElement stockResult;
        try {
            stockResult = sender.sendReceive(stockStatusRequest);
        } catch (AxisFault e) {
            OMFactory factory = OMAbstractFactory.getOMFactory();
            OMNamespace omNs = factory.createOMNamespace(NAMESPACE, "ns");
            stockResult = factory.createOMElement("error", omNs);
            stockResult.addChild(factory.createOMText(stockResult, "error"));
            log.error(e);
        }

        return stockResult;
    }

    /**
     * THis method prints the status of the stock
     *
     * @param element - the response from the server
     */
    public static void printStockStatus(OMElement element) {
        element.build();

        if ((element.getFirstElement()) != null) {

            element.detach();

            OMElement product = element.getFirstElement();
            OMElement productId;
            OMElement productName;
            OMElement quantityAvailable;

            do {
                productId = (OMElement) product.getFirstOMChild();
                productName = (OMElement) productId.getNextOMSibling();
                quantityAvailable = (OMElement) productName.getNextOMSibling();
                System.out.println(
                        "Product ID : " + productId.getText() + " --- " + "Name : " + productName.getText() + " --- "
                                + "Quantity in Stock : " + quantityAvailable.getText());
            } while ((product = (OMElement) product.getNextOMSibling()) != null);
        } else {
            System.out.println("The requested order does not exist");
        }

    }
}

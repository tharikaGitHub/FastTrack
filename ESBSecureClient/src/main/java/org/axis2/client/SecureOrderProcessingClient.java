package org.axis2.client;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;

import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.impl.httpclient3.HttpTransportPropertiesImpl;

import java.util.HashMap;
import java.util.logging.Logger;

public class SecureOrderProcessingClient {

    private static EndpointReference targetEPR = new EndpointReference(
            "https://tharika-ThinkPad-X1-Carbon-4th:8243/services/SecureOrderProcessingService ");

    private static final String NAMESPACE = "http://OrderProcessing.service.axis2.org";

    private static Logger logger = Logger.getLogger(SecureOrderProcessingClient.class.getName());

    public static void main(String[] args) {
        try {
            Options options = new Options();
            options.setTo(targetEPR);
            options.setTransportInProtocol(Constants.TRANSPORT_HTTPS);

            System.setProperty("javax.net.ssl.trustStore",
                    "/home/tharika/wso2esb-5.0.0/repository/resources/security/client-truststore.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");

            ConfigurationContext ctx = ConfigurationContextFactory.createConfigurationContextFromFileSystem("/home/tharika/axis2-1.7.4/repository", null);

            HttpTransportPropertiesImpl.Authenticator authenticator = new HttpTransportPropertiesImpl.Authenticator();

            authenticator.setUsername("admin");
            authenticator.setPassword("admin");

            options.setProperty(HTTPConstants.AUTHENTICATE, authenticator);
            options.setProperty(Constants.Configuration.ENABLE_REST, Constants.VALUE_TRUE);
            options.setManageSession(true);

            ServiceClient sender;
            sender = new ServiceClient(ctx,null);
            sender.setOptions(options);

            sender.engageModule("addressing");
            sender.engageModule("rampart");

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
            logger.info(e.getMessage());
        }
    }

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


    public static OMElement viewOrderDetailsPayload(String id) {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = factory.createOMNamespace(NAMESPACE, "ns");

        OMElement method = factory.createOMElement("viewOrderDetails", omNs);
        OMElement orderId = factory.createOMElement("orderId", omNs);
        orderId.addChild(factory.createOMText(orderId, id));
        method.addChild(orderId);
        return method;
    }

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
                    }
                    while (!(removedItemList = ((OMElement) removedItemList.getNextOMSibling())).getQName().getLocalPart().equals(qnameReason));

                    if (removedItemList.getQName().getLocalPart().equals(qnameReason)) {
                        System.out.println("Reason for removal - " + removedItemList.getText());
                    }

                    if ((removedItemList.getNextOMSibling()) != null) {
                        while (((removedItemList = (OMElement) removedItemList.getNextOMSibling()).getQName().getLocalPart().equals(qnameItem))) {
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
            logger.info(e.getMessage());
        }
        return orderId;
    }

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
            e.printStackTrace();
        }

        return orderResult;
    }

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
                System.out.println("Item ID : " + item.getText() + " ---- " + "Quantity ordered : " + Integer.parseInt(quantity.getText()));
            } while ((orderItem = (OMElement) orderItem.getNextOMSibling()) != null);
        } else {
            System.out.println("The requested order does not exist");
        }
    }

    public static OMElement stockStatusRequestPayload() {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = factory.createOMNamespace(NAMESPACE, "ns");

        OMElement method = factory.createOMElement("getStockStatus", omNs);
        return method;
    }

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
            logger.info(e.getMessage());
        }

        return stockResult;
    }

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
                System.out.println("Product ID : " + productId.getText() + " --- " + "Name : " + productName.getText() + " --- " + "Quantity in Stock : " + quantityAvailable.getText());
            } while ((product = (OMElement) product.getNextOMSibling()) != null);
        } else {
            System.out.println("The requested order does not exist");
        }

    }


}
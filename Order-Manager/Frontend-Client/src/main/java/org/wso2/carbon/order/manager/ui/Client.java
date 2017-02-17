package org.wso2.carbon.order.manager.ui;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.wso2.carbon.order.manager.stub.OrderProcessingManagerStub;
import org.wso2.carbon.order.manager.model.xsd.Item;

import java.rmi.RemoteException;

/**
 * Created by tharika on 2/16/17.
 */
public class Client {
    private OrderProcessingManagerStub stub;

    public Client(ConfigurationContext ctx, String backendServerUrl, String cookie) throws AxisFault{
        String serviceUrl = backendServerUrl + "OrderProcessingManager";
        stub = new OrderProcessingManagerStub(ctx, serviceUrl);
        ServiceClient serviceClient = stub._getServiceClient();
        Options options = serviceClient.getOptions();
        options.setManageSession(true);
        options.setProperty(HTTPConstants.COOKIE_STRING, cookie);

    }

    public Item[] getItems() throws RemoteException {
        return stub.getOrders();
    }

    public void addItem(Item item) throws RemoteException {
        stub.addOrder(item);
    }
}
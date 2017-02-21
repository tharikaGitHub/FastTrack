/*
 *
 *   Copyright (c) ${date}, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.order.manager.ui;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.wso2.carbon.order.manager.stub.OrderProcessingManagerStub;
import org.wso2.carbon.order.manager.model.xsd.Item;

import java.rmi.RemoteException;

public class Client {
    private OrderProcessingManagerStub stub;

    public Client(ConfigurationContext ctx, String backendServerUrl, String cookie) throws AxisFault {
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
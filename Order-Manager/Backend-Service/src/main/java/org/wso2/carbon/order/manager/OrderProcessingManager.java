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

package org.wso2.carbon.order.manager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.RegistryType;
import org.wso2.carbon.order.manager.model.Item;
import org.wso2.carbon.registry.api.Registry;
import org.wso2.carbon.registry.api.RegistryException;
import org.wso2.carbon.registry.api.Resource;

import java.io.*;
import java.util.HashMap;

public class OrderProcessingManager {
    private static int orderID = 1;
    private static final String REG_LOCATION = "orders_location";
    private Registry registry = null;

    private static final Log log = LogFactory.getLog(OrderProcessingManager.class);

    /**
     * This constructor is used to initialize the list of orders.
     */
    public OrderProcessingManager() {
        HashMap<Integer, Item> orders = new HashMap<Integer, Item>();

        orders.put(orderID++, new Item("MacC_1", "Mac Cheese", 34.89));
        orders.put(orderID++, new Item("PasReg_4", "Pasta Regular", 126.00));
        orders.put(orderID++, new Item("PeaB_31", "Peanut Butter", 54.50));
        orders.put(orderID++, new Item("AppSa_62", "Apple Sauce", 78.45));

        registry = CarbonContext.getThreadLocalCarbonContext()
                .getRegistry(RegistryType.valueOf(RegistryType.LOCAL_REPOSITORY.toString()));

        try {
            Resource orderRes = registry.newResource();
            orderRes.setContent(serialize(orders));
            registry.put(REG_LOCATION, orderRes);
        } catch (RegistryException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
        }
    }

    /**
     * This method gets the orders.
     *
     * @return - list of items
     */
    public Item[] getOrders() {
        Resource orderRes = null;
        try {
            orderRes = registry.get(REG_LOCATION);
            if (orderRes != null) {
                HashMap<Integer, Item> tmp = (HashMap<Integer, Item>) deserialize((byte[]) orderRes.getContent());
                return tmp.values().toArray(new Item[tmp.values().size()]);
            }
        } catch (IOException e) {
            log.error(e);
        } catch (ClassNotFoundException e) {
            log.error(e);
        } catch (RegistryException e) {
            log.error(e);
        }
        return new Item[] {};
    }

    /**
     * This method adds the order.
     *
     * @param item - item for the order
     */
    public void addOrder(Item item) {
        Resource orderRes = null;
        try {
            orderRes = registry.get(REG_LOCATION);
            if (orderRes != null) {
                HashMap<Integer, Item> tmp = (HashMap<Integer, Item>) deserialize((byte[]) orderRes.getContent());
                tmp.put(orderID++, item);
                orderRes.setContent(serialize(tmp));
                registry.put(REG_LOCATION, orderRes);
            }
        } catch (RegistryException e) {
            log.error(e);
        } catch (ClassNotFoundException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
        }
    }

    /**
     * This method serializes the object (it will be stored in the carbon registry)
     *
     * @param obj - order
     * @return - serialized order object
     * @throws IOException - exception when serializing
     */
    private static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(obj);
        return b.toByteArray();
    }

    /**
     * This method deserializes the byte stream.
     *
     * @param bytes - stream of bytes
     * @return - order object
     * @throws IOException            - exception when deserializing
     * @throws ClassNotFoundException exception when deserializing
     */
    private static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);
        return o.readObject();
    }
}
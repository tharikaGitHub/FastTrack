package org.wso2.carbon.order.manager;

import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.RegistryType;
import org.wso2.carbon.order.manager.model.Item;
import org.wso2.carbon.registry.api.Registry;
import org.wso2.carbon.registry.api.RegistryException;
import org.wso2.carbon.registry.api.Resource;

import java.io.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by tharika on 2/16/17.
 */
public class OrderProcessingManager {
    private static int orderID = 1;
    private static final String REG_LOCATION = "orders_location";
    Registry registry = null;

    private static Logger logger = Logger.getLogger(OrderProcessingManager.class.getName());

    public OrderProcessingManager() {
        HashMap<Integer, Item> orders = new HashMap<Integer, Item>();

        orders.put(orderID++, new Item("MacC_1", "Mac Cheese", 34.89));
        orders.put(orderID++, new Item("PasReg_4", "Pasta Regular", 126.00));
        orders.put(orderID++, new Item("PeaB_31", "Peanut Butter", 54.50));
        orders.put(orderID++, new Item("AppSa_62", "Apple Sauce", 78.45));

        registry = CarbonContext.getThreadLocalCarbonContext().getRegistry(RegistryType.valueOf(RegistryType.LOCAL_REPOSITORY.toString()));

        try {
            Resource orderRes = registry.newResource();
            orderRes.setContent(serialize(orders));
            registry.put(REG_LOCATION, orderRes);
        } catch (RegistryException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    public Item[] getOrders() {
        Resource orderRes = null;
        try {
            orderRes = registry.get(REG_LOCATION);
            if (orderRes != null) {
                HashMap<Integer, Item> tmp = (HashMap<Integer, Item>) deserialize((byte[]) orderRes.getContent());
                return tmp.values().toArray(new Item[tmp.values().size()]);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } catch (RegistryException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return new Item[]{};
    }

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
            logger.log(Level.SEVERE, e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    private static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(obj);
        return b.toByteArray();
    }

    private static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);
        return o.readObject();
    }
}